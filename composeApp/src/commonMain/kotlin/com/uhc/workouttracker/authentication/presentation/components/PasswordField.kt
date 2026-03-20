package com.uhc.workouttracker.authentication.presentation.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.uhc.workouttracker.core.ui.WorkoutTextField

@Composable
fun PasswordField(
    password: String,
    onPasswordChanged: (String) -> Unit,
    label: String = "Password",
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions()
) {
    var visible by remember { mutableStateOf(false) }
    WorkoutTextField(
        value = password,
        onValueChange = onPasswordChanged,
        leadingIcon = { Icon(Icons.Filled.Key, "Password") },
        label = label,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = keyboardActions,
        singleLine = true,
        trailingIcon = {
            IconButton({ visible = !visible }) {
                Icon(
                    if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    "",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        modifier = modifier
    )
}