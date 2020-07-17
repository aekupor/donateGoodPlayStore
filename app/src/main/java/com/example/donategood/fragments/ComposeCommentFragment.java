package com.example.donategood.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.donategood.R;

public class ComposeCommentFragment extends DialogFragment {

        private EditText tvCommentText;
        private EditText tvCommentRating;

        public ComposeCommentFragment() {
                // Empty constructor is required for DialogFragment
        }

        // Defines the listener interface
        public interface ComposeCommentDialogListener {
                void onFinishEditDialog(String inputText, String inputRating);
        }


        public static ComposeCommentFragment newInstance() {
                ComposeCommentFragment frag = new ComposeCommentFragment();
                Bundle args = new Bundle();
                frag.setArguments(args);
                return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                return inflater.inflate(R.layout.fragment_compose_comment, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);

                tvCommentText = (EditText) view.findViewById(R.id.etTestComment);
                tvCommentRating = (EditText) view.findViewById(R.id.etRatingComment);

                // Show soft keyboard automatically and request focus to field
                tvCommentText.requestFocus();
        }

        // sends the data back to the parent fragment
        public void sendBackResult() {
                ComposeCommentDialogListener listener = (ComposeCommentDialogListener) getTargetFragment();
                listener.onFinishEditDialog(tvCommentText.getText().toString(), tvCommentRating.getText().toString());
                dismiss();
        }
}