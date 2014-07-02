package com.lysterr.Lysterr.postflow;

import android.content.res.Resources;
import android.text.TextUtils;
import com.lysterr.Lysterr.R;

/**
 * Encapsulates business logic for generating the text for
 * new posts
 *
 * Note: there are some issues here with regards to localization (text hard-coded) but
 * who cares for now.
 */
public class NewPostDescriptionModel {
    private static String[] sIntroWords;
    private static String[] sExcellentWords;
    private static String[] sGoodWords;
    private static String[] sAverageWords;

    private static String sGenericFormatNoLocation;
    private static String sGenericFormatWithLocation;

    /**
     * Should be called by Application instance, probably in onCreate()
     */
    public static void init(Resources r) {
        sIntroWords = r.getStringArray(R.array.intro_words);
        sExcellentWords = r.getStringArray(R.array.excellent_words);
        sGoodWords = r.getStringArray(R.array.good_words);
        sAverageWords = r.getStringArray(R.array.average_words);

        sGenericFormatNoLocation = r.getString(R.string.desc_generic_no_location_format);
        sGenericFormatWithLocation = r.getString(R.string.desc_generic_with_location_format);
    }

    public static String getRandomIntro() {
        return getRandomString(sIntroWords);
    }

    public static String getRandomDescriptorForCondition(NewPostCondition cond) {
        switch (cond) {
            case Average:
                return getRandomString(sAverageWords);
            case Good:
                return getRandomString(sGoodWords);
            case Excellent:
                return getRandomString(sExcellentWords);
            default:
                throw new IllegalArgumentException("unrecognized condition: " + cond.toString());
        }
    }

    private static String getRandomString(String[] strings) {
        int index = (int)Math.floor(Math.random() * strings.length);
        return strings[index];
    }
}
