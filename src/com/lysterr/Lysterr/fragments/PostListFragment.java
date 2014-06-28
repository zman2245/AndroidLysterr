package com.lysterr.Lysterr.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.lysterr.Lysterr.fragments.interfaces.PostListDelegate;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

/**
 * Displays the list of posts
 *
 * see: https://parse.com/docs/android_guide#ui-queryadapter
 * for customizing the ParseQueryAdapter
 */
public class PostListFragment extends ListFragment {
    private ParseQueryAdapter<ParseObject> mAdapter;

    public static PostListFragment newInstance() {
        return new PostListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ParseQueryAdapter<ParseObject>(getActivity(), "Post");
        mAdapter.setTextKey("text");
        mAdapter.setImageKey("imageThumb");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setListAdapter(mAdapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ParseObject post = mAdapter.getItem(position);
        String postId = post.getObjectId();

        PostListDelegate delegate = (PostListDelegate)getActivity();
        delegate.onPostSelected(postId);
    }
}
