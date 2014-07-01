package com.lysterr.Lysterr.postflow;

public enum NewPostType {
    Generic(0), Car(1);

    // the number that represents this type in the Parse cloud
    public int val;

    private NewPostType(int val) {
        this.val = val;
    }
}
