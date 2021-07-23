package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Repository.CWD;

public abstract class Operations {

    static final File repo = Utils.join(CWD,".repository");


    private static Repository r;

    public static void init(){
        r = new Repository();
        r.initRepo();
        Utils.writeObject(repo,r);
    }

    public static void add(String[] args){
        try{
            r = Utils.readObject(repo,Repository.class);
        }catch (IllegalArgumentException e){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        int i = 1;
        while (i < args.length){
            r.addRepo(args[i]);
            i++;
        }
        Utils.writeObject(repo,r);
    }

    public static void commit(String[] message){
        try{
            r = Utils.readObject(repo,Repository.class);
        }catch (IllegalArgumentException e){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        if (message.length == 1 || message[1].equals("")){
            System.out.println("Please enter a commit message.");
            return;
        }
        r.commitRepo(message[1]);
        Utils.writeObject(repo,r);
    }

    public static void remove(String[] file){
        try{
            r = Utils.readObject(repo,Repository.class);
        }catch (IllegalArgumentException e){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        r.removeRepo(file[1]);
        Utils.writeObject(repo,r);
    }

    public static void log(){
        try{
            r = Utils.readObject(repo,Repository.class);
        }catch (IllegalArgumentException e){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        r.log();
        Utils.writeObject(repo,r);
    }

    public static void find(String[] args){
        try{
            r = Utils.readObject(repo,Repository.class);
        }catch (IllegalArgumentException e){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        r.find(args[1]);
        Utils.writeObject(repo,r);
    }

    public static void checkout(String[] args){
        try{
            r = Utils.readObject(repo,Repository.class);
        }catch (IllegalArgumentException e){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        if (args[1].equals("--")){
            r.checkoutCurrentFile(args[2]);
        }
        else if (args.length > 3){
            if (!args[2].equals("--")){
                System.out.println("Incorrect operands.");
                return;
            }
            r.checkoutPrevFile(r.IDconversion(args[1]),args[3]);
        }
        else{
            r.checkoutBranch(args[1]);
        }
        Utils.writeObject(repo,r);
    }

    public static void setUpPersistence() {
        try {
            repo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void branch(String[] args) {
        try{
            r = Utils.readObject(repo,Repository.class);
        }catch (IllegalArgumentException e){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        r.branchRepo(args[1]);
        Utils.writeObject(repo,r);
    }

    public static void globalLog() {
        try{
            r = Utils.readObject(repo,Repository.class);
        }catch (IllegalArgumentException e){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        r.global_log();
        Utils.writeObject(repo,r);
    }

    public static void status() {
        try{
            r = Utils.readObject(repo,Repository.class);
        }catch (IllegalArgumentException e){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        r.statusRepo();
        Utils.writeObject(repo,r);
    }

    public static void removeBranch(String[] args) {
        try{
            r = Utils.readObject(repo,Repository.class);
        }catch (IllegalArgumentException e){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        r.rmBranch(args[1]);
        Utils.writeObject(repo,r);
    }

    public static void reset(String[] args) {
        try{
            r = Utils.readObject(repo,Repository.class);
        }catch (IllegalArgumentException e){
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        r.resetRepo(r.IDconversion(args[1]));
        Utils.writeObject(repo,r);
    }
}
