package com.lysterr.Lysterr.postflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.data.ParsePostField;
import com.lysterr.Lysterr.fragments.interfaces.BackPressListener;
import com.lysterr.Lysterr.fragments.interfaces.PostNewListener;
import com.lysterr.Lysterr.postflow.steps.PostFlowConditionsFragment;
import com.lysterr.Lysterr.postflow.steps.PostFlowConfirmFragment;
import com.lysterr.Lysterr.postflow.steps.PostFlowDetailsFragment;
import com.lysterr.Lysterr.postflow.steps.PostFlowImageFragment;
import com.lysterr.Lysterr.util.DebugUtil;
import com.lysterr.Lysterr.util.Registry;
import com.lysterr.Lysterr.util.UiUtil;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Container of fragments that implement the "post an item" flow
 */
public class PostFlowNavFragment extends Fragment implements PostFlowMaster, BackPressListener {
    public static final String ARG_DATA = "data";

    private NewPostData mNewPostData;
    private int mCurrentStep = 0;

    private View mFragmentContainer;

    public static PostFlowNavFragment newInstance(NewPostType type) {
        PostFlowNavFragment f = new PostFlowNavFragment();

        Bundle args = new Bundle();
        NewPostData data = new NewPostData();
        data.type = type;
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

        mNewPostData = (NewPostData)getArguments().getSerializable(ARG_DATA);
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
    public void stepCompleted(Fragment fragment, NewPostData data) {
        Fragment f;

        UiUtil.hideKeyboard(getActivity());

        // could put this behind a factory or some other navigation controller object
        if (fragment instanceof PostFlowImageFragment) {
            f = PostFlowDetailsFragment.newInstance(mNewPostData);
            gotoStep(f);
        } else if (fragment instanceof PostFlowDetailsFragment) {
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

    // TODO background threading of the image file read
    private void postToServer(final NewPostData data) {
        byte[] imageBytes;

        try {
            imageBytes = FileUtils.readFileToByteArray(new File(data.pathToBitmap));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final ParseFile image = new ParseFile("photo.jpg", imageBytes);
        image.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseObject post = new ParseObject("Post");
                    post.put(ParsePostField.createdBy.toString(), ParseUser.getCurrentUser());
                    post.put(ParsePostField.type.toString(), data.type.val);
                    post.put(ParsePostField.condition.toString(), data.condition.val);
                    post.put(ParsePostField.text.toString(), data.selectedDescription);
                    post.put(ParsePostField.name.toString(), data.name);
                    post.put(ParsePostField.price.toString(), data.price);

                    fillPostWithLocationIfAvailable(post);

                    if (!TextUtils.isEmpty(data.custom)) {
                        post.put(ParsePostField.custom.toString(), data.custom);
                    }

                    post.put(ParsePostField.imageFile.toString(), image);

                    post.saveInBackground();
                } else {
                    DebugUtil.debugException(e);
                }
            }
        });
    }

    private void fillPostWithLocationIfAvailable(ParseObject post) {
        if (Registry.sLocation.hasValidLocation()) {
            ParseGeoPoint point = new ParseGeoPoint(Registry.sLocation.getLatitude(), Registry.sLocation.getLongitude());
            post.put(ParsePostField.location.toString(), point);
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
                        .commitAllowingStateLoss();
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

    @Override
    public boolean onBackPressed() {
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            getChildFragmentManager().popBackStack();
            return true;
        }

        return false;
    }
}
