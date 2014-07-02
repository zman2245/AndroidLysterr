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
import com.lysterr.Lysterr.postflow.model.NewGenericPostModel;
import com.lysterr.Lysterr.postflow.NewPostDescriptionModel;
import com.lysterr.Lysterr.postflow.PostFlowNavFragment;

/**
 * Input for a new post's condition
 */
public class PostFlowConditionsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private RadioButton mExcellent;
    private RadioButton mGood;
    private RadioButton mAverage;

    public static PostFlowConditionsFragment newInstance(NewGenericPostModel data) {
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
        NewGenericPostModel data = (NewGenericPostModel)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);

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

        NewGenericPostModel data = (NewGenericPostModel)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);

        if (buttonView == mExcellent) {
            data.condition = NewPostCondition.Excellent;
        } else if (buttonView == mGood) {
            data.condition = NewPostCondition.Good;
        } else {
            data.condition = NewPostCondition.Average;
        }

        data.selectedDescription = buttonView.getText().toString();

        PostFlowNavFragment.notifyStepComplete(this, data);
    }
}
