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
    private static File createBranchFile(String branchName) {
        File branch = join(BRANCH_FOLDER, branchName);
        try {
            branch.createNewFile();
        } catch (IOException ignore) {
        }
        return branch;
    }
    /**
     * Clears the staging area.
     */
    private static void clearStage() {
        Main.removalStage.clear();
        Main.additionStage.clear();
        for (File file : Blob.STAGE_BLOB_FOLDER.listFiles()) {
            file.delete();
        }
    }

    /**
     * Initialize the current repository.
     * Step:
     *     build the file structure for gitlet;
     *     make the initial commit;
     *     initialize all things and add initial commit to commit tree;
     *     add the initial branch to branches set.
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
        createBranchFile(Main.CUR_BRANCH);
        Main.additionStage = new TreeMap<>();
        Main.removalStage = new TreeMap<>();
        Main.commitSet = new HashMap<>();
        Main.commitSet.put(commitId, 1);
        Main.branchSet = new HashMap<>();
        Main.branchSet.put(Main.CUR_BRANCH, 1);
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
    public static void makeCommit(String message, String secondParent) {
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
        Commit curCommit = new Commit(message, Main.HEAD, secondParent);
        /* add blobs in the addition staging area and save each blob to file system;
        *  in the meantime, clear the addition staging file in the file system */
        Map<String, String> trackedFile = curCommit.trackedFiles;
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
        Main.commitSet.put(curCommit.getSha1Id(), 1);
        /* clear the addition staging area and the removal staging area */
        clearStage();
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
            cur = curCommit.getFirstParent();
        }
    }

    /**
     * Log all commit information.
     */
    public static void globalLog() {
        for (String fileName : plainFilenamesIn(Commit.COMMIT_FOLDER)) {
            Commit curCommit = Commit.fromFile(fileName);
            Date date = curCommit.getDate();
            message("===");
            message("commit %s", fileName);
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
        for (String fileName : plainFilenamesIn(Commit.COMMIT_FOLDER)) {
            Commit curCommit = Commit.fromFile(fileName);
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
     * Check out from headCommit to checkCommit.
     * @param headCommit
     * @param checkCommit
     *
     * Check If a working file is untracked in the current branch
     * and would be overwritten by the checkout;
     * Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory according to the situation;
     * Change the head pointer and clear the staging area.
     */
    private static void checkout(Commit headCommit, Commit checkCommit) {
        /* untracked file check */
        for (String fileName : plainFilenamesIn(CWD)) {
            if (!headCommit.containsFile(fileName) && checkCommit.containsFile(fileName)) {
                message("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        /* put all the file that tracked in the head commit and the check commit */
        Map<String, String> trackedFileSet = new HashMap<>();
        trackedFileSet.putAll(headCommit.getTrackedFiles());
        trackedFileSet.putAll(checkCommit.getTrackedFiles());
        /* put or overwrite or remove file from the current working directory according to different situation */
        for (Map.Entry<String, String> file : trackedFileSet.entrySet()) {
            String fileName = file.getKey();
            String blobId = file.getValue();
            // if the file is just tracked by the current commit, remove it from the CWD if it is there.
            if (headCommit.containsFile(fileName) && !checkCommit.containsFile(fileName)) {
                File curFile = join(CWD, fileName);
                if (curFile.exists()) {
                    curFile.delete();
                }
            }
            else { // the file is tracked by both commits or just by the check commit, overwrite the version in the CWD
                writeContents(join(CWD, fileName), Blob.fromFile(Blob.BLOB_FOLDER, blobId).getContent());
            }
        }
        /* change the head pointer */
        Main.HEAD = checkCommit.getSha1Id();
        /* clear the staging area */
        clearStage();
    }
    /**
     * Set the given branch as the current branch.
     * @param branchName
     *
     * Step:
     *     Check if the given branch exists;
     *     Check if the given branch equals to the current branch;
     *     Check out from current commit to the branch head commit;
     *     Change the current branch name and pointer.
     */
    public static void checkoutBranch(String branchName) {
        /* some check */
        if (!Main.branchSet.containsKey(branchName)) {
            message("No such branch exists.");
            System.exit(0);
        }
        if (Main.CUR_BRANCH.equals(branchName)) {
            message("No need to checkout the current branch.");
            System.exit(0);
        }
        Commit headCommit = Commit.fromFile(Main.HEAD);
        Commit checkCommit = Commit.fromFile(readObject(join(BRANCH_FOLDER, branchName), String.class));
        /* check out from headCommit to checkCommit */
        checkout(headCommit, checkCommit);
        /* change current branch pointer */
        Main.CUR_BRANCH_PTR = checkCommit.getSha1Id();
        Main.CUR_BRANCH = branchName;

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
        // TODO use commit id shorter
        /* check if commit exists and file exists */
        if (!Main.commitSet.containsKey(commitId)) {
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
    /**
     * Create a new branch with the given name.
     * Step:
     *     Check if the branch has been existed;
     *     Create a new branch file and add the branch name into branches set;
     *     Point the new branch pointer at the current head commit.
     * @param branchName
     */
    public static void createBranch(String branchName) {
        /* check if the branch exists */
        if (!Main.branchSet.containsKey(branchName)) {
            message("A branch with that name already exists.");
            System.exit(0);
        }
        /* create new branch file and add new branch to branches set */
        File newBranch = createBranchFile(branchName);
        Main.branchSet.put(branchName, 1);
        /* point it at the current head commit */
        writeObject(newBranch, Main.HEAD);
    }
    /**
     * Remove the given branch.
     * @param branchName
     *
     * Step:
     *     Check if the given branch exists in the branches set;
     *     Check if the given branch equals to the current branch;
     *     Remove the given branch from file system and branches set.
     */
    public static void removeBranch(String branchName) {
        /* check if the given branch exists */
        if (!Main.branchSet.containsKey(branchName)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        /* check if the given branch is the current branch */
        if (Main.CUR_BRANCH.equals(branchName)) {
            message("Cannot remove the current branch.");
            System.exit(0);
        }
        /* remove the branch file and remove the branch from branches set */
        Main.branchSet.remove(branchName);
        join(BRANCH_FOLDER, branchName).delete();
    }

    /**
     * Check out to the given commit.
     * @param commitId
     *
     * Step:
     *     Check if the commit exists in the commit tree;
     *     Check out from the head commit to the check commit.
     */
    public static void resetCommit(String commitId) {
        /* check if the commit exists */
        if (!Main.commitSet.containsKey(commitId)) {
            message("No commit with that id exists.");
            System.exit(0);
        }
        Commit headCommit = Commit.fromFile(Main.HEAD);
        Commit checkCommit = Commit.fromFile(commitId);
        /* check out from the headCommit to the checkCommit */
        checkout(headCommit, checkCommit);
    }

    /**
     * Find the split point of the current commit and the given commit.
     * @param current
     * @param given
     * @return the split point commit
     */
    private static Commit findSplitPoint(String current, String given) {
        /* bfs from current commit */
        HashMap<String, Integer> visitedCommit1 = new HashMap<>();
        Queue<String> commitQueue = new LinkedList<>();
        commitQueue.offer(current);
        while (!commitQueue.isEmpty()) {
            String cur = commitQueue.poll();
            visitedCommit1.put(cur, 1);
            Commit curCommit = Commit.fromFile(cur);
            String firstParent = curCommit.getFirstParent();
            String secondParent = curCommit.getSecondParent();
            if (firstParent != null && !visitedCommit1.containsKey(firstParent)) {
                commitQueue.offer(firstParent);
            }
            if (secondParent != null && !visitedCommit1.containsKey(secondParent)) {
                commitQueue.offer(secondParent);
            }
        }
        /* bfs from given commit and if meet a commit that has been in the visitedCommit1
        * that is the split point */
        HashMap<String, Integer> visitedCommit2 = new HashMap<>();
        commitQueue.offer(given);
        while (!commitQueue.isEmpty()) {
            String cur = commitQueue.poll();
            Commit curCommit = Commit.fromFile(cur);
            if (visitedCommit1.containsKey(cur)) {
                return curCommit;
            }
            visitedCommit2.put(cur, 1);
            String firstParent = curCommit.getFirstParent();
            String secondParent = curCommit.getSecondParent();
            if (firstParent != null && !visitedCommit2.containsKey(firstParent)) {
                commitQueue.offer(firstParent);
            }
            if (secondParent != null && !visitedCommit2.containsKey(secondParent)) {
                commitQueue.offer(secondParent);
            }
        }
        return null;
    }
    /**
     * Return the merge type of the given file.
     * @param fileName
     * @param split
     * @param current
     * @param given
     * Types:
     *     1 files modified in given but not modified in current (staged for addition)
     *     2 files modified in current but not modified in given
     *     3 files modified in both in the same way
     *     4 files modified in both in the different way         (conflict staged for addition)
     *     5 files not in split nor in given but in current
     *     6 files not in split nor in current but in given      (staged for addition)
     *     7 files in split unmodified in current but not in given (staged for removal)
     *     8 files in split unmodified in given but not in current
     *     9 else
     * @return the file type
     */
    private static int getFileType(String fileName, Commit split, Commit current, Commit given) {
        /* get the version of file in three commit(if file do not exist, version is null) */
        String verInSplit = split.getFileVersion(fileName);
        String verInCur = current.getFileVersion(fileName);
        String verInGiven = given.getFileVersion(fileName);
        /* get teh different file types when file presented in split point or not */
        if (verInSplit == null) { // file does not presented in the split point
            /* different file types */
            if (verInCur != null && verInGiven != null) {
                return verInCur.equals(verInGiven) ? 3 : 4;
            }
            else if (verInCur != null && verInGiven == null) {
                return 5;
            }
            else if (verInCur == null && verInGiven != null) {
                return 6;
            }

        }
        else {
            boolean modInCur = !verInSplit.equals(verInCur);
            boolean modInGiven = !verInSplit.equals(verInGiven);
            /* different file types */
            if (!modInGiven && verInCur == null) {
                return 8;
            }
            else if (!modInGiven && modInCur) {
                return 2;
            }
            else if(modInGiven && modInCur) {
                if ((verInCur == null && verInGiven == null) ||
                        (verInCur != null && verInCur.equals(verInGiven))) {
                    return 3;
                }
                else {
                    return 4;
                }
            }
            else if (verInGiven == null) {
                return 7;
            }
            else if (modInGiven) {
                return 1;
            }
        }
        return 9;
    }

    /**
     * Check if the file with the given file type would be changed in the file system by the merge.
     * @param fileType
     * @return true if changed, false if not changed
     */
    private static boolean ifFileChanged(int fileType) {
        return fileType == 1 || fileType == 4 || fileType == 6 || fileType == 7;
    }

    /**
     * Merges files from the given branch into the current branch.
     * @param branchName
     * Step:
     *     Check if the staging area is empty;
     *     Check if the given branch exists in the branches set;
     *     Check if the given branch equals to the current branch;
     *     Find the split point of the current branch head and the given branch head;
     *     Check if the split point equals to the given branch head commit;
     *     Check if the split point equals to the head commit;
     *     Get the file set consist of the split, current and given commit;
     */
    public static void merge(String branchName) {
        /* check if the staging area is empty */
        if (!Main.additionStage.isEmpty() || !Main.removalStage.isEmpty()) {
            message("You have uncommitted changes.");
            System.exit(0);
        }
        /* check if the given branch exist */
        if (!Main.branchSet.containsKey(branchName)) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        /* check if the given branch equals to the current branch */
        if (branchName.equals(Main.CUR_BRANCH)) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        /* find the split point */
        String givenBranch = readObject(join(BRANCH_FOLDER, branchName), String.class);
        Commit splitPoint = findSplitPoint(Main.HEAD, givenBranch);
        /* check if split point equals to the given branch head */
        if (splitPoint.getSha1Id().equals(givenBranch)) {
            message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        /* check if split point equals to the current commit */
        if (splitPoint.getSha1Id().equals(Commit.fromFile(Main.HEAD).getSha1Id())) {
            // check out from current commit to the given branch head
            checkout(Commit.fromFile(Main.HEAD), Commit.fromFile(givenBranch));
            message("Current branch fast-forwarded.");
            System.exit(0);
        }
        /* check if there is an untracked file in the current commit would be changed by the merge */
        Commit curCommit = Commit.fromFile(Main.HEAD);
        Commit givenCommit = Commit.fromFile(givenBranch);
        for (String fileName : plainFilenamesIn(CWD)) {
            int fileType = getFileType(fileName, splitPoint, curCommit, givenCommit);
            if (!curCommit.containsFile(fileName) && ifFileChanged(fileType)) {
                message("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        /* get the file set of the split, current and given commits */
        Map<String, String> fileSet = new HashMap<>();
        fileSet.putAll(splitPoint.getTrackedFiles());
        fileSet.putAll(curCommit.getTrackedFiles());
        fileSet.putAll(givenCommit.getTrackedFiles());
        /* iterate the fileSet, get type of each file, make change if the file would be change in the file system */
        boolean meetConflict = false;
        for (Map.Entry<String, String> file : fileSet.entrySet()) {
            String fileName = file.getKey();
            int fileType = getFileType(fileName, splitPoint, curCommit, givenCommit);
            if (ifFileChanged(fileType)) {
                if (fileType == 1 || fileType == 6) { // change the version of file to that in the given commit
                    checkoutCommitFile(givenBranch, fileName);
                    addFile(fileName);
                }
                else if (fileType == 4) { // conflict
                    String verInCur = curCommit.getFileVersion(fileName);
                    String verInGiven = givenCommit.getFileVersion(fileName);
                    writeContents(join(CWD, fileName), "<<<<<<< HEAD",
                            Blob.fromFile(Blob.BLOB_FOLDER, verInCur).getContent(),
                            "=======", Blob.fromFile(Blob.BLOB_FOLDER, verInGiven),
                            ">>>>>>>");
                    addFile(fileName);
                    meetConflict = true;
                }
                else if (fileType == 7) { // remove file
                    removeFile(fileName);
                }
            }
        }
        /* commit the merge */
        makeCommit("Merged " + branchName + " into " + Main.CUR_BRANCH, givenBranch);
        if (meetConflict) {
            message("Encountered a merge conflict.");
        }
    }
}
