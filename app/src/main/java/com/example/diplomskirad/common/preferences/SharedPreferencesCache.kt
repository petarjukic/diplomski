package com.example.diplomskirad.common.preferences

import java.util.concurrent.ConcurrentHashMap

object SharedPreferencesCache {

    val cacheObject: MutableMap<String, Any> = ConcurrentHashMap()
    val cacheString: MutableMap<String, String> = ConcurrentHashMap()
    val cacheLong: MutableMap<String, Long> = ConcurrentHashMap()
    val cacheInteger: MutableMap<String, Int> = ConcurrentHashMap()
    val cacheBoolean: MutableMap<String, Boolean> = ConcurrentHashMap()
    val cacheFloat: MutableMap<String, Float> = ConcurrentHashMap()

    fun clearFromCache(pKey: String) {
        cacheString.remove(pKey)
        cacheObject.remove(pKey)
        cacheLong.remove(pKey)
        cacheBoolean.remove(pKey)
        cacheInteger.remove(pKey)
        cacheFloat.remove(pKey)
    }
}