package com.example.retrofitdemo

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(private val api: PostApiInterface) : ViewModel() {
    var state by mutableStateOf(
        PostModel(
            loading = true,
            posts = mutableListOf(),
            showAddPostDialog = false
        )
    )
        private set

    init {
        getPosts()
    }

    private fun getPosts() {
        viewModelScope.launch {
            try {
                val response = api.getPosts()
                state = state.copy(posts = response.toMutableList())
            } catch (e: HttpException) {
                Log.d("$$$$", "Get posts: ${e.message}")
            } finally {
                toggleLoading()
            }
        }
    }

    fun onEvent(event: PostEvent) {
        when (event) {
            is PostEvent.OnAddPost -> {
                viewModelScope.launch {
                    toggleAddPostDialog()
                    try {
                        val post = Post(
                            title = event.postTitle,
                            body = "",
                            userId = 1,
                            id = state.posts.size
                        )

                        val response = api.addPost(post)
                        val newPosts = state.posts.toMutableList()
                        newPosts.add(response)
                        state = state.copy(
                            posts = newPosts
                        )
                    } catch (e: HttpException) {
                        Log.d("$$$$", "Add post: ${e.message}")
                    }
                }
            }
            is PostEvent.OnEditPost -> {
                viewModelScope.launch {
                    try {
                        toggleEditMode()
                        val updatedPost = event.post.copy(title = event.updatedTitle)
                        val response = api.updatePost(updatedPost.id, updatedPost)
                        updatePosts(response)
                    } catch (e: HttpException) {
                        Log.d("$$$$", "Update post: ${e.message}")
                    }
                }
            }
            is PostEvent.OnDeletePost -> {
                viewModelScope.launch {
                    try {
                        api.deletePost(event.postId)
                        state = state.copy(
                            posts = state.posts.filter { post -> post.id != event.postId }
                                .toMutableList()
                        )
                    } catch (e: HttpException) {
                        Log.d("$$$$", "Delete post: ${e.message}")
                    }
                }
            }
            is PostEvent.OnToggleEditMode -> {
                toggleEditMode(event.postId)
            }
            is PostEvent.OnToggleShowAddPostDialog -> {
                toggleAddPostDialog()
            }
        }
    }

    fun toggleEditMode(postId: Int? = null) {
        state = state.copy(editPostId = postId)
    }

    private fun toggleAddPostDialog() {
        state = state.copy(showAddPostDialog = !state.showAddPostDialog)
    }

    private fun toggleLoading() {
        state = state.copy(loading = !state.loading)
    }

    private fun updatePosts(updatedPost: Post) {
        val newPosts = state.posts.map { post ->
            if (post.id == updatedPost.id) {
                updatedPost
            } else {
                post
            }
        }.toMutableList()
        state = state.copy(posts = newPosts)
    }
}