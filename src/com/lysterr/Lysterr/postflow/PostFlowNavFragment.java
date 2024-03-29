package com.lysterr.Lysterr.postflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.fragments.interfaces.BackPressListener;
import com.lysterr.Lysterr.fragments.interfaces.PostNewListener;
import com.lysterr.Lysterr.postflow.model.NewCarPostModel;
import com.lysterr.Lysterr.postflow.model.NewGenericPostModel;
import com.lysterr.Lysterr.postflow.model.NewPostModel;
import com.lysterr.Lysterr.postflow.steps.PostFlowCarDetailsFragment;
import com.lysterr.Lysterr.postflow.steps.PostFlowCarOptionsFragment;
import com.lysterr.Lysterr.postflow.steps.PostFlowConditionsFragment;
import com.lysterr.Lysterr.postflow.steps.PostFlowConfirmFragment;
import com.lysterr.Lysterr.postflow.steps.PostFlowDetailsFragment;
import com.lysterr.Lysterr.postflow.steps.PostFlowImageFragment;
import com.lysterr.Lysterr.util.Registry;
import com.lysterr.Lysterr.util.UiUtil;
import com.parse.ParseGeoPoint;
import java.io.Serializable;

/**
 * Container of fragments that implement the "post an item" flow
 */
public class PostFlowNavFragment extends Fragment implements PostFlowMaster, BackPressListener {
    public static final String ARG_DATA = "data";

    private NewPostModel mNewPostData;
    private int mCurrentStep = 0;

    private View mFragmentContainer;

    public static PostFlowNavFragment newInstance(NewPostType type) {
        PostFlowNavFragment f = new PostFlowNavFragment();

        Bundle args = new Bundle();
        NewPostModel data;
        if (type == NewPostType.Car) {
            data = new NewCarPostModel();
            ((NewCarPostModel)data).type = type;
        } else {
            data = new NewGenericPostModel();
            ((NewGenericPostModel)data).type = type;
        }
        args.putSerializable(ARG_DATA, data);
        f.setArguments(args);

        return f;
    }

    /**
     * A helper for child fragments to use
     *
     * @param f
     * @param data
     */
    public static void putDataInFrag(Fragment f, Serializable data) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA, data);
        f.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mNewPostData = (NewPostModel)getArguments().getSerializable(ARG_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!(getActivity() instanceof PostNewListener)) {
            throw new IllegalStateException("parent activity must implement PostNewListener");
        }

        View view = inflater.inflate(R.layout.fragment_new_post_nav, container, false);

        mFragmentContainer = view.findViewById(R.id.fragment_container);

        if (getChildFragmentManager().findFragmentByTag("step") == null) {
            // only go to first step if there isn't already a step
            gotoFirstStep();
        }

        return view;
    }

    @Override
    public void stepCompleted(Fragment fragment, NewPostModel data) {
        Fragment f;

        UiUtil.hideKeyboard(getActivity());

        // could put this behind a factory or some other navigation controller object
        // there's gotta be a better way
        if (fragment instanceof PostFlowImageFragment) {
            if (data instanceof NewCarPostModel) {
                f = PostFlowCarDetailsFragment.newInstance((NewCarPostModel) mNewPostData);
                gotoStep(f);
            } else {
                f = PostFlowDetailsFragment.newInstance((NewGenericPostModel) mNewPostData);
                gotoStep(f);
            }
        } else if (fragment instanceof PostFlowCarDetailsFragment) {
            f = PostFlowCarOptionsFragment.newInstance((NewCarPostModel)mNewPostData);
            gotoStep(f);
        } else if (fragment instanceof PostFlowDetailsFragment) {
            f = PostFlowConditionsFragment.newInstance(mNewPostData);
            gotoStep(f);
        } else if (fragment instanceof PostFlowCarOptionsFragment) {
            f = PostFlowConditionsFragment.newInstance(mNewPostData);
            gotoStep(f);
        } else if (fragment instanceof PostFlowConditionsFragment) {
            f = PostFlowConfirmFragment.newInstance(mNewPostData);
            gotoStep(f);
        } else if (fragment instanceof PostFlowConfirmFragment) {
            postToServer(data);
            PostNewListener listener = (PostNewListener)getActivity();
            listener.onPostComplete();
        }
    }

    private void postToServer(final NewPostModel data) {
        ParseGeoPoint point = getLocationIfAvailable();
        data.saveToServer(point);
    }

    // returns null if unavailable
    private ParseGeoPoint getLocationIfAvailable() {
        ParseGeoPoint point = null;

        if (Registry.sLocation.hasValidLocation()) {
            point = new ParseGeoPoint(Registry.sLocation.getLatitude(), Registry.sLocation.getLongitude());
        }

        return point;
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
                        .commitAllowingStateLoss();
            }
        });
    }

    public static void notifyStepComplete(Fragment f, NewPostModel data) {
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

    @Override
    public boolean onBackPressed() {
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            getChildFragmentManager().popBackStack();
            return true;
        }

        return false;
    }
}
