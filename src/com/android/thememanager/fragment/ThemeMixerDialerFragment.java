package com.android.thememanager.fragment;

import android.os.Bundle;
import com.android.thememanager.Theme;

public class ThemeMixerDialerFragment extends ThemeMixerBaseFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mElementType = Theme.THEME_ELEMENT_TYPE_DIALER;
        super.onCreate(savedInstanceState);
    }
}
