package com.example.diplomskirad.common.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

open class SharedPreferencesService(private val context: Context) {

    protected fun save(pKey: String?, pValue: Any?) {
        if (pValue != null) {
            if (pValue is String) {
                saveString(pKey!!, (pValue as String?)!!)
            } else if (pValue is Long) {
//                saveLong(pKey, pValue as Long?)
            } else if (pValue is Boolean) {
//                saveBoolean(pKey, pValue as Boolean?)
            } else if (pValue is Int) {
//                saveInteger(pKey, pValue as Int?)
            } else if (pValue is Float) {
//                saveFloat(pKey, pValue as Float?)
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    protected fun save(prefsName: String, pKey: String, pValue: Any) {
        if (pValue is String) {
            saveString(prefsName, pKey, pValue)
        } else if (pValue is Long) {
//                saveLong(prefsName, pKey, pValue as Long?)
        } else if (pValue is Boolean) {
//                saveBoolean(prefsName, pKey, pValue as Boolean?)
        } else if (pValue is Int) {
//                saveInteger(prefsName, pKey, pValue as Int?)
        } else if (pValue is Float) {
//                saveFloat(prefsName, pKey, pValue as Float?)
        } else {
            throw IllegalArgumentException()
        }
    }

    private fun saveString(pKey: String, pValue: String) {
        SharedPreferencesCache.cacheString[pKey] = pValue
        getPreferencesEditor()!!.putString(pKey, pValue).commit()
    }

    private fun saveString(prefsName: String, pKey: String, pValue: String) {
        val cacheKey = prefsName + "_" + pKey

        SharedPreferencesCache.cacheString[cacheKey] = pValue
        getPreferencesEditor(prefsName)!!.putString(pKey, pValue).commit()
    }

    protected fun getPreferences(): SharedPreferences? {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun getPreferences(prefsName: String): SharedPreferences? {
        return context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    private fun getPreferencesEditor(prefsName: String): SharedPreferences.Editor? {
        return getPreferences(prefsName)!!.edit()
    }

    private fun getPreferencesEditor(): SharedPreferences.Editor? {
        return getPreferences()?.edit()
    }

    protected fun getAllPreferences(): Map<String?, *>? {
        return getPreferences()!!.all
    }

    fun getString(pKey: String?, pDefaultValue: String?): String? {
        var result: String? = SharedPreferencesCache.cacheString[pKey]
        if (result == null) {
            result = getPreferences()!!.getString(pKey, pDefaultValue)
            if (result != null) {
                if (pKey != null) {
                    SharedPreferencesCache.cacheString[pKey] = result
                }
            }
        }
        return result
    }

    fun getString(prefsName: String, pKey: String, pDefaultValue: String?): String? {
        val cacheKey = prefsName + "_" + pKey
        var result: String? = SharedPreferencesCache.cacheString[cacheKey]
        if (result == null) {
            result = getPreferences(prefsName)!!.getString(pKey, pDefaultValue)
            if (result != null) {
                SharedPreferencesCache.cacheString[cacheKey] = result
            }
        }
        return result
    }

    protected fun erase(pKey: String?) {
        if (pKey != null) {
            SharedPreferencesCache.clearFromCache(pKey)
        }
        getPreferencesEditor()!!.remove(pKey).commit()
    }

    protected fun erase(prefsName: String, pKey: String) {
        val cacheKey = prefsName + "_" + pKey
        SharedPreferencesCache.clearFromCache(cacheKey)
        getPreferencesEditor(prefsName)!!.remove(pKey).commit()
    }
}