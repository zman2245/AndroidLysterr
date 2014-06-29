package com.lysterr.Lysterr.fragments.postflow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lysterr.Lysterr.R;

/**
 * Container of fragments that implement the "post an item" flow
 */
public class PostFlowNavFragment extends Fragment implements PostFlowMaster {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_nav, container, false);

        mFragmentContainer = view.findViewById(R.id.fragment_container);
        gotoFirstStep();

        return view;
    }

    @Override
    public void stepCompleted(Fragment fragment, NewPostData data) {
        // could put this behind a factory or some other navigation controller object
        if (fragment instanceof PostFlowImageFragment) {

        }
    }

    private void gotoFirstStep() {
        PostFlowImageFragment f = PostFlowImageFragment.newInstance();

        gotoStep(f);
    }

    private void gotoStep(Fragment f) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f, "step")
                .addToBackStack("step")
                .commit();
    }
}
