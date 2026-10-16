import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;

import javax.swing.text.AbstractDocument.Content;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.StringBuilder;


public class Git {
    
    private File git;
    private File objects;
    private File index;
    private File HEAD;


    public File getGit() {
        return git;
    }

    public void setGit(File git) {
        this.git = git;
    }

    public File getObjects() {
        return objects;
    }

    public void setObjects(File objects) {
        this.objects = objects;
    }

    public File getIndex() {
        return index;
    }

    public void setIndex(File index) {
        this.index = index;
    }

    public File getHEAD() {
        return HEAD;
    }

    public void setHEAD(File head) {
        HEAD = head;
    }

    public static void main(String[] args) {
        Git newGit = new Git();
        

        try {

            System.out.println("----making test.txt-----");

            System.out.println(hashFile("test.txt"));
            newGit.makeBlob("test.txt");

            newGit.addFileEntry("test.txt");

            System.out.println("----making Hello.txt-----");

            System.out.println(hashFile("Hello.txt"));
            newGit.makeBlob("Hello.txt");

            newGit.addFileEntry("Hello.txt");

            
            System.out.println("----making bigfile.txt-----");

            System.out.println(hashFile("bigfile.txt"));
            newGit.makeBlob("bigfile.txt");

            newGit.addFileEntry("bigfile.txt");

            // System.out.println("----making testFolder/test.txt-----");

            // System.out.println(hashFile("testFolder/test.txt"));
            // newGit.makeBlob("testFolder/test.txt");

            // newGit.addFileEntry("testFolder/test.txt");

            System.out.println("-----testing createTree------");
            ArrayList<GitObject> testList = new ArrayList<GitObject>();
            
            // adding hello.txt
            testList.add(new BLOB(hashFile("Hello.txt"), "Hello.txt"));

            // adding bigfile.txt
            testList.add(new BLOB(hashFile("bigfile.txt"), "bigfile.txt"));

            // adding test.txt
            testList.add(new BLOB(hashFile("test.txt"), "test.txt"));

  

            String root = newGit.createTree(testList, "");
            System.out.println("root tree: " + root);
            

            
 


        } catch (Exception e) {
            System.out.println("Cannot Run SHA-1 Hash File");
        }

    }

    public Git() { 
        gitInitialize();
    }

    public void gitInitialize() { 
        try {
            int exisitingFileCount = 0;

            git = new File("git/");
            if (!git.mkdir()) { 
                exisitingFileCount++;
            }
            git.mkdir();

            objects = new File(git, "objects/");
            if (!objects.mkdir()) { 
                exisitingFileCount++;
            }

            index = new File(git, "index");
            if (!index.createNewFile()) { 
                exisitingFileCount++;
            }
            HEAD = new File(git, "HEAD");
            if (!HEAD.createNewFile()) { 
                exisitingFileCount++;
            }

            if (exisitingFileCount == 4) { 
                System.out.println("Git Repository Already Exists");
            } else { 
                System.out.println("Git Repository Created");
            }

        } catch (IOException e) { 
            System.out.println("File error: " + e.getMessage());
        }
    }

    public static String hashFile(String filePath) throws IOException {
        // TODO (FH-4): read the whole file, digest it, convert the bytes to hex

        Path path = Path.of(filePath);
        if (!Files.isRegularFile(path)) {
            throw new IOException("no such file: " + filePath);
        }

        byte[] fileBytes = Files.readAllBytes(path);

        return (hashText(fileBytes));

        // MessageDigest digest;

        // try {
        //     digest = MessageDigest.getInstance("SHA-1");
        // } catch (NoSuchAlgorithmException e) {
        //     throw new IllegalStateException("SHA-1 is not available", e);
        // }

        // byte[] hashByteArray = digest.digest(fileBytes);

        // return HexFormat.of().formatHex(hashByteArray);
    }

    public static String hashText(byte[] fileBytes) throws IOException {
        
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 is not available", e);
        }

        byte[] hashByteArray = digest.digest(fileBytes);

