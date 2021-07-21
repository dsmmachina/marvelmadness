package com.dsmmachina.marvelmadness.viewmodels

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.dsmmachina.marvelmadness.models.ComicModel

class MainViewModel : ViewModel() {

    // our list of comics (so we don't need to reload on config changes)
    var comicList : Array<ComicModel>? = null

    // the current comic book that we are showing information for
    var currentComic : ComicModel? = null

    // our custom alert dialog, we'll want the ability to keep this around (error messages, etc.)
    var dialog : AlertDialog? = null

    // a simple flag to tell us if we have loaded the comics from the api at least once
    var hasRunFirstLoad = false

}