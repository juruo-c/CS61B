package gitlet;

import java.io.File;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class does at a high level.
 *
 *
 *
 *  @author zhengyang
 */

public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */

    /**
     * Initialize the current repository
     */
    public static void initRepository() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        }
        else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        // TODO
        Commit.COMMIT_FOLDER.mkdir();
        Commit initCommit = new Commit("initial commit", null);
    }

    /**
     * Add file to the staging area
     */
    public static void addFile(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        // TODO
    }

    /**
     * Make a new commit
     */
    public static void makeCommit(String message) {

    }

    /**
     * Remove file from the staging area
     * @param fileName
     */
    public static void removeFile(String fileName) {

    }

    /**
     * Log the commit information from current commit
     */
    public static void log() {

    }

    /**
     * Log all commit information
     */
    public static void globalLog() {

    }

    /**
     * Find the specific commit that have the given commit message
     * @param commitMessage
     */
    public static void findCommit(String commitMessage) {

    }

    /**
     * Show different catagory status
     */
    public static void showStatus() {

    }

    /**
     * Takes the version of the file as it exists in the head commit and
     * puts it in the working directory, overwriting the version of the
     * file that’s already there if there is one.
     * @param fileName
     */
    public static void checkoutFile(String fileName) {

    }

    /**
     *
     * @param branchName
     */
    public static void checkoutBranch(String branchName) {

    }

    /**
     * Takes the version of the file as it exists in the commit with the
     * given id, and puts it in the working directory, overwriting the version
     * of the file that’s already there if there is one.
     * @param commitId
     * @param fileName
     */
    public static void checkoutCommitFile(String commitId, String fileName) {

    }

}
