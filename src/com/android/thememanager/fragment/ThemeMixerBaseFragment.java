/*
 * Copyright (C) 2013 The ChameleonOS Project
 *
 * Licensed under the GNU GPLv2 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.thememanager.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.IThemeManagerService;
import android.os.Bundle;
import android.os.ServiceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.thememanager.ElementPreviewManager;
import com.android.thememanager.Globals;
import com.android.thememanager.PreviewHolder;
import com.android.thememanager.R;
import com.android.thememanager.SimpleDialogs;
import com.android.thememanager.Theme;
import com.android.thememanager.ThemeUtils;
import com.android.thememanager.ThemesDataSource;
import com.android.thememanager.activity.ThemeBootanimationDetailActivity;
import com.android.thememanager.activity.ThemeElementDetailActivity;
import com.android.thememanager.activity.ThemeRingtoneDetailActivity;
import com.android.thememanager.widget.FlipImageView;

import java.util.List;

public class ThemeMixerBaseFragment extends Fragment {
    private static final String TAG = "ThemeManager";
    private static final String THEMES_PATH = Globals.DEFAULT_THEME_PATH;

    private GridView mGridView = null;
    private List<Theme> mThemeList = null;
    private PreviewAdapter mAdapter = null;
    protected int mElementType = Theme.THEME_ELEMENT_TYPE_ICONS;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mThemeList = themeList(mElementType);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_theme_chooser, null);

        getActivity().setTitle(Theme.sElementLabels[mElementType]);

        mGridView = (GridView) v.findViewById(R.id.coverflow);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                if (mElementType == Theme.THEME_ELEMENT_TYPE_BOOTANIMATION) {
                    intent = new Intent(getActivity(), ThemeBootanimationDetailActivity.class);
                } else if (mElementType == Theme.THEME_ELEMENT_TYPE_RINGTONES) {
                    intent = new Intent(getActivity(), ThemeRingtoneDetailActivity.class);
                } else {
                    intent = new Intent(getActivity(), ThemeElementDetailActivity.class);
                }
                intent.putExtra("type", mElementType);
                intent.putExtra("theme_id", mThemeList.get(i).getId());
                startActivity(intent);
            }
        });
        mAdapter = new PreviewAdapter(getActivity());
        mGridView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.destroy();
        mAdapter = null;
        mGridView = null;
        System.gc();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private List<Theme> themeList(int elementType) {
        ThemesDataSource dataSource = new ThemesDataSource(getActivity());
        dataSource.open();
        List<Theme> list = null;
        switch(elementType) {
            case Theme.THEME_ELEMENT_TYPE_ICONS:
                list = dataSource.getIconThemes();
                break;
            case Theme.THEME_ELEMENT_TYPE_WALLPAPER:
                list = dataSource.getWallpaperThemes();
                break;
            case Theme.THEME_ELEMENT_TYPE_LOCK_WALLPAPER:
                list = dataSource.getLockscreenWallpaperThemes();
                break;
            case Theme.THEME_ELEMENT_TYPE_SYSTEMUI:
                list = dataSource.getSystemUIThemes();
                break;
            case Theme.THEME_ELEMENT_TYPE_FRAMEWORK:
                list = dataSource.getFrameworkThemes();
                break;
            case Theme.THEME_ELEMENT_TYPE_CONTACTS:
                list = dataSource.getContactsThemes();
                break;
            case Theme.THEME_ELEMENT_TYPE_DIALER:
                list = dataSource.getDialerThemes();
                break;
            case Theme.THEME_ELEMENT_TYPE_RINGTONES:
                list = dataSource.getRingtoneThemes();
                break;
            case Theme.THEME_ELEMENT_TYPE_BOOTANIMATION:
                list = dataSource.getBootanimationThemes();
                break;
            case Theme.THEME_ELEMENT_TYPE_MMS:
                list = dataSource.getMmsThemes();
                break;
            case Theme.THEME_ELEMENT_TYPE_FONT:
                list = dataSource.getFontThemes();
                break;
        }

        dataSource.close();
        return list;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_theme_element, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_reset:
                String installedThemeDir = "/data/system/theme/";
                try {
                    final IThemeManagerService ts = 
                            IThemeManagerService.Stub.asInterface(ServiceManager.getService("ThemeService"));
                    switch (mElementType) {
                        case Theme.THEME_ELEMENT_TYPE_ICONS:
                            ts.resetThemeIcons();
                            break;
                        case Theme.THEME_ELEMENT_TYPE_WALLPAPER:
                            ts.resetThemeWallpaper();
                            break;
                        case Theme.THEME_ELEMENT_TYPE_LOCK_WALLPAPER:
                            ts.resetThemeLockscreenWallpaper();
                            break;
                        case Theme.THEME_ELEMENT_TYPE_SYSTEMUI:
                            ts.resetThemeSystemUI();
                            break;
                        case Theme.THEME_ELEMENT_TYPE_FRAMEWORK:
                            ts.resetThemeFramework();
                            break;
                        case Theme.THEME_ELEMENT_TYPE_RINGTONES:
                            ts.resetThemeRingtone();
                            break;
                        case Theme.THEME_ELEMENT_TYPE_BOOTANIMATION:
                            ts.resetThemeBootanimation();
                            break;
                        case Theme.THEME_ELEMENT_TYPE_MMS:
                            ts.resetThemeMms();
                            break;
                        case Theme.THEME_ELEMENT_TYPE_FONT:
                            if (ThemeUtils.installedThemeHasFonts()) {
                                SimpleDialogs.displayYesNoDialog(
                                        getString(R.string.dlg_reset_font_and_reboot),
                                        getString(R.string.dlg_reset_font_without_reboot),
                                        getString(R.string.dlg_reset_font_title),
                                        getString(R.string.dlg_reset_font_body),
                                        getActivity(),
                                        new SimpleDialogs.OnYesNoResponse() {
                                            @Override
                                            public void onYesNoResponse(boolean isYes) {
                                                if (isYes)
                                                    try {
                                                        ts.resetThemeFontReboot();
                                                    } catch(Exception e) {}
                                            }
                                        });
                            }
                            break;
                        case Theme.THEME_ELEMENT_TYPE_CONTACTS:
                            ts.resetThemeContacts();
                            break;
                        case Theme.THEME_ELEMENT_TYPE_DIALER:
                            ts.resetThemeDialer();
                            break;
                    }
                } catch (Exception e) {
                }
                return true;
            default:
                return false;
        }
    }

    public class PreviewAdapter extends BaseAdapter {
        private Context mContext;

        private ElementPreviewManager mPreviewManager = new ElementPreviewManager();

        private View[] mPreviews;
        private int mPreviewWidth;
        private int mPreviewHeight;

        public PreviewAdapter(Context c) {
            mContext = c;
            int numColumns = getResources().getInteger(R.integer.gridviewNumColumns);
            int spacingTotal = mGridView.getHorizontalSpacing() * (numColumns - 1);
            DisplayMetrics dm = c.getResources().getDisplayMetrics();
            float aspectRatio = 1;
            if (dm.heightPixels > dm.widthPixels)
                aspectRatio = (float)dm.heightPixels / dm.widthPixels;
            else
                aspectRatio = (float)dm.widthPixels / dm.heightPixels;

            mPreviewWidth = dm.widthPixels / numColumns - spacingTotal;
            mPreviewHeight = (int)(mPreviewWidth * aspectRatio);
            preloadPreviews();
        }

        private void preloadPreviews() {
            mPreviews = new View[mThemeList.size()];
            for (int i = 0; i < mPreviews.length; i++) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mPreviews[i] = inflater.inflate(R.layout.theme_preview, null);
                FrameLayout fl = (FrameLayout)mPreviews[i].findViewById(R.id.preview_layout);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)fl.getLayoutParams();
                params.width = mPreviewWidth;
                params.height = mPreviewHeight;
                fl.setLayoutParams(params);
                PreviewHolder holder = new PreviewHolder();
                holder.preview = (FlipImageView) mPreviews[i].findViewById(R.id.preview_image);
                holder.name = (TextView) mPreviews[i].findViewById(R.id.theme_name);
                holder.osTag = (ImageView) mPreviews[i].findViewById(R.id.os_indicator);
                holder.progress = mPreviews[i].findViewById(R.id.loading_indicator);
                holder.index = i;
                mPreviews[i].setTag(holder);
                mPreviewManager.fetchDrawableOnThread(mThemeList.get(i), mElementType, holder);

                holder.name.setText(mThemeList.get(i).getTitle());
                holder.preview.setImageResource(R.drawable.empty_preview);
                if (mElementType == Theme.THEME_ELEMENT_TYPE_WALLPAPER ||
                        mElementType == Theme.THEME_ELEMENT_TYPE_LOCK_WALLPAPER)
                    holder.preview.setScaleType(ImageView.ScaleType.CENTER_CROP);

                if (mThemeList.get(i).getIsCosTheme())
                    holder.osTag.setImageResource(R.drawable.chaos);
                else
                    holder.osTag.setImageResource(R.drawable.miui);
            }
        }

        public int getCount() {
            return mThemeList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return mPreviews[position];
        }

        public void destroy() {
            mPreviewManager = null;
            mContext = null;
        }
    }
}
