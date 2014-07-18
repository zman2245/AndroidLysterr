package com.lysterr.Lysterr.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.adapters.PostListAdapter;
import com.lysterr.Lysterr.factories.PostListQueryFactory;
import com.lysterr.Lysterr.fragments.interfaces.PostListDelegate;
import com.lysterr.Lysterr.util.Registry;
import com.lysterr.Lysterr.util.UiUtil;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import java.util.List;

/**
 * Displays the list of posts
 *
 * see: https://parse.com/docs/android_guide#ui-queryadapter
 * for customizing the ParseQueryAdapter
 */
public class PostListFragment extends ListFragment
        implements SwipeRefreshLayout.OnRefreshListener, ActionBar.OnNavigationListener, ParseQueryAdapter.OnQueryLoadListener, TextWatcher {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mHeaderView;
    private EditText mSearchView;

    private ParseQueryAdapter<ParseObject> mAdapter;
    private PostListQueryFactory mQueryFactory;
    private boolean mDataIsLoading = false;

    public static PostListFragment newInstance() {
        return new PostListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQueryFactory = new PostListQueryFactory();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setListAdapter(mAdapter);

        View v = super.onCreateView(inflater, container, savedInstanceState);
        mHeaderView = inflater.inflate(R.layout.post_list_header, null, false);
        mSearchView = (EditText)mHeaderView.findViewById(R.id.search);
        mSearchView.addTextChangedListener(this);

        // Now create a SwipeRefreshLayout to wrap the fragment's content view
        mSwipeRefreshLayout = new ListFragmentSwipeRefreshLayout(container.getContext());
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Add the list fragment's content view to the SwipeRefreshLayout, making sure that it fills
        // the SwipeRefreshLayout
        mSwipeRefreshLayout.addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // Make sure that the SwipeRefreshLayout will fill the fragment
        ViewGroup.LayoutParams lps = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeRefreshLayout.setLayoutParams(lps);

        // Now return the SwipeRefreshLayout as this fragment's content view
        return mSwipeRefreshLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().addHeaderView(mHeaderView);
        reinitAdapter();
    }

    @Override
    public void onDestroyView() {
        setListAdapter(null);

        super.onDestroyView();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ParseObject post = mAdapter.getItem(position);
        String postId = post.getObjectId();

        PostListDelegate delegate = (PostListDelegate)getActivity();
        delegate.onPostSelected(postId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_post_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (!mDataIsLoading) {
            mAdapter.loadObjects();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        switch (itemPosition) {
            case 0:
                // all
                mQueryFactory.setAllQuery();
                mSearchView.setText("");
                UiUtil.hideKeyboard(getActivity());
                reinitAdapter();
                break;

            case 1:
                // within 5 miles
                mQueryFactory.setLocationQuery(Registry.sLocation.getLatitude(), Registry.sLocation.getLongitude(), 5.0);
                mSearchView.setText("");
                UiUtil.hideKeyboard(getActivity());
                reinitAdapter();
                break;

            case 2:
                // my own
                mQueryFactory.setMyQuery();
                mSearchView.setText("");
                UiUtil.hideKeyboard(getActivity());
                reinitAdapter();
                break;
        }
        return false;
    }

    @Override
    public void onLoading() {
        mDataIsLoading = true;
    }

    @Override
    public void onLoaded(List list, Exception e) {
        mDataIsLoading = false;
        if (!PostListFragment.this.isDetached() && getView() != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void reinitAdapter() {
        if (mAdapter != null) {
            mAdapter.removeOnQueryLoadListener(this);
        }

        mAdapter = new PostListAdapter(getActivity(), mQueryFactory);
        mAdapter.addOnQueryLoadListener(this);

        setListAdapter(mAdapter);
    }

    // TODO: move text watcher stuff to its own class
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    private String mSearchString = "";
    @Override
    public void onTextChanged(final CharSequence s, int start, int before, int count) {

        mSearchString = s.toString();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mSearchString.equals(s.toString())) {
                    // Search string has already been updated so do nothing! Let the next scheduled runnable try!
                    return;
                }

                mQueryFactory.setSearchText(mSearchString.toString(), new Runnable() {
                    @Override
                    public void run() {
                        if (!PostListFragment.this.isDetached() && getView() != null) {
                            reinitAdapter();
                            mSearchView.requestFocus();
                        }
                    }
                });
            }
        }, 500);
    }

    @Override
    public void afterTextChanged(Editable s) {}

    /**
     * Sub-class of {@link android.support.v4.widget.SwipeRefreshLayout} for use in this
     * {@link android.support.v4.app.ListFragment}. The reason that this is needed is because
     * {@link android.support.v4.widget.SwipeRefreshLayout} only supports a single child, which it
     * expects to be the one which triggers refreshes. In our case the layout's child is the content
     * view returned from
     * {@link android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     * which is a {@link android.view.ViewGroup}.
     *
     * <p>To enable 'swipe-to-refresh' support via the {@link android.widget.ListView} we need to
     * override the default behavior and properly signal when a gesture is possible. This is done by
     * overriding {@link #canChildScrollUp()}.
     */
    private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

        public ListFragmentSwipeRefreshLayout(Context context) {
            super(context);
        }

        /**
         * As mentioned above, we need to override this method to properly signal when a
         * 'swipe-to-refresh' is possible.
         *
         * @return true if the {@link android.widget.ListView} is visible and can scroll up.
         */
        @Override
        public boolean canChildScrollUp() {
            final ListView listView = getListView();
            if (listView.getVisibility() == View.VISIBLE) {
                return canListViewScrollUp(listView);
            } else {
                return false;
            }
        }

        /**
         * Utility method to check whether a {@link ListView} can scroll up from it's current position.
         * Handles platform version differences, providing backwards compatible functionality where
         * needed.
         */
        private boolean canListViewScrollUp(ListView listView) {
            if (android.os.Build.VERSION.SDK_INT >= 14) {
                // For ICS and above we can call canScrollVertically() to determine this
                return ViewCompat.canScrollVertically(listView, -1);
            } else {
                // Pre-ICS we need to manually check the first visible item and the child view's top
                // value
                return listView.getChildCount() > 0 &&
                        (listView.getFirstVisiblePosition() > 0
                                || listView.getChildAt(0).getTop() < listView.getPaddingTop());
            }
        }
    }
}
