package io.lb.stefaniniandroidchallenge.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import io.lb.stefaniniandroidchallenge.R
import io.lb.stefaniniandroidchallenge.model.picture.Picture
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity: DaggerAppCompatActivity() {
    lateinit var adapter: PostAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: PostViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_StefaniniAndroidChallenge)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        setupViewModel()
    }

    private fun setupRecyclerView() {
        runOnUiThread {
            shimmerPostPictures.startShimmer()
        }

        rvPostPictures.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter()
        rvPostPictures.adapter = adapter
    }

    private fun setupViewModel() {
        viewModel.loadPosts().observe(this) {
            val pictures = viewModel.setupPictures(it)
            updateAdapter(pictures)
        }

        viewModel.makeApiCall {
            viewModel.loadPosts()
        }
    }

    private fun updateAdapter(it: ArrayList<Picture>?) {
        adapter.updateList(it)
        adapter.notifyDataSetChanged()

        Handler(Looper.getMainLooper()).postDelayed({
            disableShimmer()
            rvPostPictures.visibility =
                if (!it.isNullOrEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }, 1500)
    }

    private fun disableShimmer() {
        shimmerPostPictures.visibility = View.GONE
        shimmerPostPictures.stopShimmer()
    }
}