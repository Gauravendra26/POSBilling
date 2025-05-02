package de.syss.MifareClassicTool.Printer;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

import androidx.appcompat.app.ActionBar;

import de.syss.MifareClassicTool.R;

public class SettingsFragment extends PreferenceFragment {

    private static final String PRINT_KEY = "demo_print_key";
    private static final String CARD_KEY = "demo_card_key";

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        addPreferencesFromResource(R.xml.pref_demo_all);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        findPreference(PRINT_KEY).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                switchFragment(SettingsFragment.this, new PrintFragment(), mActivity.getString(R.string.pref_print));
                return true;
            }
        });

        findPreference(CARD_KEY).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                switchFragment(SettingsFragment.this, new CardFragment(), mActivity.getString(R.string.pref_card));
                return true;
            }
        });
    }

    private void switchFragment(Fragment from, Fragment to, String title) {
        PosPrinterMainActivity posPrinterMainActivity = (PosPrinterMainActivity) mActivity;
        ActionBar actionBar = posPrinterMainActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (!to.isAdded()) {
            getFragmentManager().beginTransaction().addToBackStack(null).hide(from).add(R.id.frame_container, to).commit();
        } else {
            getFragmentManager().beginTransaction().addToBackStack(null).hide(from).show(to).commit();
        }
    }
}
