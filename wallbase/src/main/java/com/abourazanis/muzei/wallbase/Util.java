/*
 * Copyright 2014 Anastasios Bourazanis (a.bourazanis@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abourazanis.muzei.wallbase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class Util {
    private static final String TAG = "Wallhaven.Util";

    public static boolean isWifiConnected(Context c) {
        if (c!= null) {
            ConnectivityManager connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] connections = connManager.getAllNetworkInfo();
            int count = connections.length;
            for (int i = 0; i < count; i++)
                if (connections[i]!= null && connections[i].getType() == ConnectivityManager.TYPE_WIFI && connections[i].isConnectedOrConnecting() ||
                        connections[i]!= null &&  connections[i].getType() == ConnectivityManager.TYPE_ETHERNET && connections[i].isConnectedOrConnecting())
                    return true;
        }
        return false;
    }

    public static boolean exists(String URLName){
        try {
            HttpURLConnection.setFollowRedirects(false);
//            HttpURLConnection.setInstanceFollowRedirects(false);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            if (Build.VERSION.SDK_INT > 13) {
                con.setRequestProperty("Connection", "close");
            }
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            Log.e(TAG,e.toString());
            return false;
        }
    }
}
