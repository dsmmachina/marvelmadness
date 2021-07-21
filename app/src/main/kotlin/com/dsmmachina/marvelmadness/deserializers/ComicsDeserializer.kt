package com.dsmmachina.marvelmadness.deserializers

import com.google.gson.*
import com.dsmmachina.marvelmadness.models.ComicModel
import java.lang.reflect.Type

class ComicsDeserializer : JsonDeserializer<Array<ComicModel>> {

    companion object {

        fun deserialize(je : JsonElement) : Array<ComicModel>? {
            return try
            {
                val results = je.asJsonObject.get("data").asJsonObject.get("results").asJsonArray
                Gson().fromJson(results, Array<ComicModel>::class.java)
            }
            catch (ex: Exception) {
                arrayOf()
            }
        }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(je: JsonElement, type: Type, jdc: JsonDeserializationContext): Array<ComicModel>? {
        return deserialize(je)
    }
}
