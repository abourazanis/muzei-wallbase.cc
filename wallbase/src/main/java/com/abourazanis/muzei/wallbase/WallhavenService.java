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


public class WallhavenService {
    private static final String TAG = "WallhavenService";


    // Resolution.
    // Use Width x Height format. Empty for all resolutions
    private static final String RESOLUTION = "";

    // Ratio, empty for all
    private static final String RATIO = "";

    // Wallpapers per scrape
    // Multiply of 64.
    private static final String WALLPAPERS = "64";

    private static final String SORTING = "random";

    private static final String ORDER = "desc";

    //Config End

    // Generate search link from variables
    private static final String SEARCH_LINK = "http://alpha.wallhaven.cc/wallpaper/search?page=1"
            + "&categories=%03d&purity=%03d&resolutions=" + RESOLUTION + "&ratios="
            + RATIO + "&sorting=" + SORTING + "&order=" + ORDER;


    public ArrayList<Wallpaper> getWallpapers(Context context){
        ArrayList<Wallpaper> list = new ArrayList<Wallpaper>();
        String search = String.format(SEARCH_LINK,
                PreferenceHelper.getConfigCategories(context),
                PreferenceHelper.getConfigPurity(context));
//                PreferenceHelper.getConfigSearchMode(context),
//                PreferenceHelper.getConfigTimeSpan(context));
                Log.d(TAG,"search link is " + search);
        Connection connection = Jsoup.connect(search);

        try {
            Connection.Response response = connection.execute();
            Document document = response.parse();
            Elements wallpapers = document.select("#thumbs .thumb");
            for(Element wallpaper : wallpapers){
                Element image = wallpaper.select("a.preview").first();
                if(image == null){
                    Log.w(TAG, "Selected wallpaper without image, requesting retry");
                    continue;
                }

                String thumbSrc = image.attr("href");
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

                Element link = wallpaper.select(".thumb-tags li a").first();
                String tagString = "";
                if (link != null) {
                    tagString = link.text();
                    if (!StringUtil.isBlank(tagString)) {
                        tagString = tagString.replaceAll("\\|(\\d+)*", " ");
                    }
                }

                String imageSource = "http://alpha.wallhaven.cc/wallpapers/full/wallhaven-" + id + ".jpg";
                if (!Util.exists(imageSource)){
                    imageSource = imageSource.replace(".jpg",".png");
                }

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
