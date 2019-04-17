package models

import java.util.*


/**
 * Class representing an [Issue] in our [Issue] tracker
 *
 * @property title the title of the [Issue]
 * @property ID unique identifier for the issue
 * @property creationDate when it was created
 * @property state what [State] the issue is currently in
 * @property stateChangedDate when was the state last changed?
 * @property stateChangedComment comment about the change
 * @property userID the ID to whom the [Issue] belongs to
 * @property listOfComments a [List] of comments belonging to [Issue]
 */

class Issue(
    val title: String = "",
    val ID: String = UUID.randomUUID().toString(),
    val creationDate: Date = Calendar.getInstance().time,
    var state: State = State.TODO,
    var stateChangedDate: MutableList<Date> = mutableListOf(),
    var stateChangedComment: String? = null,
    var userID: String? = null,
    val listOfComments: MutableList<Comment> = mutableListOf()

)
