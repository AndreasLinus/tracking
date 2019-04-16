import java.util.*

val listOfIssues: MutableList<Issue> = mutableListOf()
val listOfUsers: MutableList<User> = mutableListOf()


//Every issue can be uniquely identified = v
//Every issue has a creation date = v
//And issue is always in a specific state = v
//Starts in the _TODO state = v
//State changes are recorded by timestamps = v
//Users have a name and can be uniquely identified = v
//Users can be assigned to one or more issues = v
//Users can comment on issues = v
//Comments have creating dates = v


//When somebody starts working on it moves to inProgress = x

/**
 * Adds a [Issue] to list of issues.
 *
 * @return The ID of the newly created issue.
 */
fun addIssue(title: String): String {
    val issueToAdd = Issue(title)
    //Probably nice if we check so that the list not already contains the issue
    if (!listOfIssues.contains(issueToAdd))
        listOfIssues.add(issueToAdd)

    return issueToAdd.ID
}

/**
 * Removes a [Issue] with uuid
 *
 * @param issueId ID of the issue to remove
 */
fun removeIssue(issueId: String) {
    listOfIssues.removeIf { it.ID == issueId }
}

/**
 *Set the state of an [Issue], with an optional comment
 *
 * comment : And optional description string of the state change
 * @param issueID ID that uniquely identifies the issue
 * @param state The new state
 * @param comment An optional description string of the state change
 */
fun setIssueState(issueID: String, state: State, comment: String? = null) {
    val issue: Issue? = getIssue(issueID)
    if (issue != null) {
        issue.state = state
        //This could be bad, if this computer clock is wrong then so will the issue state changed
        //so maybe pass it as an argument from a more reliable source?
        issue.stateChangedDate.add(Calendar.getInstance().time)
        if (comment != null)
            issue.stateChangedComment = comment
    }

}

/**
 *Assign User to issue
 *
 * @param userID An ID of some sort that uniquely identifies the [User] to assign.
 * If null, the issue no longer has a user assigned to it.
 * @param issueId An ID of some sort that uniquely identifies the [Issue] to assign the user to.
 */
fun assignUser(userID: String?, issueId: String) {
    val issue: Issue? = getIssue(issueId)
    if (issue != null) {
        if (userID == null)
        // This is probably bad design what if user accidentally passes null and clears all users from issue?
            issue.userID = null
        else {
            issue.userID = userID
        }
    }
}


/**
 * Adds a [Comment] to an [Issue]
 *
 * @param issueID The ID of the [Issue] on which to comment onto
 * @param comment the comment
 * @param userID the ID of the [User] that wants to comment on the [Issue]
 */
fun addIssueComment(issueID: String, comment: String, userID: String) {
    val issue = getIssue(issueID)
    issue!!.listOfComments.add(Comment(issueID, userID = userID, comment = comment))
}

/**
 * Retrieve a collection of issues, either all issues in a specific state, or all issues if no state is specified. The
issues can also be filtered by assigned user.
An interval can be specified to restrict the issues that are to be returned:
If both startDate and endDate are specified, only issues within the defined time range are returned.
If only startDate is specified, all issues created after that date are returned.
If only endDate is specified, all issues created up to that date are returned.

state : The state of the issues to retrieve. If null, all issues are returned.
ID : If specified, only consider issues assigned to this user. If null, consider all issues.
startDate : Optional start date. See discussion above.
endDate : Optional end date. See discussion above.
Returns
A collection of issues. The issues should include the most relevant information, such as state, title,
creation date, etc. NOTE: Comments and recorded state change history should not be included in the
result.
 *
 * @param state The state of the issues to retrieve. If null, all issues are returned.
 * @param userID If specified, only consider issues assigned to this user. If null, consider all issues.
 * @param startDate Optional start date. See discussion above.
 * @param endDate Optional end date. See discussion above.
 */
