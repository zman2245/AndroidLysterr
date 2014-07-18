package com.lysterr.Lysterr.postflow.steps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.postflow.model.NewGenericPostModel;
import com.lysterr.Lysterr.postflow.PostFlowNavFragment;
import com.lysterr.Lysterr.postflow.model.NewPostModel;

/**
 * Final page in post flow prompting the user to confirm
 */
public class PostFlowConfirmFragment extends Fragment {
    private ImageView mImage;
    private EditText mDescription;
    private Button mApprove;

    public static PostFlowConfirmFragment newInstance(NewPostModel data) {
        PostFlowConfirmFragment f = new PostFlowConfirmFragment();
        PostFlowNavFragment.putDataInFrag(f, data);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_post_confirm, container, false);

        mImage = (ImageView)v.findViewById(R.id.image);
        mDescription = (EditText)v.findViewById(R.id.description);
        mApprove = (Button)v.findViewById(R.id.approve);

        final NewPostModel data = (NewPostModel)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);

        mDescription.setText(data.getSelectedDescription());
        loadImage(data.getImagePath());

        mApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user may have editted the text
                data.setSelectedDescription(mDescription.getText().toString());

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
