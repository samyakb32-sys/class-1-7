package com.gumthala.learningapp.core

import java.security.SecureRandom
import java.util.Locale
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Local credential hashing for teacher/admin sign-in. Auth has to work with no
 * network, so the device is the source of truth; Firestore only mirrors the hash.
 */
object PasswordHasher {

    private const val ITERATIONS = 20_000
    private const val KEY_LENGTH = 256
    private const val ALGORITHM = "PBKDF2WithHmacSHA1"

    fun newSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    fun hash(password: String, salt: String): String {
        val spec = PBEKeySpec(password.toCharArray(), salt.hexToBytes(), ITERATIONS, KEY_LENGTH)
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded.toHex()
    }

    fun verify(password: String, salt: String, expectedHash: String): Boolean =
        constantTimeEquals(hash(password, salt), expectedHash)

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var diff = 0
        for (i in a.indices) diff = diff or (a[i].code xor b[i].code)
        return diff == 0
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.hexToBytes(): ByteArray =
        chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}

/** Student names are matched loosely so "  ravi  PATIL " signs in as "Ravi Patil". */
fun String.normalizedName(): String =
    trim().replace(Regex("\\s+"), " ").lowercase(Locale.ROOT)

fun String.normalizedEmail(): String = trim().lowercase(Locale.ROOT)
