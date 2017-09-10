package com.github.chagall.notificationlistenerexample;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jiawei on 2017/9/10.
 */

public class PrefsAccessor {

    /** share preference file to save */
    public static final String SHARE_DATA_FILE_NAME = "itcast";

    private static PrefsAccessor instance = null;
    private SharedPreferences mSharedPreferences;

    private PrefsAccessor(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARE_DATA_FILE_NAME, Context.MODE_PRIVATE);
    }

    public synchronized static PrefsAccessor getInstance(Context context) {
        if (instance == null) {
            instance = new PrefsAccessor(context.getApplicationContext());
        }
        return instance;
    }

    public void saveString(String key, String value) {
        getEditor().putString(key, value).commit();
    }

    public void saveBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }

    /**
     * read the value from preference settings for the given key
     *
     * @param key
     *            which preference key to search for
     * @param defValue
     *            return when the given key not found, if not specified, "" will
     *            return.
     * @return the result value for the given key.
     */
    public String getString(String key, String... defValue) {
        String defaultValue = "";
        if (null == defValue) {
            defaultValue = null;
        } else if (null != defValue && defValue.length > 0) {
            defaultValue = defValue[0];
        }
        return mSharedPreferences.getString(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean... defValue) {
        boolean def = false;
        if (null != defValue && defValue.length > 0) {
            def = defValue[0];
        }
        return mSharedPreferences.getBoolean(key, def);
    }

    public void remove(String key) {
        if (mSharedPreferences.contains(key)) {
            getEditor().remove(key).commit();
        }
    }

    private SharedPreferences.Editor getEditor() {
        return mSharedPreferences.edit();
    }
}
