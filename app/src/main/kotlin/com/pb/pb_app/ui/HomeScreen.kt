package com.pb.pb_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pb.pb_app.ui.theme.Values


@Preview
@Composable
fun TopAppBarPreview() {
    TopAppBar(Modifier.width(1080.dp), {}, {}, "Home")
}

@Composable
fun TopAppBar(
    modifier: Modifier,
    openHamburgerMenu: () -> Unit,
    openNotificationShade: () -> Unit,
    pageTitle: String
) {
    Surface(
        color = MaterialTheme.colorScheme.primary, modifier = modifier.height(Values.appBarHeight)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { openHamburgerMenu() }) {
                Icon(Icons.Default.Menu, contentDescription = "")
            }
            Text(pageTitle, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = openNotificationShade) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }
        }
    }
}


@Preview
@Composable
fun HomeScreen() {
    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            openHamburgerMenu = { },
            openNotificationShade = { },
            pageTitle = "Home"
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Greetings!")
            PBToggle()
        }
    }

}