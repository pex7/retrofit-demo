package com.example.retrofitdemo

data class PostModel(
    val loading: Boolean,
    val posts: MutableList<Post>,
    val editPostId: Int? = null,
    val showAddPostDialog: Boolean
)