fun getIssues(
    state: State? = null, userID: String? = null, startDate: Date? = null, endDate: Date? = null
): List<IssueLight> {
    //If all parameters are null return all issues
    if (state == null && userID == null && startDate == null && endDate == null)
        return convertIssueListToIssueLight(listOfIssues)
    //else create list of issues
    var listOfIssuesToReturn: List<IssueLight> = convertIssueListToIssueLight(listOfIssues)
    //If user id not null get issues by user
    if (userID != null)
        listOfIssuesToReturn = getIssueLightByUserID(userID, listOfIssuesToReturn)
    //Same for state
    if (state != null)
        listOfIssuesToReturn = getIssueLightByState(state, listOfIssuesToReturn)
    //If start and end date not provided return issues
    if (startDate == null && endDate == null)
        return listOfIssuesToReturn
    //Otherwise filter by date
    else {
        if (startDate != null && endDate != null)
            return getIssuesBetweenDate(startDate, endDate, listOfIssuesToReturn)
        return if (startDate != null)
            getIssueLightAfter(startDate, listOfIssuesToReturn)
        else
            getIssueLightBefore(endDate, listOfIssuesToReturn)
    }
}

/**
 * Retrieve an [Issue].
 * The issue with the specified ID. The issue should include all relevant information, such as state, title,
 * creation
 *
 * @param issueID An ID of some sort that uniquely identifies the issue.
 * @return [Issue] with specified id, null if none was found
 */
fun getIssue(issueID: String): Issue? {
    for (issue in listOfIssues) {
        if (issue.ID == issueID)
            return issue
    }
    return null
}

/**
 * Adds an [User]
 *
 * @param name String with the name of the user to add.
 * @return Returns an ID that uniquely identifies the user.
 */

fun addUser(name: String): String {
    val user = User(name)
    listOfUsers.add(user)
    return user.ID
}

/**
 * Get a specific [User]
 *
 * @param userID of the [User] you want to get
 * @return the [User] or null if none was found
 */
fun getUser(userID: String?): User? {
    listOfUsers.forEach {
        if (it.ID == userID)
            return it
    }
    return null
}

fun getUsers(): MutableList<User> {
    return listOfUsers
}

fun getIssuesBetweenDate(
    startDate: Date?,
    endDate: Date?,
    list: List<IssueLight>
): List<IssueLight> {
    return list.filter { issue -> issue.creationDate >= startDate && issue.creationDate <= endDate }
}

fun getIssueLightAfter(startDate: Date?, list: List<IssueLight>): List<IssueLight> {
    return list.filter { issue -> issue.creationDate >= startDate }
}

fun getIssueLightBefore(endDate: Date?, list: List<IssueLight>): List<IssueLight> {
    return list.filter { issue -> issue.creationDate <= endDate }
}

fun getIssueLightByState(state: State, list: List<IssueLight>): List<IssueLight> {
    return list.filter { issue -> issue.state == state }
}

fun getIssueLightByUserID(userID: String, list: List<IssueLight>): List<IssueLight> {
    return list.filter { issue -> issue.userId == userID }
}


//Enum not most memory efficient way but nice to read
enum class State {
    TODO, IN_PROGRESS_STATE, DONE
}

class User(val name: String, val ID: String = UUID.randomUUID().toString())

class Comment(
    val issueID: String,
    val creationDate: Date = java.util.Calendar.getInstance().time,
    userID: String,
    val comment: String? = null
)

/**
 * Class representing an Issue in our Issue tracker
 *
 * @property title the title of the Issue
 * @property ID unique identifier for the issue
 * @property creationDate when it was created
 * @property state what [State] the issue is currently in
 * @property stateChangedDate when was the state last changed? todo make list so we can add
 * @property stateChangedComment
 * @property userID
 * @property listOfComments
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

/**
 * Converts a [List] of [Issue] to a [List] of [IssueLight]
 *
 * @param list the [List] of [Issue] to convert to a [List] of [IssueLight]
 * @return [List] of [IssueLight] created from provided [List] of [Issue]
 */
fun convertIssueListToIssueLight(list: List<Issue>): List<IssueLight> {

    val listLight: MutableList<IssueLight> = mutableListOf()

    list.forEach {
        listLight.add(
            IssueLight(
                state = it.state,
                title = it.title,
                creationDate = it.creationDate,
                ID = it.ID,
                userId = it.userID
            )
        )

    }
    return listLight
}

fun convertIssueToIssueLight(issue: Issue): IssueLight {
    return IssueLight(
        state = issue.state,
        title = issue.title,
        creationDate = issue.creationDate,
        ID = issue.ID,
        userId = issue.userID
    )
}

class StateChanged(userID: String, dateChanged: Date)

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
    var state: State = State.TODO,
    val title: String = "",
    var userId: String? = null
)