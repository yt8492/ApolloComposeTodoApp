package com.yt8492.apollocomposetodoapp.ui.pages.todolist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.apollographql.apollo3.ApolloClient
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.yt8492.apollocomposetodoapp.TodoListPageQuery
import com.yt8492.apollocomposetodoapp.ToggleTodoDoneMutation
import com.yt8492.apollocomposetodoapp.infra.mutation
import com.yt8492.apollocomposetodoapp.infra.watch
import com.yt8492.apollocomposetodoapp.ui.common.ErrorDialog
import kotlinx.coroutines.launch

@Composable
fun TodoListPage(
    navController: NavController,
    apolloClient: ApolloClient,
) {
    val coroutineScope = rememberCoroutineScope()

    val (data, loading, listError, refetch) = apolloClient.watch(query = TodoListPageQuery())
    val (toggle, _, _, toggleError) = apolloClient.mutation<ToggleTodoDoneMutation.Data>()

    val error = listError ?: toggleError
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Todo List")
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("todoCreate")
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "create todo",
                )
            }
        }
    ) { paddingValues ->
        if (error != null) {
            ErrorDialog(error = error)
        }
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = loading),
            onRefresh = {
                coroutineScope.launch {
                    refetch()
                }
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    Text(
                        text = "All Todos",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    )
                }
                val allTodos = data?.todoRoot?.all ?: listOf()
                items(allTodos) { todo ->
                    TodoItem(
                        item = todo.todoListItem,
                        toggleDone = { done ->
                            coroutineScope.launch {
                                toggle(ToggleTodoDoneMutation(todo.id, done))
                            }
                        },
                        navigateToDetail = {
                            navController.navigate("todoDetail?id=${todo.id}")
                        },
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    Text(
                        text = "Completed Todos",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    )
                }
                val completedTodos = data?.todoRoot?.completed ?: listOf()
                items(completedTodos) { todo ->
                    TodoItem(
                        item = todo.todoListItem,
                        toggleDone = { done ->
                            coroutineScope.launch {
                                toggle(ToggleTodoDoneMutation(todo.id, done))
                            }
                        },
                        navigateToDetail = {
                            navController.navigate("todoDetail?id=${todo.id}")
                        },
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    Text(
                        text = "UnCompleted Todos",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    )
                }
                val unCompletedTodos = data?.todoRoot?.unCompleted ?: listOf()
                items(unCompletedTodos) { todo ->
                    TodoItem(
                        item = todo.todoListItem,
                        toggleDone = { done ->
                            coroutineScope.launch {
                                toggle(ToggleTodoDoneMutation(todo.id, done))
                            }
                        },
                        navigateToDetail = {
                            navController.navigate("todoDetail?id=${todo.id}")
                        },
                    )
                }
            }
        }
    }
}