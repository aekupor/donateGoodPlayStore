package com.example.donategood.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.donategood.R;
import com.parse.ParseUser;

public class ChangeNameFragment extends DialogFragment {

    private EditText etName;
    private Button btnSubmit;

    private static Boolean bioEdit;

    public ChangeNameFragment() {
        // Empty constructor is required for DialogFragment
    }

    // Defines the listener interface
    public interface ChangeNameDialogListener {
        void onFinishEditDialog(String inputText);
    }

    public static ChangeNameFragment newInstance(Boolean isBioEdit) {
        ChangeNameFragment frag = new ChangeNameFragment();
        Bundle args = new Bundle();
        bioEdit = isBioEdit;
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_name, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.etName);
        btnSubmit = view.findViewById(R.id.btnChangeNameSubmit);

        if (bioEdit) {
            //if editing the user's bio
            if (ParseUser.getCurrentUser().get("bio") != null) {
                etName.setText(ParseUser.getCurrentUser().get("bio").toString());
            }
        } else {
            //if editing the user's venmo username
            etName.setText(ParseUser.getCurrentUser().get("venmoName").toString());
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when submit is clicked, take user back to ProfileFragment
                sendBackResult();
            }
        });
    }

    // sends the data back to the parent fragment
    public void sendBackResult() {
        ChangeNameDialogListener listener = (ChangeNameDialogListener) getTargetFragment();
        listener.onFinishEditDialog(etName.getText().toString());
        dismiss();
    }
}