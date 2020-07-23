package com.example.donategood.helperClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donategood.R;
import com.example.donategood.adapters.NotificationAdapter;
import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.models.Notification;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ParentProfile {

    public static final String TAG = "ParentProfile";
    public static final String KEY_BOUGHT = "bought";
    public static final String KEY_SELLING = "selling";
    public static final String KEY_SOLD = "sold";

    public LoadPost loadPost;
    public Query query;
    public ParseUser user;
    public Boolean isCurrentUser;

    public TextView tvNotificationsTitle;
    public TextView tvPendingNotificationsTitle;
    public List<Notification> notifications;
    public RecyclerView rvNotifications;
    public NotificationAdapter notificationAdapter;
    public LinearLayout pendingNotifications;

    public TextView tvName;
    public ImageView ivProfileImage;
    public TextView tvMoneyRaised;
    public RecyclerView rvOfferings;
    public SmallOfferingAdapter adapter;
    public List<Offering> selectedOfferings;
    public TextView tvBoughtTitle;
    public TextView tvSoldTitle;
    public TextView tvSellingTitle;
    public ProgressBar pb;
    public RatingBar ratingBar;

    public void initializeVariables(View view, Context context, Boolean currentUser) {
        isCurrentUser = currentUser;

        tvName = view.findViewById(R.id.tvOtherProfileProfileName);
        ivProfileImage = view.findViewById(R.id.ivOtherProfileProfileImage);
        tvMoneyRaised = view.findViewById(R.id.tvOtherUserMoneyRaised);
        rvOfferings = view.findViewById(R.id.rvOtherUserSelling);
        tvBoughtTitle = view.findViewById(R.id.tvOtherBoughtTitle);
        tvSellingTitle = view.findViewById(R.id.tvOtherSellingTitle);
        tvSoldTitle = view.findViewById(R.id.tvOtherSoldTitle);
        pb = (ProgressBar) view.findViewById(R.id.pbOtherProfileLoading);
        ratingBar = (RatingBar) view.findViewById(R.id.rbOtherUserProfile);

        if (isCurrentUser) {
            initializeNotifications(view, context);
        }

        selectedOfferings = new ArrayList<>();
        adapter = new SmallOfferingAdapter(context, selectedOfferings);

        rvOfferings.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvOfferings.setLayoutManager(linearLayoutManager);

        loadPost = new LoadPost();
        query = new Query();

        tvBoughtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_BOUGHT);
            }
        });

        tvSellingTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_SELLING);
            }
        });

        tvSoldTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_SOLD);
            }
        });
    }

    public void initializeNotifications(View view, final Context context) {
        rvNotifications = view.findViewById(R.id.rvNotifications);
        tvNotificationsTitle = view.findViewById(R.id.tvNotificationsTitle);
        pendingNotifications = view.findViewById(R.id.layoutNotification);
        tvPendingNotificationsTitle = view.findViewById(R.id.tvWaitingNotificationsTitle);

        tvPendingNotificationsTitle.setVisibility(View.INVISIBLE);
        pendingNotifications.setVisibility(View.INVISIBLE);

        notifications = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(context, notifications);

        rvNotifications.setAdapter(notificationAdapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);
        rvNotifications.setLayoutManager(linearLayoutManager2);

        tvNotificationsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNotifications(context);
            }
        });
    }

    public void queryPosts(String queryType) {
        if (isCurrentUser) {
            changeVisibility();
        }
        pb.setVisibility(ProgressBar.VISIBLE);
        query.setBold(queryType, tvSoldTitle, tvSellingTitle, tvBoughtTitle);
        query.queryPosts(user, queryType, adapter, selectedOfferings, pb);
    }

    public void changeVisibility() {
        rvOfferings.setVisibility(View.VISIBLE);
        rvNotifications.setVisibility(View.INVISIBLE);
        notificationAdapter.clear();
        tvNotificationsTitle.setTypeface(null, Typeface.NORMAL);
        tvPendingNotificationsTitle.setVisibility(View.INVISIBLE);
        pendingNotifications.setVisibility(View.INVISIBLE);
    }

    public void queryInfo(Context context) {
        loadPost.setUser(user, context, tvName, ivProfileImage);

        queryPosts(KEY_BOUGHT);
        query.queryMoneyRaised(user, tvMoneyRaised);
        query.queryUserRating(user, ratingBar);
    }

    public void setUser(ParseUser parseUser) {
        user = parseUser;
    }

    public void getNotifications(final Context context) {
        Log.i(TAG, "notification button clicked");

        tvNotificationsTitle.setTypeface(null, Typeface.BOLD);
        tvSoldTitle.setTypeface(null, Typeface.NORMAL);
        tvSellingTitle.setTypeface(null, Typeface.NORMAL);
        tvBoughtTitle.setTypeface(null, Typeface.NORMAL);

        adapter.clear();
        notifications.clear();
        rvOfferings.setVisibility(View.INVISIBLE);
        rvNotifications.setVisibility(View.VISIBLE);
        tvPendingNotificationsTitle.setVisibility(View.VISIBLE);
        pendingNotifications.setVisibility(View.VISIBLE);
        pendingNotifications.removeAllViews();

        query.queryNotificationsForSeller(new FindCallback<Notification>() {
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
                            notifications.add(notification);
                        }
                    }
                    notificationAdapter.notifyDataSetChanged();
                }
            }
        });

        query.queryNotificationsForBuyer(ParseUser.getCurrentUser(), new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    return;
                }

                if (objects != null) {
                    for (Notification notification : objects) {
                        Log.i(TAG, "found notification for title for post: " + notification.getKeyOffering().getTitle());

                        if (!notification.getUserSeen()) {
                            TextView textView = new TextView(context);
                            if (!notification.getUserActed()) {
                                textView.setText("Still waiting on seller to approval your purchase of " + notification.getKeyOffering().getTitle() + ".");
                            } else if (notification.getKeyApproved()) {
                                textView.setText("You have been approved to buy " + notification.getKeyOffering().getTitle() + ".");
                                notification.setUserSeen(true);
                                notification.saveInBackground();
                            } else {
                                textView.setText("You have NOT been approved to buy " + notification.getKeyOffering().getTitle() + ".");
                                notification.setUserSeen(true);
                                notification.saveInBackground();
                            }
                            pendingNotifications.addView(textView);
                        }
                    }
                }
            }
        });
    }
}