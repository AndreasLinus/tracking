import models.*
import java.util.*

class IssueTrackingEngine {

    val listOfIssues: MutableList<Issue> = mutableListOf()
    val listOfUsers: MutableList<User> = mutableListOf()
    val listOfComments: MutableList<Comment> = mutableListOf()

    /**
     * Adds a [Issue] to list of issues.
     *
     * @return The ID of the newly created issue.
     */
    fun addIssue(title: String): String {
        val issueToAdd = Issue(title)
        //Probably nice if we check so that the list not already contains the issue
        //if (!listOfIssues.contains(issueToAdd))
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
            //Also probably add new object State changed so we can include userID so we know who changed it not just when
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
    fun assignUserToIssue(userID: String?, issueId: String) {
        val issue: Issue? = getIssue(issueId)
        if (issue != null) {
            if (userID == null)
            // This is probably bad design what if we accidentally pass null
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
        listOfComments.add(Comment(issueID, userID = userID, comment = comment))
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
        //Convert since issue light has fever properties
        var listOfIssuesLightToReturn: List<IssueLight> = convertIssueListToIssueLight(listOfIssues)
        //If user id not null get issues by user
        if (userID != null)
            listOfIssuesLightToReturn = getIssueLightByUserID(userID, listOfIssuesLightToReturn)
        //Same for state
        if (state != null)
            listOfIssuesLightToReturn = getIssueLightByState(state, listOfIssuesLightToReturn)
        //If start and end date not provided return issues
        if (startDate == null && endDate == null)
            return listOfIssuesLightToReturn
        //Otherwise filter by date
        else {
            if (startDate != null && endDate != null)
                return getIssuesBetweenDate(startDate, endDate, listOfIssuesLightToReturn)
            return if (startDate != null)
                getIssueLightAfter(startDate, listOfIssuesLightToReturn)
            else
                getIssueLightBefore(endDate, listOfIssuesLightToReturn)
        }
    }

    fun getAllIssues(): MutableList<Issue> {
        return listOfIssues
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
        listOfIssues.forEach {
            if (it.ID == issueID)
                return it
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
        //Should probably check so that user doesn't already exist...
        //if (!listOfUsers.contains(user))
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

    private fun getIssuesBetweenDate(
        startDate: Date?,
        endDate: Date?,
        list: List<IssueLight>
    ): List<IssueLight> {
        return list.filter { issue -> issue.creationDate >= startDate && issue.creationDate <= endDate }
    }

    private fun getIssueLightAfter(startDate: Date?, list: List<IssueLight>): List<IssueLight> {
        return list.filter { issue -> issue.creationDate >= startDate }
    }

    private fun getIssueLightBefore(endDate: Date?, list: List<IssueLight>): List<IssueLight> {
        return list.filter { issue -> issue.creationDate <= endDate }
    }

    private fun getIssueLightByState(state: State, list: List<IssueLight>): List<IssueLight> {
        return list.filter { issue -> issue.state == state }
    }

    private fun getIssueLightByUserID(userID: String, list: List<IssueLight>): List<IssueLight> {
        return list.filter { issue -> issue.userId == userID }
    }

    /**
     * Converts a [List] of [Issue] to a [List] of [IssueLight]
     *
     * @param list the [List] of [Issue] to convert to a [List] of [IssueLight]
     * @return [List] of [IssueLight] created from provided [List] of [Issue]
     */
    private fun convertIssueListToIssueLight(list: List<Issue>): List<IssueLight> {

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
}