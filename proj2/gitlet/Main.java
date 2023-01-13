package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author zhengyang
 */
public class Main {

    /**
     *  Check if the directory has contained .gitlet subdirectory
     */
    private static void checkInit() {
        if (!Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static void checkArgsLength(int argsLength, int neededLength) {
        if (argsLength != neededLength) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void checkOperands(String[] args, int length) {
        do {
            if (length != 1 && length != 2) {
                break;
            }
            if (length == 2 && !args[1].startsWith("--")) {
                break;
            }
            return;
        } while (false);
        System.out.println("Incorrect operands.");
        System.exit(0);
    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        int argsLength = args.length;
        if (argsLength == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        if (!firstArg.equals("init")) {
            checkInit();
        }
        switch(firstArg) {
            case "init":
                checkArgsLength(argsLength, 1);
                // TODO: handle the `init` command
                Repository.initRepository();
                break;
            case "add":
                checkArgsLength(argsLength, 2);
                // TODO: handle the `add [filename]` command
                Repository.addFile(args[1]);
                break;
            case "commit":
                checkArgsLength(argsLength, 2);
                Repository.makeCommit(args[1]);
                break;
            case "rm":
                checkArgsLength(argsLength, 2);
                Repository.removeFile(args[1]);
                break;
            case "log":
                checkArgsLength(argsLength, 1);
                Repository.log();
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
                Repository.showStatus();
                break;
            case "checkout":
                checkOperands(args, argsLength);
                if (argsLength == 1 && args[0].startsWith("--")) {
                    Repository.checkoutWithFileName(args[0].substring(2));
                }
                else if (argsLength == 1) {
                    Repository.checkoutWithBranchName(args[0]);
                }
                else {
                    Repository.checkoutWithCommitAndFileName(args[0], args[1].substring(2));
                }
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
