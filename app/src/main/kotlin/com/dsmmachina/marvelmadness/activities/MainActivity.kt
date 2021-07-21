package com.dsmmachina.marvelmadness.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsmmachina.marvelmadness.R
import com.dsmmachina.marvelmadness.adapters.ComicAdapter
import com.dsmmachina.marvelmadness.api.Api
import com.dsmmachina.marvelmadness.base.BaseObserver
import com.dsmmachina.marvelmadness.databinding.MainActivityBinding
import com.dsmmachina.marvelmadness.decorations.GridSpacingDecoration
import com.dsmmachina.marvelmadness.fragments.InformationFragment
import com.dsmmachina.marvelmadness.models.ComicModel
import com.dsmmachina.marvelmadness.viewmodels.MainViewModel


class MainActivity : AppCompatActivity() {

    private val maxComicsPerPage = 100

    private val currentPage = 0

    private lateinit var mainViewModel : MainViewModel

    private var binding : MainActivityBinding? = null

    private var informationFragment : InformationFragment? = null

    private var adapter : ComicAdapter? = null




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        window.navigationBarColor = ContextCompat.getColor(this, R.color.grey)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val view = layoutInflater.inflate(R.layout.activity_main, null)
        binding = DataBindingUtil.bind(view)

        binding?.swipeRefresh?.setOnRefreshListener {
            refreshComics()
        }

        setupRecyclerView()

        setContentView(binding?.root)
    }

    override fun onResume() {
        super.onResume()
        loadComics()
    }

    override fun onPause() {
        mainViewModel.dialog?.dismiss()
        super.onPause()
    }

    override fun onBackPressed() {

        if (mainViewModel.currentComic != null) {
            informationFragment?.close()
        }
        else {
            super.onBackPressed()
        }

    }



    private fun setupRecyclerView() {

        adapter = ComicAdapter(this@MainActivity)
        adapter?.setClickObserver(ComicClickObserver())

        val listColumns = 2
        val decoration = GridSpacingDecoration(listColumns, 0, true)
        val llm = GridLayoutManager(this@MainActivity, listColumns)
        llm.orientation = LinearLayoutManager.VERTICAL

        with(binding?.recyclerView) {
            this?.setItemViewCacheSize(maxComicsPerPage)
            this?.isNestedScrollingEnabled = true
            this?.itemAnimator = DefaultItemAnimator()
            this?.layoutManager = llm
            this?.addItemDecoration(decoration, 0)
            this?.adapter = adapter
        }

    }

    private fun setLoadingState(state: Boolean) {
        this@MainActivity.runOnUiThread { binding?.isLoading = state }
    }



    private fun loadComics(forceLoad: Boolean? = null) {

        when {

            // sometimes we'll want to force a reload (such as for a pull to refresh)
            !mainViewModel.hasRunFirstLoad || forceLoad == true -> {
                mainViewModel.hasRunFirstLoad = true
                setLoadingState(true)
                adapter?.clearList()

                Api.getComics(
                    offset = currentPage * maxComicsPerPage,
                    limit = maxComicsPerPage,
                    ComicListObserver()
                )
            }

            // in every other case, we can just give our current list to the adapter
            else -> {
                adapter?.setComicList(mainViewModel.comicList ?: arrayOf())
                setLoadingState(false)
            }
        }

        // if we had a dialog opened, we should re-open it
        mainViewModel.dialog?.show()


        // finally, if we were showing the comic information, we need to show it again
        if (mainViewModel.currentComic != null) {
            showInformationView(skipAnimation = true)
        }

    }

    private fun refreshComics() {

        runOnUiThread {
            binding?.swipeRefresh?.isRefreshing = false
            binding?.isLoading = true
        }

        adapter?.clearList()
        loadComics(forceLoad = true)
    }



    private fun showInformationView(skipAnimation : Boolean?=null) {

        setLoadingState(true)

        informationFragment = InformationFragment()
        informationFragment?.setCloseObserver(object : BaseObserver<Boolean>() {
            override fun onNext(next: Boolean) {
                dismissInformationView()
            }
        })

        val transaction = supportFragmentManager.beginTransaction()

        // in the case where we are re-creating the fragment, we don't want to run the animations...
        if (skipAnimation != true) {
            transaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
        }

        transaction.replace(binding?.childFrame?.id ?: R.id.childFrame, informationFragment!!)
            .runOnCommit {
                this@MainActivity.runOnUiThread {
                    binding?.showingInformation = true
                    setLoadingState(false)
                }
                informationFragment?.load(mainViewModel.currentComic)
            }
            .commitAllowingStateLoss()
    }

    private fun dismissInformationView() {

        if (informationFragment == null) {
            return
        }

        supportFragmentManager.beginTransaction()
            .remove(informationFragment!!)
            .runOnCommit {
                mainViewModel.currentComic = null
                this@MainActivity.runOnUiThread {
                    binding?.showingInformation = false
                }
            }
            .commitAllowingStateLoss()
    }



    fun getComicAdapter() : ComicAdapter? { return adapter }

    fun showErrorMessage(message: String) {
        runOnUiThread {
            setLoadingState(false)
            val builder = AlertDialog.Builder(this)
            builder
                .setCancelable(false)
                .setMessage(message)
                .setTitle(R.string.error_title)
                .setPositiveButton(R.string.error_button_title) { _, _ ->
                    mainViewModel.dialog?.dismiss()
                    mainViewModel.dialog = null
                }
            mainViewModel.dialog = builder.create()
            mainViewModel.dialog?.show()
        }
    }



    // custom observer to listen for Api response when connecting to the comics endpoint
    inner class ComicListObserver : BaseObserver<Array<ComicModel>>() {

        override fun onNext(next: Array<ComicModel>) {
            mainViewModel.comicList = next
            adapter?.setComicList(mainViewModel.comicList ?: arrayOf())
            setLoadingState(false)
        }

        override fun onError(error: Throwable) {
            showErrorMessage(error.message ?: resources.getString(R.string.error_unknown_reason))
        }
    }

    // custom observer to listen for comic book click event from the adapter
    inner class ComicClickObserver : BaseObserver<ComicModel>() {
        override fun onNext(next: ComicModel) {
            mainViewModel.currentComic = next
            showInformationView()
        }
    }

}