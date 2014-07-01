package com.lysterr.Lysterr.postflow.steps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.postflow.NewPostCondition;
import com.lysterr.Lysterr.postflow.NewPostData;
import com.lysterr.Lysterr.postflow.NewPostDescriptionModel;
import com.lysterr.Lysterr.postflow.PostFlowNavFragment;

/**
 * Input for a new post's condition
 */
public class PostFlowConfirmFragment extends Fragment {
    private ImageView mImage;
    private TextView mDescription;
    private Button mApprove;

    public static PostFlowConfirmFragment newInstance(NewPostData data) {
        PostFlowConfirmFragment f = new PostFlowConfirmFragment();
        PostFlowNavFragment.putDataInFrag(f, data);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_post_confirm, container, false);

        mImage = (ImageView)v.findViewById(R.id.image);
        mDescription = (TextView)v.findViewById(R.id.description);
        mApprove = (Button)v.findViewById(R.id.approve);

        final NewPostData data = (NewPostData)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);

        mDescription.setText(data.selectedDescription);
        loadImage(data.pathToBitmap);

        mApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostFlowNavFragment.notifyStepComplete(PostFlowConfirmFragment.this, data);
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    private void loadImage(String uri) {
        // TODO: should probably put this on a background thread
        Bitmap bmp = BitmapFactory.decodeFile(uri);

        mImage.setImageBitmap(bmp);
    }
}
