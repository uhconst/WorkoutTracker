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
import com.uhc.workouttracker.navigation.LocalNavController
import com.uhc.workouttracker.navigation.NavRoute
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.compose.auth.ui.ProviderButtonContent
import io.github.jan.supabase.compose.auth.ui.annotations.AuthUiExperimental
import org.koin.compose.viewmodel.koinViewModel

@OptIn(AuthUiExperimental::class)
@Composable
fun LoginScreen() {
    val navController = LocalNavController.current

    val viewModel: LoginViewModel = koinViewModel()

    val sessionStatus by viewModel.sessionStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(sessionStatus) {
        if (sessionStatus is AuthState.Authenticated) {
            navController?.navigate(NavRoute.WorkoutListDestination)
        }
    }

    var signUp by remember { mutableStateOf(false) }
    val loginAlert by viewModel.alert.collectAsState()
    var email by remember { mutableStateOf("") }
    var otpDialogState by remember { mutableStateOf<OTPDialogState>(OTPDialogState.Invisible) }
    var showPasswordRecoveryDialog by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { contentVisible = true }

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
                // App name header
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

                var password by remember { mutableStateOf("") }
                val passwordFocus = remember { FocusRequester() }
                WorkoutTextField(
                    value = email,
                    onValueChange = { email = it },
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
                    onPasswordChanged = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocus)
                        .padding(top = 10.dp),
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(onDone = {
                        authenticate(signUp, viewModel, email, password)
                    }),
                )

                AnimatedButton(
                    onClick = { authenticate(signUp, viewModel, email, password) },
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    enabled = email.isNotBlank() && password.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        AnimatedContent(
                            targetState = signUp,
                            transitionSpec = {
                                fadeIn(tween(200)) + slideInVertically { -it } togetherWith
                                    fadeOut(tween(200)) + slideOutVertically { it }
                            },
                            label = "loginRegisterText"
                        ) { isSignUp ->
                            Text(if (isSignUp) "Register" else "Login")
                        }
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.loginWithGoogle() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProviderButtonContent(
                        Google,
                        text = if (signUp) "Sign Up with Google" else "Login with Google"
                    )
                }

                TextButton(
                    onClick = { otpDialogState = OTPDialogState.Visible(email) }
                ) {
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
            TextButton(onClick = { signUp = !signUp }) {
                AnimatedContent(
                    targetState = signUp,
                    transitionSpec = {
                        fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                    },
                    label = "toggleModeText"
                ) { isSignUp ->
                    Text(if (isSignUp) "Already have an account? Login" else "Not registered? Register")
                }
            }
            TextButton(onClick = { showPasswordRecoveryDialog = true }) {
                Text("Forgot password?")
            }
        }
    }

    if (otpDialogState is OTPDialogState.Visible) {
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

    if (showPasswordRecoveryDialog) {
        PasswordRecoveryDialog(
            onDismiss = { showPasswordRecoveryDialog = false },
            onConfirm = { email ->
                viewModel.resetPassword(email)
                otpDialogState = OTPDialogState.Visible(
                    title = "Password recovery",
                    email = email,
                    resetFlow = true
                )
            }
        )
    }

    if (loginAlert != null) {
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
