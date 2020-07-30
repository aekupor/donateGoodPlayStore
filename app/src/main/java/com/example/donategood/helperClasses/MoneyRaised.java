package com.example.donategood.helperClasses;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.donategood.models.Charity;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoneyRaised {

    //determines amount of money raised for a charity
    public void findCharityMoneyRaised(final Charity charity, final TextView tvMoney, final ProgressBar pb, final Query query) {
        pb.setVisibility(View.VISIBLE);
        final Integer[] moneyRaised = {0};
        final HashMap<ParseUser, Integer> moneyRaisedByPerson = new HashMap<>();
        query.queryAllPosts(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                for (Offering offering : objects) {
                    if (offering.getCharity().getObjectId().equals(charity.getObjectId())) {
                        ArrayList<Object> boughtUsers = offering.getBoughtByArray();
                        if (boughtUsers != null && !boughtUsers.isEmpty()) {
                            moneyRaised[0] += boughtUsers.size() * offering.getPrice();

                            //fill HashMap with users
                            for (Object boughtObject : boughtUsers) {
                                ParseUser boughtUser = (ParseUser) boughtObject;
                                //add each user who bought an item for that charity
                                if (moneyRaisedByPerson.containsKey(boughtUser)) {
                                    moneyRaisedByPerson.put(boughtUser, moneyRaisedByPerson.get(boughtUser) + offering.getPrice());
                                } else {
                                    moneyRaisedByPerson.put(boughtUser, offering.getPrice());
                                }
                            }
                            //add sellers who raised money for that charity
                            if (moneyRaisedByPerson.containsKey(offering.getUser())) {
                                moneyRaisedByPerson.put(offering.getUser(), moneyRaisedByPerson.get(offering.getUser()) + offering.getPrice() * boughtUsers.size());
                            } else {
                                moneyRaisedByPerson.put(offering.getUser(), offering.getPrice());
                            }
                        }
                    }
                }
                tvMoney.setText("$" + moneyRaised[0].toString());

                //make map that has only one entry by user (add up all the prices)
                HashMap<String, Integer> consolidateMapByUser = new HashMap<>();
                for (Map.Entry mapElement : moneyRaisedByPerson.entrySet()) {
                    ParseUser key = (ParseUser) mapElement.getKey();
                    ParseUser user = null;
                    try {
                        user = key.fetchIfNeeded();
                    } catch (ParseException e2) {
                        e2.printStackTrace();
                    }
                    int value = (int)mapElement.getValue();

                    if (consolidateMapByUser.containsKey(user.getUsername())) {
                        consolidateMapByUser.put(user.getUsername(), consolidateMapByUser.get(user.getUsername()) + value);
                    } else {
                        consolidateMapByUser.put(user.getUsername(), value);
                    }
                }

                HashMap<String, Integer> sortedMap = query.sortMapByPointsByUser(consolidateMapByUser);
                query.moneyRaisedForCharityByPerson = sortedMap;
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }
}
