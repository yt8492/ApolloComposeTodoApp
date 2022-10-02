package com.yt8492.apollocomposetodoapp.ui.pages.todocreate

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.apollographql.apollo3.ApolloClient
import com.yt8492.apollocomposetodoapp.CreateTodoMutation
import com.yt8492.apollocomposetodoapp.infra.mutation
import kotlinx.coroutines.launch

@Composable
fun TodoCreatePage(
    navController: NavController,
    apolloClient: ApolloClient,
) {
    val coroutineScope = rememberCoroutineScope()
    val (createTodo, data, loading, _) = apolloClient.mutation<CreateTodoMutation.Data>()
    LaunchedEffect(data) {
        if (data != null) {
            navController.popBackStack()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Create Todo")
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                val (title, setTitle) = remember {
                    mutableStateOf("")
                }
                Text(text = "Todo title")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = title,
                    onValueChange = setTitle,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            createTodo(CreateTodoMutation(title))
                        }
                    },
                    enabled = title.isNotBlank(),
                ) {
                    Text(text = "Create")
                }
            }
            if (loading) {
                CircularProgressIndicator()
            }
        }
    }
}
