package com.lysterr.Lysterr.fragments.postflow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import com.lysterr.Lysterr.R;

/**
 * Input for a new post's condition
 */
public class PostFlowConditionsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private RadioButton mExcellent;
    private RadioButton mGood;
    private RadioButton mAverage;

    public static PostFlowConditionsFragment newInstance(NewPostData data) {
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
        NewPostData data = (NewPostData)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);
        NewPostDescriptionModel model = new NewPostDescriptionModel();

        mExcellent.setText(model.getDescriptionForGenericItem(NewPostCondition.Excellent, data.name, data.price, data.custom));
        mGood.setText(model.getDescriptionForGenericItem(NewPostCondition.Good, data.name, data.price, data.custom));
        mAverage.setText(model.getDescriptionForGenericItem(NewPostCondition.Average, data.name, data.price, data.custom));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            return;
        }

        NewPostData data = (NewPostData)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);

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
