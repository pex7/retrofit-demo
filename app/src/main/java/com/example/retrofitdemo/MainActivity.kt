package com.example.retrofitdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.retrofitdemo.ui.theme.RetrofitDemoTheme

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val postApi = PostApi.getApi()

        mainViewModel = MainViewModel(postApi)

        setContent {
            RetrofitDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PostList(mainViewModel)
                }
            }
        }
    }
}

@Composable
fun PostList(
    viewModel: MainViewModel = viewModel()
) {
    val state = viewModel.state
    val posts = state.posts
    var editTitleText by remember { mutableStateOf("") }
    val onTextChange = { title: String -> editTitleText = title }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(PostEvent.OnToggleShowAddPostDialog)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add post")
            }
        }
    ) {
        if (state.loading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
        }
        LazyColumn {
            items(posts) { post ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(
                                color = Color.Black,
                                start = Offset(0f, 1f),
                                end = Offset(size.width, 1f),
                                strokeWidth = Dp.Hairline.toPx()
                            )
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.editPostId == post.id) {
                        EditPost(editTitleText, onTextChange)
                        Row {
                            IconButton(onClick = {
                                viewModel.onEvent(PostEvent.OnEditPost(post, editTitleText))
                                editTitleText = ""
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Save post")
                            }
                        }
                    } else {
                        Text(
                            text = post.title,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row {
                            IconButton(onClick = { viewModel.toggleEditMode(post.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit post")
                            }
                            IconButton(onClick = {
                                viewModel.onEvent(PostEvent.OnDeletePost(post.id))
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Delete post")
                            }
                        }
                    }
                }
            }
        }
        if (state.showAddPostDialog) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.onEvent(PostEvent.OnAddPost(editTitleText))
                        editTitleText = ""
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.onEvent(PostEvent.OnToggleShowAddPostDialog)
                    }) {
                        Text("Cancel")
                    }
                },
                title = {
                    Text("Add post")
                },
                text = {
                    EditPost(editTitleText, onTextChange)
                }
            )
        }
    }
}

@Composable
fun EditPost(updatedText: String, onChange: (String) -> Unit) {
    TextField(
        value = updatedText,
        onValueChange = { onChange(it) },
        label = { Text("Title") }
    )
}