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

import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;

import java.util.ArrayList;
import java.util.Random;

public class WallbaseArtSource extends RemoteMuzeiArtSource {
    private static final String TAG = "Wallbase";
    private static final String SOURCE_NAME = "WallbaseArtSource";

    private static final int ROTATE_TIME_MILLIS = (int)(3 * DateUtils.HOUR_IN_MILLIS); // rotate every 3 hours


    public WallbaseArtSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        String currentToken = (getCurrentArtwork() != null) ? getCurrentArtwork().getToken() : null;

        WallbaseService service = new WallbaseService();
        ArrayList<WallbaseService.Wallpaper> walllist = service.getWallpapers();

        if (walllist == null) {
            Log.w(TAG, "No wallpaper list returned from API.");
            throw new RetryException();
        }

        if (walllist.isEmpty()) {
            Log.w(TAG, "No wallpapers returned from API.");
            reschedule();
            return;
        }

        Random random = new Random();
        WallbaseService.Wallpaper wallpaper;
        String token;
        int id;
        while (true) {
            wallpaper = walllist.get(random.nextInt(walllist.size()));
            id = wallpaper.id;
            token = Integer.toString(id);
            if (!token.equals(currentToken)) {
                break;
            }
        }

        if(BuildConfig.DEBUG) {
            Log.d(TAG, "Wallpaper URL: " + wallpaper.url);
        }

        publishArtwork(new Artwork.Builder()
                .title(getString(R.string.wallpaper_title, id))
                .byline(wallpaper.tags)
                .imageUri(Uri.parse(wallpaper.url))
                .token(token)
                .viewIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(wallpaper.url)))
                .build());

        reschedule();

    }

    private void reschedule()
    {
        scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);

    }
}
