package com.dsmmachina.marvelmadness.deserializers

import com.google.gson.*
import com.dsmmachina.marvelmadness.models.CharacterModel
import java.lang.reflect.Type

class CharactersDeserializer : JsonDeserializer<Array<CharacterModel>>{

    companion object {
        fun deserialize(je : JsonElement) : Array<CharacterModel>? {
            return try
            {
                val results = je.asJsonObject.get("data").asJsonObject.get("results").asJsonArray
                Gson().fromJson(results, Array<CharacterModel>::class.java)
            }
            catch (ex: Exception) {
                arrayOf()
            }
        }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(je: JsonElement, type: Type, jdc: JsonDeserializationContext): Array<CharacterModel>? {
        return deserialize(je)
    }

}