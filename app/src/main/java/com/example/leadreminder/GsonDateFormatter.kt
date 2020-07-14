package com.example.leadreminder

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class GsonDateFormatter:JsonDeserializer<Date> {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date? {
        if(json== null || json.toString() == ""){
            return null
        }
        return try{
            dateFormat.parse(json.toString())
        }catch (ex:Exception){
            null;
        }
    }
}