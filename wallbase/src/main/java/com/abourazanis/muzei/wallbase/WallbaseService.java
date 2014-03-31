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
import android.util.Log;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WallbaseService {
    private static final String TAG = "WallbaseService";


    // Resolution.
    // Use Width x Height format. 0x0 for all resolutions
    private static final String RESOLUTION = "0x0";

    // Use gteq for greater than or equal to *Recommended*
    // Use eqeq for ONLY your specified resolution
    private static final String RES_OPT = "gteq";

    // Aspect
    private static final String ASPECT = "0.00";

    // Wallpapers per scrape
    // Max is 60.
    private static final String WALLPAPERS = "30";

    //Config End

    // Generate search link from variables
    private static final String SEARCH_LINK = "http://wallbase.cc/%s"
                + "?section=wallpapers&q=&res_opt=" + RES_OPT +
                "&res=" + RESOLUTION + "&thpp=" + WALLPAPERS + "&purity=%03d"+
                "&board=%s&aspect=" + ASPECT + "&ts=%s";


    public ArrayList<Wallpaper> getWallpapers(Context context){
        ArrayList<Wallpaper> list = new ArrayList<Wallpaper>();
        String search = String.format(SEARCH_LINK,
                PreferenceHelper.getConfigSearchMode(context),
                PreferenceHelper.getConfigPurity(context),
                PreferenceHelper.getConfigBoard(context),
                PreferenceHelper.getConfigTimeSpan(context));
        Log.d(TAG,"search link is " + search);
        Connection connection = Jsoup.connect(search);

        try {
            Connection.Response response = connection.execute();
            Document document = response.parse();
            Elements wallpapers = document.select("#thumbs .thumbnail");
            for(Element wallpaper : wallpapers){
                Element image = wallpaper.select("img").first();
                if(image == null){
                    Log.w(TAG, "Selected wallpaper without image, requesting retry");
                    continue;
                }

                String thumbSrc = image.attr("data-original");
                if(StringUtil.isBlank(thumbSrc)){
                    Log.w(TAG, "Selected wallpaper with blank data original, requesting retry");
                    continue;
                }

                String id = "";
                Pattern regID = Pattern.compile("(\\d+)");
                Matcher m = regID.matcher(thumbSrc);
                if (m.find()) {
                    id = m.group(0);
                }

                String tagString = wallpaper.attr("data-tags");
                if(!StringUtil.isBlank(tagString)){
                    tagString = tagString.replaceAll("\\|(\\d+)*"," ");
                }

                String imgdir = "";
                if(thumbSrc.contains("high-resolution")){
                    imgdir = "high-resolution";
                }

                if(thumbSrc.contains("manga-anime")){
                    imgdir = "mange-anime";
                }

                if(thumbSrc.contains("rozne")){
                    imgdir = "rozne";
                }

                String imageSource = "http://wallpapers.wallbase.cc/" + imgdir + "/wallpaper-" + id + ".jpg";
                Wallpaper wall = new Wallpaper();
                wall.id = Integer.parseInt(id);
                wall.tags = tagString;
                wall.url = imageSource;

                list.add(wall);
            }
        } catch (IOException e) {
            Log.e(TAG,e.toString());
            return null;
        }

        return list;
    }

    public class Wallpaper {
        int id;
        String url;
        String tags;

    }
}
