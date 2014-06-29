package com.lysterr.Lysterr.fragments.postflow;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.util.DebugUtil;
import com.lysterr.Lysterr.util.UiUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Post flow step to get a picture for a new post
 */
public class PostFlowImageFragment extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private String mCurrentPhotoPath;

    public static PostFlowImageFragment newInstance(NewPostData data) {
        PostFlowImageFragment f = new PostFlowImageFragment();
        PostFlowNavFragment.putDataInFrag(f, data);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_post_image, container, false);

        Button button = (Button)view.findViewById(R.id.image_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just use an implicit intent for now, but it would probably be nicer to embed a camera surface view
                dispatchTakePictureIntent();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            Bundle extras = dataIntent.getExtras();
//            // this is only a thumbnail, the full image was written to the file we gave in the intent
//            // the path should be stored in mCurrentPhotoPath and should be passed along
//            Bitmap imageBitmap = (Bitmap) extras.get("data");

            NewPostData data = (NewPostData)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);

            // TODO: validate file exists and has non-zero length?
            data.pathToBitmap = mCurrentPhotoPath;
            PostFlowNavFragment.notifyStepComplete(this, data);
        } else {
            UiUtil.showToast(R.string.error_post_image_load);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                DebugUtil.debugException(e);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                getParentFragment().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}