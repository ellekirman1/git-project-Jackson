import java.io.IOException;

public class GitTester {
    public static void main(String[] args) throws IOException {
        GitWrapper.init();
        GitWrapper.add("Hello.txt");
        String hash = GitWrapper.commit("John Doe", "Initial commit");
        System.out.println("Commit: " + hash);
    }
}