# APP_NAME_HERE

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
A marketplace where all proceeds are donated to charities. 

Sellers will offer their services (ACT tutoring, teaching a language, etc.) or products (custom art, homemade jewelry, etc.) for a price and will either specify which charity all proceeds will be directed to or will leave it up to the choice of the buyer.

Buyers will buy services or products from the sellers and all proceed money will be directed to a charity. They will receive something in return for their donation.

### App Evaluation
- **Category:** Marketplace
- **Mobile:** Mobile will utilize infinite scroll, card view, and tap to see more details. App will be available both on mobile and desktop. 
- **Story:** Allows buyers to make charitable donations while receiving something in return. Allows sellers offering their services or products to expand their reach and raise money for charities.
- **Market:** Everyone has services that they can offer (tutoring, language help, career advice, etc.). With a wide range of offerings and charities to donate to, all buyers can find a product they enjoy with a charity that aligns to their preference.
- **Habit:** Offerings will be updated regularly and buyers will be able to infintely scroll through all options. Sellers will need to check frequently in order to see who has bought their offering and chat with buyers.
- **Scope:** This will start as a simple place to view offerings and buy them. It will evolve by being able to view offerings by type and charity as well as see recommend offerings. It will also include chat and potentially a score for each user based on their activity on the app and charitable donations.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Seller can post an offering
    * Can upload picture
    * Can include description
    * Can include price and charity to be donated to
    * Can see when their offering has been bought
* User can login
* User can create new account
* Buyer can view a feed of offerings
* Buyer can search for offerings
* Buyer can select offering to see more details and to purchase

**Optional Nice-to-have Stories**

* Buyers can search for offerings by type of offering or by charity
* Buyers can see reccomended items that are similar to their preferences
* Buyers and Sellers can chat in a chat feature
* Buyers can leave reviews for sellers
* Sellers can see all their products they are selling in a dashboard with various analytics
* Users have a profile page that lists the history of their buying/selling as well as reviews
* Sellers will get notifications when a buyer is interested in their product
* Payment can occur through app (PayPal API?)
* Sellers can add posting to FB Marketplace through the app
* Every user will have a score based on their activity on the app and charitable donations

### 2. Screen Archetypes

* Login Screen
   * User can login
* Registration Screen
   * User can create new account
* Stream
    * Buyer can view a feed of offerings
* Detail
    * Buyer can select offering to see more details and to purchase
* Search
    * Buyer can search for offerings
* Creation
    * Seller can post an offering
* Profile
    * Users have a profile page that lists the history of their buying/selling as well as reviews (stretch feature)

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home feed
* Search offering
* Post an offering
* Profile screen

**Flow Navigation** (Screen to Screen)

* Login screen
    --> home page when completed
* Registration Screen
    --> home page when completed
* Stream/home page
    --> detail page when offering clicked
* Detail page
    --> see profile page of seller
    --> finalize order/payment page (stretch goal)
* Search page
    --> detail page when offering clicked
* Creation page
    --> home page when completed
    --> this will need multiple pages to get all data needed for offering
* Profile page (stretch feature)
    --> detail page when offering clicked

## Wireframes
Wireframes can be viewed here: https://drive.google.com/file/d/146l-aN6o9uCAqbVUuSgk3E6lM08SJO6b/view?usp=sharing

## Schema 
### Models

#### Offering
| Property | Type | Description |
| -------- | -------- | -------- |
| objectId     | String     | unique id for the user offering (default field)     |
| author |  pointer to User |pointer to the user who is selling the offering 
| image |File | image that user uploads to describe the offering
| description | String |  description of offering by author
| charity | pointer to Charity | name of charity that cost will be donated to
| price | Number | price of item 
| donationPrice | Number | how much of the price will be donated to charity (i.e. some sellers may want to only donate profits in order to cover their costs)
| createdAt | DateTime | date when offering is created (default field)
| likesCount | Number | number of likes for the post 
| isBought | bool | false if offering has not been bought yet (only true when leftQuantity == 0)
| boughtBy | pointer to User | user who bought the item (will be null until someone buys the offering)
| totalQuantity | Number | total number of availability of offering (i.e. 15 earring)
| leftQuantity | Number | number of offering left (i.e. 5 earrings left)

#### User
| Property | Type | Description |
| -------- | -------- | -------- |
| objectId     | String     | unique id for the user | (default field)     |
| profileImage |File | image that user posts as a profile image
| isSeller | bool |  true if the user is selling/has sold any offerings
| isBuyer | bool | true if the user has bought any offerings (both isSeller and isBuyer can be true)
| isCharity | bool | if true, then user can edit the charity profile page; only one user can be linked to a charity
| numOfferingsBought | Number | number of offerings the user has bought
| numOfferingsSelling | Number | number of offerings the user has sold/is selling
| totalMoneyRaised | Number | amount of money they have donated (either from buying or selling)
| description | String | profile description

#### Charity

| Property | Type | Description |
| -------- | -------- | -------- |
| objectId     | String     | unique id for the charity (default field)     |
| profileImage |File | image of charity logo
| description | String |  description of charity
| type | String | type of charity (i.e. environmental, education, etc.)
| numMoneyRaised | Number | amount of money raised for charity
| website | String | url pointing to charity website
| owner | pointer to User | pointer to the user who can edit the charity profile page

#### Comment
| Property | Type | Description |
| -------- | -------- | -------- |
| objectId     | String     | unique id for the user post (default field)     |
| author |pointer to User | pointing to the author of the comment
| text | String |  text of the comment
| rating | Number | rating of the offering
| forOffering | pointer to Offering | pointer to the offering that the comment is tied to


### Networking
- Registration screen
    - (Create/POST) Create new user credentials
- Home page
    - (Read/GET) Query all offerings that have not been sold

```
     protected void queryPosts(Integer page) {
        Integer displayLimit = 20;
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(displayLimit);
        query.whereEqualTo(Post.KEY_IS_BOUGHT, false);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                //do something with posts
            }
        });
    }
```
- Profile page
    - (Read/GET) Query logged in user object
    - (Update/PUT) Update user profile image
    - (Read/GET) Query all posts where user is author

```
     protected void queryPosts(Integer page) {
        Integer displayLimit = 20;
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(displayLimit);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());            query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                //do something with posts
            }
        });
    }
```
- Detail offering page
    - (Create/POST) Create a new like on a post
    - (Delete) Delete existing like
    - (Read/GET) Query all comments related to that post
    ```
        protected void queryComments() {
        Integer displayLimit = 20;
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.setLimit(displayLimit);

        //since comparing a pointer, need to create pointer to compare to
        ParseObject obj = ParseObject.createWithoutData("Post", postId);
        query.whereEqualTo("forPost", obj);

        query.addDescendingOrder(Comment.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }
                //do something with comments
            }
        });
    }
    ```
    - (Create/POST) Create a new comment on a post
    - (Delete) Delete existing comment
- Creation page
    - (Create/POST) Create a new offering object
- Search page
    - (Read/GET) Query all offerings related to search terms
