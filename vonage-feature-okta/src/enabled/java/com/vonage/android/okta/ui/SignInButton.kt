package com.vonage.android.okta.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.components.VonageOutlinedButton
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.User2
import com.vonage.android.okta.AuthenticatedUser
import com.vonage.android.okta.IdProvider
import com.vonage.android.okta.R
import com.vonage.android.okta.VonageOktaAuth
import kotlinx.coroutines.launch

/**
 * Top-bar authentication entry point.
 *
 * Shows a person icon that opens the sign-in sheet when the user is not
 * authenticated, or the account menu (name + sign out) when they are.
 * The disabled flavor renders nothing, so callers need no feature-flag branching.
 */
@Composable
fun SignInButton(
    auth: VonageOktaAuth,
    modifier: Modifier = Modifier,
) {
    val authState by auth.authState.collectAsState()
    var showSignInSheet by remember { mutableStateOf(false) }
    var showAccountSheet by remember { mutableStateOf(false) }

    LaunchedEffect(auth) {
        auth.restoreSession()
    }

    IconButton(
        modifier = modifier.testTag(AuthTestTags.AUTH_BUTTON_TAG),
        onClick = {
            if (authState.isAuthenticated) {
                showAccountSheet = true
            } else {
                showSignInSheet = true
            }
        },
    ) {
        Icon(
            imageVector = VividIcons.Solid.User2,
            contentDescription = stringResource(
                if (authState.isAuthenticated) R.string.auth_signed_in else R.string.auth_sign_in
            ),
            tint = if (authState.isAuthenticated) {
                VonageVideoTheme.colors.primary
            } else {
                VonageVideoTheme.colors.onSurface
            },
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault),
        )
    }

    if (showSignInSheet) {
        SignInSheet(
            auth = auth,
            onDismiss = { showSignInSheet = false },
        )
    }

    val user = authState.user
    if (showAccountSheet && user != null) {
        AccountSheet(
            user = user,
            auth = auth,
            onDismiss = { showAccountSheet = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignInSheet(
    auth: VonageOktaAuth,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag(AuthTestTags.SIGN_IN_SHEET_TAG),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VonageVideoTheme.dimens.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceDefault),
        ) {
            Text(
                text = stringResource(R.string.auth_sign_in_title),
                style = VonageVideoTheme.typography.heading4,
                modifier = Modifier.testTag(AuthTestTags.SIGN_IN_TITLE_TAG),
            )
            Text(
                text = stringResource(R.string.auth_sign_in_subtitle),
                style = VonageVideoTheme.typography.bodyBase,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag(AuthTestTags.SIGN_IN_SUBTITLE_TAG),
            )
            errorMessage?.let { message ->
                Text(
                    text = message,
                    style = VonageVideoTheme.typography.caption,
                    color = VonageVideoTheme.colors.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag(AuthTestTags.SIGN_IN_ERROR_TAG),
                )
            }
            providers.forEach { provider ->
                VonageButton(
                    text = stringResource(R.string.auth_sign_in_with, provider.displayName),
                    enabled = !isLoading,
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            auth.signIn(context)
                                .onSuccess { onDismiss() }
                                .onFailure { error ->
                                    errorMessage = error.message
                                        ?: context.getString(R.string.auth_sign_in_error)
                                }
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("${AuthTestTags.SIGN_IN_PROVIDER_TAG_PREFIX}${provider.id}"),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountSheet(
    user: AuthenticatedUser,
    auth: VonageOktaAuth,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var isSigningOut by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag(AuthTestTags.ACCOUNT_MENU_TAG),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VonageVideoTheme.dimens.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceDefault),
        ) {
            user.name?.let { name ->
                Text(
                    text = name,
                    style = VonageVideoTheme.typography.heading4,
                    modifier = Modifier.testTag(AuthTestTags.ACCOUNT_NAME_TAG),
                )
            }
            VonageOutlinedButton(
                text = stringResource(R.string.auth_sign_out),
                enabled = !isSigningOut,
                onClick = {
                    scope.launch {
                        isSigningOut = true
                        auth.signOut()
                        isSigningOut = false
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(AuthTestTags.SIGN_OUT_BUTTON_TAG),
            )
        }
    }
}

private val providers = listOf(IdProvider.okta)
