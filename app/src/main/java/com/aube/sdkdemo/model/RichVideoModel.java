package com.aube.sdkdemo.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by huyaonan on 16/4/8.
 */
public class RichVideoModel implements Serializable {

    public List<RichVideoItem> qualities;

    public int size() {
        if(qualities == null)
            return 0;
        return qualities.size();
    }

    public String[] getResolution() {
        if(size() > 0) {
            String[] resolutions = new String[size()];
            int i=0;
            for(RichVideoItem item : qualities) {
                resolutions[i] = item.desp;
                i++;
            }
            return resolutions;
        }
        return null;
    }

    public HashMap<Integer, String> getResolutionMap() {
        HashMap<Integer, String> definitions = new HashMap<>();
        if(size() > 0) {
            for(RichVideoItem item : qualities) {
                definitions.put(item.quality, item.desp);
            }
        }
        return definitions;
    }

    public String getOneUrl(int quanlity) {
        if(size() > 0) {
            for(RichVideoItem s : qualities)
                if(s.quality == quanlity)
                    return s.getCopy();
            return null;
        }
        return null;
    }

}
