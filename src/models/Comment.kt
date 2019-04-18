package models

import java.util.*

/**
 * Object representing a comment that can be attached to an [Issue]
 *
 * @property issueID the id of the [Issue] that this comment belongs to
 * @property creationDate when this comment was created
 * @property comment the String representing the comment
 * @param userID the unique identifier of the [User] who wrote the comment
 */

class Comment(
    val issueID: String,
    val creationDate: Date = java.util.Calendar.getInstance().time,
    val userID: String,
    val comment: String? = null
)
