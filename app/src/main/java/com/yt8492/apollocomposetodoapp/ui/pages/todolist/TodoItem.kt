package com.yt8492.apollocomposetodoapp.ui.pages.todolist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yt8492.apollocomposetodoapp.fragment.TodoListItem

@Composable
fun TodoItem(
    item: TodoListItem,
    toggleDone: (Boolean) -> Unit,
    navigateToDetail: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigateToDetail()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = item.title)
            Spacer(modifier = Modifier.weight(1f))
            Checkbox(checked = item.done, onCheckedChange = toggleDone)
        }
        Divider()
    }
}
