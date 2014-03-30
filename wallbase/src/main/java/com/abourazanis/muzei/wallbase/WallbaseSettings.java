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

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WallbaseSettings extends Activity {

    private Spinner mTimeSpanSpinner, mUpdateIntervalSpinner;
    private CheckBox mChkSafe, mChkSketchy, mChkNSFW;
    private CheckBox mChkAnime, mChkGeneral, mChkHiRes;
    private RadioButton mRdTopList, mRdRandom;
    private RadioGroup mRdGSearch;
    private CheckBox mChkUpdateOnWifiOnly;

    private List<Interval> mIntervalList = new ArrayList<Interval>();
    private List<TimeSpan> mTimeSpanList = new ArrayList<TimeSpan>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wallbase_settings);

        mTimeSpanSpinner = (Spinner) findViewById(R.id.spTimeSpan);
        mUpdateIntervalSpinner = (Spinner) findViewById(R.id.spUpdateInterval);

        mChkSafe = (CheckBox) findViewById(R.id.chkSafe);
        mChkSketchy = (CheckBox) findViewById(R.id.chkSketchy);
        mChkNSFW = (CheckBox) findViewById(R.id.chkNSFW);
        mChkAnime = (CheckBox) findViewById(R.id.chkAnime);
        mChkGeneral = (CheckBox) findViewById(R.id.chkGeneral);
        mChkHiRes = (CheckBox) findViewById(R.id.chkHR);
        mChkUpdateOnWifiOnly = (CheckBox) findViewById(R.id.chkUpdateOnWifiOnly);

        mRdTopList = (RadioButton)findViewById(R.id.rdToplist);
        mRdRandom = (RadioButton)findViewById(R.id.rdRandom);
        mRdGSearch = (RadioGroup)findViewById(R.id.rdgSearchModes);

        setupIntervalSpinner();
        setupTimeSpanSpinner();
        setCheckboxes();

        setRadioButtons();
    }

    private void setupTimeSpanSpinner() {
        mTimeSpanList.clear();
        mTimeSpanList.add(new TimeSpan(R.string.alltime, PreferenceHelper.ALLTIME));
        mTimeSpanList.add(new TimeSpan(R.string.threemonths, PreferenceHelper.THREE_MONTHS));
        mTimeSpanList.add(new TimeSpan(R.string.twomonths, PreferenceHelper.TWO_MONTHS ));
        mTimeSpanList.add(new TimeSpan(R.string.onemonth, PreferenceHelper.ONE_MONTH));
        mTimeSpanList.add(new TimeSpan(R.string.twoweeks, PreferenceHelper.TWO_WEEKS));
        mTimeSpanList.add(new TimeSpan(R.string.oneweek, PreferenceHelper.ONE_WEEK));
        mTimeSpanList.add(new TimeSpan(R.string.threedays, PreferenceHelper.THREE_DAYS));
        mTimeSpanList.add(new TimeSpan(R.string.oneday, PreferenceHelper.ONE_DAY));

        mTimeSpanSpinner.setAdapter(new ArrayAdapter<TimeSpan>(this, android.R.layout.simple_list_item_1, mTimeSpanList));
        mTimeSpanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PreferenceHelper.setConfigTimeSpan(view.getContext(), mTimeSpanList.get(position).getTime());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        String preference = PreferenceHelper.getConfigTimeSpan(this);

        for (int i = 0; i < mTimeSpanList.size(); i++)
            if (preference.equals(mTimeSpanList.get(i).getTime())) {
                mTimeSpanSpinner.setSelection(i, true);
            }
    }

    private void setupIntervalSpinner() {
        mIntervalList.clear();
        mIntervalList.add(new Interval(R.string.threeHours, 3 * DateUtils.HOUR_IN_MILLIS));
        mIntervalList.add(new Interval(R.string.sixHours, 6* DateUtils.HOUR_IN_MILLIS));
        mIntervalList.add(new Interval(R.string.nineHours, 9 * DateUtils.HOUR_IN_MILLIS ));
        mIntervalList.add(new Interval(R.string.twelveHours, 12 * DateUtils.HOUR_IN_MILLIS));
        mIntervalList.add(new Interval(R.string.day, DateUtils.DAY_IN_MILLIS));

        mUpdateIntervalSpinner.setAdapter(new ArrayAdapter<Interval>(this, android.R.layout.simple_list_item_1, mIntervalList));
        mUpdateIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PreferenceHelper.setConfigFreq(view.getContext(), (int)mIntervalList.get(position).getTimeMillis());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        int preference = PreferenceHelper.getConfigFreq(this);

        for (int i = 0; i < mIntervalList.size(); i++)
            if (preference == mIntervalList.get(i).getTimeMillis()) {
                mUpdateIntervalSpinner.setSelection(i, true);
            }
    }

    private void setCheckboxes(){
        final int purityValue = PreferenceHelper.getConfigPurity(this);
        final String boardValue = PreferenceHelper.getConfigBoard(this);

        mChkSafe.setChecked(purityValue >= PreferenceHelper.SAFE);
        mChkSafe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.setConfigPurity(buttonView.getContext(), purityValue + PreferenceHelper.SAFE);
                }else{
                    PreferenceHelper.setConfigPurity(buttonView.getContext(), purityValue - PreferenceHelper.SAFE);
                }
            }
        });

        mChkSketchy.setChecked(Arrays.asList(PreferenceHelper.SKETCHY, PreferenceHelper.SKETCHY+PreferenceHelper.SAFE,PreferenceHelper.SKETCHY+PreferenceHelper.NSFW, PreferenceHelper.SKETCHY+PreferenceHelper.SAFE+PreferenceHelper.NSFW).contains(purityValue));
        mChkSketchy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.setConfigPurity(buttonView.getContext(), purityValue + PreferenceHelper.SKETCHY);
                }else{
                    PreferenceHelper.setConfigPurity(buttonView.getContext(), purityValue - PreferenceHelper.SKETCHY);
                }
            }
        });

        mChkNSFW.setChecked(Arrays.asList(PreferenceHelper.NSFW, PreferenceHelper.NSFW + PreferenceHelper.SAFE, PreferenceHelper.NSFW + PreferenceHelper.SKETCHY, PreferenceHelper.SKETCHY + PreferenceHelper.SAFE + PreferenceHelper.NSFW).contains(purityValue));
        mChkNSFW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.setConfigPurity(buttonView.getContext(), purityValue + PreferenceHelper.NSFW);
                }else{
                    PreferenceHelper.setConfigPurity(buttonView.getContext(), purityValue - PreferenceHelper.NSFW);
                }
            }
        });

        mChkAnime.setChecked(boardValue.contains(PreferenceHelper.MANGA));
        mChkAnime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.setConfigBoard(buttonView.getContext(), boardValue + PreferenceHelper.MANGA);
                }else{
                    PreferenceHelper.setConfigBoard(buttonView.getContext(), boardValue.replace(PreferenceHelper.MANGA,""));
                }
            }
        });

        mChkGeneral.setChecked(boardValue.contains(PreferenceHelper.GENERAL));
        mChkGeneral.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.setConfigBoard(buttonView.getContext(), boardValue + PreferenceHelper.GENERAL);
                }else{
                    PreferenceHelper.setConfigBoard(buttonView.getContext(), boardValue.replace(PreferenceHelper.GENERAL,""));
                }
            }
        });

        mChkHiRes.setChecked(boardValue.contains(PreferenceHelper.HIRES));
        mChkHiRes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.setConfigBoard(buttonView.getContext(), boardValue + PreferenceHelper.HIRES);
                }else{
                    PreferenceHelper.setConfigBoard(buttonView.getContext(), boardValue.replace(PreferenceHelper.HIRES,""));
                }
            }
        });


        mChkUpdateOnWifiOnly.setChecked(PreferenceHelper.getConfigWifiOnly(this));
        mChkUpdateOnWifiOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceHelper.setConfigWifiOnly(buttonView.getContext(), isChecked);
            }
        });

    }

    private void setRadioButtons(){
        mRdGSearch.check(PreferenceHelper.getConfigSearchMode(this) == PreferenceHelper.TOPLIST?mRdTopList.getId():mRdRandom.getId());

        mRdGSearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == mRdRandom.getId()){
                    PreferenceHelper.setConfigSearchMode(radioGroup.getContext(),PreferenceHelper.RANDOM);
                }else{
                    PreferenceHelper.setConfigSearchMode(radioGroup.getContext(),PreferenceHelper.TOPLIST);
                }
            }
        });
    }
    private class Interval {

        private int name;
        private long timeMillis;

        public Interval(int name, long timeMillis) {
            this.name = name;
            this.timeMillis = timeMillis;
        }

        public String getName() {
            return getString(name);
        }

        public long getTimeMillis() {
            return timeMillis;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    private class TimeSpan {

        private int name;
        private String time;

        public TimeSpan(int name, String time) {
            this.name = name;
            this.time = time;
        }

        public String getName() {
            return getString(name);
        }

        public String getTime() {
            return time;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
