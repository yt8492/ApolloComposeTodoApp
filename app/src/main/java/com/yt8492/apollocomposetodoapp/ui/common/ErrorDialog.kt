package com.yt8492.apollocomposetodoapp.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.yt8492.apollocomposetodoapp.infra.ApolloError
import com.yt8492.apollocomposetodoapp.ui.theme.Typography

@Composable
fun ErrorDialog(error: ApolloError) {
    val (isVisible, setVisible) = remember {
        mutableStateOf(true)
    }
    val closeDialog = {
        setVisible(false)
    }
    if (isVisible) {
        AlertDialog(
            title = {
                Text(
                    text = "エラー",
                    style = Typography.h6,
                )
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
                        onClick = closeDialog
                    ) {
                        Text(text = "OK")
                    }
                }
            },
            onDismissRequest = closeDialog,
        )
    }
}
