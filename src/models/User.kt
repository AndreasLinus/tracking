package models

import java.util.*

/**
 * Class representing a User
 *
 * @property name the name of the User
 * @property ID the unique identifier of the User
 */
class User(val name: String, val ID: String = UUID.randomUUID().toString())