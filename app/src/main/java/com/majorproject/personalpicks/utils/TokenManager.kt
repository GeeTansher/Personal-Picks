package com.majorproject.personalpicks.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.majorproject.personalpicks.utils.Constants.PREFS_TOKEN_FILE
import com.majorproject.personalpicks.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_TOKEN_FILE)
class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {
    private  var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveToken(token:String){
        val editor =  prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }
    fun getBusinessLongitude():String?{
        return prefs.getString("BUSINESS_LONG", "-122.67890")
    }
    fun clearToken(){
        val editor = prefs.edit()
        editor.clear();
        editor.apply();
    }

}