package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.BRANCHES;

public class Branch implements Serializable {

    private Commit HEAD;
    private String name;

    public Branch(Commit HEAD, String name){
        this.HEAD = HEAD;
        this.name = name;
    }

    public void setHead(Commit HEAD) {
        this.HEAD = HEAD;
    }

    public String getName(){
        return name;
    }

    public Commit getHead(){
        return HEAD;
    }

    public void saveBranch(){
        Branch input = this;
        File f = Utils.join(BRANCHES,input.name);
        Utils.writeObject(f,input);
    }

    public boolean equals(Branch other){
        return (this.name.equals(other.name));
    }


}
