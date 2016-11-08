package com.aube.sdkdemo.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huyaonan on 16/4/8.
 */
public class RichVideoItem implements Serializable {

    public int quality;
    public String desp;
    public List<RichVideoCopy> copies;

    public String getCopy() {
        if(copies.size() == 0)
            return null;
        return copies.get(0).playurl;
    }

}
