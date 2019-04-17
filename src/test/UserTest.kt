package test

import models.User
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*

internal class UserTest {

    @Test
    fun getName() {
        val name = "Hans"
        val user = User(name)
        assertEquals(name, user.name)
    }

    @Test
    fun getUserID() {
        val userID = UUID.randomUUID().toString()
        val user = User("test", userID)
        assertEquals(userID, user.ID)

    }
}