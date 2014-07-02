package com.lysterr.Lysterr.postflow.model;

import android.text.TextUtils;
import com.lysterr.Lysterr.data.ParsePostField;
import com.lysterr.Lysterr.postflow.NewPostCondition;
import com.lysterr.Lysterr.postflow.NewPostDescriptionModel;
import com.lysterr.Lysterr.postflow.NewPostType;
import com.lysterr.Lysterr.util.DebugUtil;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * A struct that contains data for the new post being built
 *
 * Serializable so that it can be easily passed along the new post flow
 * between fragments
 */
public class NewGenericPostModel implements NewPostModel {
    public NewPostType type;
    public String name;
    public double price;
    public String custom;
    public NewPostCondition condition;
    public String pathToBitmap;

    public String selectedDescription;

    @Override
    public String getDescriptionForCondition(NewPostCondition cond) {
        String intro = NewPostDescriptionModel.getRandomIntro();
        String desc1 = NewPostDescriptionModel.getRandomDescriptorForCondition(cond);
        String desc2 = null;
        String priceText = String.format("$%.2f", price);

        // naive way to avoid duplicate descriptors
        while (!desc1.equals(desc2)) {
            desc2 = NewPostDescriptionModel.getRandomDescriptorForCondition(cond);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(intro)
                .append(", ")
                .append(name)
                .append(", ")
                .append(desc1)
                .append(", only ")
                .append(priceText)
                .append(", ")
                .append(cond.toString())
                .append(" condition, ");

        if (!TextUtils.isEmpty(custom)) {
            sb.append(custom).append(", ");
        }

        sb.append(desc2);

        // TODO: if location


        return sb.toString();
    }

    // TODO background threading of the image file read
    @Override
    public void saveToServer(final ParseGeoPoint point) {
        byte[] imageBytes;

        try {
            imageBytes = FileUtils.readFileToByteArray(new File(pathToBitmap));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final ParseFile image = new ParseFile("photo.jpg", imageBytes);
        image.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseObject post = new ParseObject("Post");
                    post.put(ParsePostField.createdBy.toString(), ParseUser.getCurrentUser());
                    post.put(ParsePostField.type.toString(), type.val);
                    post.put(ParsePostField.condition.toString(), condition.val);
                    post.put(ParsePostField.text.toString(), selectedDescription);
                    post.put(ParsePostField.name.toString(), name);
                    post.put(ParsePostField.price.toString(), price);

                    if (point != null) {
                        post.put(ParsePostField.location.toString(), point);
                    }

                    if (!TextUtils.isEmpty(custom)) {
                        post.put(ParsePostField.custom.toString(), custom);
                    }

                    post.put(ParsePostField.imageFile.toString(), image);

                    post.saveInBackground();
                } else {
                    DebugUtil.debugException(e);
                }
            }
        });
    }

    @Override
    public String getImagePath() {
        return pathToBitmap;
    }

    @Override
    public String getSelectedDescription() {
        return selectedDescription;
    }

    @Override
    public void setImagePath(String path) {
        pathToBitmap = path;
    }

    @Override
    public void setSelectedDescription(String desc) {
        selectedDescription = desc;
    }

    @Override
    public void setConditino(NewPostCondition cond) {
        condition = cond;
    }
}