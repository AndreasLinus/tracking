package models

import java.util.*

class User(val name: String, val ID: String = UUID.randomUUID().toString())