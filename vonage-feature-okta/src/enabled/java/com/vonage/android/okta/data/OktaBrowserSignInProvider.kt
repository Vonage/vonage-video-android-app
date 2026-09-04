package com.vonage.android.okta.data

import android.content.Context
import com.okta.authfoundation.AuthFoundation
import com.okta.authfoundation.client.OAuth2ClientBuilder
import com.okta.authfoundation.client.OAuth2ClientConfiguration
import com.okta.authfoundation.client.OAuth2ClientResult
import com.okta.authfoundation.client.TokenInfo
import com.okta.authfoundation.client.kmp.OAuth2Client
import com.okta.authfoundation.credential.kmp.AndroidTokenEncryptionHandler
import com.okta.authfoundation.credential.kmp.TokenCredentialManager
import com.okta.authfoundation.credential.kmp.TokenData
import com.okta.authfoundation.credential.kmp.storage.RoomDefaultCredentialIdStore
import com.okta.authfoundation.credential.kmp.storage.RoomTokenStorage
import com.okta.authfoundation.credential.kmp.storage.createTokenDatabase
import com.okta.webauthenticationui.WebAuthentication
import com.vonage.android.okta.OktaConfig

/**
 * Default [BrowserSignInProvider] backed by the
 * [okta-mobile-kotlin](https://github.com/okta/okta-mobile-kotlin) SDK.
 *
 * Sign-in opens a Chrome Custom Tab; tokens are persisted encrypted in the SDK's
 * Room-backed token storage so the session survives app restarts.
 */
internal class OktaBrowserSignInProvider(
    private val applicationContext: Context,
    private val config: OktaConfig,
) : BrowserSignInProvider {

    private val client: OAuth2Client by lazy {
        AuthFoundation.initializeAndroidContext(applicationContext)
        OAuth2ClientBuilder
            .create(
                issuerUrl = config.issuerUrl,
                clientId = config.clientId,
                scope = config.scope.split(" "),
            ).getOrThrow()
    }

    private val credentialManager: TokenCredentialManager by lazy {
        val database = createTokenDatabase(applicationContext)
        val storage = RoomTokenStorage(database, AndroidTokenEncryptionHandler(), client.configuration)
        val defaultIdStore = RoomDefaultCredentialIdStore(database)
        TokenCredentialManager(client, storage, defaultIdStore)
    }

    override suspend fun signIn(context: Context): Result<SignInResult> = runCatching {
        check(config.isValid) { "Okta is not configured. See docs/AUTHENTICATION.md." }
        val result = WebAuthentication(client).login(
            context = context,
            redirectUrl = config.signInRedirectUri,
            extraRequestParameters = emptyMap(),
            scope = config.scope,
        )
        when (result) {
            is OAuth2ClientResult.Error -> throw result.exception
            is OAuth2ClientResult.Success -> {
                val tokenData = result.result.toTokenData(client.configuration)
                val credential = credentialManager.store(tokenData).getOrThrow()
                credentialManager.setDefault(credential).getOrThrow()
                SignInResult(
                    accessToken = tokenData.accessToken,
                    userName = IdTokenDecoder.name(tokenData.idToken),
                )
            }
        }
    }

    override suspend fun currentToken(): String? {
        if (!config.isValid) return null
        val credential = credentialManager.getDefault().getOrNull() ?: return null
        return credential.accessTokenIfNotExpired()
            ?: credential.refreshToken().getOrNull()?.accessTokenIfNotExpired()
    }

    override suspend fun removeCredential(): Result<Unit> = runCatching {
        val credential = credentialManager.getDefault().getOrNull() ?: return@runCatching
        credential.deleteAsync().getOrThrow()
    }

    override suspend fun restoreSession(): SignInResult? {
        if (!config.isValid) return null
        val credential = credentialManager.getDefault().getOrNull() ?: return null
        val token = credential.token
        return SignInResult(
            accessToken = token.accessToken,
            userName = IdTokenDecoder.name(token.idToken),
        )
    }
}

/**
 * The KMP flows return a [TokenInfo] whose concrete runtime type is not [TokenData],
 * while [TokenCredentialManager.store] requires a [TokenData] snapshot — so every
 * freshly-minted token is converted explicitly rather than cast.
 */
internal fun TokenInfo.toTokenData(configuration: OAuth2ClientConfiguration): TokenData =
    TokenData(
        id = id,
        tokenType = tokenType,
        expiresIn = expiresIn,
        accessToken = accessToken,
        scope = scope,
        refreshToken = refreshToken,
        idToken = idToken,
        deviceSecret = deviceSecret,
        issuedTokenType = issuedTokenType,
        configuration = configuration,
        issuedAt = configuration.clock.currentTimeEpochSecond(),
    )
