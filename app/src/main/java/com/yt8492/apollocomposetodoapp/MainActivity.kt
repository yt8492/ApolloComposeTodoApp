package com.yt8492.apollocomposetodoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.CompiledField
import com.apollographql.apollo3.api.Executable
import com.apollographql.apollo3.cache.normalized.api.*
import com.apollographql.apollo3.cache.normalized.logCacheMisses
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.yt8492.apollocomposetodoapp.infra.ApolloClientFactory
import com.yt8492.apollocomposetodoapp.ui.MainView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apolloClient = ApolloClientFactory.create()
        setContent {
            MainView(apolloClient)
        }
    }
}