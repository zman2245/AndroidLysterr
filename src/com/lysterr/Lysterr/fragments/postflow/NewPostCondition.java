package com.lysterr.Lysterr.fragments.postflow;

public enum NewPostCondition {
    Excellent(0), Good(1), Average(2);

    // the number that represents this condition in the Parse cloud
    public int val;

    private NewPostCondition(int val) {
        this.val = val;
    }
}
