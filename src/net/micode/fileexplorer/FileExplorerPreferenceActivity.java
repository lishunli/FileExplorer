/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.micode.fileexplorer;

import java.io.File;

import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 *
 * @author ShunLi
 */
public class FileExplorerPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String PRIMARY_FOLDER = "pref_key_primary_folder";
	private static final String READ_ROOT = "pref_key_read_root";
	private static final String SYSTEM_SEPARATOR = File.separator;

	private EditTextPreference mEditTextPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		mEditTextPreference = (EditTextPreference) findPreference(PRIMARY_FOLDER);

		showYouMiAd();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Setup the initial values
		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

		mEditTextPreference
				.setSummary(this.getString(R.string.pref_primary_folder_summary, sharedPreferences.getString(PRIMARY_FOLDER, GlobalConsts.ROOT_PATH)));

		// Set up a listener whenever a key changes
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedpreferences, String key) {
		if (PRIMARY_FOLDER.equals(key)) {
			mEditTextPreference.setSummary(this.getString(R.string.pref_primary_folder_summary,
					sharedpreferences.getString(PRIMARY_FOLDER, GlobalConsts.ROOT_PATH)));
		}
	}

	public static String getPrimaryFolder(Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String primaryFolder = settings.getString(PRIMARY_FOLDER, context.getString(R.string.default_primary_folder, GlobalConsts.ROOT_PATH));

		if (TextUtils.isEmpty(primaryFolder)) { // setting primary folder =
												// empty("")
			primaryFolder = GlobalConsts.ROOT_PATH;
		}

		// it's remove the end char of the home folder setting when it with the
		// '/' at the end.
		// if has the backslash at end of the home folder, it's has minor bug at
		// "UpLevel" function.
		int length = primaryFolder.length();
		if (length > 1 && SYSTEM_SEPARATOR.equals(primaryFolder.substring(length - 1))) { // length
																							// =
																							// 1,
																							// ROOT_PATH
			return primaryFolder.substring(0, length - 1);
		} else {
			return primaryFolder;
		}
	}

	public static boolean isReadRoot(Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

		boolean isReadRootFromSetting = settings.getBoolean(READ_ROOT, false);
		boolean isReadRootWhenSettingPrimaryFolderWithoutSdCardPrefix = !getPrimaryFolder(context).startsWith(Util.getSdDirectory());

		return isReadRootFromSetting || isReadRootWhenSettingPrimaryFolderWithoutSdCardPrefix;
	}

	/**
	 * YouMi Ad
	 */
	public void showYouMiAd() {
		AdManager.init(this, "20b6c62b5852a27f ", "a52799ae2825031c ", 30, false);

		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.ad_youmi, null);
		// init ad view
		AdView adView = new AdView(this);
		adView.addView(view);
		adView.refreshAd();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		// set up an ad to appear suspended in the bottom right corner of the
		// screen
		params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		// add ad view into main activity
		addContentView(adView, params);
	}
}
