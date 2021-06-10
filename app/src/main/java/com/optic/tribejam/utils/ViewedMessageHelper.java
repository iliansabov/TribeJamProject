package com.optic.tribejam.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.optic.tribejam.providers.AuthProvider;
import com.optic.tribejam.providers.UserProvider;

import java.util.List;

//Metodo de espeficicacion del estado tanto en backgroud como dentro de la app
public class ViewedMessageHelper {

    public static void updateOnline(boolean status, final Context context) {
        UserProvider usersProvider = new UserProvider();
        AuthProvider authProvider = new AuthProvider();
        if (authProvider.getUid() != null) {
            if (isApplicationSentToBackground(context)) {
                usersProvider.updateOnline(authProvider.getUid(), status);
            }
            else if (status){
                usersProvider.updateOnline(authProvider.getUid(), status);
            }
        }
    }

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

}

