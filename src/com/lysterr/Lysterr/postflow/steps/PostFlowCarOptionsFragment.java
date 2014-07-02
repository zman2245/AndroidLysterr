package com.lysterr.Lysterr.postflow.steps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.postflow.PostFlowNavFragment;
import com.lysterr.Lysterr.postflow.model.NewCarPostModel;

/**
 * Input for a new post's details
 */
public class PostFlowCarOptionsFragment extends Fragment {

    ViewGroup mOptionsContainer;

    public static PostFlowCarOptionsFragment newInstance(NewCarPostModel data) {
        PostFlowCarOptionsFragment f = new PostFlowCarOptionsFragment();
        PostFlowNavFragment.putDataInFrag(f, data);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_post_car_options, container, false);

        final NewCarPostModel model = (NewCarPostModel)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);
        mOptionsContainer = (ViewGroup)v.findViewById(R.id.options);

        String[] options = model.getSupportedOptions();

        for (String option : options) {
            CheckBox chk = (CheckBox)inflater.inflate(R.layout.car_option, mOptionsContainer, false);
            chk.setText(option);
            chk.setChecked(model.isOptionSet(option));
            chk.setTag(option);
            mOptionsContainer.addView(chk);
        }

        v.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCarPostModel data = (NewCarPostModel)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);

                int count = mOptionsContainer.getChildCount();
                for (int i = 0; i < count; i++) {
                    CheckBox chk = (CheckBox)mOptionsContainer.getChildAt(i);
                    if (chk.isChecked()) {
                        model.setOption((String)chk.getTag());
                    } else {
                        model.unsetOption((String)chk.getTag());
                    }
                }

                PostFlowNavFragment.notifyStepComplete(PostFlowCarOptionsFragment.this, data);
            }
        });

        return v;
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}
