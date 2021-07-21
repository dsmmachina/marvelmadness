package com.dsmmachina.marvelmadness.fragments

import android.animation.Animator
import android.graphics.Paint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dsmmachina.marvelmadness.R
import com.dsmmachina.marvelmadness.api.Api
import com.dsmmachina.marvelmadness.databinding.InformationFragmentBinding
import com.dsmmachina.marvelmadness.models.CharacterModel
import com.dsmmachina.marvelmadness.models.ComicModel
import com.dsmmachina.marvelmadness.models.CreatorModel
import com.dsmmachina.marvelmadness.base.BaseObserver
import com.squareup.picasso.Picasso
import io.reactivex.Observer
import java.lang.StringBuilder

class InformationFragment : Fragment() {

    private var binding : InformationFragmentBinding? = null

    private var closeObserver : Observer<Boolean>?= null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = layoutInflater.inflate(R.layout.fragment_information, null)
        binding = DataBindingUtil.bind(view)

        binding?.content?.setOnClickListener {
            // just consume this to keep clicks from being passed up the view tree
        }

        binding?.closeButton?.setOnClickListener {
            close()
        }

        return binding?.root
    }

    fun setCloseObserver(observer : Observer<Boolean>) {
        this.closeObserver = observer
    }

    fun load(comic : ComicModel?=null) {

        if (comic?.id == null) {
            return
        }


        // load the thumbnail
        Picasso.get().load(comic.getThumbnailImage()).into(binding?.thumbnail)

        // make the creators list
        val totalCreatorSize = comic.creators?.list?.size ?: 1
        val creatorsBuilder = StringBuilder()

        when (totalCreatorSize) {

            0 -> {
                creatorsBuilder.append("No creators listed")
            }

            1 -> {
                creatorsBuilder.append("Created by: ")
                creatorsBuilder.append(comic.creators?.list?.get(0)?.name)
            }

            else -> {

                creatorsBuilder.append("Created by: ")
                comic.creators?.list?.forEachIndexed { index: Int, creator: CreatorModel ->

                    when(index) {

                        (totalCreatorSize - 1) -> {
                            creatorsBuilder.append(" and ${creator.name}")
                        }

                        (totalCreatorSize - 2) -> {
                            creatorsBuilder.append(creator.name)
                        }

                        else -> {
                            creatorsBuilder.append("${creator.name}, ")
                        }
                    }
                }
            }
        }

        val hasCharacters = (comic.characters?.returned ?: 0) > 0


        // queue up a batch of UI work
        activity?.runOnUiThread {
            binding?.title = comic.title ?: ""
            binding?.issue = "Issue #${comic.issueNumber}"
            binding?.creators = "$creatorsBuilder"
            binding?.hasCharacters = hasCharacters

            val description = comic.description ?: "No description given"
            binding?.description = Html.fromHtml(description, Paint.FAKE_BOLD_TEXT_FLAG).toString()
        }


        // get the list of characters
        if (hasCharacters){
            Api.getCharacters(comic.id ?: 0L, object : BaseObserver<Array<CharacterModel>>() {

                override fun onNext(next: Array<CharacterModel>) {
                    if (next.isNotEmpty()) {
                        next.forEach {
                            activity?.runOnUiThread {

                                val view = layoutInflater.inflate(R.layout.item_character_chip, null)
                                view.findViewById<TextView>(R.id.name)?.text = it.name
                                val imageView = view.findViewById<ImageView>(R.id.thumbnail)

                                Picasso.get().load(it.parsedThumbnail).into(imageView)

                                binding?.characterScrollViewLayout?.addView(view)
                            }
                        }
                    }
                    else {
                        activity?.runOnUiThread { binding?.hasCharacters = false }
                    }
                }

                override fun onError(error: Throwable) {
                    activity?.runOnUiThread { binding?.hasCharacters = false }
                }
            })
        }

    }


    // it appears exit transitions for fragments are only displayed if they have another fragment
    // or view to transition to. In this case, we need to apply our own animation
    fun close() {

        activity?.runOnUiThread {
            binding?.root?.animate()
                ?.setDuration(resources.getInteger(R.integer.fragment_animation_duration).toLong())
                ?.translationY(activity?.window?.decorView?.height?.toFloat() ?: 0f)
                ?.setListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator?) { }

                    override fun onAnimationCancel(animation: Animator?) { }

                    override fun onAnimationRepeat(animation: Animator?) { }

                    override fun onAnimationEnd(animation: Animator?) {
                        closeObserver?.onNext(true) ?: this@InformationFragment.onDestroy()
                    }

                })
        }
    }
}