public class GitObject {
    

    private String hash;
    private String path;


    public GitObject(String givenHash, String givenPath) {
        hash = givenHash;
        path = givenPath;
    }


    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }


    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }


    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }


    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    
}
