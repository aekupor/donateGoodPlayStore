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
    public void findCharityMoneyRaised(final Charity charity, final TextView tvMoney, final ProgressBar pb, final Query query) {
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
                                ParseUser boughtUser = (ParseUser) boughtObject;
                                String boughtUsername = "";
                                try {
                                    boughtUsername = boughtUser.fetchIfNeeded().getUsername();
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }

                                if (moneyRaisedMap.containsKey(boughtUsername)) {
                                    moneyRaisedMap.put(boughtUsername, moneyRaisedMap.get(boughtUsername) + offering.getPrice());
                                } else {
                                    moneyRaisedMap.put(boughtUsername, offering.getPrice());
                                }
                            }

                            //give the "sellers" of the offering credit towards supporting that charity
                            String username = "";
                            try {
                                username = offering.getUser().fetchIfNeeded().getUsername();
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }

                            if (moneyRaisedMap.containsKey(username)) {
                                moneyRaisedMap.put(username, moneyRaisedMap.get(username) + offering.getPrice() * boughtUsers.size());
                            } else {
                                moneyRaisedMap.put(username, offering.getPrice() * boughtUsers.size());
                            }
                        }
                    }
                }
                // NOTE: tvMoney will be actual amount of money raised for a charity,
                // while the values in moneyRaisedMap will add up to twice that amount.
                // Ex: Nathan buys Ashlee's product for $10 for Charity X. The amount of money
                // raised for that charity is $10, but Nathan and Ashlee both get $10 of credit
                // towards supporting that charity.
                tvMoney.setText("$" + moneyRaised[0].toString());
                query.moneyRaisedForCharityByPerson = sortMapByPointsByUser(moneyRaisedMap);
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }

    //find money raised by a specified user
    public void queryMoneyRaised(final ParentProfile parentProfile, final Context context, final Query query) {
        parentProfile.pb.setVisibility(View.VISIBLE);

        final Integer[] moneyRaised = {0};
        final Integer[] moneySold = {0};

        final HashMap<Charity, Integer> moneyRaisedByCharity = new HashMap<>();

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
                                Charity charity = null;
                                try {
                                    charity = offering.getCharity().fetchIfNeeded();
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }
                                if (moneyRaisedByCharity.containsKey(charity)) {
                                    moneyRaisedByCharity.put(charity, moneyRaisedByCharity.get(charity) + offering.getPrice());
                                } else {
                                    moneyRaisedByCharity.put(charity, offering.getPrice());
                                }
                            }
                        }
                        if (offering.getUser().getObjectId().equals(parentProfile.user.getObjectId())) {
                            //if user sold the offering, add its price * quantity sold to the total money sold
                            moneySold[0] += offering.getPrice() * boughtUsers.size();
                            Charity charity = offering.getCharity();
                            if (moneyRaisedByCharity.containsKey(charity)) {
                                moneyRaisedByCharity.put(charity, moneyRaisedByCharity.get(charity) + offering.getPrice() * boughtUsers.size());
                            } else {
                                moneyRaisedByCharity.put(charity, offering.getPrice() * boughtUsers.size());
                            }
                        }
                    }
                }
                //find and set total money raised
                Integer totalMoney = moneyRaised[0] + moneySold[0];
                parentProfile.tvMoneyRaised.setText("$" + totalMoney.toString());

                //determine the level of the user based on the amount of money raised + sold
                int iconImage = -1;
                if (totalMoney < 100) {
                    iconImage = R.drawable.level_one;
                } else if (totalMoney < 200) {
                    iconImage = R.drawable.level_two;
                } else if (totalMoney < 300) {
                    iconImage = R.drawable.level_three;
                } else if (totalMoney < 400) {
                    iconImage = R.drawable.level_four;
                } else if (totalMoney < 500) {
                    iconImage = R.drawable.level_five;
                } else {
                    iconImage = R.drawable.crown;
                }

                //set icon based on level
                if (totalMoney < 25) {
                    parentProfile.ivLevelIcon.setVisibility(View.INVISIBLE);
                } else {
                    Glide.with(context)
                            .load(iconImage)
                            .circleCrop()
                            .into(parentProfile.ivLevelIcon);
                }

                //sort map
                HashMap<Charity, Integer> sortedMap = sortMapByPoints(moneyRaisedByCharity);
                query.sortedMapMoneyRaisedByCharity = sortedMap;

                //make map that has only one entry by charity (add up all the prices)
                HashMap<String, Integer> consolidateMapByCharity = new HashMap<>();
                for (Map.Entry mapElement : query.sortedMapMoneyRaisedByCharity.entrySet()) {
                    Charity key = (Charity) mapElement.getKey();
                    Charity charity = null;
                    try {
                        charity = key.fetchIfNeeded();
                    } catch (ParseException e2) {
                        e2.printStackTrace();
                    }
                    int value = (int) mapElement.getValue();

                    if (consolidateMapByCharity.containsKey(charity.getTitle())) {
                        consolidateMapByCharity.put(charity.getTitle(), consolidateMapByCharity.get(charity.getTitle()) + value);
                    } else {
                        consolidateMapByCharity.put(charity.getTitle(), value);
                    }
                }
                query.moneyRaisedForPersonByCharity = sortMapByPointsByUser(consolidateMapByCharity);

                Map.Entry<String,Integer> entry = query.moneyRaisedForPersonByCharity.entrySet().iterator().next();
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
        });
    }

    //sorts map with the largest number of points first
    public HashMap<Charity, Integer> sortMapByPoints(Map<Charity, Integer> pointValues) {
        // Create a list from elements of HashMap
        List<Map.Entry<Charity, Integer> > list = new LinkedList<Map.Entry<Charity, Integer> >(pointValues.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Charity, Integer>>() {
            public int compare(Map.Entry<Charity, Integer> o1, Map.Entry<Charity, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Charity, Integer> temp = new LinkedHashMap<Charity, Integer>();
        for (Map.Entry<Charity, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    //sorts map with the largest number of points first
    public HashMap<String, Integer> sortMapByPointsByUser(Map<String, Integer> pointValues) {
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
}
