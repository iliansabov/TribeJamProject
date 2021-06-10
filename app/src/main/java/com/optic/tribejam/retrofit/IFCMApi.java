package com.optic.tribejam.retrofit;

import com.optic.tribejam.models.FCMBody;
import com.optic.tribejam.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAeqwEO9M:APA91bE4jHmyolMqCcKuIi9sxk3edUTmFBy2A-WlynKGdaNh1Fc9TveSUtbMqd3AP2CH1zcKbLDgSsBLY4L_ET0_dg88Y7HXWkgHbOQ5r_UP3YN0w0li2GCw0kI00qK6XTUNjHQ5mtjn"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
