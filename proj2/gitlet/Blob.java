package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import static gitlet.Utils.*;

/** Represents a gitlet blob object.
 *  TODO: It's a good idea to give a description here of what else this Class does at a high level.
 *
 *  @author Yang Zheng
 */
public class Blob implements Serializable {
    /** =================================== Members =================================== */
    /** The folder of all blobs. */
    public static final File BLOB_FOLDER = join(Repository.OBJECT_FOLDER, "blobs");
    public static final File STAGE_BLOB_FOLDER = join(Repository.STAGING_AREA, "blobs");
    /** The name of the file. */
    private String fileName;
    /** The content of the file. */
    private byte[] content;

    /** =================================== Constructors =================================== */
    /**
     * Initialize a Blob object
     *
     * @param fileName
     * @param content
     */
    public Blob(String fileName, byte[] content) {
        this.fileName = fileName;
        this.content = content;
    }

    /** =================================== Get Functions =================================== */
    /**
     * Get SHA-1 id of this blob.
     *
     * @return the unique SHA-1 id
     */
    public String getSha1Id() {
        return sha1(serialize(this));
    }
    /**
     * Get the content of the file.
     */
    public byte[] getContent() {
        return content;
    }

    /** =================================== Other Functions =================================== */
    /**
     * Read in and deserializes a blob from a file
     * with SHA-1 id in the given path.
     *
     * @param sha1
     * @return Blob object
     */
    public static Blob fromFile(File path, String sha1) {
        File file = join(path, sha1);
        return readObject(file, Blob.class);
    }
    /**
     * Saves a blob to a file(named by file's SHA-1 id) for future use
     * under the given path
     */
    public void saveBlob(File path) {
        File file = join(path, this.getSha1Id());
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException ignore) {
        }
        writeObject(file, this);
    }
    /**
     * Remove this blob from the file system.
     */
    public void removeBlob() {
        File file = join(BLOB_FOLDER, this.getSha1Id());
        file.delete();
    }
}
