package com.example.donategood.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.donategood.adapters.CommentAdapter;
import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.R;
import com.example.donategood.helperClasses.Recommend;
import com.example.donategood.models.Comment;
import com.example.donategood.models.Notification;
import com.example.donategood.models.Offering;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailFragment extends Fragment implements ComposeCommentFragment.ComposeCommentDialogListener {

    public static final String TAG = "DetailFragment";

    private String offeringId;
    private Query query;
    private Offering offering;
    private LoadPost loadPost;

    private TextView tvTitle;
    private TextView tvPrice;
    private TextView tvUser;
    private TextView tvCharity;
    private ImageView ivCharityImage;
    private ImageView ivOfferingPhoto;
    private Button btnPurchase;
    private Button btnComment;
    private TextView tvQuantityLeft;
    private TextView tvCommentTitle;
    private TextView tvAvgRating;
    private LinearLayout layoutImages;

    private RecyclerView rvRecommendedOfferings;
    private SmallOfferingAdapter adapter;
    private List<Offering> reccomendedOfferings;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private List<Comment> allComments;
    private Integer numComments;

    private ShareButton shareButton;
    private ShareLinkContent content;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(String offeringId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("offeringId", offeringId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offeringId = getArguments().getString("offeringId", "");
        Log.i(TAG, "post id: " + offeringId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTitle = view.findViewById(R.id.tvDetailTitle);
        tvPrice = view.findViewById(R.id.tvDetailPrice);
        tvCharity = view.findViewById(R.id.tvDetailCharity);
        tvUser = view.findViewById(R.id.tvDetailUser);
        ivCharityImage = view.findViewById(R.id.ivDetailCharityImage);
        btnPurchase = view.findViewById(R.id.btnPurchase);
        ivOfferingPhoto = view.findViewById(R.id.ivDetailOfferingPhoto);
        shareButton = (ShareButton)view.findViewById(R.id.fbShareButtonDetail);
        rvRecommendedOfferings = view.findViewById(R.id.rvRecommendOfferings);
        btnComment = view.findViewById(R.id.btnComment);
        tvQuantityLeft = view.findViewById(R.id.tvQuantityLeft);
        tvCommentTitle = view.findViewById(R.id.tvViewCommentsTitle);
        tvAvgRating = view.findViewById(R.id.tvAvgRating);
        layoutImages = (LinearLayout) view.findViewById(R.id.linearImages);

        numComments = 0;

        reccomendedOfferings = new ArrayList<>();
        adapter = new SmallOfferingAdapter(getContext(), reccomendedOfferings);

        rvRecommendedOfferings.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvRecommendedOfferings.setLayoutManager(linearLayoutManager);

        rvComments = view.findViewById(R.id.rvComments);

        allComments = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), allComments);

        rvComments.setAdapter(commentAdapter);
        LinearLayoutManager linearLayoutManagerComment = new LinearLayoutManager(getContext());
        rvComments.setLayoutManager(linearLayoutManagerComment);

        tvCharity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCharity();
            }
        });

        ivCharityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCharity();
            }
        });

        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUser();
            }
        });

        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchaseItem();
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "comment button clicked");
                showEditDialog();
            }
        });

        loadPost = new LoadPost();

        query = new Query();
        query.queryOfferingById(offeringId, new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting post", e);
                    return;
                }
                offering = objects.get(0);
                Log.i(TAG, "got offering with title: " + offering.getTitle());

                loadPost.setTitlePriceUser(offering, tvTitle, tvPrice, tvUser);
                loadPost.setCharity(offering, getContext(), tvCharity, ivCharityImage);

                if (!offering.hasMultipleImages()) {
                    loadPost.setPostImage(offering.getImage(), getContext(), ivOfferingPhoto);
                } else {
                    ArrayList<ParseFile> imagesArray = offering.getImagesArray();
                    for (ParseFile image : imagesArray) {

                        ImageView ivImage = new ImageView(getContext());

                        ViewTarget<ImageView, Drawable> into = Glide.with(getContext())
                                .load(image.getUrl())
                                .into(ivImage);

                        ivImage.setAdjustViewBounds(true);
                        layoutImages.addView(into.getView());
                        Log.i(TAG, "adding view to layoutImages");

                    }
                }

                if (offering.getRating() != 0) {
                    tvAvgRating.setText("Average Rating: " + offering.getRating());
                } else {
                    tvAvgRating.setText("Average Rating: n/a");
                }

                tvQuantityLeft.setText("Quantity Left: " + offering.getQuantityLeft().toString());
                if (offering.getQuantityLeft() == 0) {
                    btnPurchase.setVisibility(View.INVISIBLE);
                }

                setShareButton();
                queryRecommendedPosts();
                queryComments();
            }
        });
    }

    private void queryRecommendedPosts() {
        final Recommend recommend = new Recommend();
        final Map<Offering, Integer>[] pointValues = new Map[]{new HashMap<>()};

        query.queryAllPostsWithoutPage(new FindCallback<Offering>() {
               @Override
               public void done(List<Offering> offerings, ParseException e) {
                   if (e != null) {
                       Log.e(TAG, "Issue with getting offerings", e);
                       return;
                   }
                   for (Offering otherOffering : offerings) {
                       if (otherOffering.getObjectId().equals(offering.getObjectId())) {
                           //if offering is the same, do not include as recommended offering
                           continue;
                       }

                       Integer pointValue = recommend.getPointValue(offering, otherOffering);
                       pointValues[0].put(otherOffering, pointValue);
                   }
                   final Map<Offering, Integer>[] sortedPointValues = new Map[]{new HashMap<>()};
                   sortedPointValues[0] = recommend.sortMapByPoints(pointValues[0]);
                   Log.i(TAG, "sorted point values list: " + sortedPointValues[0].toString());

                   adapter.clear();
                   reccomendedOfferings.clear();
                   reccomendedOfferings.addAll(sortedPointValues[0].keySet());
                   adapter.notifyDataSetChanged();
               }
           });
    }

    private void queryComments() {
        query.queryComments(offering, new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }
                if (objects.size() != 0) {
                    commentAdapter.clear();
                    allComments.clear();
                    allComments.addAll(objects);
                    commentAdapter.notifyDataSetChanged();
                    numComments = objects.size();
                } else {
                    tvCommentTitle.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setShareButton() {
        content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(offering.getImage().getUrl()))
                .setQuote("Check out this " + offering.getTitle() + " that I am purchasing on Donate Good!")
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#DonateGood")
                        .build())
                .build();

        shareButton.setShareContent(content);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "share button clicked");
                ShareDialog.show(DetailFragment.this, content);
            }
        });
    }

    private void goToCharity() {
        Log.i(TAG, "go to charity");

        //go to charity fragment
        final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
        Fragment fragment = CharityFragment.newInstance(offering.getCharity().getTitle());
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
    }

    private void goToUser() {
        Log.i(TAG, "go to user");

        //go to user fragment
        final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
        Fragment fragment = OtherUserProfileFragment.newInstance((String) tvUser.getText());
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
    }

    private void purchaseItem() {
        Log.i(TAG, "purchase item");

        Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse("venmo://paycharge?txn=pay&recipients=" + offering.getUser().get("venmoName") + "&amount=" + offering.getPrice().toString() + "&note=" + offering.getTitle()));
        startActivity(implicit);

        Integer quantityLeft = offering.getQuantityLeft() - 1;
        if (quantityLeft == 0) {
            offering.setIsBought(true);
            btnPurchase.setVisibility(View.INVISIBLE);
        }
        offering.setBoughtBy(ParseUser.getCurrentUser());
        offering.addToBoughtByArray(ParseUser.getCurrentUser());
        offering.setQuantityLeft(quantityLeft);
        offering.saveInBackground();

        Toast.makeText(getContext(), "Thank you for your purchase!", Toast.LENGTH_SHORT).show();
        tvQuantityLeft.setText("Quantity Left: " + quantityLeft.toString());

        Notification notification = new Notification();
        notification.setUserActed(false);
        notification.setKeyOffering(offering);
        notification.setKeyUser(ParseUser.getCurrentUser());
        notification.setSellingUser(offering.getUser());
        notification.saveInBackground();
    }

    @Override
    public void onFinishEditDialog(String inputText, String rating) {
        Log.i(TAG, "got comment with text: " + inputText + " and rating: " + rating);

        Comment comment = new Comment();
        comment.setByUser(ParseUser.getCurrentUser());
        comment.setForPost(offering);
        comment.setText(inputText);
        comment.setRating(Integer.parseInt(rating));
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
            }
        });
        allComments.add(comment);
        commentAdapter.notifyDataSetChanged();
        tvCommentTitle.setVisibility(View.VISIBLE);

        //update offering rating
        Integer offeringRating = offering.getRating();
        if (offeringRating == 0) {
            offering.setRating(Integer.parseInt(rating));
            tvAvgRating.setText("Average Rating: " + rating);
        } else {
            Log.i(TAG, "current rating: " + offeringRating.toString() + "num comments: " + numComments.toString());
            offeringRating = (offeringRating * numComments) + Integer.parseInt(rating);
            numComments++;
            offeringRating = offeringRating / numComments;
            offering.setRating(offeringRating);
            tvAvgRating.setText("Average Rating: " + offeringRating);
        }
        offering.saveInBackground();
    }

    // Call this method to launch the edit dialog
    private void showEditDialog() {
        FragmentManager fm = getFragmentManager();
        ComposeCommentFragment composeCommentFragment = (ComposeCommentFragment) ComposeCommentFragment.newInstance();
        // SETS the target fragment for use later when sending results
        composeCommentFragment.setTargetFragment(DetailFragment.this, 300);
        composeCommentFragment.show(fm, "fragment_compose_comment");
    }
}