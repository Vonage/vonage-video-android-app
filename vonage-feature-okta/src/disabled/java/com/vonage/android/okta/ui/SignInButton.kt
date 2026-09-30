package com.vonage.android.okta.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.vonage.android.okta.VonageOktaAuth

@Suppress("UnusedParameter", "EmptyFunctionBlock")
@Composable
fun SignInButton(
    auth: VonageOktaAuth,
    modifier: Modifier = Modifier,
) {

}

@Suppress("UnusedParameter")
@Composable
fun AuthenticationRequiredSheet(
    auth: VonageOktaAuth,
    onAuthenticated: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Authentication cannot be performed without the Okta SDK: dismiss so the caller never gets stuck.
    LaunchedEffect(Unit) { onDismiss() }
}
