/*
 * Copyright (C) 2013 The ChameleonOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.thememanager.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.thememanager.R;
import com.android.thememanager.fragment.BackupRestoreFragment;
import com.android.thememanager.fragment.GetThemesFragment;
import com.android.thememanager.fragment.ThemeChooserFragment;
import com.android.thememanager.fragment.ThemeMixerBootAniFragment;
import com.android.thememanager.fragment.ThemeMixerContactsFragment;
import com.android.thememanager.fragment.ThemeMixerDialerFragment;
import com.android.thememanager.fragment.ThemeMixerFontsFragment;
import com.android.thememanager.fragment.ThemeMixerFrameworkFragment;
import com.android.thememanager.fragment.ThemeMixerIconsFragment;
import com.android.thememanager.fragment.ThemeMixerLockWallpaperFragment;
import com.android.thememanager.fragment.ThemeMixerMmsFragment;
import com.android.thememanager.fragment.ThemeMixerRingtonesFragment;
import com.android.thememanager.fragment.ThemeMixerSystemUIFragment;
import com.android.thememanager.fragment.ThemeMixerWallpaperFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Clark Scheff
 *
 */
public class ThemeManagerActivity extends Activity {
	private ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private CharSequence mTitle = "";
	private ActionBarDrawerToggle mDrawerToggle;
	private List<NavigationDrawerItem> mNavItems;
    private int mCurrentFragment = -1;
	
