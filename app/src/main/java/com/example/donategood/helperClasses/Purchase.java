package com.example.donategood.helperClasses;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donategood.models.Offering;
import com.parse.ParseUser;

public class Purchase {

    public static final String TAG = "Purchase";

    //update rating of offering based on rating from new comment
    public void updateOfferingRating(String rating, Offering offering, RatingBar ratingBar, Integer numComments) {
        Integer offeringRating = offering.getRating();
        if (offeringRating == 0) {
            offering.setRating(Integer.parseInt(rating));
            ratingBar.setVisibility(View.INVISIBLE);
        } else {
            Log.i(TAG, "current rating: " + offeringRating.toString() + "num comments: " + numComments.toString());
            offeringRating = (offeringRating * numComments) + Integer.parseInt(rating);
            numComments++;
            offeringRating = offeringRating / numComments;
            offering.setRating(offeringRating);
            ratingBar.setNumStars(offeringRating);
        }
        offering.saveInBackground();
    }

    //update quantity left of offering
    public void updateQuantityLeft(Integer quantityLeft, Offering offering, Button btnPurchase) {
        if (quantityLeft == 0) {
            //determine if all quantities of the offering has been bought
            offering.setIsBought(true);
            btnPurchase.setVisibility(View.INVISIBLE);
        }
        offering.setBoughtBy(ParseUser.getCurrentUser());
        offering.addToBoughtByArray(ParseUser.getCurrentUser());
        offering.setQuantityLeft(quantityLeft);
        offering.saveInBackground();
    }

    public void purchaseItem(Offering offering, Button btnPurchase, NotificationLoader notificationLoader, Context context, TextView tvQuantityLeft) {
        Log.i(TAG, "purchase item");

        //remove one from the quantity left
        Integer quantityLeft = offering.getQuantityLeft() - 1;
        Toast.makeText(context, "Thank you for your purchase!", Toast.LENGTH_SHORT).show();
        tvQuantityLeft.setText("Quantity Left: " + quantityLeft.toString());
        updateQuantityLeft(quantityLeft, offering, btnPurchase);

        //creates notification on the "notifications" tab within the app
        notificationLoader.createNotification(offering);
    }
}
