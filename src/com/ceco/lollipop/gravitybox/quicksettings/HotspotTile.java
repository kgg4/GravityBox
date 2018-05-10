package com.ceco.lollipop.gravitybox.quicksettings;

import com.ceco.lollipop.gravitybox.GravityBox;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook.Unhook;

public class HotspotTile extends AospTile {
    public static final String AOSP_KEY = "hotspot";
    public static final String XPERIA_KEY = "tethering";

    private static final Intent TETHER_SETTINGS = new Intent().setComponent(new ComponentName(
            "com.android.settings", "com.android.settings.TetherSettings"));

    private String mAospKey;
    private Unhook mLongClickHook;

    protected HotspotTile(Object host, String aospKey, Object tile, XSharedPreferences prefs,
            QsTileEventDistributor eventDistributor) throws Throwable {
        super(host, "aosp_tile_hotspot", tile, prefs, eventDistributor);

        mAospKey = aospKey;

        createHooks();
    }

    @Override
    public String getAospKey() {
        return mAospKey;
    }

    @Override
    public boolean handleLongClick() {
        startSettingsActivity(TETHER_SETTINGS);
        return true;
    }

    @Override
    public void handleDestroy() {
        super.handleDestroy();
        destroyHooks();
    }

    private void createHooks() {
        try {
            if (Build.VERSION.SDK_INT >= 22) {
                mLongClickHook = XposedHelpers.findAndHookMethod(mTile.getClass().getName(), 
                        mContext.getClassLoader(), "handleLongClick", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (handleLongClick()) {
                            param.setResult(null);
                        }
                    }
                });
            }
        } catch (Throwable t) {
            GravityBox.log(TAG, t);
        }
    }

    private void destroyHooks() {
        if (mLongClickHook != null) {
            mLongClickHook.unhook();
            mLongClickHook = null;
        }
    }
}
