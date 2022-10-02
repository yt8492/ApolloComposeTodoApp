package com.yt8492.apollocomposetodoapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.apollographql.apollo3.ApolloClient
import com.yt8492.apollocomposetodoapp.ui.pages.todocreate.TodoCreatePage
import com.yt8492.apollocomposetodoapp.ui.pages.tododetail.TodoDetailPage
import com.yt8492.apollocomposetodoapp.ui.pages.todolist.TodoListPage
import com.yt8492.apollocomposetodoapp.ui.theme.ApolloComposeTodoAppTheme

@Composable
fun MainView(
    apolloClient: ApolloClient,
) {
    ApolloComposeTodoAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "todoList") {
                composable(
                    route = "todoList",
                ) {
                    TodoListPage(
                        navController = navController,
                        apolloClient = apolloClient,
                    )
                }

                composable(
                    route = "todoDetail?id={id}",
                    arguments = listOf(
                        navArgument(
                            name = "id"
                        ) {
                            type = NavType.StringType
                        }
                    ),
                ) { backStackEntry ->
                    val id = requireNotNull(backStackEntry.arguments?.getString("id"))
                    TodoDetailPage(
                        id = id,
                        navController = navController,
                        apolloClient = apolloClient,
                    )
                }

                composable(
                    route = "todoCreate",
                ) {
                    TodoCreatePage(
                        navController = navController,
                        apolloClient = apolloClient,
                    )
                }
            }
        }
    }
}
