package com.android.plauncher3;

/**
 * Created by an.pan on 2017/4/13.
 */

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThemePickerActivity extends Activity {

    public static final String THEME_ICON_ROOT_PATH = "storage/sdcard0/theme/";
    public static final String THEME_THUMBS_ROOT_PATH = "storage/sdcard0/theme_thumbs/";
    private String mSelectedTheme = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_picker);
        getThemeBitmaps(THEME_THUMBS_ROOT_PATH);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        mSelectedTheme = getSharedPreferences(LauncherAppState.getSharedPreferencesKey(),
                Context.MODE_PRIVATE).getString("theme_key", "defautl");
        gridView.setAdapter(new ImageAdapter(this));
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(ThemePickerActivity.this);
                if (position == mThemesNames.size()) {
                    getSharedPreferences(LauncherAppState.getSharedPreferencesKey(),
                            Context.MODE_PRIVATE).edit().putString("theme_key", "default").commit();
                    try {
                        wallpaperManager.setResource(android.content.res.Resources.getSystem().getIdentifier("default_wallpaper", "drawable", "android"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else if (isFileEffect(THEME_ICON_ROOT_PATH + mThemesNames.get(position))) {
                    getSharedPreferences(LauncherAppState.getSharedPreferencesKey(),
                            Context.MODE_PRIVATE).edit().putString("theme_key", mThemesNames.get(position)).commit();
                    try {
                        wallpaperManager.setBitmap(BitmapFactory.decodeFile(THEME_ICON_ROOT_PATH + mThemesNames.get(position) + "/" + mThemesNames.get(position) + "_wallpaper.jpg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
//down load this theme
                }
            }
        });
    }

    private List<String> mThemesNames = null;
    private List<Bitmap> mThemesBitmaps = null;

    private class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public ImageAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mThemesNames.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == mThemesNames.size())
                return BitmapFactory.decodeResource(ThemePickerActivity.this.getResources(), R.mipmap.default_scene);
            else
                return mThemesBitmaps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.theme_picker_item, null);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.theme_title);
                holder.thumbs = (ImageView) convertView.findViewById(R.id.theme_thumbs);
                holder.selected = (ImageView) convertView.findViewById(R.id.theme_selected);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == mThemesNames.size()) {
                holder.thumbs.setImageBitmap(BitmapFactory.decodeResource(ThemePickerActivity.this.getResources(), R.mipmap.default_scene));
                holder.title.setText(ThemePickerActivity.this.getResources().getString(R.string.def_theme_title));
                if(mSelectedTheme.equals("default")){
                    holder.selected.setVisibility(View.VISIBLE);
                }
            } else {
                holder.thumbs.setImageBitmap(mThemesBitmaps.get(position));
                holder.title.setText(mThemesNames.get(position));
                if (mSelectedTheme.equals(mThemesNames.get(position))) {
                    holder.selected.setVisibility(View.VISIBLE);
                }
            }
            return convertView;
        }

        public final class ViewHolder {
            public TextView title;
            public ImageView thumbs;
            public ImageView selected;
        }
    }

    private void getThemeBitmaps(String path) {
        mThemesNames = new ArrayList<String>();
        mThemesBitmaps = new ArrayList<Bitmap>();
        List<String> themetitles = new ArrayList<String>();
        File pathFile = new File(path);
        if (pathFile.exists() && pathFile.isDirectory()) {
            String[] fileNames = pathFile.list();
            if (fileNames.length <= 0)
                return;
            for (String filename : fileNames) {
                File subFile = new File(path + "/" + filename);
                if (subFile.exists() && (!subFile.isDirectory()) && subFile.getName().endsWith(".png")) {
                    Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(path + "/" + filename);
                    String namekey = trimExtension(filename);
                    if (bitmap != null && (namekey != null)) {
                        mThemesNames.add(namekey);
                        mThemesBitmaps.add(bitmap);
                    }
                }
            }
        }
    }

    private String trimExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');
            if ((i > -1) && (i < (filename.length()))) {
                return filename.substring(0, i);
            }
        }
        return null;
    }

    private boolean isFileEffect(String name) {
        File file = new File(name);
        if (file.exists() && file.isDirectory() && (file.list().length > 0))
            return true;
        else
            return false;

    }
}