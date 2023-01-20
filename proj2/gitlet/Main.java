package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Yang Zheng
 */
public class Main {
    /** =================================== Members =================================== */
    /** Staging area for addition and for removal. */
    public static File STAGING_AREA_ADD = join(Repository.STAGING_AREA, "ADDITION");
    public static File STAGING_AREA_REMOVE = join(Repository.STAGING_AREA, "REMOVAL");
    /** Commit Tree for all commits. */
    public static File COMMIT_TREE = join(Repository.GITLET_DIR, "TREE");
    /** The set contains all branches' name. */
    public static File BRANCH_SET = join(Repository.GITLET_DIR, "BRANCH_SET");
    /** The head pointer/current branch name/current branch pointer
     *  that the program maintains. */
    public static String HEAD;
    public static String CUR_BRANCH;
    public static String CUR_BRANCH_PTR;
    /** The staging area that the program maintains for addition. */
    public static TreeMap<String, String> additionStage;
    /** The staging area that the program maintains for removal. */
    public static TreeMap<String, Integer> removalStage;
    /** The commit tree. */
    public static HashMap<String, Integer> commitSet;
    /** The branch set. */
    public static HashMap<String, Integer> branchSet;

    /** =================================== Functions =================================== */
    /**
     *  Check if the directory has contained .gitlet subdirectory
     */
    private static void checkInit() {
        if (!Repository.GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
    /**
     * Check if the command has the correct number of arguments
     * @param argsLength
     * @param neededLength
     */
    private static void checkArgsLength(int argsLength, int neededLength) {
        if (argsLength != neededLength) {
            message("Incorrect operands.");
            System.exit(0);
        }
    }
    /**
     * Check the legitimacy of command "checkout" arguments
     * @param args
     * @param length
     */
    private static void checkOperands(String[] args, int length) {
        do {
            if (length < 2 || length > 4) {
                break;
            }
            if (length == 3 && !args[1].equals("--")) {
                break;
            }
            if (length == 4 && !args[2].equals("--")) {
                break;
            }
            return;
        } while (false);
        message("Incorrect operands.");
        System.exit(0);
    }
    /**
     * Get HEAD pointer from .gitlet/HEAD.
     */
    private static void getHead() {
        HEAD = readObject(Repository.HEAD, String.class);
    }
    /**
     * Get current branch name from .gitlet/CURRENT_BRANCH,
     * and get the current branch pointer.
     */
    private static void getCurrentBranch() {
        CUR_BRANCH = readObject(Repository.CURRENT_BRANCH, String.class);
        CUR_BRANCH_PTR = readObject(join(Repository.BRANCH_FOLDER, CUR_BRANCH), String.class);
    }
    /**
     * Get staging area from .gitlet/staging
     */
    private static void getStagingArea() {
        additionStage = readObject(STAGING_AREA_ADD, TreeMap.class);
        removalStage = readObject(STAGING_AREA_REMOVE, TreeMap.class);
    }
    /**
     * Get commit tree from .gitlet/TREE
     */
    private static void getcommitSet() {
        commitSet = readObject(COMMIT_TREE, HashMap.class);
    }
    /**
     * Get branches set from .gitlet/BRANCH_SET
     */
    private static void getBranchSet() {
        branchSet = readObject(BRANCH_SET, HashMap.class);
    }
    /**
     * Save HEAD pointer to .gitlet/HEAD.
     */
    public static void saveHead() {
        writeObject(Repository.HEAD, HEAD);
    }
    /**
     * Save current branch name to .gitlet/CURRENT_BRANCH.
     */
    public static void saveCurrentBranch() {
        writeObject(Repository.CURRENT_BRANCH, CUR_BRANCH);
    }
    /**
     * Save current branch pointer to the corresponding branch file.
     */
    public static void saveCurrentBranchPtr() {
        writeObject(join(Repository.BRANCH_FOLDER, CUR_BRANCH), CUR_BRANCH_PTR);
    }
    /**
     * Save staging area to .gitlet/staging/ADDITION and .gitlet/staging/REMOVAL.
     */
    public static void saveStagingArea() {
        writeObject(STAGING_AREA_ADD, additionStage);
        writeObject(STAGING_AREA_REMOVE, removalStage);
    }
    /**
     * Save commit Tree to .gitlet/TREE
     */
    public static void savecommitSet() {
        writeObject(COMMIT_TREE, commitSet);
    }
    /**
     * Save branches set to .gitlet/BRANCH_SET
     */
    public static void saveBranchSet() {
        writeObject(BRANCH_SET, branchSet);
    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        int argsLength = args.length;
        if (argsLength == 0) {
            message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        if (!firstArg.equals("init")) {
            checkInit();
        }
        switch(firstArg) {
            case "init":
                checkArgsLength(argsLength, 1);
                Repository.initRepository();
                /* save all things that have been initialized */
                saveHead();
                saveCurrentBranch();
                saveCurrentBranchPtr();
                saveStagingArea();
                savecommitSet();
                saveBranchSet();
                break;
            case "add":
                checkArgsLength(argsLength, 2);
                /* get staging area and head pointer from file */
                getStagingArea();
                getHead();
                /* call addFile function */
                Repository.addFile(args[1]);
                /* add command only change staging area */
                saveStagingArea();
                break;
            case "commit":
                checkArgsLength(argsLength, 2);
                /* get all things */
                getHead();
                getStagingArea();
                getCurrentBranch();
                getcommitSet();
                /* call makeCommit function */
                Repository.makeCommit(args[1], null);
                /* commit command change all thing except current branch */
                saveHead();
                saveStagingArea();
                saveCurrentBranchPtr();
                savecommitSet();
                break;
            case "rm":
                checkArgsLength(argsLength, 2);
                /* get staging area and head pointer from file */
                getStagingArea();
                getHead();
                /* call removeFile function */
                Repository.removeFile(args[1]);
                /* rm command only change staging area */
                saveStagingArea();
                break;
            case "log":
                checkArgsLength(argsLength, 1);
                /* get head pointer from file */
                getHead();
                /* call log function */
                Repository.log();
                /* change nothing */
                break;
            case "global-log":
                checkArgsLength(argsLength, 1);
                Repository.globalLog();
                break;
            case "find":
                checkArgsLength(argsLength, 2);
                Repository.findCommit(args[1]);
                break;
            case "status":
                checkArgsLength(argsLength, 1);
                /* get head pointer, current branch name and staging area */
                getHead();
                getCurrentBranch();
                getStagingArea();
                /* call showStatus function */
                Repository.showStatus();
                /* change nothing */
                break;
            case "checkout":
                checkOperands(args, argsLength);
                if (argsLength == 2) {
                    /* get head pointer, current branch,
                    staging area and branches set */
                    getHead();
                    getCurrentBranch();
                    getStagingArea();
                    getBranchSet();
                    /* call checkoutBranch function */
                    Repository.checkoutBranch(args[1]);
                    /* change head pointer, current branch, staging area */
                    saveHead();
                    saveCurrentBranch();
                    saveStagingArea();
                }
                else if (argsLength == 3) {
                    /* get head pointer */
                    getHead();
                    /* call checkoutFile function */
                    Repository.checkoutFile(args[2]);
                    /* change nothing */
                }
                else {
                    /* get commit Tree */
                    getcommitSet();
                    /* call checkoutCommitFile function */
                    Repository.checkoutCommitFile(args[1], args[3]);
                    /* change nothing */
                }
                break;
            case "branch":
                checkArgsLength(argsLength, 2);
                /* get branches set and head pointer */
                getBranchSet();
                getHead();
                /* call createBranch function */
                Repository.createBranch(args[1]);
                /* save branches set */
                saveBranchSet();
                break;
            case "rm-branch":
                checkArgsLength(argsLength, 2);
                /* get the branch set and the current branch name */
                getBranchSet();
                getCurrentBranch();
                /* call removeBranch function */
                Repository.removeBranch(args[1]);
                /* save branches set */
                saveBranchSet();
                break;
            case "reset":
                checkArgsLength(argsLength, 2);
                /* get head pointer, current branch pointer, staging area and commit tree */
                getHead();
                getCurrentBranch();
                getStagingArea();
                getcommitSet();
                /* call checkoutBranch function */
                Repository.resetCommit(args[1]);
                /* change head pointer, current branch pointer, staging area */
                saveHead();
                saveCurrentBranchPtr();
                saveStagingArea();
                break;
            case "merge":
                checkArgsLength(argsLength, 2);
                /* get all things */
                getHead();
                getCurrentBranch();
                getStagingArea();
                getcommitSet();
                getBranchSet();
                /* call merge function */
                Repository.merge(args[1]);
                /* change all things that get except the branch set */
                saveHead();
                saveCurrentBranch();
                saveStagingArea();
                savecommitSet();
                break;
            default:
                message("No command with that name exists.");
                System.exit(0);
        }
    }
}
