package com.optic.tribejam.providers;

import com.optic.tribejam.models.FCMBody;
import com.optic.tribejam.models.FCMResponse;
import com.optic.tribejam.retrofit.IFCMApi;
import com.optic.tribejam.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }

}
