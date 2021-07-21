package com.dsmmachina.marvelmadness.models

import com.google.gson.annotations.SerializedName


class CharacterWrapperModel {

    @SerializedName("returned")
    var returned: Int = 0
}

class CharacterModel {

    @SerializedName("id")
    var id : Long? = null

    @SerializedName("name")
    var name : String? = null

    @SerializedName("thumbnail")
    var thumbnail: ImageModel? = null

    val parsedThumbnail : String get() {
        return "${thumbnail?.path}.${thumbnail?.extension}"
    }
}