package com.authentik.ke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.authentik.utils.SyncDbService;

public class ConnectivityReceiver extends BroadcastReceiver {
    private boolean isConnected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
            if (isOnline(context)) {
                Intent intent2 = new Intent(context, SyncDbService.class);
                context.startService(intent2);
//                Toast.makeText(context,"Network Connected",Toast.LENGTH_SHORT).show();
            } else {
                Intent intent2 = new Intent(context, SyncDbService.class);
                context.stopService(intent2);
//                Toast.makeText(context,"No Network Connection",Toast.LENGTH_SHORT).show();
            }

    }

    public boolean isOnline(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            isConnected = (networkInfo != null && networkInfo.isConnected());
            return isConnected;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

}
