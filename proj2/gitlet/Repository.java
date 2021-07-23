package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Ruiqi Wang
 */
public class Repository implements Serializable{

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** The commit directory. */
    public static final File COMMIT_DIR = Utils.join(GITLET_DIR,"commit");
    /** The blobs directory. */
    public static final File BLOBS_DIR = Utils.join(GITLET_DIR,"blobs");
    /** the staging area. */
    public static final File STAGING_AREA = Utils.join(GITLET_DIR,"staging area");
    /** the branches. */
    public static final File BRANCHES = Utils.join(GITLET_DIR,"branches");

    private Commit head;
    private Branch current;
    private LinkedList<String> removed = new LinkedList<>();

    public void initRepo(){
        if (GITLET_DIR.isDirectory()){
            System.out.println("A gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOBS_DIR.mkdir();
        STAGING_AREA.mkdir();
        BRANCHES.mkdir();
        Commit initCommit = new Commit("initial commit", null, new Date(0), new HashMap<>());
        Branch master = new Branch(initCommit,"master");
        master.saveBranch();
        initCommit.saveCommit();
        head = initCommit;
        current = master;
    }

    public void addRepo(String arg) {

        File f = Utils.join(CWD,arg);
        if (!f.isFile()){
            System.out.println("File does not exist.");
            return;
        }
        if (removed.contains(arg)){
            removed.remove(arg);
            return;
        }
        byte[] original = Utils.readContents(f);
        String blobID = sha1((Object) original);
        if(head.getBlobs().containsValue(blobID) && head.getBlobs().containsKey(arg)){
            return;
        }
        Blobs n = new Blobs(Utils.join(BLOBS_DIR,blobID).getPath(), blobID);
        try {
            n.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeContents(n,original);
        File x = Utils.join(STAGING_AREA,arg);
        Utils.writeContents(x,n.getshaID());
    }

    public void commitRepo(String s) {

        HashMap<String,String> blobs = new HashMap<>();
        if (head.getBlobs() != null){
            blobs = head.getBlobs();
        }
        File[] listOfFiles = STAGING_AREA.listFiles();
        if (listOfFiles.length == 0 && removed.isEmpty()){
            System.out.println("No changes added to the commit.");
            return;
        }
        for (String deleted : removed){
            blobs.remove(deleted);
        }
        for (File f : listOfFiles){
            String name = f.getName();
            String blobSha = Utils.readContentsAsString(f);
            blobs.put(name,blobSha);
            f.delete();
        }
        Commit newHead = new Commit(s,head.getshaID(),new Date(),blobs);
        newHead.saveCommit();
        current.setHead(newHead);
        current.saveBranch();
        head = newHead;
        removed.clear();
    }

    public void removeRepo(String s) {

        File f = Utils.join(STAGING_AREA,s);
        if (f.isFile()){
            f.delete();
        }
        else if (!head.getBlobs().containsKey(s)){
            System.out.println("No reason to remove the file.");
            return;
        }
        if (head.getBlobs().containsKey(s)){
            File w = Utils.join(CWD,s);
            removed.add(s);
            w.delete();
        }
    }

    public void log() {

        Commit c = head;
        while (c.getParent() != null){
            c.print();
            File f = Utils.join(COMMIT_DIR,IDconversion(c.getParent()));
            c = Utils.readObject(f,Commit.class);
        }
        c.print();
    }

    public void find(String message){
        boolean rval = false;

        Commit check = new Commit();
        File[] listOfFiles = COMMIT_DIR.listFiles();
        for (File f : listOfFiles){
            check = Utils.readObject(f,Commit.class);
            if (check.getMessage().equals(message)){
                rval = true;
                System.out.println(check.getshaID());
            }
        }
        if(!rval){
            System.out.println("Found no commit with that message.");
        }
    }

    public void checkoutCurrentFile(String arg) {

        File editing = Utils.join(CWD,arg);
        if (!editing.isFile()){
            System.out.println("File does not exist in that commit");
            return;
        }
        HashMap<String,String> current = head.getBlobs();
        String blobID = current.get(arg);
        File f = Utils.join(BLOBS_DIR,blobID);
        byte[] record = Utils.readContents(f);
        Utils.writeContents(editing,record);
    }

    public void checkoutPrevFile(String commitID, String fileName) {

        File f = Utils.join(COMMIT_DIR,commitID);
        if (!f.isFile()){
            System.out.println("No commit with that id exists.");
            return;
        }
        File editing = Utils.join(CWD,fileName);
        Commit target = Utils.readObject(f,Commit.class);
        HashMap<String,String> current = target.getBlobs();
        if (!current.containsKey(fileName)){
            System.out.println("File does not exist in that commit.");
            return;
        }
        String blobID = current.get(fileName);
        File blob = Utils.join(BLOBS_DIR,blobID);
        byte[] record = Utils.readContents(blob);
        Utils.writeContents(editing,record);
    }

    public void branchRepo(String arg) {

        Branch newBranch = new Branch(head, arg);
        File f = Utils.join(BRANCHES,newBranch.getName());
        if (f.isFile()){
            System.out.println("A branch with that name already exists.");
            return;
        }
        newBranch.saveBranch();
    }

    public void checkoutBranch(String arg) {

        File branch = Utils.join(BRANCHES,arg);
        if (!branch.isFile()){
            System.out.println("No such branch exists.");
            return;
        }
        Branch present = Utils.readObject(branch,Branch.class);
        if (untrackedFiles(head)){
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        if (present.equals(current)){
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Commit target = present.getHead();
        HashMap<String,String> current = target.getBlobs();
        File[] listOfFiles = CWD.listFiles();
        for (File f : listOfFiles){
            if (!current.containsKey(f.getName())){
                f.delete();
                continue;
            }
        }
        for (Map.Entry<String, String> entry : current.entrySet()) {
            String fileName = entry.getKey();
            String blobID = entry.getValue();
            File editing = Utils.join(CWD,fileName);
            if (!editing.isFile()){
                try {
                    editing.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            File blob = Utils.join(BLOBS_DIR,blobID);
            byte[] record = Utils.readContents(blob);
            Utils.writeContents(editing,record);
        }
        File f = Utils.join(BRANCHES,arg);
        this.current = Utils.readObject(f,Branch.class);
        head = this.current.getHead();
        clearStagingArea();
        removed.clear();
    }

    public void clearStagingArea(){

        File[] listOfFiles = STAGING_AREA.listFiles();
        for (File f : listOfFiles){
            f.delete();
        }
    }

    public boolean untrackedFiles(Commit checking){
        File[] listOfFiles = CWD.listFiles();
        HashMap<String,String> blobs = checking.getBlobs();
        for (File f : listOfFiles){
            if (!f.equals(GITLET_DIR) && !f.getName().equals(".repository") && !blobs.containsKey(f.getName())){
                if (!Utils.join(STAGING_AREA,f.getName()).isFile()){
                    return true;
                }
            }
        }
        return false;
    }

    public void global_log() {

        File[] listOfFiles = COMMIT_DIR.listFiles();
        for (File f : listOfFiles){
            Commit printing = Utils.readObject(f,Commit.class);
            printing.print();
        }
    }


    /**
     * Displays what branches currently exist, and marks the current branch with a *.
     *  Also displays what files have been staged for addition or removal.
     */
    public void statusRepo() {
        String[] branchNames = BRANCHES.list();
        Arrays.sort(branchNames);
        String[] stagedNames = STAGING_AREA.list();
        Arrays.sort(stagedNames);
        List<String> sorted = removed.stream().sorted().collect(Collectors.toList());
        System.out.println("=== Branches ===");
        for (String s : branchNames){
            if(current.getName().equals(s)){
                System.out.println("*" + s);
            }else{
                System.out.println(s);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String s : stagedNames){
            System.out.println(s);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String s : sorted){
            System.out.println(s);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public void rmBranch(String arg) {

        File removing = Utils.join(BRANCHES,arg);
        if (!removing.isFile()){
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (arg.equals(current.getName())){
            System.out.println("Cannot remove the current branch.");
            return;
        }
        removing.delete();
    }

    public void resetRepo(String commitID) {

        if (untrackedFiles(head)){
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        File[] workingFiles = CWD.listFiles();
        File f = Utils.join(COMMIT_DIR,commitID);
        if (!f.isFile()){
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit target = Utils.readObject(f,Commit.class);
        for (File file : workingFiles){
            checkoutPrev(commitID,file.getName());
        }
        head = target;
        current.setHead(head);
        current.saveBranch();
        clearStagingArea();
    }

    public void checkoutPrev(String commitID, String fileName) {
        File f = Utils.join(COMMIT_DIR,commitID);
        File editing = Utils.join(CWD,fileName);
        Commit target = Utils.readObject(f,Commit.class);
        HashMap<String,String> current = target.getBlobs();
        if (!current.containsKey(fileName)){
            editing.delete();
            return;
        }
        String blobID = current.get(fileName);
        File blob = Utils.join(BLOBS_DIR,blobID);
        byte[] record = Utils.readContents(blob);
        Utils.writeContents(editing,record);
    }

    public String IDconversion(String commitID){
        char[] arr = commitID.toCharArray();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 6; i++){
            s.append(arr[i]);
        }
        return s.toString();
    }
}



