package com.lysterr.Lysterr.fragments.postflow;

import java.io.Serializable;

/**
 * A struct that contains data for the new post being built
 *
 * Serializable so that it can be easily passed along the new post flow
 * between fragments
 */
public class NewPostData implements Serializable {
    public String name;
    public double price;
    public String customDescription;
    public NewPostCondition condition;
    public String pathToBitmap;
}