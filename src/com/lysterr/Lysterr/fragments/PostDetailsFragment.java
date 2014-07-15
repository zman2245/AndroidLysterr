package com.lysterr.Lysterr.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.data.ParseClass;
import com.lysterr.Lysterr.data.ParsePostField;
import com.lysterr.Lysterr.data.ParseUserField;
import com.lysterr.Lysterr.util.DebugUtil;
import com.lysterr.Lysterr.util.UiUtil;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            public void done(ParseObject post, ParseException e) {
                if (e == null) {
                    String text = post.getString(ParsePostField.text.toString());

                    mDescription.setText(text);

                    loadImage(post);
                    loadCreatedByUser(post);

                    getActivity().getActionBar().setTitle(post.getString(ParsePostField.name.toString()));
                } else {
                    DebugUtil.debugException(e);
                    UiUtil.showToast(R.string.error_post_details_load);
                }
            }
        });
    }

    private void loadImage(ParseObject post) {
        ParseFile image = post.getParseFile(ParsePostField.imageFile.toString());
        image.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    mImage.setImageBitmap(bmp);
                } else {
                    DebugUtil.debugException(e);
                    UiUtil.showToast(R.string.error_post_image_load);
                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer percentDone) {
                Log.d("LYSTERR", "post details image progress: " + percentDone);
            }
        });
    }

    private void loadCreatedByUser(final ParseObject post) {
        ParseUser user = post.getParseUser(ParsePostField.createdBy.toString());
        user.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser creator, ParseException e) {
                if (e == null) {
                    String text = getMetaStringFromObjects(post, creator);
                    mMeta.setText(text);
                } else {
                    DebugUtil.debugException(e);

                    // TODO: can we handle this gracefully? IDK, without User you can't chat
                }
            }
        });
    }

    private String getMetaStringFromObjects(ParseObject post, ParseUser creator) {
        StringBuilder sb = new StringBuilder("Posted by ");

        String name = creator.getString(ParseUserField.fullName.toString());
        if (TextUtils.isEmpty(name)) {
            name = creator.getUsername();
        }

        sb.append(name).append(" on ");

        Date created = post.getCreatedAt();
        DateFormat df = new SimpleDateFormat("MM/dd @ h:mm a");
        sb.append(df.format(created));

        String city = post.getString(ParsePostField.city.toString());
        if (!TextUtils.isEmpty(city)) {
            sb.append(" in ").append(city);
        }

        return sb.toString();
    }
}
