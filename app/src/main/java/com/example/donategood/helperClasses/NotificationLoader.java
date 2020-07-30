package com.example.donategood.helperClasses;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.donategood.R;
import com.example.donategood.adapters.NotificationAdapter;
import com.example.donategood.models.Notification;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
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

    //initialize notifications and related variables
    public void initializeNotifications(View view, final Context context, ParentProfile parentProfile) {
        parentProfile.rvNotifications = view.findViewById(R.id.rvNotifications);
        parentProfile.pendingNotifications = view.findViewById(R.id.layoutNotification);
        parentProfile.tvPendingNotificationsTitle = view.findViewById(R.id.tvWaitingNotificationsTitle);

        //make notifications invisible until user clicks on "notification" tab
        parentProfile.tvPendingNotificationsTitle.setVisibility(View.INVISIBLE);
        parentProfile.pendingNotifications.setVisibility(View.INVISIBLE);

        //initialize adapter and recycler view
        parentProfile.notifications = new ArrayList<>();
        parentProfile.notificationAdapter = new NotificationAdapter(context, parentProfile.notifications);

        parentProfile.rvNotifications.setAdapter(parentProfile.notificationAdapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);
        parentProfile.rvNotifications.setLayoutManager(linearLayoutManager2);
    }


    //make notifications tab invisible
    public void hideNotificationsTab(ParentProfile parentProfile) {
        parentProfile.rvOfferings.setVisibility(View.VISIBLE);
        parentProfile.rvNotifications.setVisibility(View.INVISIBLE);
        parentProfile.notificationAdapter.clear();
        parentProfile.tvPendingNotificationsTitle.setVisibility(View.INVISIBLE);
        parentProfile.pendingNotifications.setVisibility(View.INVISIBLE);
    }
}
