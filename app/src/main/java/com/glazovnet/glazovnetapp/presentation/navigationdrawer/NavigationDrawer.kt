package com.glazovnet.glazovnetapp.presentation.navigationdrawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R

private val items = listOf<NavigationDrawerItemData>(
    NavigationDrawerItemData(
        stringResource = R.string.posts_list_screen_name,
        icon = Icons.Default.DateRange,
        route = "posts-list-screen"
    ),
    NavigationDrawerItemData(
        stringResource = R.string.add_post_screen_name,
        icon = Icons.Default.List,
        route = "edit-posts-screen"
    )

)

@Composable
fun NavigationDrawer(
    modifier: Modifier = Modifier,
    onNavigate: (route: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        var selectedItemIndexed by rememberSaveable {
            mutableIntStateOf(0)
        }
        items.forEachIndexed {index, item ->
            NavigationDrawerItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                data = item,
                onClick = {
                    onNavigate.invoke(it)
                    selectedItemIndexed = index
                },
                isSelected = index == selectedItemIndexed
            )
        }
    }
}