package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

import static gitlet.Repository.COMMIT_DIR;
import static gitlet.Utils.serialize;
import static gitlet.Utils.sha1;

/** Represents a gitlet commit object.
 *
 *  @author Ruiqi Wang
 */
public class Commit implements Serializable{

    private String timeStamp;
    private String message;
    private String parent;
    private String shaID;
    private HashMap<String,String> Blobs;
    private String shortSha;

    public Commit(String message, String parent, Date date, HashMap<String, String> Blobs){
        this.message = message;
        SimpleDateFormat mydate
                = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        this.timeStamp = mydate.format(date);
        shaID = Utils.sha1((Object) Utils.serialize(this));
        this.parent = parent;
        this.Blobs = Blobs;
        shortSha = IDconversion(shaID);
        File f = new File (COMMIT_DIR, shortSha);
    }

    public Commit(){
        timeStamp = null;
        message = null;
        parent = null;
        shaID = null;
        Blobs = null;
        shortSha = null;
    }

    public void saveCommit(){
        Commit input = this;
        File commit = Utils.join(COMMIT_DIR,input.shortSha);
        try {
            commit.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeObject(commit,input);
    }

    public String getParent(){
        return parent;
    }

    public String getshaID(){
        return shaID;
    }

    public HashMap<String,String> getBlobs(){
        return Blobs;
    }

    public String getMessage() {
        return message;
    }

    public void print(){
        System.out.println("===");
        System.out.println("commit " + this.shaID);
        System.out.println("Date: " + this.timeStamp);
        System.out.println(message);
        System.out.println();
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
