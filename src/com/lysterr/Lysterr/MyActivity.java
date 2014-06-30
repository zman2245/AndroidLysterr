package com.lysterr.Lysterr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.lysterr.Lysterr.fragments.PostDetailsFragment;
import com.lysterr.Lysterr.fragments.PostListFragment;
import com.lysterr.Lysterr.fragments.interfaces.BackPressListener;
import com.lysterr.Lysterr.fragments.interfaces.PostListDelegate;
import com.lysterr.Lysterr.fragments.interfaces.PostNewListener;
import com.lysterr.Lysterr.fragments.postflow.PostFlowNavFragment;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

public class MyActivity extends FragmentActivity implements PostListDelegate, PostNewListener {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isLoggedIn()) {
            showLogin();
        } else {
            showPostList();
        }

        updateHomeButtonState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("LYSTERR", "res code: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_logout:
                ParseUser.logOut();
                showLogin();
                return true;

            case R.id.menu_item_post_generic:
            case R.id.menu_item_post_car:
                PostFlowNavFragment f = PostFlowNavFragment.newInstance();
                showFragmentAsModal(f);
                return true;

            case android.R.id.home:
                handleHomeButtonTap();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        boolean consume = false;

        // handle case where a modal exists
        Fragment f = getSupportFragmentManager().findFragmentByTag("modal-fragment");
        if (f != null && f instanceof BackPressListener) {
            consume = ((BackPressListener)f).onBackPressed();

            if (!consume) {
                // modal fragment did not consume the event; only thing left to do is dismiss the modal!
                getSupportFragmentManager().beginTransaction().remove(f).commit();
                updateHomeButtonState();
            }

            return;
        }

        super.onBackPressed();
    }

    private boolean isLoggedIn() {
        return ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().isAuthenticated();
    }

    private void showLogin() {
        ParseLoginBuilder builder = new ParseLoginBuilder(MyActivity.this);
        startActivityForResult(builder.build(), 0);
    }

    private void showPostList() {
        PostListFragment f = PostListFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f, "post-fragment")
                .addToBackStack("post-fragment")
                .commit();
    }

    private void showPostDetails(String postId) {
        PostDetailsFragment f = PostDetailsFragment.newInstance(postId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f, "post-details-fragment")
                .addToBackStack("post-details-fragment")
                .commit();
    }

    private void showFragmentAsModal(Fragment f) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_modal_container, f, "modal-fragment")
                .commit();

        updateHomeButtonState();
    }

    private void handleHomeButtonTap() {
        Fragment f = getSupportFragmentManager().findFragmentByTag("modal-fragment");
        if (f != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(f)
                    .commit();

            updateHomeButtonState();
        }
    }

    private void updateHomeButtonState() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (getSupportFragmentManager().findFragmentByTag("modal-fragment") != null) {
                    // show home as up
                    getActionBar().setHomeButtonEnabled(true);
                    getActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    // no longer show home as up
                    getActionBar().setHomeButtonEnabled(false);
                    getActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });
    }

    @Override
    public void onPostSelected(String postId) {
        showPostDetails(postId);
    }

    @Override
    public void onPostComplete() {
        Fragment f = getSupportFragmentManager().findFragmentByTag("modal-fragment");
        if (f != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(f)
                    .commit();

            updateHomeButtonState();
        }
    }
}
