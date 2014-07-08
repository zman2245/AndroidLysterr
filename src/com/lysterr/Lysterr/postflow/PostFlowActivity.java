package com.lysterr.Lysterr.postflow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.fragments.interfaces.BackPressListener;
import com.lysterr.Lysterr.fragments.interfaces.PostNewListener;

/**
 * Created by zfoster on 7/7/14.
 */
public class PostFlowActivity extends FragmentActivity implements PostNewListener {
    public static final String ARG_TYPE = "type";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        NewPostType type = (NewPostType)getIntent().getSerializableExtra(ARG_TYPE);

        PostFlowNavFragment f;
        if (type == NewPostType.Car) {
            f = PostFlowNavFragment.newInstance(NewPostType.Car);
            showFragmentAsModal(f);
        } else {
            // generic
            f = PostFlowNavFragment.newInstance(NewPostType.Generic);
            showFragmentAsModal(f);
        }
    }

    private void showFragmentAsModal(Fragment f) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f, "frag")
                .commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        boolean consume = false;

        // handle case where a modal exists
        Fragment f = getSupportFragmentManager().findFragmentByTag("frag");
        if (f != null && f instanceof BackPressListener) {
            consume = ((BackPressListener)f).onBackPressed();

            if (!consume) {
                // modal fragment did not consume the event; only thing left to do is die!!
                finish();
            }

            return;
        }

        super.onBackPressed();
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

    @Override
    public void onPostComplete() {
        finish();
    }
}
