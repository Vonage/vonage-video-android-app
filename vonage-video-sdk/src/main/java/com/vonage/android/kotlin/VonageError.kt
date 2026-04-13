package com.vonage.android.kotlin

/**
 * Domain-level error from the Vonage Video SDK.
 *
 * Replaces direct use of OpenTok error types, making the error fully mockable
 * and decoupled from the underlying SDK implementation.
 *
 * @property code Numeric error code
 * @property message Human-readable error description
 * @property domain Error domain identifier
 */
data class VonageError(
    val code: Int,
    val message: String,
    val domain: String = "",
)
