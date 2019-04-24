package models

import java.util.*

/*A collection of issues. The issues should include the most relevant information, such as state, title,
creation date, etc. NOTE: Comments and recorded state change history should not be included in the
result.*/

/**
 * A lightweight version of [Issue] with fewer properties
 *
 * @property ID unique identifier for this [IssueLight]
 * @property creationDate when the [IssueLight] was created
 * @property state the [State] the [IssueLight] is in
 * @property title the title of the [IssueLight]
 * @property userId unique identifier that identifies an [User] that owns this [IssueLight]
 */

class IssueLight(
    val ID: String = UUID.randomUUID().toString(),
    val creationDate: Date = Calendar.getInstance().time,
    val state: State = State.TODO,
    val title: String = "",
    val userId: String? = null
)
