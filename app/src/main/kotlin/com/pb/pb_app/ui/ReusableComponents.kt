package com.pb.pb_app.ui

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun OnlineStatusSwitch() {
    val state = remember { mutableStateOf(true) }

    val minWidth = 56.dp;
    val minHeight = 32.dp;

    val onShape = ShapeDefaults.Large.copy(bottomEnd = CornerSize(0.dp), topEnd = CornerSize(0.dp))
    val offShape =
        ShapeDefaults.Large.copy(bottomStart = CornerSize(0.dp), topStart = CornerSize(0.dp))
    val stateWidth = minWidth + 4.dp
    val stateHeight = minHeight + 4.dp

    val statePosX = animateIntAsState(
        if (state.value) {
            with(LocalDensity.current) {
                ((stateWidth - minWidth) / 2).roundToPx()
            }
        } else {
            with(LocalDensity.current) {
                (stateWidth + (stateWidth - minWidth) / 2).roundToPx()
            }
        }
    )

    Layout({
        Text(
            text = "On",
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .size(stateWidth, stateHeight)
                .background(Color.Black, onShape)
                .wrapContentHeight(Alignment.CenterVertically)
        )
        Text(
            text = "Off",
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .size(stateWidth, stateHeight)
                .background(Color.Black, offShape)
                .wrapContentHeight(Alignment.CenterVertically)
        )
        Box(
            Modifier
                .size(minWidth, minHeight)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(0.5F),
                    MaterialTheme.shapes.large
                )
        )
    }, modifier = Modifier.clickable {
        state.value = !state.value
    }) { measurables, constraints ->

        val onStatePlaceable = measurables[0].measure(constraints)
        val offStatePlaceable = measurables[1].measure(constraints)
        val stateIndicatorPlaceable = measurables[2].measure(constraints)

        layout(
            onStatePlaceable.width + offStatePlaceable.width,
            offStatePlaceable.height
        ) {
            onStatePlaceable.place(0, 0)
            offStatePlaceable.place(onStatePlaceable.width, 0)
            stateIndicatorPlaceable.place(
                statePosX.value, (onStatePlaceable.height - stateIndicatorPlaceable.height) / 2
            )
        }

    }
}
