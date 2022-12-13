package com.example.retrofitdemo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Posts(
    val posts: MutableList<Post>
)
