package com.uhc.workouttracker.authentication.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.uhc.workouttracker.authentication.domain.model.AuthState
import com.uhc.workouttracker.authentication.presentation.components.OTPDialog
import com.uhc.workouttracker.authentication.presentation.components.OTPDialogState
import com.uhc.workouttracker.authentication.presentation.components.PasswordField
import com.uhc.workouttracker.authentication.presentation.components.PasswordRecoveryDialog
import com.uhc.workouttracker.core.ui.AnimatedButton
import com.uhc.workouttracker.core.ui.WorkoutTextField
import com.uhc.workouttracker.core.theme.WorkoutTrackerTheme
import com.uhc.workouttracker.navigation.LocalNavController
import com.uhc.workouttracker.navigation.NavRoute
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(AuthUiExperimental::class)
@Composable
fun LoginScreen() {
    val navController = LocalNavController.current
    val viewModel: LoginViewModel = koinViewModel()
    val sessionStatus by viewModel.sessionStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loginAlert by viewModel.alert.collectAsState()

    var signUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otpDialogState by remember { mutableStateOf<OTPDialogState>(OTPDialogState.Invisible) }
    var showPasswordRecoveryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(sessionStatus) {
        if (sessionStatus is AuthState.Authenticated) {
            navController?.navigate(NavRoute.WorkoutListDestination)
        }
    }

    LoginLayout(
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        isSignUp = signUp,
        onToggleSignUp = { signUp = !signUp },
        isLoading = isLoading,
        onAuthenticate = { authenticate(signUp, viewModel, email, password) },
        onLoginWithGoogle = { viewModel.loginWithGoogle() },
        onLoginWithOTP = { otpDialogState = OTPDialogState.Visible(email) },
        onForgotPassword = { showPasswordRecoveryDialog = true }
    )

    if (otpDialogState is OTPDialogState.Visible) {
        val state = (otpDialogState as OTPDialogState.Visible)
        OTPDialog(
            email = state.email,
            title = state.title,
            onDismiss = { otpDialogState = OTPDialogState.Invisible },
            onConfirm = { otpEmail, code ->
                viewModel.loginWithOTP(otpEmail, code, state.resetFlow)
            }
        )
    }

    if (showPasswordRecoveryDialog) {
        PasswordRecoveryDialog(
            onDismiss = { showPasswordRecoveryDialog = false },
            onConfirm = { recoveryEmail ->
                viewModel.resetPassword(recoveryEmail)
                otpDialogState = OTPDialogState.Visible(
                    title = "Password recovery",
                    email = recoveryEmail,
                    resetFlow = true
                )
            }
        )
    }

    if (loginAlert != null) {
        AlertDialog(
            onDismissRequest = { viewModel.alert.value = null },
            text = { Text(loginAlert!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.alert.value = null }) {
                    Text("Ok")
                }
            }
        )
    }
}

@OptIn(AuthUiExperimental::class)
@Composable
internal fun LoginLayout(
    email: String = "",
    onEmailChange: (String) -> Unit = {},
    password: String = "",
    onPasswordChange: (String) -> Unit = {},
    isSignUp: Boolean = false,
    onToggleSignUp: () -> Unit = {},
    isLoading: Boolean = false,
    onAuthenticate: () -> Unit = {},
    onLoginWithGoogle: () -> Unit = {},
    onLoginWithOTP: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    val passwordFocus = remember { FocusRequester() }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = contentVisible,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                animationSpec = tween(500),
                initialOffsetY = { it / 6 }
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Workout Tracker",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Track your progress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))

                WorkoutTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    singleLine = true,
                    label = "E-Mail",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() }),
                    leadingIcon = { Icon(Icons.Filled.Mail, "Mail") },
                    modifier = Modifier.fillMaxWidth()
                )
                PasswordField(
                    password = password,
                    onPasswordChanged = onPasswordChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocus)
                        .padding(top = 10.dp),
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(onDone = { onAuthenticate() }),
                )

                AnimatedButton(
                    onClick = onAuthenticate,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    enabled = email.isNotBlank() && password.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        AnimatedContent(
                            targetState = isSignUp,
                            transitionSpec = {
                                fadeIn(tween(200)) + slideInVertically { -it } togetherWith
                                    fadeOut(tween(200)) + slideOutVertically { it }
                            },
                            label = "loginRegisterText"
                        ) { isSignUpState ->
                            Text(if (isSignUpState) "Register" else "Login")
                        }
                    }
                }

                OutlinedButton(
                    onClick = onLoginWithGoogle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProviderButtonContent(
                        Google,
                        text = if (isSignUp) "Sign Up with Google" else "Login with Google"
                    )
                }

                TextButton(onClick = onLoginWithOTP) {
                    Text("Login with an OTP")
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onToggleSignUp) {
                AnimatedContent(
                    targetState = isSignUp,
                    transitionSpec = {
                        fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                    },
                    label = "toggleModeText"
                ) { isSignUpState ->
                    Text(if (isSignUpState) "Already have an account? Login" else "Not registered? Register")
                }
            }
            TextButton(onClick = onForgotPassword) {
                Text("Forgot password?")
            }
        }
    }
}

@Preview
@Composable
private fun LoginPreview() {
    WorkoutTrackerTheme {
        LoginLayout()
    }
}

@Preview
@Composable
private fun LoginSignUpPreview() {
    WorkoutTrackerTheme {
        LoginLayout(
            email = "user@example.com",
            password = "password",
            isSignUp = true
        )
    }
}

@Preview
@Composable
private fun LoginLoadingPreview() {
    WorkoutTrackerTheme {
        LoginLayout(
            email = "user@example.com",
            password = "password",
            isLoading = true
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
