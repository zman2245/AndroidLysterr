package com.lysterr.Lysterr.fragments.postflow;

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
import com.lysterr.Lysterr.util.UiUtil;

/**
 * Input for a new post's details
 */
public class PostFlowDetailsFragment extends Fragment implements TextWatcher {
    private EditText mName;
    private EditText mPrice;
    private EditText mCustom;
    private Button mNext;

    public static PostFlowDetailsFragment newInstance(NewPostData data) {
        PostFlowDetailsFragment f = new PostFlowDetailsFragment();
        PostFlowNavFragment.putDataInFrag(f, data);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_post_details, container, false);

        mName = (EditText)v.findViewById(R.id.name);
        mPrice = (EditText)v.findViewById(R.id.price);
        mCustom = (EditText)v.findViewById(R.id.custom);
        mNext = (Button)v.findViewById(R.id.next);

        mName.addTextChangedListener(this);
        mPrice.addTextChangedListener(this);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPostData data = (NewPostData)getArguments().getSerializable(PostFlowNavFragment.ARG_DATA);
                data.name = mName.getText().toString();
                data.price = Double.valueOf(mPrice.getText().toString());

                if (data.price <= 0) {
                    UiUtil.showToast(R.string.error_invalid_price);
                } else {
                    PostFlowNavFragment.notifyStepComplete(PostFlowDetailsFragment.this, data);
                }
            }
        });

        return v;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mNext.setEnabled(
                !TextUtils.isEmpty(mName.getText()) &&
                !TextUtils.isEmpty(mPrice.getText())
              );
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}
}
