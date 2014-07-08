package com.lysterr.Lysterr.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.data.ParseClass;
import com.lysterr.Lysterr.data.ParsePostField;
import com.lysterr.Lysterr.data.ParseUserField;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class PostListAdapter extends ParseQueryAdapter<ParseObject> {
    private String mPosterFormat;

    public PostListAdapter(Context context, QueryFactory<ParseObject> queryFactory) {
        super(context, queryFactory);

        setTextKey("text");
        setImageKey("imageThumb");

        mPosterFormat = context.getString(R.string.format_poster_line);
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_item_post, null);
        }

        super.getItemView(object, v, parent);

        TextView poster = (TextView)v.findViewById(R.id.poster);
        ParseUser user = object.getParseUser(ParsePostField.createdBy.toString());

        // user should really not be null but I've hit the case during testing
        if (user != null) {
            String name = user.getString(ParseUserField.fullName.toString());

            if (TextUtils.isEmpty(name)) {
                name = user.getUsername();
            }
            poster.setText(String.format(mPosterFormat, name));
        }

        return v;
    }
}
