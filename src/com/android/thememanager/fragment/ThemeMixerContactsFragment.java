package com.android.thememanager.fragment;

import android.os.Bundle;
import com.android.thememanager.Theme;

public class ThemeMixerContactsFragment extends ThemeMixerBaseFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mElementType = Theme.THEME_ELEMENT_TYPE_CONTACTS;
        super.onCreate(savedInstanceState);
    }
}