	enum Type { Header, Fragment }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.theme_manager_activity);

		//mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		setupNavItems();
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setupNavigationDrawer();

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_run", true)) {
            mDrawerLayout.openDrawer(mDrawerList);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_run", false).commit();
        }
	}
	
	private void setupNavItems() {
		mNavItems = new ArrayList<NavigationDrawerItem>();
		mNavItems.add(new NavigationDrawerItem(ThemeChooserFragment.class.getName(),
                getString(R.string.installed_themes), Type.Fragment, R.drawable.ic_themes));
        mNavItems.add(new NavigationDrawerItem(BackupRestoreFragment.class.getName(),
                getString(R.string.backup_restore), Type.Fragment, R.drawable.ic_menu_save));
        mNavItems.add(new NavigationDrawerItem(GetThemesFragment.class.getName(),
                getString(R.string.get_themes), Type.Fragment, R.drawable.ic_get_themes));
        mNavItems.add(new NavigationDrawerItem(null,
                getString(R.string.theme_mixer), Type.Header, 0));
        mNavItems.add(new NavigationDrawerItem(ThemeMixerIconsFragment.class.getName(),
                getString(R.string.mixer_icons_label), Type.Fragment, R.drawable.ic_icons));
        mNavItems.add(new NavigationDrawerItem(ThemeMixerSystemUIFragment.class.getName(),
                getString(R.string.mixer_systemui_label), Type.Fragment, R.drawable.ic_systemui));
        mNavItems.add(new NavigationDrawerItem(ThemeMixerFrameworkFragment.class.getName(),
                getString(R.string.mixer_framework_label), Type.Fragment, R.drawable.ic_framework));
        mNavItems.add(new NavigationDrawerItem(ThemeMixerContactsFragment.class.getName(),
                getString(R.string.mixer_contacts_label), Type.Fragment, R.drawable.ic_contacts));
        mNavItems.add(new NavigationDrawerItem(ThemeMixerDialerFragment.class.getName(),
                getString(R.string.mixer_dialer_label), Type.Fragment, R.drawable.ic_dialer));
        mNavItems.add(new NavigationDrawerItem(ThemeMixerMmsFragment.class.getName(),
                getString(R.string.mixer_mms_label), Type.Fragment, R.drawable.ic_mms));
        mNavItems.add(new NavigationDrawerItem(ThemeMixerWallpaperFragment.class.getName(),
                getString(R.string.mixer_walllpaper_label), Type.Fragment, R.drawable.ic_wallpaper));
        mNavItems.add(new NavigationDrawerItem(ThemeMixerLockWallpaperFragment.class.getName(),
                getString(R.string.mixer_lockscreen_wallpaper_label),
                Type.Fragment, R.drawable.ic_lock_wallpaper));
        mNavItems.add(new NavigationDrawerItem(ThemeMixerRingtonesFragment.class.getName(),
                getString(R.string.mixer_ringtones_label), Type.Fragment, R.drawable.ic_ringtones));
        mNavItems.add(new NavigationDrawerItem(ThemeMixerBootAniFragment.class.getName(),
                getString(R.string.mixer_bootanimation_label), Type.Fragment, R.drawable.ic_bootani));
        mNavItems.add(new NavigationDrawerItem(ThemeMixerFontsFragment.class.getName(),
                getString(R.string.mixer_font_label), Type.Fragment, R.drawable.ic_fonts));

		// Set the adapter for the list view
		mDrawerList.setAdapter(new NavigationAdapter());
		selectItemByFragment(ThemeChooserFragment.class.getName());
	}

    private void setupNavigationDrawer() {
        TypedArray a = getTheme().obtainStyledAttributes(
                new int[] {
                        com.android.internal.R.attr.ic_drawer });
        int drawerIconRes = a.getResourceId(0, 0);
        a.recycle();
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                drawerIconRes,
                0,
                0
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

    }
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
	    // Create a new fragment and specify the planet to show based on position
        if (mCurrentFragment != position) {
            Fragment fragment = null;
            try {
                fragment = (Fragment)(Class.forName(mNavItems.get(position).fragment)).newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {
            }

            if (fragment != null) {
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
                setTitle(mNavItems.get(position).title);
                mCurrentFragment = position;
            }
        }

	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(position, true);
	    mDrawerLayout.closeDrawer(mDrawerList);
	}

    private void selectItemByFragment(String className) {
        final int count = mNavItems.size();
        for (int i = 0; i < count; i++ ) {
            if (className.equals(mNavItems.get(i).fragment)) {
                selectItem(i);
                break;
            }
        }
    }

	@Override
	public void setTitle(CharSequence title) {
	    mTitle = title;
	    getActionBar().setTitle(mTitle);
	}
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private class NavigationDrawerItem {
		Type type;
		String fragment;
		String title;
		int iconId;
		
		public NavigationDrawerItem(String fragment, String title, Type type) {
			this(fragment, title, type, 0);
		}

		public NavigationDrawerItem(String fragment, String title, Type type, int iconId) {
			this.fragment = fragment;
			this.title = title;
			this.type = type;
			this.iconId = iconId;
		}
	}
	
	private class NavigationAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mNavItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mNavItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) == Type.Fragment.ordinal();
        }

        @Override
        public int getItemViewType(int position) {
            return mNavItems.get(position).type.ordinal();
        }

        @Override
		public View getView(int position, View convertView, ViewGroup parent) {
            final int type = getItemViewType(position);
            if (type == Type.Header.ordinal()) {
                return createOrUpdateHeaderView(position, convertView);
            } else {
                return createOrUpdateItemView(position, convertView);
            }
		}

        private View createOrUpdateHeaderView(int position, View convertView) {
            if (convertView == null || convertView.findViewById(R.id.icon) != null)
                convertView = getLayoutInflater().inflate(R.layout.navigation_list_header, null);

            NavigationDrawerItem item = mNavItems.get(position);

            TextView tv = (TextView) convertView.findViewById(R.id.title);
            if (item.title != null) {
                tv.setText(item.title);
                tv.setVisibility(View.VISIBLE);
            } else {
                tv.setVisibility(View.GONE);
            }
            return convertView;
        }

        private View createOrUpdateItemView(int position, View convertView) {
            if (convertView == null || convertView.findViewById(R.id.icon) == null)
                convertView = getLayoutInflater().inflate(R.layout.navigation_list_item, null);

            NavigationDrawerItem item = mNavItems.get(position);

            ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
            iv.setImageResource(item.iconId);

            TextView tv = (TextView) convertView.findViewById(R.id.title);
            tv.setText(item.title);
            return convertView;
        }
	}
}
