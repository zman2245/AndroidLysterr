package com.lysterr.Lysterr.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.fragments.PostDetailsFragment;
import com.lysterr.Lysterr.fragments.interfaces.BackPressListener;
import com.lysterr.Lysterr.fragments.interfaces.PostNewListener;
import com.lysterr.Lysterr.postflow.NewPostType;
import com.lysterr.Lysterr.postflow.PostFlowNavFragment;

public class PostDetailsActivity extends FragmentActivity {
    public static final String ARG_POSTID = "post-id";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        String postId = getIntent().getStringExtra(ARG_POSTID);

        PostDetailsFragment f = PostDetailsFragment.newInstance(postId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f, "post-details-fragment")
                .addToBackStack("post-details-fragment")
                .commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PostFlowNavFragment f;

        switch (item.getItemId()) {

            case android.R.id.home:
                // Note: if this activity ever provides intent filters so that other apps can
                // add an instance to another tasks back-stack, a different impl is needed. See:
                // http://developer.android.com/training/implementing-navigation/ancestral.html
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
