package com.example.retrofitdemo

sealed interface PostEvent {
    data class OnAddPost(val postTitle: String) : PostEvent
    data class OnEditPost(val post: Post, val updatedTitle: String) : PostEvent
    data class OnDeletePost(val postId: Int) : PostEvent
    data class OnToggleEditMode(val postId: Int? = null) : PostEvent
    object OnToggleShowAddPostDialog : PostEvent
}