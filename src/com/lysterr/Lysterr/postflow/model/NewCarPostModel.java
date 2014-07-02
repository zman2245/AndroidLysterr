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
import java.util.HashMap;

/**
 * A struct that contains data for the new post being built
 *
 * Serializable so that it can be easily passed along the new post flow
 * between fragments
 */
public class NewCarPostModel implements NewPostModel {
    public NewPostType type;
    public NewPostCondition condition;
    public String pathToBitmap;
    public String selectedDescription;

    public double price;
    public String custom;
    public String year;
    public String color;
    public String make;
    public String model;
    public long miles;
    public int doors;

    private HashMap<String, Boolean> mOptions = new HashMap<String, Boolean>();
    private static final String[] supportedOptions =
            {       "Moonroof",
                    "Navigation",
                    "Automatic",
                    "AWD",
                    "Leather",
                    "Original Owner",
                    "New Tires",
                    "New Breaks"
            };

    public NewCarPostModel() {
        for (String s : supportedOptions) {
            mOptions.put(s , false);
        }
    }

    public String[] getSupportedOptions() {
        return supportedOptions;
    }

    public boolean isOptionSet(String opt) {
        if (!mOptions.containsKey(opt)) {
            throw new IllegalArgumentException("unrecognized option");
        }

        return mOptions.get(opt);
    }

    public void setOption(String opt) {
        if (!mOptions.containsKey(opt)) {
            throw new IllegalArgumentException("unrecognized option");
        }

        mOptions.put(opt, true);
    }

    public void unsetOption(String opt) {
        if (!mOptions.containsKey(opt)) {
            throw new IllegalArgumentException("unrecognized option");
        }

        mOptions.put(opt, false);
    }

    @Override
    public String getDescriptionForCondition(NewPostCondition cond) {
        String intro = NewPostDescriptionModel.getRandomIntro();
        String priceText = String.format("$%.2f", price);

        StringBuilder sb = new StringBuilder();
        sb.append(intro)
                .append(", ")
                .append(year)
                .append(", ")
                .append(color)
                .append(", ")
                .append(make)
                .append(" ")
                .append(model)
                .append(", ")
                .append(", ")
                .append(cond.toString())
                .append(" condition");

        if (miles > 0) {
            sb.append(", only ").append(miles).append(" miles");
        }

        if (doors > 0) {
            sb.append(", ").append(doors).append("dr");
        }

        for (String opt : supportedOptions) {
            if (mOptions.get(opt)) {
                sb.append(", ").append(opt);
            }
        }

        if (!TextUtils.isEmpty(custom)) {
            sb.append(", ").append(custom);
        }

        // TODO: city

        sb.append(", ").append(priceText);

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
                    post.put(ParsePostField.name.toString(), String.format("%s %s", make, model));
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