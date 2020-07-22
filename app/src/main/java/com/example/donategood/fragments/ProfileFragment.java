package com.example.donategood.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donategood.adapters.NotificationAdapter;
import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.helperClasses.Camera;
import com.example.donategood.helperClasses.FBQuery;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.LoginActivity;
import com.example.donategood.R;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Comment;
import com.example.donategood.models.Notification;
import com.example.donategood.models.Offering;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements ChangeNameFragment.ChangeNameDialogListener {

    public static final String TAG = "ProfileFragment";
    public static final String KEY_BOUGHT = "bought";
    public static final String KEY_SELLING = "selling";
    public static final String KEY_SOLD = "sold";

    private LoadPost loadPost;
    private static Camera camera;

    private TextView tvName;
    private ImageView ivProfileImage;
    private TextView tvMoneyRaised;
    private TextView tvYouBoughtTitle;
    private TextView tvYouSellingTitle;
    private TextView tvYouSoldTitle;
    private TextView tvNotificationsTitle;
    private ProgressBar pb;
    private Button btnSubmit;
    private EditText etName;
    private TextView tvPendingNotificationsTitle;
    private RatingBar ratingBar;
    private Boolean fbEdit;

    private RecyclerView rvBoughtItems;
    private SmallOfferingAdapter adapter;
    private List<Offering> selectedOfferings;
    private Query query;

    private List<Notification> notifications;
    private RecyclerView rvNotifications;
    private NotificationAdapter notificationAdapter;
    private LinearLayout pendingNotifications;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                Log.i(TAG, "action_logout clicked");
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                return true;
            case R.id.action_take_photo:
                Log.i(TAG, "action_take_photo clicked");
                camera.launchCamera(getContext(), true);
                return true;
            case R.id.action_upload_photo:
                Log.i(TAG, "action_upload_photo clicked");
                camera.pickPhoto(getContext(), true, false);
                return true;
            case R.id.action_messenger_name:
                Log.i(TAG, "action_messenger_name clicked");
                fbEdit = true;
                //changeName();
                showEditDialog();
                return true;
            case R.id.action_venmo_name:
                Log.i(TAG, "action_venmo_name clicked");
                fbEdit = false;
                //changeName();
                showEditDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        tvName = view.findViewById(R.id.tvProfileProfileName);
        ivProfileImage = view.findViewById(R.id.ivProfileProfileImage);
        tvMoneyRaised = view.findViewById(R.id.tvProfileMoneyRaised);
        rvBoughtItems = view.findViewById(R.id.rvBoughtItems);
        rvNotifications = view.findViewById(R.id.rvNotifications);
        tvYouBoughtTitle = view.findViewById(R.id.tvYouBoughtTitle);
        tvYouSellingTitle = view.findViewById(R.id.tvYouSellingTitle);
        tvYouSoldTitle = view.findViewById(R.id.tvYouSoldTitle);
        tvNotificationsTitle = view.findViewById(R.id.tvNotificationsTitle);
        pb = (ProgressBar) view.findViewById(R.id.pbProfileLoading);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        etName = view.findViewById(R.id.etVenmo);
        pendingNotifications = view.findViewById(R.id.layoutNotification);
        tvPendingNotificationsTitle = view.findViewById(R.id.tvWaitingNotificationsTitle);
        ratingBar = (RatingBar) view.findViewById(R.id.rbProfile);

        etName.setVisibility(View.INVISIBLE);
        tvPendingNotificationsTitle.setVisibility(View.INVISIBLE);
        pendingNotifications.setVisibility(View.INVISIBLE);

        query = new Query();

        btnSubmit.setVisibility(View.INVISIBLE);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fbEdit) {
                    ParseUser.getCurrentUser().put("fbMessenger", etName.getText().toString());
                } else {
                    ParseUser.getCurrentUser().put("venmoName", etName.getText().toString());
                }
                ParseUser.getCurrentUser().saveInBackground();
                btnSubmit.setVisibility(View.INVISIBLE);
                etName.setText("");
                etName.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            }
        });

        //set up adapters and recycler views
        selectedOfferings = new ArrayList<>();
        adapter = new SmallOfferingAdapter(getContext(), selectedOfferings);

        rvBoughtItems.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvBoughtItems.setLayoutManager(linearLayoutManager);

        notifications = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notifications);

        rvNotifications.setAdapter(notificationAdapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        rvNotifications.setLayoutManager(linearLayoutManager2);

        loadPost = new LoadPost();
        camera = new Camera();

        checkFBLogin();

        tvYouBoughtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_BOUGHT);
            }
        });

        tvYouSellingTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_SELLING);
            }
        });

        tvYouSoldTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_SOLD);
            }
        });

        tvNotificationsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNotifications();
            }
        });

        queryPosts(KEY_BOUGHT);
        query.queryMoneyRaised(ParseUser.getCurrentUser(), tvMoneyRaised);
        query.queryUserRating(ParseUser.getCurrentUser(), ratingBar);
    }

    protected void queryPosts(final String queryType) {
        rvBoughtItems.setVisibility(View.VISIBLE);
        rvNotifications.setVisibility(View.INVISIBLE);
        notificationAdapter.clear();
        tvNotificationsTitle.setTypeface(null, Typeface.NORMAL);
        tvPendingNotificationsTitle.setVisibility(View.INVISIBLE);
        pendingNotifications.setVisibility(View.INVISIBLE);

        pb.setVisibility(ProgressBar.VISIBLE);
        query.setBold(queryType, tvYouSoldTitle, tvYouSellingTitle, tvYouBoughtTitle);
        query.queryPosts(ParseUser.getCurrentUser(), queryType, adapter, selectedOfferings, pb);
    }

    public static Camera getCamera() {
        return camera;
    }

    public void getNotifications() {
        Log.i(TAG, "notification button clicked");

        tvNotificationsTitle.setTypeface(null, Typeface.BOLD);
        tvYouSoldTitle.setTypeface(null, Typeface.NORMAL);
        tvYouSellingTitle.setTypeface(null, Typeface.NORMAL);
        tvYouBoughtTitle.setTypeface(null, Typeface.NORMAL);

        adapter.clear();
        notifications.clear();
        rvBoughtItems.setVisibility(View.INVISIBLE);
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
                            TextView textView = new TextView(getContext());
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

    public void checkFBLogin() {
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            //user is logged in with facebook
            Log.i(TAG, "user is logged in with FB");
            final Long[] userId = new Long[1];
            final FBQuery fbQuery = new FBQuery();

            //get user name from FB
            fbQuery.getName(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        final String name = object.getString("name");
                        Log.i(TAG, "got graph response: " + name);
                        userId[0] = object.getLong("id");

                        //get user profile picture from FB
                        fbQuery.getProfileImage(accessToken, userId[0], new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                try {
                                    JSONObject data = response.getJSONObject().getJSONObject("data");
                                    String url = data.getString("url");
                                    Log.i(TAG, "got image url: " + url);

                                    loadPost.setUserFromFB(name, url, getContext(), tvName, ivProfileImage);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            //user is not logged in with FB
            loadPost.setUser(ParseUser.getCurrentUser(), getContext(), tvName, ivProfileImage);
        }
    }

    private void changeName() {
        etName.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);
        if (fbEdit) {
            etName.setText(ParseUser.getCurrentUser().get("fbMessenger").toString());
        } else {
            etName.setText(ParseUser.getCurrentUser().get("venmoName").toString());
        }
    }

    private void showEditDialog() {
        FragmentManager fm = getFragmentManager();
        ChangeNameFragment changeNameFragment = (ChangeNameFragment) ChangeNameFragment.newInstance(fbEdit);
        // SETS the target fragment for use later when sending results
        changeNameFragment.setTargetFragment(ProfileFragment.this, 300);
        changeNameFragment.show(fm, "fragment_change_name");
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        Log.i(TAG, "change name to: " + inputText);

        if (fbEdit) {
            ParseUser.getCurrentUser().put("fbMessenger", inputText);
        } else {
            ParseUser.getCurrentUser().put("venmoName", inputText);
        }
        ParseUser.getCurrentUser().saveInBackground();
        Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }
}