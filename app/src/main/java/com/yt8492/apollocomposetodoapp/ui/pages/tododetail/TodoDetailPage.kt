package com.yt8492.apollocomposetodoapp.ui.pages.tododetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.apollographql.apollo3.ApolloClient
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.yt8492.apollocomposetodoapp.TodoDetailPageQuery
import com.yt8492.apollocomposetodoapp.ToggleTodoDoneMutation
import com.yt8492.apollocomposetodoapp.infra.mutation
import com.yt8492.apollocomposetodoapp.ui.theme.Typography
import com.yt8492.apollocomposetodoapp.infra.watch
import kotlinx.coroutines.launch

@Composable
fun TodoDetailPage(
    id: String,
    navController: NavController,
    apolloClient: ApolloClient,
) {
    val coroutineScope = rememberCoroutineScope()

    val (data, loading, detailError, refetch) = apolloClient.watch(query = TodoDetailPageQuery(id))
    val (toggle, _, _, toggleError) = apolloClient.mutation<ToggleTodoDoneMutation.Data>()

    val error = detailError ?: toggleError
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Todo Detail")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back to list",
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        if (error != null) {
            AlertDialog(
                onDismissRequest = {
                    coroutineScope.launch {
                        refetch()
                    }
                },
                text = {
                    Text(text = error.message)
                },
                buttons = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    refetch()
                                }
                            },
                        ) {
                            Text(text = "ok")
                        }
                    }
                },
            )
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
            val todo = data?.todoRoot?.todo ?: return@SwipeRefresh
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
            ) {
                item {
                    Text(
                        text = "title",
                        style = Typography.subtitle1,
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text(text = todo.title)
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "done:",
                            style = Typography.subtitle1,
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                        Checkbox(
                            checked = todo.done,
                            onCheckedChange = { done ->
                                coroutineScope.launch {
                                    toggle(ToggleTodoDoneMutation(todo.id, done))
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
