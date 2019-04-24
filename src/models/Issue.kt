package models

import java.util.*


/**
 * Class representing an [Issue] in our [Issue] tracker engine
 *
 * @property title the title of the [Issue]
 * @property ID unique identifier for the issue
 * @property creationDate when it was created
 * @property state what [State] the issue is currently in
 * @property stateChangedDate when was the state last changed?
 * @property stateChangedComment comment about the change
 * @property userID the ID to whom the [Issue] belongs to
 */

class Issue(
    val title: String = "",
    val ID: String = UUID.randomUUID().toString(),
    val creationDate: Date = Calendar.getInstance().time,
    var state: State = State.TODO,
    val stateChangedDate: MutableList<Date> = mutableListOf(),
    var stateChangedComment: String? = null,
    var userID: String? = null
)
