package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class does at a high level.
 *
 *
 *
 *  @author Yang Zheng
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
    /** The staging area(consist of removal and addition). */
    public static final File STAGING_AREA = join(GITLET_DIR, "staging");
    /** The object folder(consist of commits and blobs). */
    public static final File OBJECT_FOLDER = join(GITLET_DIR, "objects");
    /** The branch folder. */
    public static final File BRANCH_FOLDER = join(GITLET_DIR, "branches");
    /** The head pointer file. */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /** The current branch(name) file. */
    public static final File CURRENT_BRANCH = join(GITLET_DIR, "CURRENT_BRANCH");

    /**
     * Create a new branch with the given name.
     * @param branchName
     * @return branch file
     */
    private static File createBranch(String branchName) {
        File branch = join(BRANCH_FOLDER, branchName);
        try {
            branch.createNewFile();
        } catch (IOException ignore) {
        }
        return branch;
    }

    /**
     * Initialize the current repository.
     * Step:
     *     build the file structure for gitlet;
     *     make the initial commit;
     *     initialize all things and add initial commit to commit tree;
     */
    public static void initRepository() {
        /* build the file structure */
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        }
        else {
            message("A Gitlet version-control system already " +
                    "exists in the current directory.");
            System.exit(0);
        }
        OBJECT_FOLDER.mkdir();
        Commit.COMMIT_FOLDER.mkdir();
        Blob.BLOB_FOLDER.mkdir();
        BRANCH_FOLDER.mkdir();
        STAGING_AREA.mkdir();
        Blob.STAGE_BLOB_FOLDER.mkdir();
        try {
            HEAD.createNewFile();
            CURRENT_BRANCH.createNewFile();
            Main.STAGING_AREA_ADD.createNewFile();
            Main.STAGING_AREA_REMOVE.createNewFile();
        } catch (IOException ignore) {
        }
        /* make the initial commit and save to file named by its unique sha1 id */
        Commit.initialCommit.saveCommit();
        /* initial the head pointer, master pointer, initial branch and staging area;
           add the initial commit to the commit tree */
        String commitId = Commit.initialCommit.getSha1Id();
        Main.CUR_BRANCH_PTR = Main.HEAD = commitId;
        Main.CUR_BRANCH = "master";
        Main.additionStage = new TreeMap<>();
        Main.removalStage = new TreeMap<>();
        Main.commitTree = new HashMap<>();
        Main.commitTree.put(commitId, 1);
    }

    /**
     * Add file to the staging area for addition
     * @param fileName
     * Step:
     *     check if the current commit(HEAD) has the same version of
     *     the given file with that in the current working directory;
     *     if same and the staging area contains the given file: remove it;
     *     if different: just update staging area and save blob
     */
    public static void addFile(String fileName) {
        // TODO remove useless staging file blob
        /* check if the current working directory contains the given file */
        File f = new File(fileName);
        if (!f.exists()) {
            message("File does not exist.");
            System.exit(0);
        }
        /* check the version and make different action */
        Blob curFile = new Blob(fileName, readContents(new File(fileName)));
        Commit curCommit = Commit.fromFile(Main.HEAD);
        if (curCommit.containsFileVersion(fileName, curFile.getSha1Id())) { // same version
            /* check if staging area contain the given file */
            if (Main.additionStage.containsKey(fileName)) {
                Main.additionStage.remove(fileName);
            }
        }
        else { // different version
            /* put the current version of given file to the staging area
            *  for addition and remove the file from staging area for removal */
            Main.additionStage.put(fileName, curFile.getSha1Id());
            Main.removalStage.remove(fileName);
            /* save the blob */
            curFile.saveBlob(Blob.STAGE_BLOB_FOLDER);
        }
    }

    /**
     * Make a new commit.
     * Step:
     *     Check if there is any change added, if the message is empty;
     *     Create a new Commit inheriting the head commit;
     *     Add blobs in the addition staging area
     *     into the new commit tracked files set;
     *     Remove blobs in the removal staging area
     *     from the new commit tracked files set;
     *     Save commit to the file system and add commit to commit tree;
     *     Clear the staging area;
     *     Change the head pointer and current branch pointer.
     */
    public static void makeCommit(String message) {
        /* check if change and message length */
        if (Main.additionStage.size() == 0 && Main.removalStage.size() == 0) {
            message("No changes added to the commit.");
            System.exit(0);
        }
        if (message.length() == 0) {
            message("Please enter a commit message.");
            System.exit(0);
        }
        /* create a new Commit */
        Commit curCommit = new Commit(message, Main.HEAD);
        /* add blobs in the addition staging area and save each blob to file system;
        *  in the meantime, clear the addition staging file in the file system */
        TreeMap<String, String> trackedFile = curCommit.trackedFiles;
        for (Map.Entry<String, String> blob : Main.additionStage.entrySet()) {
            trackedFile.put(blob.getKey(), blob.getValue());
            Blob curBlob = Blob.fromFile(Blob.STAGE_BLOB_FOLDER, blob.getValue());
            curBlob.saveBlob(Blob.BLOB_FOLDER);
        }
        /* remove blobs in the removal staging area */
        for (Map.Entry<String, Integer> file : Main.removalStage.entrySet()) {
            trackedFile.remove(file.getKey());
        }
        /* save the new commit to file system and add commit to commit tree */
        curCommit.saveCommit();
        Main.commitTree.put(curCommit.getSha1Id(), 1);
        /* clear the addition staging area and the removal staging area */
        Main.removalStage.clear();
        Main.additionStage.clear();
        for (File file : Blob.STAGE_BLOB_FOLDER.listFiles()) {
            file.delete();
        }
        /* change head pointer and current branch pointer */
        Main.HEAD = Main.CUR_BRANCH_PTR = curCommit.getSha1Id();
    }

    /**
     * Remove file from the staging area.
     * @param fileName
     * Step:
     *     check if the given file is staged or tracked by the head Commit;
     *     if staged, removes file from addition staging area;
     *     if tracked, stages file for removal and remove it from working directory
     *     if it exists.
     */
    public static void removeFile(String fileName) {
        Commit headCommit = Commit.fromFile(Main.HEAD);
        /* check the given file status */
        if (!Main.additionStage.containsKey(fileName) &&
            !headCommit.containsFile(fileName)) {
            message("No reason to remove the file.");
            System.exit(0);
        }
        /* if staged */
        if (Main.additionStage.containsKey(fileName)) {
            /* remove file from addition staging */
            Main.additionStage.remove(fileName);
        }
        /* if tracked by the head commit */
        if (headCommit.containsFile(fileName)) {
            /* stages file for removal */
            Main.removalStage.put(fileName, 1);
            /* remove the file from working directory if it exists */
            File file = join(Repository.CWD, fileName);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * Log the commit information from current commit.
     * Step:
     *     from head pointer, move to the initial commit(when parent is null)
     *     and print message
     */
    public static void log() {
        String cur = Main.HEAD;
        while (cur != null) {
            Commit curCommit = Commit.fromFile(cur);
            Date date = curCommit.getDate();
            message("===");
            message("commit %s", cur);
            message("Date: %ta %tb %te %tT %tY %tz",
                    date, date, date, date, date, date);
            message("%s\n", curCommit.getMessage());
            cur = curCommit.getParent();
        }
    }

    /**
     * Log all commit information.
     */
    public static void globalLog() {
        for (File file : Commit.COMMIT_FOLDER.listFiles()) {
            Commit curCommit = Commit.fromFile(file.getName());
            Date date = curCommit.getDate();
            message("===");
            message("commit %s", file.getName());
            message("Date: %ta %tb %te %tT %tY %tz",
                    date, date, date, date, date, date);
            message("%s\n", curCommit.getMessage());
        }
    }

    /**
     * Find the specific commit that have the given commit message.
     * @param commitMessage
     */
    public static void findCommit(String commitMessage) {
        boolean flag = false;
        for (File file : Commit.COMMIT_FOLDER.listFiles()) {
            Commit curCommit = Commit.fromFile(file.getName());
            if (curCommit.getMessage().equals(commitMessage)) {
                flag = true;
                message("%s", curCommit.getSha1Id());
            }
        }
        /* if there is not any commit contains the given commit message */
        if (!flag) {
            message("Found no commit with that message.");
            System.exit(0);
        }
    }

    /**
     * Show different sections' status.
     * Step:
     *     List all branches and add an asterisk before the current branch;
     *     List all Staged file in the addition staging area;
     *     List all Removed file in the removal staging area;
     *     // TODO extra task
     */
    public static void showStatus() {
        message("=== Branches ===");
        TreeMap<String, Integer> branches = new TreeMap<>();
        for (String branch : plainFilenamesIn(BRANCH_FOLDER)) {
            branches.put(branch, 1);
        }
        for (Map.Entry<String, Integer> branch : branches.entrySet()) {
            if (branch.getKey().equals(Main.CUR_BRANCH)) {
                message("*%s", branch.getKey());
            }
            else {
                message("%s", branch.getKey());
            }
        }
        message("");
        message("=== Staged Files ===");
        for (Map.Entry<String, String> stagedFile : Main.additionStage.entrySet()) {
            message("%s", stagedFile.getKey());
        }
        message("");
        message("=== Removed Files ===");
        for (Map.Entry<String, Integer> removedFile : Main.removalStage.entrySet()) {
            message("%s", removedFile.getKey());
        }
        message("");
        message("=== Modifications Not Staged For Commit ===");
        // TODO
        message("");
        message("=== Untracked Files ===");
        // TODO
        message("");
    }
    /**
     * Takes the version of the file as it exists in the head commit and
     * puts it in the working directory, overwriting the version of the
     * file that’s already there if there is one.
     * @param fileName
     *
     * Step:
     *     Check if the file exists in the previous commit;
     *     Overwrite the version of the file in the current working directory.
     */
    public static void checkoutFile(String fileName) {
        /* check if file exists in head commit */
        Commit headCommit = Commit.fromFile(Main.HEAD);
        if (!headCommit.containsFile(fileName)) {
            message("File does not exist in that commit.");
            System.exit(0);
        }
        /* overwrite the file */
        writeContents(join(CWD, fileName), headCommit.getFileContent(fileName));
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
     *
     * Step:
     *     Check if the commit exists in the commit tree;
     *     Check if the file was tracked in the given commit;
     *     Overwrite the file in the current working directory.
     */
    public static void checkoutCommitFile(String commitId, String fileName) {
        /* check if commit exists and file exists */
        if (!Main.commitTree.containsKey(commitId)) {
            message("No commit with that id exists.");
            System.exit(0);
        }
        Commit givenCommit = Commit.fromFile(commitId);
        if (!givenCommit.containsFile(fileName)) {
            message("File does not exist in that commit.");
            System.exit(0);
        }
        /* overwrite the file */
        writeContents(join(CWD, fileName), givenCommit.getFileContent(fileName));
    }

}
