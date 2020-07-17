package com.example.donategood.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donategood.adapters.OfferingAdapter;
import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.helperClasses.Camera;
import com.example.donategood.helperClasses.FBQuery;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.LoginActivity;
import com.example.donategood.R;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Offering;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public static final String KEY_BOUGHT = "bought";
    public static final String KEY_SELLING = "selling";
    public static final String KEY_SOLD = "sold";

    private LoadPost loadPost;
    private static Camera camera;

    private Button btnLogout;
    private Button btnTakePhoto;
    private Button btnUploadPhoto;
    private TextView tvName;
    private ImageView ivProfileImage;
    private TextView tvMoneyRaised;
    private TextView tvYouBoughtTitle;
    private TextView tvYouSellingTitle;
    private TextView tvYouSoldTitle;
    private ProgressBar pb;

    private RecyclerView rvBoughtItems;
    private SmallOfferingAdapter adapter;
    private List<Offering> selectedOfferings;
    private Query query;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogout = view.findViewById(R.id.btnLogout);
        tvName = view.findViewById(R.id.tvProfileProfileName);
        ivProfileImage = view.findViewById(R.id.ivProfileProfileImage);
        btnTakePhoto = view.findViewById(R.id.btnProfileTakePhoto);
        btnUploadPhoto = view.findViewById(R.id.btnProfileUploadPhoto);
        tvMoneyRaised = view.findViewById(R.id.tvProfileMoneyRaised);
        rvBoughtItems = view.findViewById(R.id.rvBoughtItems);
        tvYouBoughtTitle = view.findViewById(R.id.tvYouBoughtTitle);
        tvYouSellingTitle = view.findViewById(R.id.tvYouSellingTitle);
        tvYouSoldTitle = view.findViewById(R.id.tvYouSoldTitle);
        pb = (ProgressBar) view.findViewById(R.id.pbProfileLoading);

        query = new Query();
        selectedOfferings = new ArrayList<>();
        adapter = new SmallOfferingAdapter(getContext(), selectedOfferings);

        rvBoughtItems.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvBoughtItems.setLayoutManager(linearLayoutManager);

        loadPost = new LoadPost();
        camera = new Camera();

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

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.launchCamera(getContext(), true);
            }
        });

        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.pickPhoto(getContext(), true);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "logout user");
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });

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

        queryPosts(KEY_BOUGHT);
        query.queryMoneyRaised(ParseUser.getCurrentUser(), tvMoneyRaised);
    }

    protected void queryPosts(final String queryType) {
        pb.setVisibility(ProgressBar.VISIBLE);
        query.setBold(queryType, tvYouSoldTitle, tvYouSellingTitle, tvYouBoughtTitle);
        query.queryPosts(ParseUser.getCurrentUser(), queryType, adapter, selectedOfferings, pb);
    }

    public static Camera getCamera() {
        return camera;
    }
}