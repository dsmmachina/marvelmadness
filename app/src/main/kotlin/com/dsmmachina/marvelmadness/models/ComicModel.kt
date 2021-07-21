package com.dsmmachina.marvelmadness.models

import com.google.gson.annotations.SerializedName
import com.dsmmachina.marvelmadness.BuildConfig

class ComicModel {

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("issueNumber")
    var issueNumber: Int? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("thumbnail")
    var thumbnail: ImageModel? = null

    @SerializedName("images")
    var images: Array<ImageModel>? = null

    @SerializedName("creators")
    var creators: CreatorsWrapperModel? = null

    @SerializedName("characters")
    var characters: CharacterWrapperModel? = null


    // some comics don't appear to have a thumbnail, can we find one in the images list?
    fun getThumbnailImage() : String {

        if (thumbnail?.path == null || thumbnail?.extension == null) {
            return ""
        }

        val defaultMarvelThumbnailPath = "https://i.annihil.us/u/prod/marvel/images/logo/marvel-logo.png"
        val path = "${thumbnail?.path}.${thumbnail?.extension}"
        val needle = "image_not_available.jpg"

        return when {

            path.endsWith(needle) -> {
                when {
                    images?.isNotEmpty() == true -> "${images?.get(0)?.path}.${images?.get(0)?.extension}"
                    BuildConfig.USE_PRETTY_THUMBNAIL -> defaultMarvelThumbnailPath
                    else -> path
                }
            }

            else -> {
                path
            }
        }
    }
}