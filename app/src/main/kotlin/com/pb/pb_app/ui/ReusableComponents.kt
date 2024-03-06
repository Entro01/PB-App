package com.pb.pb_app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val TAG = "ReusableComponents"


@Preview
@Composable
fun PBToggle(modifier: Modifier = Modifier, onClick: (Boolean) -> Unit = {}) {
    val state = remember {
        mutableStateOf(true)
    }

    val alignment = animateFloatAsState(
        if (state.value) {
            -1F
        } else {
            1F
        }
    )

    Box(modifier = modifier
        .size(120.dp, 40.dp)
        .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.large)
        .clickable {
            state.value = !state.value
            onClick(state.value)
        }) {
        Box(Modifier.padding(4.dp)) {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Online",
                    modifier = Modifier.weight(1F),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "Offline",
                    modifier = Modifier.weight(1F),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = BiasAlignment.Horizontal(alignment.value)
            ) {
                Spacer(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5F)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(0.5F),
                            MaterialTheme.shapes.medium
                        )
                )
            }
        }
    }
}