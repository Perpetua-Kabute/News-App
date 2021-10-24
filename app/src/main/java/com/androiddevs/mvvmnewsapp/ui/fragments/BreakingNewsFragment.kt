package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.viewModels.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.viewModels.NewsViewModelProviderFactory
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.delay

const val TAG = "BreakingNewsFragment"
class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news)  {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        showProgressBar()
        setUpRecycyclerViewAdapter()

        newsAdapter.setOnItemClickListener { article ->
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            Log.d(TAG, "article clicked")
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)

        }


        viewModel.getSavedNewsResponse().observe(viewLifecycleOwner, Observer { newsResponse ->
            hideProgressBar()
            if(newsResponse == null){
                showProgressBar()
                Log.e(TAG, "NewsResponse is null")
//                Toast.makeText(activity, "No news in database",Toast.LENGTH_LONG).show()
            }else{
                newsAdapter.differ.submitList(newsResponse.articles.toList())
            }


        })

//        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
//            when(response){
//                is Resource.Success -> {
//                    hideProgressBar()
//                    response.data?.let{newsResponse ->
//                        newsAdapter.differ.submitList(newsResponse.articles.toList())
//                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
//                        isLastPage = viewModel.breakingNewsPage == totalPages
//                        if(isLastPage){
//                            rvBreakingNews.setPadding(0, 0, 0, 0)
//                        }
//                    }
//                }
//                is Resource.Error -> {
//                    hideProgressBar()
//                    response.message?.let{ message ->
//                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG).show()
//                        Log.e(TAG, "An error occurred $message")
//                    }
//                }
//                is Resource.Loading -> {
//                    showProgressBar()
//                }
//            }
//        })

    }

    private fun setUpRecycyclerViewAdapter(){
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
//            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
    fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    /**
     * create a scroll listener
     */
    val scrollListener = object : RecyclerView.OnScrollListener(){


        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            //calculate if we're at the end of the list using a layout manager
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            Log.d(TAG, "Should Paginate $shouldPaginate")
            Log.d(TAG, "Page Number ${viewModel.breakingNewsPage}")
            Log.d(TAG, "isScrolling $isScrolling")
            Log.d(TAG, "isLoading $isLoading")
            Log.d(TAG, "isLastPage $isLastPage")
            if(shouldPaginate){
                viewModel.getBreakingNews("us")
                Log.d(TAG,viewModel.getBreakingNews("us").toString() )
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //check if we're currently scrolling
            if( newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                Log.d(TAG, "NewState $newState")
                Log.d(TAG, "Are we currently Scrolling ${AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL == newState}")
                isScrolling = true
                Log.d(TAG, "isScrolling after onScrollStateChanged $isScrolling")
            }
        }

    }

}