package com.lysterr.Lysterr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.lysterr.Lysterr.fragments.PostDetailsFragment;
import com.lysterr.Lysterr.fragments.PostListFragment;
import com.lysterr.Lysterr.fragments.interfaces.PostListDelegate;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

public class MyActivity extends Activity implements PostListDelegate {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("LYSTERR", "res code: " + resultCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_logout) {
            ParseUser.logOut();
            showLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f, "post-fragment")
                .addToBackStack("post-fragment")
                .commit();
    }

    private void showPostDetails(String postId) {
        PostDetailsFragment f = PostDetailsFragment.newInstance(postId);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f, "post-details-fragment")
                .addToBackStack("post-details-fragment")
                .commit();
    }

    @Override
    public void onPostSelected(String postId) {
        showPostDetails(postId);
    }
}
