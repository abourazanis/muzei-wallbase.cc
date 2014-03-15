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

    //Config

    // Purity mode
    // Safe, Sketchy and NSFW. 1 = on, 0 = off. e.g Safe and NSFW is 101.
    private static final String PURITY = "100";

    //Board
    // 1 = Manga/Anime, 2 = Wallpaper/General, 3 = High Res.
    // You can combine them
    private static final String BOARD = "123";

    // Resolution.
    // Use Width x Height format. 0x0 for all resolutions
    private static final String RESOLUTION = "0x0";

    // Resolution search mode.
    // Use gteq for greater than or equal to *Recommended*
    // Use eqeq for ONLY your specified resolution
    private static final String RES_OPT = "gteq";

    // Aspect
    private static final String ASPECT = "0.00";

    // Wallpapers per scrape
    // Max is 60.
    private static final String WALLPAPERS = "30";

    //Timespan.
    // 1 = All time, 3m = 3 months, 2m = 2 months, 1m = 1 month, 2w = 2 weeks, 1w = 1 week, 3d = 3 days,
    // 1d = 1 day.
    private static final String TIMESPAN = "1w";

    // Mode
    // random for random wallpapers, toplist for most popular.
    private static final String SEARCHMODE = "toplist";

    //Config End

    // Generate search link from variables
    private static final String SEARCH_LINK = "http://wallbase.cc/"
                + SEARCHMODE + "?section=wallpapers&q=&res_opt=" + RES_OPT +
                "&res=" + RESOLUTION + "&thpp=" + WALLPAPERS + "&purity=" + PURITY +
                "&board=" + BOARD + "&aspect=" + ASPECT + "&ts=" + TIMESPAN;


    public ArrayList<Wallpaper> getWallpapers(){
        ArrayList<Wallpaper> list = new ArrayList<Wallpaper>();
        Connection connection = Jsoup.connect(SEARCH_LINK);

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
