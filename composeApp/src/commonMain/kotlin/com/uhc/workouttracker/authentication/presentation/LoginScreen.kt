package com.uhc.workouttracker.authentication.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uhc.workouttracker.authentication.presentation.components.OTPDialog
import com.uhc.workouttracker.authentication.presentation.components.OTPDialogState
import com.uhc.workouttracker.authentication.presentation.components.PasswordField
import com.uhc.workouttracker.authentication.presentation.components.PasswordRecoveryDialog
import com.uhc.workouttracker.navigation.NavRoute
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import org.koin.compose.viewmodel.koinViewModel

@OptIn(AuthUiExperimental::class)
@Composable
fun LoginScreen(
    navController: NavHostController
) {

    val viewModel: LoginViewModel = koinViewModel()

    val sessionStatus by viewModel.sessionStatus.collectAsState()

    LaunchedEffect(sessionStatus) {
        if (sessionStatus is SessionStatus.Authenticated) {
            navController.navigate(NavRoute.WorkoutListDestination.value)
        }
    }

    var signUp by remember { mutableStateOf(false) }
    val loginAlert by viewModel.alert.collectAsState()
    var email by remember { mutableStateOf("") }
    var otpDialogState by remember { mutableStateOf<OTPDialogState>(OTPDialogState.Invisible) }
    var showPasswordRecoveryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var password by remember { mutableStateOf("") }
        val passwordFocus = remember { FocusRequester() }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            label = { Text("E-Mail") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() }),
            leadingIcon = { Icon(Icons.Filled.Mail, "Mail") },
        )
        PasswordField(
            password = password,
            onPasswordChanged = { password = it },
            modifier = Modifier.focusRequester(passwordFocus)
                .padding(top = 10.dp),
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = {
                authenticate(signUp, viewModel, email, password)
            }),
        )

        Button(
            onClick = { authenticate(signUp, viewModel, email, password) },
            modifier = Modifier.padding(top = 10.dp),
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            Text(if (signUp) "Register" else "Login")
        }

        OutlinedButton(
            onClick = {
                viewModel.loginWithGoogle()
            }
        ) {
            ProviderButtonContent(Google, text = if (signUp) "Sign Up with Google" else "Login with Google")
        }

        TextButton(
            onClick = { otpDialogState = OTPDialogState.Visible(email) }
        ) {
            Text("Login with an OTP")
        }

    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        TextButton(onClick = { signUp = !signUp }) {
            Text(if (signUp) "Already have an account? Login" else "Not registered? Register")
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        TextButton(onClick = { showPasswordRecoveryDialog = true }) {
            Text("Forgot password?")
        }
    }

    if(otpDialogState is OTPDialogState.Visible) {
        val state = (otpDialogState as OTPDialogState.Visible)
        OTPDialog(
            email = state.email,
            title = state.title,
            onDismiss = { otpDialogState = OTPDialogState.Invisible },
            onConfirm = { email, code ->
                viewModel.loginWithOTP(email, code, state.resetFlow)
            }
        )
    }

    if(showPasswordRecoveryDialog) {
        PasswordRecoveryDialog(
            onDismiss = { showPasswordRecoveryDialog = false },
            onConfirm = { email ->
                viewModel.resetPassword(email)
                otpDialogState = OTPDialogState.Visible(title = "Password recovery", email = email, resetFlow = true)
            }
        )
    }

    if(loginAlert != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.alert.value = null
            },
            text = {
                Text(loginAlert!!)
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.alert.value = null
                }) {
                    Text("Ok")
                }
            }
        )
    }
}

fun authenticate(signUp: Boolean, viewModel: LoginViewModel, email: String, password: String) {
    if (signUp) {
        viewModel.signUp(email, password)
    } else {
        viewModel.login(email, password)
    }
}
