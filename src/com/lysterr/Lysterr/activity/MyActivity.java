package com.lysterr.Lysterr.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.fragments.PostListFragment;
import com.lysterr.Lysterr.fragments.interfaces.PostListDelegate;
import com.lysterr.Lysterr.postflow.NewPostType;
import com.lysterr.Lysterr.postflow.PostFlowActivity;
import com.lysterr.Lysterr.postflow.PostFlowNavFragment;
import com.lysterr.Lysterr.util.Registry;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import java.util.ArrayList;

public class MyActivity extends FragmentActivity implements PostListDelegate {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Registry.sLocation.startChecking(this);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        Registry.sLocation.startChecking(this);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStart();
//
//        Registry.sLocation.stopChecking(this);
//    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isLoggedIn()) {
            removeFragment();
            showLogin();
            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        } else if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            PostListFragment f = showPostList();

            getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            ArrayList<String> itemList = new ArrayList<String>();
            itemList.add("All Listings");
            itemList.add("Listings in 5 Miles");
            itemList.add("My Listings");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, itemList);

            getActionBar().setListNavigationCallbacks(adapter, f);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getSupportFragmentManager().findFragmentByTag("modal-fragment") == null) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PostFlowNavFragment f;
        Intent intent;

        switch (item.getItemId()) {
            case R.id.menu_item_logout:
                ParseUser.logOut();
                showLogin();
                return true;

            case R.id.menu_item_post_generic:
                intent = new Intent(this, PostFlowActivity.class);
                intent.putExtra(PostFlowActivity.ARG_TYPE, NewPostType.Generic);
                startActivity(intent);
                return true;

            case R.id.menu_item_post_car:
                intent = new Intent(this, PostFlowActivity.class);
                intent.putExtra(PostFlowActivity.ARG_TYPE, NewPostType.Car);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        boolean consume = false;
//
//        // handle case where a modal exists
//        Fragment f = getSupportFragmentManager().findFragmentByTag("modal-fragment");
//        if (f != null && f instanceof BackPressListener) {
//            consume = ((BackPressListener)f).onBackPressed();
//
////            if (!consume) {
////                // modal fragment did not consume the event; only thing left to do is dismiss the modal!
////                removeModal();
////            }
//
//            return;
//        }

        super.onBackPressed();
    }

    private boolean isLoggedIn() {
        return ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().isAuthenticated();
    }

    private void showLogin() {
        ParseLoginBuilder builder = new ParseLoginBuilder(MyActivity.this);
        startActivityForResult(builder.build(), 0);
    }

    private PostListFragment showPostList() {
        PostListFragment f = PostListFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f, "post-fragment")
                .addToBackStack("post-fragment")
                .commit();

        return f;
    }

    private void showPostDetails(String postId) {
        Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra(PostDetailsActivity.ARG_POSTID, postId);
        startActivity(intent);
    }

    private void removeFragment() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    @Override
    public void onPostSelected(String postId) {
        showPostDetails(postId);
    }
}
