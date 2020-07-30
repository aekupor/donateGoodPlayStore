package com.example.donategood.helperClasses;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.donategood.models.Notification;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class NotificationLoader {

    public static final String TAG = "NotificationLoader";

    //query and set notifications for current user
    public void getNotifications(final ParentProfile parentProfile, final Context context) {
        Log.i(TAG, "notification button clicked");

        //clear adapter and set notifications tab as visible
        parentProfile.adapter.clear();
        parentProfile.notifications.clear();
        parentProfile.rvOfferings.setVisibility(View.INVISIBLE);
        parentProfile.rvNotifications.setVisibility(View.VISIBLE);
        parentProfile.tvPendingNotificationsTitle.setVisibility(View.VISIBLE);
        parentProfile.pendingNotifications.setVisibility(View.VISIBLE);
        parentProfile.pendingNotifications.removeAllViews();

        //query notifications for offerings that the current user is selling
        parentProfile.query.queryNotificationsForSeller(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    return;
                }

                if (objects != null) {
                    for (Notification notification : objects) {
                        if (notification.getSellingUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            //notification is for current user to approve
                            Log.i(TAG, "found notification for title for post: " + notification.getKeyOffering().getTitle());
                            parentProfile.notifications.add(notification);
                        }
                    }
                    parentProfile.notificationAdapter.notifyDataSetChanged();
                }
            }
        });

        //query notifications for offering that the current user is attempting to buy
        parentProfile.query.queryNotificationsForBuyer(ParseUser.getCurrentUser(), new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    return;
                }

                if (objects != null) {
                    for (Notification notification : objects) {
                        Log.i(TAG, "found notification for title for post: " + notification.getKeyOffering().getTitle());

                        //if user hasn't seen the notification yet, then display
                        if (!notification.getUserSeen()) {
                            TextView textView = new TextView(context);
                            if (!notification.getUserActed()) {
                                //notification is still pending on seller approval
                                textView.setText("Still waiting on seller to approval your purchase of " + notification.getKeyOffering().getTitle() + ".");
                            } else if (notification.getKeyApproved()) {
                                //attempt to buy has been approved by seller
                                textView.setText("You have been approved to buy " + notification.getKeyOffering().getTitle() + ".");
                                notification.setUserSeen(true);
                                notification.saveInBackground();
                            } else {
                                //attempt to buy has been denied by seller
                                textView.setText("You have NOT been approved to buy " + notification.getKeyOffering().getTitle() + ".");
                                notification.setUserSeen(true);
                                notification.saveInBackground();
                            }
                            parentProfile.pendingNotifications.addView(textView);
                        }
                    }
                }
            }
        });
    }
}
