package com.dznow.browse


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dznow.R
import com.dznow.category.CategoryFeed
import com.dznow.network.OkHttpRequest
import com.dznow.source.SourceDataFactory
import com.dznow.source.SourceFeed
import com.google.gson.GsonBuilder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class BrowseFragment : Fragment() {
    lateinit var categoriesRecyclerView: RecyclerView
    lateinit var sourcesRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // asynchronous call to fetch data from api
        fetchJson()
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_browse, container, false)
        // categories recycler view
        categoriesRecyclerView = rootView.findViewById(R.id.rv_categories) as RecyclerView
        categoriesRecyclerView.setHasFixedSize(true)
        categoriesRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        // categoriesRecyclerView.adapter = CategoryAdapter(CategoryDataFactory.getCategories(4))
        // sources recycler view
        sourcesRecyclerView = rootView.findViewById(R.id.rv_sources) as RecyclerView
        sourcesRecyclerView.setHasFixedSize(true)
        sourcesRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        // sourcesRecyclerView.adapter = SourceAdapter(SourceDataFactory.getSources(3))
        // return rootView
        return rootView
    }

    private fun fetchJson() {
        val categoriesUrl = "https://dznow.herokuapp.com/api/v0/fr/categories"
        val client = OkHttpClient()
        val request = OkHttpRequest(client)
        request.GET(categoriesUrl, object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = "{ categories: " + response.body()?.string() + "}"
                val gson = GsonBuilder().create()
                val categoryFeed = gson.fromJson(body, CategoryFeed::class.java)
                getActivity()?.runOnUiThread {
                    categoriesRecyclerView.adapter = CategoryAdapter(categoryFeed.categories)
                }
            }
        })
        val sourcesUrl = "https://dznow.herokuapp.com/api/v0/fr/sources"
        request.GET(sourcesUrl, object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = "{ sources: " + response.body()?.string() + "}"
                val gson = GsonBuilder().create()
                val sourceFeed = gson.fromJson(body, SourceFeed::class.java)
                getActivity()?.runOnUiThread {
                    sourcesRecyclerView.adapter = SourceAdapter(sourceFeed.sources)
                }
            }
        })
    }
}
