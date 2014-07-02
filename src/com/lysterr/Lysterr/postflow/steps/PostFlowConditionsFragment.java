package com.lysterr.Lysterr.postflow.steps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.postflow.NewPostCondition;
import com.lysterr.Lysterr.postflow.PostFlowNavFragment;
import com.lysterr.Lysterr.postflow.model.NewPostModel;

/**
 * Input for a new post's condition
 */
public class PostFlowConditionsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private RadioButton mExcellent;
    private RadioButton mGood;
    private RadioButton mAverage;

    public static PostFlowConditionsFragment newInstance(NewPostModel data) {
        PostFlowConditionsFragment f = new PostFlowConditionsFragment();
        PostFlowNavFragment.putDataInFrag(f, data);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_post_conditions, container, false);

        mExcellent = (RadioButton)v.findViewById(R.id.excellent);
        mGood = (RadioButton)v.findViewById(R.id.good);
        mAverage = (RadioButton)v.findViewById(R.id.average);

        mExcellent.setOnCheckedChangeListener(this);
        mGood.setOnCheckedChangeListener(this);
        mAverage.setOnCheckedChangeListener(this);

        fillDescriptions();

        return v;
    }

    private void fillDescriptions() {
        NewPostModel data = (NewPostModel)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);

        mExcellent.setText(data.getDescriptionForCondition(NewPostCondition.Excellent));
        mGood.setText(data.getDescriptionForCondition(NewPostCondition.Good));
        mAverage.setText(data.getDescriptionForCondition(NewPostCondition.Average));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            return;
        }

        NewPostModel data = (NewPostModel)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);

        if (buttonView == mExcellent) {
            data.setConditino(NewPostCondition.Excellent);
        } else if (buttonView == mGood) {
            data.setConditino(NewPostCondition.Good);
        } else {
            data.setConditino(NewPostCondition.Average);
        }

        data.setSelectedDescription(buttonView.getText().toString());

        PostFlowNavFragment.notifyStepComplete(this, data);
    }
}
