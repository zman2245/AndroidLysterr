package com.lysterr.Lysterr.postflow;

import android.support.v4.app.Fragment;
import com.lysterr.Lysterr.postflow.NewPostData;

public interface PostFlowMaster {

    public void stepCompleted(Fragment fragment, NewPostData data);
}