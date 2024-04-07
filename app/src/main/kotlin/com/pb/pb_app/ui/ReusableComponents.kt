package com.pb.pb_app.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pb.pb_app.utils.models.projects.Enquiry

private const val TAG = "ReusableComponents"

class PBUsernameVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val newText = text.toUpperCase()
        return TransformedText(newText, OffsetMapping.Identity)
    }
}

class PBPasswordVisualTransformation(private val shouldShowPassword: Boolean) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return if (shouldShowPassword) {
            TransformedText(text, OffsetMapping.Identity)
        } else {
            TransformedText(AnnotatedString("•".repeat(text.length)), OffsetMapping.Identity)
        }
    }
}


val pbPasswordKeyboardOptions = KeyboardOptions(
    KeyboardCapitalization.None,
    false,
    KeyboardType.Password,
    ImeAction.Done,
)
val pbUsernameKeyboardOptions = KeyboardOptions(
    KeyboardCapitalization.Characters,
    false,
    KeyboardType.Text,
    ImeAction.Next,
)
val pbUsernameKeyboardActions: KeyboardActions
    @Composable get() {
        val focusManager = LocalFocusManager.current

        return KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Next)
        })
    }
val pbPasswordKeyboardActions: KeyboardActions
    @Composable get() {
        val localKeyboardControllerManager = LocalSoftwareKeyboardController.current

        return KeyboardActions(onDone = {
            localKeyboardControllerManager?.hide()
        })
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
fun CredentialsField(
    text: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Username",
    visualTransformation: VisualTransformation = PBUsernameVisualTransformation(),
    keyboardOptions: KeyboardOptions = pbUsernameKeyboardOptions,
    keyboardActions: KeyboardActions = pbUsernameKeyboardActions,
    onShowPasswordToggle: (() -> Unit)? = null,
    isError: Boolean? = null,
    supportingText: String = ""
) {

    TextField(
        value = text,
        onValueChange = { onValueChange(it) },
        modifier = Modifier.widthIn(TextFieldDefaults.MinWidth, TextFieldDefaults.MinWidth),
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        placeholder = { Text(placeholder) },
        supportingText = { if (supportingText.isNotEmpty()) Text(supportingText) },
        label = { Text(placeholder) },
        trailingIcon = {
            if (onShowPasswordToggle != null) ShowPasswordIconToggle(onShowPasswordToggle)
        },
        isError = isError ?: false
    )
}

@Composable
fun LoginFailureSnackbar() {
    Toast.makeText(LocalContext.current, "Incorrect credentials, try again", Toast.LENGTH_SHORT).show()
}

@Composable
fun ShowPasswordIconToggle(onClick: () -> Unit) {
    var toggle by remember { mutableStateOf(false) }

    IconButton({
        onClick()
        toggle = !toggle
    }) {
        Icon(if (toggle) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Show password toggle")
    }
}

@Preview
@Composable
fun SectionHeader(label: String = "Enquiries") {
    Text(
        label.uppercase(),
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}


@Composable
fun InquiryTitleText(modifier: Modifier = Modifier, title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
}

@Composable
fun InquiryDescriptionText(modifier: Modifier = Modifier, projectDescription: String) {
    Text(modifier = modifier.padding(4.dp), text = projectDescription, style = MaterialTheme.typography.bodySmall)
}

@Composable
fun TableRow(modifier: Modifier = Modifier, isEven: Boolean, vararg rowCells: @Composable RowScope.() -> Unit) {
    val bgColor = if (isEven) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    Row(
        modifier
            .background(bgColor)
            .padding(4.dp), horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (cell in rowCells) {
            cell()
        }
    }
}

@Composable
fun LoginButton(isEnabled: Boolean, onClick: () -> Unit) {
    Button(
        modifier = Modifier.widthIn(TextFieldDefaults.MinWidth, TextFieldDefaults.MinWidth), onClick = onClick, enabled = isEnabled
    ) {
        Text(text = "Log in", modifier = Modifier)
    }
}

@Composable
fun InquiryCard(modifier: Modifier = Modifier, enquiry: Enquiry, actionButtons: @Composable RowScope.() -> Unit) {

    Column(
        modifier
            .aspectRatio(1.0F)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .padding(12.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InquiryTitleText(title = enquiry.name)
        InquiryDescriptionText(Modifier.weight(1F), projectDescription = enquiry.description)
        Row {
            actionButtons()
        }
    }
}


@Composable
fun Table(title: String, cellLayout: LazyListScope.() -> Unit) {
    val borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)

    Surface(
        border = borderStroke
    ) {
        Text(title, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .heightIn(120.dp, 180.dp)
        ) {
            cellLayout()
        }
    }
}


@Composable
fun BooleanButton(positiveLabel: String, negativeLabel: String, onInteracted: (Boolean) -> Unit) {
    val colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally), modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = {
            onInteracted(true)
        }, colors = colors, modifier = Modifier.weight(1F)) {
            Text(text = positiveLabel)
        }
        TextButton(onClick = {
            onInteracted(false)
        }, colors = colors, modifier = Modifier.weight(1F)) {
            Text(text = negativeLabel)
        }
    }
}

@Preview
@Composable
fun EnquiryActionLayout(onEmployeeSelect: (String) -> Unit = {}) {
    var inquiryAccepted by remember { mutableStateOf(false) }

    val buttonEnterTransition = slideIn {
        IntOffset(-it.width, 0)
    }
    val buttonExitTransition = slideOut {
        IntOffset(-it.width, 0)
    }
    val fieldEnterTransition = slideIn {
        IntOffset(it.width, 0)
    }

    val fieldExitTransition = slideOut {
        IntOffset(it.width, 0)
    }

    AnimatedVisibility(!inquiryAccepted, enter = buttonEnterTransition, exit = buttonExitTransition) {
        BooleanButton("Accept", "Reject") {
            inquiryAccepted = true
        }
    }
    AnimatedVisibility(visible = inquiryAccepted, enter = fieldEnterTransition, exit = fieldExitTransition) {
        ConfirmationTextField("Enter Employee Username") {
            inquiryAccepted = false
            onEmployeeSelect(it)
        }
    }
}

@Composable
private fun ConfirmationTextField(placeholder: String, onDone: (String) -> Unit) {
    var text by remember {
        mutableStateOf("")
    }

    val fieldModifier = Modifier
        .height(ButtonDefaults.MinHeight)
        .clip(ButtonDefaults.shape)
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .padding(
            horizontal = TextFieldDefaults
                .contentPaddingWithLabel()
                .calculateLeftPadding(LayoutDirection.Ltr)
        )

    val iconButtonColors =
        IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)

    BasicTextField(value = text, onValueChange = { text = it }, singleLine = true) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(fieldModifier.weight(1F), contentAlignment = Alignment.CenterStart) {
                if (text.isEmpty()) Text(text = placeholder, color = TextFieldDefaults.colors().focusedPlaceholderColor)
                it()
            }
            IconButton(
                onClick = { onDone(text) }, colors = iconButtonColors
            ) {
                Icon(Icons.Default.Done, "", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun HorizontalCardCarousel(
    modifier: Modifier = Modifier, enquiryLayout: LazyListScope.() -> Unit
) {
    LazyRow(
        modifier
            .height(250.dp)
            .fillMaxWidth(), contentPadding = PaddingValues(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        enquiryLayout()
    }
}

@Composable
fun Dp.toPx(): Int {
    with(LocalDensity.current) {
        return roundToPx()
    }
}