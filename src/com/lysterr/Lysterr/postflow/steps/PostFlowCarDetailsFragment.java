package com.lysterr.Lysterr.postflow.steps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.lysterr.Lysterr.R;
import com.lysterr.Lysterr.postflow.PostFlowNavFragment;
import com.lysterr.Lysterr.postflow.model.NewCarPostModel;
import com.lysterr.Lysterr.util.UiUtil;

/**
 * Input for a new car post's details
 */
public class PostFlowCarDetailsFragment extends Fragment implements TextWatcher {
    private EditText mYear;
    private EditText mColor;
    private EditText mMake;
    private EditText mModel;
    private EditText mPrice;
    private EditText mMiles;
    private EditText mDoors;
    private EditText mCustom;
    private Button mNext;

    public static PostFlowCarDetailsFragment newInstance(NewCarPostModel data) {
        PostFlowCarDetailsFragment f = new PostFlowCarDetailsFragment();
        PostFlowNavFragment.putDataInFrag(f, data);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_post_car_details, container, false);

        mYear = (EditText)v.findViewById(R.id.year);
        mColor = (EditText)v.findViewById(R.id.color);
        mMake = (EditText)v.findViewById(R.id.make);
        mModel = (EditText)v.findViewById(R.id.model);
        mMiles = (EditText)v.findViewById(R.id.miles);
        mDoors = (EditText)v.findViewById(R.id.doors);
        mPrice = (EditText)v.findViewById(R.id.price);
        mCustom = (EditText)v.findViewById(R.id.custom);
        mNext = (Button)v.findViewById(R.id.next);

        mYear.addTextChangedListener(this);
        mColor.addTextChangedListener(this);
        mMake.addTextChangedListener(this);
        mModel.addTextChangedListener(this);
        mPrice.addTextChangedListener(this);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCarPostModel data = (NewCarPostModel)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);
                data.year = mYear.getText().toString();
                data.color = mColor.getText().toString();
                data.make = mMake.getText().toString();
                data.model = mModel.getText().toString();
                data.miles = Long.valueOf(mMiles.getText().toString());
                data.doors = Integer.valueOf(mDoors.getText().toString());
                data.price = Double.valueOf(mPrice.getText().toString());
                data.custom = mCustom.getText().toString();

                if (data.price <= 0) {
                    UiUtil.showToast(R.string.error_invalid_price);
                } else {
                    PostFlowNavFragment.notifyStepComplete(PostFlowCarDetailsFragment.this, data);
                }
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mNext.setEnabled(
                !TextUtils.isEmpty(mYear.getText()) &&
                !TextUtils.isEmpty(mColor.getText()) &&
                !TextUtils.isEmpty(mMake.getText()) &&
                !TextUtils.isEmpty(mModel.getText()) &&
                !TextUtils.isEmpty(mPrice.getText())
              );
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}
}
