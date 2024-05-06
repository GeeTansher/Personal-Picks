package com.majorproject.personalpicks.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.majorproject.personalpicks.utils.Constants.CUSTOMER_ID
import com.majorproject.personalpicks.utils.Constants.PREFS_TOKEN_FILE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {
    private  var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveCustomerId(id:String){
        val editor =  prefs.edit()
        editor.putString(CUSTOMER_ID, id)
        editor.apply()
    }
    fun getCustomerId(): String? {
        return prefs.getString("CUSTOMER_ID", "32158956")
    }
//    fun clearCustomerId(){
//        val editor = prefs.edit()
//        editor.clear();
//        editor.apply();
//    }

}