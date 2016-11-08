package com.aube.sdkdemo.model;

import com.huyn.baseframework.model.BaseModel;

import java.util.HashMap;

/**
 * Created by huyaonan on 16/4/8.
 */
public class RichVideoResponse extends BaseModel {

    public RichVideoModel response;

    public int size() {
        if(response == null)
            return 0;
        return response.size();
    }

    public String[] getResolution() {
        if(response == null)
            return null;
        return response.getResolution();
    }

    public String getOneUrl(int quanlity) {
        if(response == null)
            return null;
        return response.getOneUrl(quanlity);
    }

    public HashMap<Integer, String> getResolutionMap() {
        if(size() > 0) {
            return response.getResolutionMap();
        }
        return null;
    }

}
