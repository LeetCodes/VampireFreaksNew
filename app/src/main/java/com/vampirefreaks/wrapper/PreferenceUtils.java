package com.vampirefreaks.wrapper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nikhil Vashistha on 3/9/2016 for vampirefreaks.
 */
public class PreferenceUtils {

    public static final String PREFERNCE_NAME		 =	 "com.vampirefreaks.wrapper";

    private static final String LOGIN_SESSION  		 =   "login_session";

    private static final String EMAIL                =    "email";

    private static final String PASSWORD             =    "password";

    private static final String REG_ID               =    "reg_id";



    /**
     *  Getter & Setter Methods for SharedPreferences
     * @param context
     * @return
     */
    public static boolean getLoginSession(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getBoolean(LOGIN_SESSION,false);

    }

    public static void setLoginSession(Context context, boolean login_session) {

        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putBoolean(LOGIN_SESSION, login_session).commit();
    }


    public static String getEmail(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getString(EMAIL, "");
    }

    public static void setEmail(Context context,String email) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putString(EMAIL, email).commit();
    }


    public static String getPassword(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getString(PASSWORD, "");
    }

    public static void setPassword(Context context,String password) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putString(PASSWORD, password).commit();
    }


    public static String getRegisterId(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        return mPrefs.getString(REG_ID, "");
    }

    public static void setRegisterId(Context context,String id) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().putString(REG_ID, id).commit();
    }

    public static void clearAllPreference(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFERNCE_NAME, Context.MODE_PRIVATE);
        mPrefs.edit().clear().commit();
    }


}
