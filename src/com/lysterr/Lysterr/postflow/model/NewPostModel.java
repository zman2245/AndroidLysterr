package com.lysterr.Lysterr.postflow.model;

import com.lysterr.Lysterr.postflow.NewPostCondition;
import com.parse.ParseGeoPoint;

import java.io.Serializable;

public interface NewPostModel extends Serializable {
    public String getDescriptionForCondition(NewPostCondition cond);
    public void saveToServer(ParseGeoPoint location);
    public String getImagePath();
    public String getSelectedDescription();
    public void setImagePath(String path);
    public void setSelectedDescription(String desc);
    public void setConditino(NewPostCondition cond);
}
