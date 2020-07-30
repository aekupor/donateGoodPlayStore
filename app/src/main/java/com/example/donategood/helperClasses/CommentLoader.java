package com.example.donategood.helperClasses;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.donategood.fragments.DetailFragment;
import com.example.donategood.models.Comment;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class CommentLoader {

    public static final String TAG = "CommentLoader";

    //saves comments to backend
    public Comment saveComment(String inputText, String rating, final Context context, Offering offering) {
        //check if user writing the comment is verified or not
        Boolean verified = false;
        ArrayList<Object> boughtByArray = offering.getBoughtByArray();
        if (boughtByArray != null) {
            for (Object object : boughtByArray) {
                if (((ParseUser) object).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    verified = true;
                }
            }
        }

        Comment comment = new Comment();
        comment.setByUser(ParseUser.getCurrentUser());
        comment.setForPost(offering);
        comment.setText(inputText);
        comment.setVerified(verified);
        comment.setRating(Integer.parseInt(rating));
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(context, "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
            }
        });

       return comment;
    }

    //find all comments related to that post
    public void queryComments(final DetailFragment detailFragment) {
        detailFragment.query.queryComments(detailFragment.offering, new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }
                if (objects.size() != 0) {
                    //if there are comments related to offering, display them
                    detailFragment.commentAdapter.clear();
                    detailFragment.allComments.clear();
                    detailFragment.allComments.addAll(objects);
                    detailFragment.commentAdapter.notifyDataSetChanged();
                    detailFragment.numComments = objects.size();
                } else {
                    //if no comments related to offering, hide comments title
                    detailFragment.tvCommentTitle.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}
