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

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class WallhavenSettings extends FragmentActivity {

    private Spinner  mUpdateIntervalSpinner;
    private CheckBox mChkSafe, mChkSketchy;
    private CheckBox mChkAnime, mChkGeneral, mChkPeople;
    private CheckBox mChkUpdateOnWifiOnly;

    private List<Interval> mIntervalList = new ArrayList<Interval>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wallbase_settings);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mUpdateIntervalSpinner = (Spinner) findViewById(R.id.spUpdateInterval);

        mChkSafe = (CheckBox) findViewById(R.id.chkSafe);
        mChkSketchy = (CheckBox) findViewById(R.id.chkSketchy);
        mChkAnime = (CheckBox) findViewById(R.id.chkAnime);
        mChkGeneral = (CheckBox) findViewById(R.id.chkGeneral);
        mChkPeople = (CheckBox) findViewById(R.id.chkPeople);
        mChkUpdateOnWifiOnly = (CheckBox) findViewById(R.id.chkUpdateOnWifiOnly);


        setupIntervalSpinner();
        setCheckboxes();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        final int categoryValue = PreferenceHelper.getConfigCategories(this);

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

        mChkSketchy.setChecked(purityValue == PreferenceHelper.SKETCHY || purityValue > PreferenceHelper.SAFE );
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

        mChkAnime.setChecked(categoryValue == PreferenceHelper.MANGA || categoryValue > PreferenceHelper.GENERAL);
        mChkAnime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.setConfigCategories(buttonView.getContext(), categoryValue + PreferenceHelper.MANGA);
                }else{
                    PreferenceHelper.setConfigCategories(buttonView.getContext(), categoryValue - PreferenceHelper.MANGA);
                }
            }
        });

        mChkGeneral.setChecked(categoryValue >= PreferenceHelper.GENERAL);
        mChkGeneral.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.setConfigCategories(buttonView.getContext(), categoryValue + PreferenceHelper.GENERAL);
                }else{
                    PreferenceHelper.setConfigCategories(buttonView.getContext(), categoryValue - PreferenceHelper.GENERAL);
                }
            }
        });

        mChkPeople.setChecked(categoryValue % 2 != 0 );
        mChkPeople.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.setConfigCategories(buttonView.getContext(), categoryValue + PreferenceHelper.PEOPLE);
                }else{
                    PreferenceHelper.setConfigCategories(buttonView.getContext(), categoryValue - PreferenceHelper.PEOPLE);
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
}
