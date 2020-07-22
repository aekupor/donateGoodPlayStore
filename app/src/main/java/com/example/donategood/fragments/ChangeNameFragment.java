package com.example.donategood.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.donategood.R;

public class ChangeNameFragment extends DialogFragment {

    private EditText etName;
    private Button btnSubmit;

    public ChangeNameFragment() {
        // Empty constructor is required for DialogFragment
    }

    // Defines the listener interface
    public interface ChangeNameDialogListener {
        void onFinishEditDialog(String inputText);
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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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