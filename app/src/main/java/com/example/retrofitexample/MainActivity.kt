package com.example.retrofitexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitexample.data.api.ApiService
import com.example.retrofitexample.data.api.ApiServiceClient
import com.example.retrofitexample.data.models.Comment
import com.example.retrofitexample.data.models.Post
import com.example.retrofitexample.ui.adapters.CommentAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private  val apiService = ApiServiceClient.instace
    private  val mMenuSelection: AutoCompleteTextView by lazy { findViewById(R.id.menu_selection)}
    private val mCommentList: RecyclerView by lazy {findViewById(R.id.comment_list)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mMenuSelection.setOnItemClickListener { parent, view, position, id ->

            val postTitle = parent.getItemAtPosition(position) as String
            val postId = postTitle.split("-")
                .first()
                .trim()
                .toInt()


            CoroutineScope(Dispatchers.IO).launch {
                val commentsResponse = apiService.getCommentsByPostId(postId)
                val comments = commentsResponse.body().orEmpty()

                val adapter = CommentAdapter(comments)

                withContext(Dispatchers.Main) {
                    mCommentList.adapter = adapter
                    mCommentList.layoutManager = LinearLayoutManager(baseContext)
                    adapter.notifyDataSetChanged()

                }
            }
        }


    }


    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.IO).launch {
            val postResponse = apiService.getPosts()
            val posts = postResponse.body()
            val postTitles = posts?.map{ "${it.id} -- ${it.title}"}?.toList().orEmpty()
            val adapter = ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, postTitles)
            withContext(Dispatchers.Main) {
                mMenuSelection.setAdapter(adapter)
            }
        }
    }
}