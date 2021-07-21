package com.dsmmachina.marvelmadness.models

import com.google.gson.annotations.SerializedName

class CreatorsWrapperModel {
    @SerializedName("items")
    var list: Array<CreatorModel>? = null
}

class CreatorModel {
    @SerializedName("name")
    var name: String? = null

}