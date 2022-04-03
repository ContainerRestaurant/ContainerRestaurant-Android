package container.restaurant.android.util

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object SharedPrefUtil {
    internal fun getString(context: Context, key: StringKey.() -> String): String = SharedPrefManager.getString(context, key(StringKey))
    internal fun setString(context: Context, key: StringKey.() -> String, value: String) = SharedPrefManager.setString(context, key(StringKey), value)
    internal fun removeString(context: Context, key: StringKey.() -> String) = SharedPrefManager.removeString(context, key(StringKey))

    internal object StringKey {
        const val TOKEN_BEARER = "TOKEN_BEARER"
    }

    internal fun getMap(context: Context, key: CollectionSetStringKey.() -> String): MutableMap<String, MutableSet<String>> {
        val jsonObjStr = SharedPrefManager.getString(context, key(CollectionSetStringKey))
        val resultMap = mutableMapOf<String, MutableSet<String>>()
        if(jsonObjStr.isNotEmpty()) {
            try {
                val jsonObj = JSONObject(jsonObjStr)
                jsonObj.keys().forEach { mapKey ->
                    val strings = mutableSetOf<String>()
                    val jsonArr = jsonObj.optJSONArray(mapKey)
                    if(jsonArr != null) {
                        for(index in 0 until jsonArr.length()) {
                            strings.add(jsonArr.optString(index))
                        }
                    }
                    resultMap[mapKey] = strings
                }
            }
            catch (e: JSONException){}
        }
        return resultMap
    }
    internal fun setMap(context: Context, key: CollectionSetStringKey.() -> String, value: Map<String, Set<String>>) {
        val jsonObj = JSONObject()
        for(entry in value){
            val jsonArr = JSONArray()
            for(str in entry.value) {
                jsonArr.put(str)
            }
            jsonObj.put(entry.key, jsonArr)
        }
        if(value.isNotEmpty()) {
            SharedPrefManager.setString(context, key(CollectionSetStringKey), jsonObj.toString())
        }
        else {
            SharedPrefManager.removeString(context, key(CollectionSetStringKey))
        }
    }
    internal object CollectionSetStringKey {

    }

    internal fun getBoolean(context: Context, key: BooleanKey.() -> String): Boolean = SharedPrefManager.getBoolean(context, key(BooleanKey))
    internal fun setBoolean(context: Context, key: BooleanKey.() -> String, value: Boolean) = SharedPrefManager.setBoolean(context, key(BooleanKey), value)
    internal fun removeBoolean(context: Context, key: BooleanKey.() -> String) = SharedPrefManager.removeBoolean(context, key(BooleanKey))
    internal object BooleanKey{
        const val IS_USER_LOGIN = "IS_USER_LOGIN"
        const val IS_ON_BOARDING_FIRST = "IS_ON_BOARDING_FIRST"
    }

    internal fun getInt(context: Context, key: IntKey.() -> String): Int = SharedPrefManager.getInt(context, key(IntKey))
    internal fun setInt(context: Context, key: IntKey.() -> String, value: Int) = SharedPrefManager.setInt(context, key(IntKey), value)
    internal fun removeInt(context: Context, key: IntKey.() -> String) = SharedPrefManager.removeInt(context, key(IntKey))
    internal object IntKey {
        const val USER_ID =  "USER_ID"
    }

    internal fun getLong(context: Context, key: LongKey.() -> String): Long = SharedPrefManager.getLong(context, key(LongKey))
    internal fun setLong(context: Context, key: LongKey.() -> String, value: Long) = SharedPrefManager.setLong(context, key(LongKey), value)
    internal fun removeLong(context: Context, key: LongKey.() -> String) = SharedPrefManager.removeLong(context, key(LongKey))
    internal object LongKey {

    }

    internal fun getFloat(context: Context, key: FloatKey.() -> String): Float = SharedPrefManager.getFloat(context, key(FloatKey))
    internal fun setFloat(context: Context, key: FloatKey.() -> String, value: Float) = SharedPrefManager.setFloat(context, key(FloatKey), value)
    internal fun removeFloat(context: Context, key: FloatKey.() -> String) = SharedPrefManager.removeFloat(context, key(FloatKey))
    internal object FloatKey {

    }
}