package com.authentik.ke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.authentik.utils.SyncDbService;

public class ConnectivityReceiver extends BroadcastReceiver {
    private boolean isConnected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//            ConnectivityManager cm =
//                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//            isConnected = activeNetwork != null &&
//                    activeNetwork.isAvailable();

            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            isConnected =  (networkInfo != null && networkInfo.isConnected());

            if (isConnected) {
                Intent intent2 = new Intent(context, SyncDbService.class);
                context.startService(intent2);
            } else {
                Intent intent2 = new Intent(context, SyncDbService.class);
                context.stopService(intent2);
            }
        }
//    }

}
