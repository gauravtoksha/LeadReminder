package com.example.leadreminder

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class GsonIntAdapter:JsonDeserializer<Int> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Int {
        return try{
            Integer.valueOf(json.toString().trim())
        }catch(ex:Exception){
            Log.e("ERROR",ex.toString())
            0
        }
    }
}