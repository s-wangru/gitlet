package gitlet;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Ruiqi Wang 
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        Operations.setUpPersistence();
        switch(firstArg) {
            case "init":
                Operations.init();
                break;
            case "add":
                Operations.add(args);
                break;
            case "commit":
                Operations.commit(args);
                break;
            case "rm":
                Operations.remove(args);
                break;
            case "log":
                Operations.log();
                break;
            case "find":
                Operations.find(args);
                break;
            case "checkout":
                Operations.checkout(args);
                break;
            case "branch":
                Operations.branch(args);
                break;
            case "global-log":
                Operations.globalLog();
                break;
            case "status":
                Operations.status();
                break;
            case "rm-branch":
                Operations.removeBranch(args);
                break;
            case "reset":
                Operations.reset(args);
                break;
            default:
               System.out.println("No command with that name exists.");
        }
        return;
    }

}
