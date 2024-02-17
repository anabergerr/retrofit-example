package com.example.retrofitexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitexample.data.api.ApiService
import com.example.retrofitexample.data.api.ApiServiceClient
import com.example.retrofitexample.data.models.Comment
import com.example.retrofitexample.data.models.Post
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

        val callComments = apiService.getCommentsByPostId(1)




        callComments.enqueue(object : Callback<List<Comment>>{
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if(response.isSuccessful) {
                    val comments = response.body()
                    comments?.forEach {
                        Log.i("App", it.toString())
                    }
                }
            }

            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                Log.e("App", "Ocorrreu uma falha na execução")
            }
        })
    }


    override fun onStart() {
        super.onStart()
        val callGetPosts = apiService.getPosts()
        callGetPosts.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if(response.isSuccessful) {
                    val posts = response.body()
                    val postTitles = posts?.map{ "${it.id} -- ${it.title}"}?.toList().orEmpty()
                    val adapter = ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, postTitles)
                    mMenuSelection.setAdapter(adapter)
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.e("App", "Ocorreu um erro na requisicao")
            }
        })

    }
}