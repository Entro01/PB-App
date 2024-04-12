package com.pb.pb_app.utils.reusables

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

private const val TAG = "ReusableComponents"

class PBUsernameVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(text, OffsetMapping.Identity)
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

val phoneNumberKeyboardOptions = KeyboardOptions(
    autoCorrect = false, keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
)

val emailAddressKeyboardOptions = KeyboardOptions(
    autoCorrect = false, keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
)

val pbPasswordKeyboardOptions = KeyboardOptions(
    KeyboardCapitalization.None,
    false,
    KeyboardType.Password,
    ImeAction.Done,
)
val pbUsernameKeyboardOptions = KeyboardOptions(
    KeyboardCapitalization.None,
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

