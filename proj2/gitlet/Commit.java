package gitlet;

import static gitlet.Utils.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Map;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class does at a high level.
 *
 *  @author Yang Zheng
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** =================================== Members =================================== */
    /** The path of all commits folder. */
    public static final File COMMIT_FOLDER = join(Repository.OBJECT_FOLDER, "commits");
    /** The initial commit. */
    public static Commit initialCommit = new Commit("initial commit", null, null);

    /** The message of this Commit. */
    private String message;
    /** The first parent of this Commit. */
    private String firstParent;
    /** The second parent of this Commit. */
    private String secondParent;
    /** The date when this commit made. */
    Date date;
    /** The tracked files of this commit. */
    Map<String, String> trackedFiles;

    /** =================================== Constructors =================================== */
    /**
     * Create a commit object with the specified parameters.
     *
     * @param message
     * @param firstParent
     */
    public Commit(String message, String firstParent, String secondParent) {
        /* check if it is the initial commit */
        this.message = message;
        this.firstParent = firstParent;
        this.secondParent = secondParent;
        if (firstParent == null) {
            this.date = new Date(0);
            this.trackedFiles = new TreeMap<>();
        }
        else {
            this.date = new Date();
            this.trackedFiles = new TreeMap<>(fromFile(firstParent).getTrackedFiles());
        }
    }

    /** =================================== Get Functions =================================== */
    /**
     * Get SHA-1 id of this commit.
     *
     * @return the unique SHA-1 id
     */
    public String getSha1Id() {
        return sha1(serialize(this));
    }
    /**
     * Get the tracked files of this commit.
     */
    public Map<String, String> getTrackedFiles() {
        return this.trackedFiles;
    }
    /**
     * Get the firstParent commit(SHA-1 id) of this commit.
     */
    public String getFirstParent() {
        return this.firstParent;
    }
    /**
     * Get the secondParent commit(SHA-1 id) of this commit.
     */
    public String getSecondParent() {
        return this.secondParent;
    }
    /**
     * Get the creation date of this commit.
     */
    public Date getDate() {
        return this.date;
    }
    /**
     * Get the message of this commit.
     */
    public String getMessage() {
        return this.message;
    }
    /**
     * Get the content of given tracked file
     * @param fileName
     */
    public byte[] getFileContent(String fileName) {
        String blobId = trackedFiles.get(fileName);
        return Blob.fromFile(Blob.BLOB_FOLDER, blobId).getContent();
    }
    /**
     * Return the blob id of the given file if the file is tracked by this commit,
     * else return null.
     * @param fileName
     * @return blob id or null
     */
    public String getFileVersion(String fileName) {
        if (!trackedFiles.containsKey(fileName)) {
            return null;
        }
        return trackedFiles.get(fileName);
    }

    /** =================================== Other Functions =================================== */
    /**
     * Read in and deserializes a commit from a file with sha1 id in COMMIT_FOLDER.
     *
     * @param sha1
     * @return Commit object
     */
    public static Commit fromFile(String sha1) {
        File file = join(COMMIT_FOLDER, sha1);
        return readObject(file, Commit.class);
    }
    /**
     * Saves a commit to a file(named by file's sha1 id) for future use.
     */
    public void saveCommit() {
        File file = join(COMMIT_FOLDER, this.getSha1Id());
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException ignore) {
        }
        writeObject(file, this);
    }
    /**
     * Check if the commit tracks the given file with specified version
     *
     * @param fileName
     * @param blobSh1Id
     * @return true if tracked, false if untracked.
     */
    public boolean containsFileVersion(String fileName, String blobSh1Id) {
        return trackedFiles.containsKey(fileName) &&
                trackedFiles.get(fileName).equals(blobSh1Id);
    }
    /**
     * Check if the commit tracks the given file with specified version
     *
     * @param fileName
     * @return true if tracked, false if untracked.
     */
    public boolean containsFile(String fileName) {
        return trackedFiles.containsKey(fileName);
    }
}
