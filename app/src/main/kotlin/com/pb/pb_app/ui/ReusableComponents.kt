package com.pb.pb_app.ui

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pb.pb_app.utils.models.employees.GenericEmployee

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

@Composable
fun PBDialogLayout(shouldShowDialog: Boolean, dialogBoxContent: @Composable RowScope.() -> Unit, dialogBoxActionButtons: @Composable RowScope.() -> Unit) {
    val internalShouldShowDialog = remember {
        mutableStateOf(shouldShowDialog)
    }

    if (internalShouldShowDialog.value) {
        Dialog({ internalShouldShowDialog.value = false }) {
            Surface(shape = MaterialTheme.shapes.large, modifier = Modifier.size(400.dp, 250.dp)) {
                Row(Modifier.fillMaxSize()) {
                    dialogBoxContent()
                    dialogBoxActionButtons()
                }
            }
        }
    }
}

@Composable
fun PBBooleanSwitch(initialState: Boolean, onClick: (Boolean) -> Unit = {}) {
    val state = remember {
        mutableStateOf(initialState)
    }

    val shape = MaterialTheme.shapes.medium.copy(CornerSize(12.dp + 4.dp))

    val alignmentFloat by animateFloatAsState(
        if (state.value) {
            -1F
        } else {
            1F
        }, label = "indicator alignment"
    )

    Box(modifier = Modifier
        .size(120.dp, 40.dp)
        .background(MaterialTheme.colorScheme.primaryContainer, shape)
        .clip(shape)
        .clickable {
            state.value = !state.value
            onClick(state.value)
        }
        .padding(4.dp)) {

        PBBooleanSwitchStateLabel(
            label = "Online", modifier = Modifier
                .fillMaxWidth(0.5F)
                .align(Alignment.CenterStart)
        )
        PBBooleanSwitchStateLabel(
            label = "Offline", modifier = Modifier
                .fillMaxWidth(0.5F)
                .align(Alignment.CenterEnd)
        )
        PBBooleanSwitchStateIndicator(
            Modifier
                .align(BiasAlignment(alignmentFloat, 0F))
                .fillMaxHeight()
                .fillMaxWidth(0.5F)
        )

    }
}

@Composable
fun PBBooleanSwitchStateLabel(label: String, modifier: Modifier) {
    Text(text = label, modifier = modifier, textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall)
}

@Composable
fun PBBooleanSwitchStateIndicator(modifier: Modifier) {
    Spacer(modifier.background(MaterialTheme.colorScheme.primary.copy(0.5F), MaterialTheme.shapes.medium))
}

@Composable
fun CredentialsField(
    onValueChange: (String) -> Unit,
    placeholder: String = "Username",
    visualTransformation: VisualTransformation = PBUsernameVisualTransformation(),
    keyboardOptions: KeyboardOptions = pbUsernameKeyboardOptions,
    keyboardActions: KeyboardActions = pbUsernameKeyboardActions,
    onShowPasswordToggle: (() -> Unit)? = null,
    isError: Boolean? = null
) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it; onValueChange(it) },
        modifier = Modifier.widthIn(TextFieldDefaults.MinWidth, TextFieldDefaults.MinWidth),
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        placeholder = { Text(placeholder) },
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
fun EnquiryTitleText(modifier: Modifier = Modifier, title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
}

@Composable
fun EnquiryDescriptionText(modifier: Modifier = Modifier, projectDescription: String) {
    Text(modifier = modifier.padding(4.dp), text = projectDescription, style = MaterialTheme.typography.bodySmall)
}

@Composable
fun PBFab(onClick: () -> Unit) {
    ExtendedFloatingActionButton(onClick) {
        Icon(Icons.Default.Add, "Add new enquiry fab")
        Text("New Enquiry")
    }
}

@Composable
fun EmployeeCell(modifier: Modifier = Modifier, employee: GenericEmployee, isEven: Boolean = true) {
    val bgColor = if (isEven) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface

    Row(
        modifier
            .background(bgColor)
            .padding(4.dp), horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(employee.username, Modifier.weight(1F), textAlign = TextAlign.Center)
        Text(employee.name, Modifier.weight(1F), textAlign = TextAlign.Center)
        Text(
            if (employee.isUserOnline) "Online" else "Offline",
            Modifier.weight(1F),
            color = if (employee.isUserOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4F),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ForgotPasswordTextButton() {
    TextButton(onClick = {}) {
        Text(text = "Forgot password?")
    }
}


@Composable
fun LoginButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier.widthIn(TextFieldDefaults.MinWidth, TextFieldDefaults.MinWidth), onClick = onClick
    ) {
        Text(text = "Log in")
    }
}