        return HexFormat.of().formatHex(hashByteArray);
    }

    public void makeBlob(String filePath) throws IOException { 
        
        String hash = Git.hashFile(filePath);

        File newBLOB = new File(objects, hash);
        newBLOB.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(filePath));

        FileWriter writer = new FileWriter(newBLOB);

        int character = br.read();
        while (character != -1) {
            writer.write((char) character);
            character = br.read();
        }

        br.close();
        writer.close();
        
        // StringBuilder fileContents = new StringBuilder();
        // String fileLine = br.readLine();
        
        

        // while (fileLine != null) {
        //     fileContents.append(fileLine);

        //     String nextFileLine = br.readLine();
        //     if (nextFileLine != null) {
        //         fileContents.append("\n");
        //     }

        //     fileLine = nextFileLine;
        // }

        // br.close();

        // String finalContents = fileContents.toString();

        // FileWriter writer = new FileWriter(newBLOB);
        // writer.write(finalContents);
        //writer.close();
    }

    public void addFileEntry(String filePath) throws IOException { 
        String hash = hashFile(filePath);


        StringBuilder fileContents = new StringBuilder();

        BufferedReader br = new BufferedReader(new FileReader(index));

        String newContents = hash + " " + filePath;

       
        String contents = br.readLine();

        while (contents != null) {
            if (contents.equals(newContents)) {
                br.close();
                return;
            } else {
                fileContents.append(contents + "\n");
                contents = br.readLine();
            }
        }

        
        fileContents.append(newContents);

        String finalContents = fileContents.toString();
        
        FileWriter writer = new FileWriter(index);
        writer.write(finalContents);

        writer.close();
        br.close();

       
    }


    public String createTree(ArrayList<GitObject> workingList, String dirPath) throws IOException {
        
        ArrayList<GitObject> children = new ArrayList<GitObject>();

        // this for loop will compare the last / in each path inside the working list
        // it will take the substring of that path from its start to that last /
        // if that substring equals the dirPath, then it must be a child
        // all children get added to the children array
        
        for (int i = 0; i < workingList.size(); i++) {
            GitObject currentObject = workingList.get(i);
            int objectLastSlash = currentObject.getPath().lastIndexOf("/");

            String objectFolderPath;

            if (objectLastSlash == -1) {
                objectFolderPath = "";
            } else {
                objectFolderPath = currentObject.getPath().substring(0, objectLastSlash);
            }

            
            if (dirPath.equals(objectFolderPath)) {
                children.add(currentObject);
            }
            
        }

        StringBuilder directoryContents = new StringBuilder();

        for (int j = 0; j < children.size(); j++) {
            GitObject child = children.get(j);
            
            String objectType = getObjectType(child); // see method below
            String objectHash = child.getHash(); // hash of child
            String objectPath = child.getPath(); // path of child

            String childName = objectPath.substring(objectPath.lastIndexOf("/") + 1); // gives just the local path, not absolute one
            
            if (j == 0) { // if, then used to account for new lines between appends
                directoryContents.append(objectType + " " + objectHash + " " + childName);
            } else {
                directoryContents.append("\n" + objectType + " " + objectHash + " " + childName);
            }
        }

        //below lines get the hash of this whole file for the name
        String treeContents = directoryContents.toString(); // turning the stringbuilder into a string to then hash
        String hashedFile = hashText(treeContents.getBytes());

        // below lines will actually write into objects folder
        File newFile = new File(objects, hashedFile);
        FileWriter writer = new FileWriter(newFile);
        writer.write(treeContents);
        writer.close();

        return hashedFile;



    }

    private String getObjectType(GitObject unknownObject) {
        if (unknownObject instanceof BLOB) {
            return "blob";
        } else {
            return "tree";
        }
    }


    public ArrayList<GitObject> createWorkingList() throws IOException {
        
        ArrayList<GitObject> newList = new ArrayList<GitObject>();

        BufferedReader br = new BufferedReader(new FileReader(index));
        String contents = br.readLine();

    

        while (contents != null) {
            String[] result = contents.split(" ");
            String hash = result[0];
            String path = result[1];

            newList.add(new BLOB(hash, path)); // see the BLOB.java file where I made blob objects

            contents = br.readLine();
            
        }

        br.close();

        return newList;

    }

}
