import java.io.File;
import java.io.IOException;

public class GitWrapper {
        
    private static Git git;


    public static void init() {
        git = new Git();
    }

    /**
     * Stages a file for the next commit.
     * Throws IOException if the path does not exist or is a directory.
     * Does nothing if the file's content is already staged for that path.
     *
     * @param filePath The path to the file to stage.
     */
    public static void add(String filePath) throws IOException {

        if (git == null) {
    init();
}
        File file = new File(filePath);

        if (!file.exists() || file.isDirectory()) {
            throw new IOException("Invalid file: " + filePath);
        }

        git.makeBlob(filePath);
        git.addFileEntry(filePath);
    }

    /**
     * Creates a commit capturing the current state of the index.
     * Builds trees, writes the commit file, updates HEAD.
     *
     * Commit format:
     *   tree: <tree_sha>
     *   parent: <parent_sha>   (omit line for initial commit)
     *   author: <author>
     *   date: <date>
     *   message: <message>
     *
     * @param author  The author name.
     * @param message The commit message.
     * @return The SHA-1 hash of the new commit.
     * @throws IOException 
     */
    public static String commit(String author, String message) {
        if (git == null) {
            init();
        }

        try {
            return git.commit(author, message);
    }    catch (IOException e) {
            throw new RuntimeException("Commit failed", e);
    }
}
    //deleted extra credit
}