package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Repository.STAGING_AREA;

public class Blobs extends File implements Serializable {

    private String shaID;

    public Blobs(String pathname, String ID){
        super(pathname);
        shaID = ID;
    }

    public String getshaID(){
        return shaID;
    }

}
