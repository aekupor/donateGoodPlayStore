package com.example.donategood.helperClasses;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donategood.R;
import com.example.donategood.models.Charity;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MoneyRaised {

    public static final String TAG = "MoneyRaised";

    //determines amount of money raised for a charity
    public void queryMoneyRaisedForCharity(final Charity charity, final TextView tvMoney, final ProgressBar pb, final Query query) {
        pb.setVisibility(View.VISIBLE);

        final HashMap<String, Integer> moneyRaisedMap = new HashMap<>();
        final Integer[] moneyRaised = {0};
        query.queryAllPosts(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                for (Offering offering : objects) {
                    if (offering.getCharity().getObjectId().equals(charity.getObjectId())) {
                        ArrayList<Object> boughtUsers = offering.getBoughtByArray();

                        if (boughtUsers != null && !boughtUsers.isEmpty()) {
                            moneyRaised[0] += boughtUsers.size() * offering.getPrice();

                            //give the "buyers" of the offering credit towards supporting that charity
                            for (Object boughtObject : boughtUsers) {
                                addToMapWithUser((ParseUser)boughtObject, moneyRaisedMap, offering.getPrice());
                            }

                            //give the "sellers" of the offering credit towards supporting that charity
                            addToMapWithUser(offering.getUser(), moneyRaisedMap, offering.getPrice() * boughtUsers.size());
                        }
                    }
                }

                // NOTE: tvMoney will be actual amount of money raised for a charity,
                // while the values in moneyRaisedMap will add up to twice that amount.
                // Ex: Nathan buys Ashlee's product for $10 for Charity X. The amount of money
                // raised for that charity is $10, but Nathan and Ashlee both get $10 of credit
                // towards supporting that charity.

                //set money raised
                tvMoney.setText("$" + moneyRaised[0].toString());

                //save map
                query.moneyRaisedForSpecificCharity = sortMapByValues(moneyRaisedMap);
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }

    //find money raised by a specified user
    public void queryMoneyRaisedByUser(final ParentProfile parentProfile, final Context context, final Query query) {
        parentProfile.pb.setVisibility(View.VISIBLE);

        final Integer[] moneyRaised = {0};
        final Integer[] moneySold = {0};
        final HashMap<String, Integer> moneyRaisedMap = new HashMap<>();

        query.queryAllPosts(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                for (Offering offering : objects) {
                    ArrayList<Object> boughtUsers = offering.getBoughtByArray();

                    if (boughtUsers != null && !boughtUsers.isEmpty()) {
                        for (Object object : boughtUsers) {
                            ParseUser user = (ParseUser) object;

                            if (user.getObjectId().equals(parentProfile.user.getObjectId())) {
                                //if user bought the offering, add its price to the total money raised
                                moneyRaised[0] += offering.getPrice();
                                addToMapWithOffering(offering, moneyRaisedMap, offering.getPrice());
                            }
                        }

                        if (offering.getUser().getObjectId().equals(parentProfile.user.getObjectId())) {
                            //if user sold the offering, add its price * quantity sold to the total money sold
                            moneySold[0] += offering.getPrice() * boughtUsers.size();
                            addToMapWithOffering(offering, moneyRaisedMap, offering.getPrice() * boughtUsers.size());
                        }
                    }
                }
                //find and set total money raised
                Integer totalMoney = moneyRaised[0] + moneySold[0];
                parentProfile.tvMoneyRaised.setText("$" + totalMoney.toString());

                //save map
                query.moneyRaisedBySpecificUser = sortMapByValues(moneyRaisedMap);

                //set appropriate icons for user
                setIcon(totalMoney, parentProfile, context, determineImage(totalMoney));
                setCharityIcon(query, context, parentProfile);
            }
        });
    }

    //sorts map with the largest number of points first
    public HashMap<String, Integer> sortMapByValues(Map<String, Integer> pointValues) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(pointValues.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    //determine the level of the user based on the amount of money raised + sold
    public Integer determineImage(Integer totalMoney) {
        if (totalMoney < 100) {
            return R.drawable.level_one;
        } else if (totalMoney < 200) {
            return R.drawable.level_two;
        } else if (totalMoney < 300) {
            return R.drawable.level_three;
        } else if (totalMoney < 400) {
            return R.drawable.level_four;
        } else if (totalMoney < 500) {
            return R.drawable.level_five;
        } else {
            return R.drawable.crown;
        }
    }

    //set icon based on level
    public void setIcon(Integer totalMoney, ParentProfile parentProfile, Context context, Integer iconImage) {
        if (totalMoney < 25) {
            parentProfile.ivLevelIcon.setVisibility(View.INVISIBLE);
        } else {
            Glide.with(context)
                    .load(iconImage)
                    .circleCrop()
                    .into(parentProfile.ivLevelIcon);
        }
    }

    public void addToMapWithOffering(Offering offering, Map<String, Integer> map, Integer money) {
        String charityName = null;
        try {
            Charity charity = offering.getCharity().fetchIfNeeded();
            charityName = charity.getTitle();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        if (map.containsKey(charityName)) {
            map.put(charityName, map.get(charityName) + money);
        } else {
            map.put(charityName, money);
        }
    }

    public void addToMapWithUser(ParseUser user, Map<String, Integer> map, Integer money) {
        String username = "";
        try {
            username = user.fetchIfNeeded().getUsername();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        if (map.containsKey(username)) {
            map.put(username, map.get(username) + money);
        } else {
            map.put(username, money);
        }
    }

    public void setCharityIcon(Query query, final Context context, final ParentProfile parentProfile) {
        Map.Entry<String,Integer> entry = query.moneyRaisedBySpecificUser.entrySet().iterator().next();
        String key = entry.getKey();

        query.findCharity(key, new FindCallback<Charity>() {
            @Override
            public void done(List<Charity> objects, ParseException e) {
                //set charity icon with profile image of charity with most money raised
                Glide.with(context)
                        .load(objects.get(0).getImage().getUrl())
                        .into(parentProfile.ivCharityIcon);
                parentProfile.pb.setVisibility(View.INVISIBLE);
                return;
            }
        });
    }
}
