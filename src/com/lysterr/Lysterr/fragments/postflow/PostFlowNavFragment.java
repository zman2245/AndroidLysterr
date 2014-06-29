package com.lysterr.Lysterr.fragments.postflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lysterr.Lysterr.R;

import java.io.Serializable;

/**
 * Container of fragments that implement the "post an item" flow
 */
public class PostFlowNavFragment extends Fragment implements PostFlowMaster {
    public static final String ARG_DATA = "data";

    private NewPostData mNewPostData;
    private int mCurrentStep = 0;

    private View mFragmentContainer;

    public static PostFlowNavFragment newInstance() {
        PostFlowNavFragment f = new PostFlowNavFragment();

        Bundle args = new Bundle();
        NewPostData data = new NewPostData();
        args.putSerializable("data", data);
        f.setArguments(args);

        return f;
    }

    public static void putDataInFrag(Fragment f, Serializable data) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA, data);
        f.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNewPostData = new NewPostData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_nav, container, false);

        mFragmentContainer = view.findViewById(R.id.fragment_container);
        gotoFirstStep();

        return view;
    }

    @Override
    public void stepCompleted(Fragment fragment, NewPostData data) {
        Fragment f;

        // could put this behind a factory or some other navigation controller object
        if (fragment instanceof PostFlowImageFragment) {
            f = PostFlowDetailsFragment.newInstance(mNewPostData);
            gotoStep(f);
        } else if (fragment instanceof PostFlowDetailsFragment) {
            // TODO
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // workaround for issue where child fragments don't get their onActivityResult invoked
        Fragment fragment = (Fragment) getChildFragmentManager().findFragmentByTag("step");
        if(fragment != null){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void gotoFirstStep() {
        PostFlowImageFragment f = PostFlowImageFragment.newInstance(mNewPostData);

        gotoStep(f);
    }

    private void gotoStep(final Fragment f) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, f, "step")
                        .addToBackStack("step")
                        .commit();
            }
        });
    }

    public static void notifyStepComplete(Fragment f, NewPostData data) {
        PostFlowMaster master;
        Fragment parentFrag = f.getParentFragment();
        if (parentFrag != null) {
            master = (PostFlowMaster)parentFrag;
        } else {
            Activity activity = f.getActivity();
            master = (PostFlowMaster)activity;
        }

        master.stepCompleted(f, data);
    }
}
