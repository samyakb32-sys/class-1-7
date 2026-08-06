package com.gumthala.learningapp

import com.gumthala.learningapp.data.session.PasswordHasher
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordHasherTest {

    @Test
    fun `correct password verifies, wrong password does not`() {
        val hashed = PasswordHasher.hash("Sup3rSecret!")
        assertTrue(PasswordHasher.verify("Sup3rSecret!", hashed.salt, hashed.hash))
        assertFalse(PasswordHasher.verify("wrongpassword", hashed.salt, hashed.hash))
        assertFalse(PasswordHasher.verify("Sup3rSecret", hashed.salt, hashed.hash))
    }

    @Test
    fun `salts and hashes are unique per call even for the same password`() {
        val a = PasswordHasher.hash("password123")
        val b = PasswordHasher.hash("password123")
        assertNotEquals(a.salt, b.salt)
        assertNotEquals(a.hash, b.hash)
        // but each should still verify correctly against its own salt
        assertTrue(PasswordHasher.verify("password123", a.salt, a.hash))
        assertTrue(PasswordHasher.verify("password123", b.salt, b.hash))
    }
}
