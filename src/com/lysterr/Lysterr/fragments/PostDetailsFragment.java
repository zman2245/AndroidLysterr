package com.lysterr.Lysterr.fragments;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.data.ParseClass;
import com.lysterr.Lysterr.data.ParsePostField;
import com.lysterr.Lysterr.util.UiUtil;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import org.w3c.dom.Text;

/**
 * Displays information and supports actions relevant to a specific post
 */
public class PostDetailsFragment extends Fragment {
    private ImageView mImage;
    private TextView mDescription;
    private TextView mMeta;

    public static PostDetailsFragment newInstance(String postId) {
        PostDetailsFragment f = new PostDetailsFragment();

        Bundle args = new Bundle();
        args.putString("postId", postId);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);

        mImage = (ImageView)view.findViewById(R.id.image);
        mDescription = (TextView)view.findViewById(R.id.description);
        mMeta = (TextView)view.findViewById(R.id.meta);

        loadDetails();

        return view;
    }

    private void loadDetails() {
        String postId = getArguments().getString("postId");

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.Post.toString());
        query.getInBackground(postId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    String text = parseObject.getString(ParsePostField.text.toString());

                    mDescription.setText(text);
                } else {
                    UiUtil.showToast(R.string.error_post_details_load);
                }
            }
        });
    }
}
