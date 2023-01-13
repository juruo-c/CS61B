package gitlet;

// TODO: any imports you need here

import static gitlet.Utils.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class does at a high level.
 *
 *  @author zhengyang
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The path of all commits folder. */
    public static final File COMMIT_FOLDER = join(Repository.GITLET_DIR, "commits");
    public static String HEAD;
    public static String CUR_BRANCH;

    /** The message of this Commit. */
    private String message;
    /** The parent of this Commit. */
    private String parent;
    /** The date when this commit made. */
    Date date;

    /**
     * Create a commit object with the specified parameters.
     *
     * @param message
     * @param parent
     */
    public Commit(String message, String parent) {
        /* check if it is the initial commit */
        if (parent == null) {
            this.date = new Date(0);
        }
        else {
            this.date = new Date();
        }

        this.message = message;
        this.parent = parent;
    }

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
        File file = join(COMMIT_FOLDER, sha1(this));
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            writeObject(file, this);
        } catch (IOException ignore) {

        }
    }
}
