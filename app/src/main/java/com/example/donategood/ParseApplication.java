package com.example.donategood;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.donategood.models.Charity;
import com.example.donategood.models.Comment;
import com.example.donategood.models.Message;
import com.example.donategood.models.Notification;
import com.example.donategood.models.Offering;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        // Register parse models
        ParseObject.registerSubclass(Offering.class);
        ParseObject.registerSubclass(Charity.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Notification.class);
        ParseObject.registerSubclass(Message.class);

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("donate-good") // should correspond to APP_ID env variable
                .clientKey("donateGoodMasterKeySecret")  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder)
                .server("https://donate-good.herokuapp.com/parse/").build());
    }
}
