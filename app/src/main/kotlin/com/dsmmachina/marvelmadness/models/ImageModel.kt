package com.dsmmachina.marvelmadness.models

import com.google.gson.annotations.SerializedName

class ImageModel {
    @SerializedName("path")
    var path : String? = null

    @SerializedName("extension")
    var extension : String? = null
}