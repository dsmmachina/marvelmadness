package com.dsmmachina.marvelmadness.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsmmachina.marvelmadness.R
import com.dsmmachina.marvelmadness.databinding.ComicItemBinding
import com.dsmmachina.marvelmadness.models.ComicModel
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import io.reactivex.Observer

/**
 * ComicAdapter
 * A simple implementation of the RecyclerView.Adapter for our list of comics
 */
class ComicAdapter(private val context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    lateinit var inflater: LayoutInflater

    private var binding: ComicItemBinding? = null

    private var comicList : Array<ComicModel> ? = null

    private var clickObserver : Observer<ComicModel>? = null



    inner class ComicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ComicItemBinding = DataBindingUtil.bind(itemView)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.item_comic, parent, false)

        return ComicHolder(binding?.root!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val comic = this.comicList!![position]
        val comicHolder = holder as ComicHolder

        with(comicHolder) {

            Picasso.get()
                .load(comic.getThumbnailImage())
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(binding.thumbnail)

            binding.name = comic.title
            binding.issue = "Issue #${comic.issueNumber}"
            binding.root.setOnClickListener {
                clickObserver?.onNext(comic)
            }
        }
    }

    override fun getItemCount(): Int {
        return comicList?.size ?: 0
    }


    // set an observer for the comic book click event
    fun setClickObserver(observer : Observer<ComicModel>) {
        clickObserver = observer
    }

    fun setComicList(list : Array<ComicModel>) {

        comicList = list

        // remember to remove this if the server returns the list pre-sorted!
        comicList?.sortBy { it.title }

        notifyDataSetChanged()
    }


    // an easy way to clear the list and blank the UI
    fun clearList() {
        comicList = arrayOf()
        notifyDataSetChanged()
    }
}
