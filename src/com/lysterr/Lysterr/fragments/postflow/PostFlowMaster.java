package com.lysterr.Lysterr.fragments.postflow;

import android.support.v4.app.Fragment;
import com.lysterr.Lysterr.fragments.postflow.NewPostData;

public interface PostFlowMaster {

    public void stepCompleted(Fragment fragment, NewPostData data);
}
