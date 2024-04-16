package com.pb.pb_app.ui.reusables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun Dp.toPx(): Int {
    with(LocalDensity.current) {
        return roundToPx()
    }
}

@Composable
fun HorizontalCarousel(
    modifier: Modifier = Modifier, itemsCount: Int, itemsLayout: @Composable (Int) -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        if (itemsCount == 0) Text(text = "Nothing to show", style = MaterialTheme.typography.labelMedium, modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)


        LazyRow(
            modifier
                .height(300.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            items(itemsCount) {
                itemsLayout(it)
            }
        }
    }
}

@Preview
@Composable
fun PBBooleanSwitch(initialState: Boolean = true, onToggle: (Boolean) -> Unit = {}) {
    val state = remember {
        mutableStateOf(initialState)
    }

    val shape = RoundedCornerShape(12.dp + 4.dp)

    val alignmentFloat by animateFloatAsState(
        if (state.value) {
            -1F
        } else {
            1F
        }, label = "indicator alignment"
    )

    Box(modifier = Modifier
        .size(120.dp, 40.dp)
        .clip(shape)
        .border(1.dp, MaterialTheme.colorScheme.primaryContainer, shape)
        .clickable {
            state.value = !state.value
            onToggle(state.value)
        }
        .padding(4.dp)) {
        PBBooleanSwitchStateIndicator(
            Modifier
                .align(BiasAlignment(alignmentFloat, 0F))
                .fillMaxHeight()
                .fillMaxWidth(0.5F)
        )
        PBBooleanSwitchStateLabel(
            label = "Online",
            modifier = Modifier
                .fillMaxWidth(0.5F)
                .align(Alignment.CenterStart),
            textColor = if (state.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
        PBBooleanSwitchStateLabel(
            label = "Offline",
            modifier = Modifier
                .fillMaxWidth(0.5F)
                .align(Alignment.CenterEnd),
            textColor = if (!state.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PBBooleanSwitchStateLabel(label: String, modifier: Modifier, textColor: Color) {
    Text(text = label, modifier = modifier, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = textColor)
}

@Composable
fun PBBooleanSwitchStateIndicator(modifier: Modifier) {
    Spacer(modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)))
}

@Composable
fun SingleLineFormField(
    modifier: Modifier = Modifier, onTextChange: (String) -> Unit, placeholder: String, keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var text by remember {
        mutableStateOf("")
    }

    TextField(
        value = text, onValueChange = { text = it;onTextChange(text) }, placeholder = { Text(text = placeholder) }, modifier = modifier, singleLine = true, keyboardOptions = keyboardOptions
    )
}

@Composable
fun BigFormField(modifier: Modifier = Modifier, onTextChange: (String) -> Unit, placeholder: String) {
    var text by remember {
        mutableStateOf("")
    }
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = text,
        onValueChange = { text = it; onTextChange(it) },
        placeholder = { Text(text = placeholder) },
        singleLine = false
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PBMediumTopBar(title: String, isSaveIconEnabled: Boolean, onSave: () -> Unit) {
    MediumTopAppBar(title = { Text(text = title) }, actions = {
        TextButton(onClick = onSave, enabled = isSaveIconEnabled) {
            Text("Save")
        }
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(datePickerState: DatePickerState, onDismissRequest: () -> Unit, onConfirm: (Long) -> Unit) {
    DatePickerDialog(onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = { onConfirm(datePickerState.selectedDateMillis!!) }) {
            Text(text = "OK")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}