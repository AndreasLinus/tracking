import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.Instant
import java.util.*

internal class TestKtIssueTrackingEngine {

    private val issueTitle = "Test"
    private val userName = "Steve"
    private val issueComment = "Something went wrong"

/*
    fun test() {
        val deliminator = "-----------------------------------------"
        val userID = addUser(userName)
        assertEquals(userID, listOfUsers.first().ID)
        val issueID = addIssue(issueTitle)
        assertEquals(issueID, listOfIssues.first().ID)
        //Check id of issue and user
        println("\nissue id : $issueID\nuser id : $userID")
        println("\nIssue\n$deliminator")
        //Assign user to issue
        assignUser(userID, issueID)
        assertEquals(userID, listOfIssues.first().userID)
        //Check that user is the same that we assigned
        val issue = getIssue(issueID)
        //Reflection might be slow
        if (issue != null) {
            for (prop in Issue::class.memberProperties) {
                println("${prop.name} = ${prop.get(issue)}")
            }
            println(deliminator)
            println("\nChanged Issue\n$deliminator")
            //Set issue state and add a comment
            val comment = "I'm on it!"
            setIssueState(issueID, State.IN_PROGRESS_STATE, comment)
            assertEquals(State.IN_PROGRESS_STATE, listOfIssues.first().state)
            assertEquals(comment, listOfIssues.first().stateChangedComment)
            //Get updated issue
            addIssueComment(issueID, issueComment, userID)
            val updatedIssue = getIssue(issueID)
            assertEquals(issueComment, listOfIssues.first().listOfComments.first().comment)
            //Print the state and comment to check if it was changed
            for (prop in Issue::class.memberProperties) {
                if (updatedIssue != null)
                    println("${prop.name} = ${prop.get(updatedIssue)}")
            }
            println(deliminator)
        }

        val user = getUser(userID)
        assertEquals(user!!.ID, userID)
        println("\nUser\n$deliminator")
        for (prop in User::class.memberProperties) {
            println("${prop.name} = ${prop.get(user)}")
        }
        println(deliminator)

        //Remove issue
        removeIssue(issueID)
        //Get issue
        val removedIssue = getIssue(issueID)
        assertEquals(null, removedIssue)
        //If issue is null that means that issue was removed
        if (removedIssue == null)
            println("Issue is removed")
        else
            println("Issue was not removed, remove issue doesn't work properly")
    }
*/


    val name1 = "Johan"
    val name2 = "Benny"
    val name3 = "Anna"

    private fun initUserList(): MutableList<User> {
        val user1 = User(name1)
        val user2 = User(name2)
        val user3 = User(name3)

        return mutableListOf(user1, user2, user3)
    }

    private fun initNameList(): List<String> {
        return listOf(name1, name2, name3)
    }


    @Test
    fun getListOfIssues() {
        //TODO check so that userID's are the same as well
        val userList = initUserList()
        val nameList = initNameList()
        if (nameList.size == userList.size)
            for (i in nameList.indices) {
                assertEquals(nameList[i], userList[i].name)
            }
    }

    @Test
    fun getListOfUsers() {
        val userList = initUserList()
        val nameList = initNameList()
        if (nameList.size == userList.size)
            for (i in nameList.indices) {
                assertEquals(nameList[i], userList[i].name)
            }
    }

    @Test
    fun addAndGetIssue() {
        val issueID = addIssue(issueTitle)
        val issue = getIssue(issueID)
        assertNotEquals(null, issue)
        assertEquals(issueID, issue!!.ID)
        assertEquals(issueTitle, issue.title)
    }

    @Test
    fun addAndGetUser() {
        val userID = addUser(userName)
        val user = getUser(userID)
        assertNotEquals(null, user)
        assertEquals(userID, user!!.ID)
        assertEquals(userName, user.name)
    }

    @Test
    fun removeIssue() {
        val issueID = addIssue(issueTitle)
        //Remove issue
        removeIssue(issueID)
        //Get issue
        val removedIssue = getIssue(issueID)
        assertEquals(null, removedIssue)
    }

    @Test
    fun setIssueState() {
        val issueID = addIssue(issueTitle)
        setIssueState(issueID, State.IN_PROGRESS_STATE, issueComment)
        val issue = getIssue(issueID)
        assertEquals(State.IN_PROGRESS_STATE, issue!!.state)
        assertEquals(issueComment, issue.stateChangedComment)
    }

    @Test
    fun assignUser() {
        val userID = addUser(userName)
        val issueID = addIssue(issueTitle)
        assignUser(userID, issueID)
        val issue = getIssue(issueID)
        assertEquals(issue!!.userID, userID)
    }


    @Test
    fun addIssueComment() {
        val userID = addUser(userName)
        val issueID = addIssue(issueTitle)
        addIssueComment(issueID, issueComment, userID)
        val updatedIssue = getIssue(issueID)
        assertEquals(issueComment, updatedIssue!!.listOfComments.first().comment)
        assertEquals(updatedIssue.listOfComments.first().issueID, issueID)
    }

    @Test
    fun getIssues() {
        listOfIssues.clear()
        val loopLength = 3
        for (i in 1..loopLength)
            addIssue(issueTitle)

        val issues = getIssues(null)
        assertEquals(loopLength, issues.size)
    }

    @Test
    fun getAndAddUser() {
        val userID = addUser(userName)
        val user = getUser(userID)
        assertEquals(userID, user!!.ID)
        assertEquals(userName, user.name)
    }


    @Test
    fun getUsers() {
        listOfUsers.clear()
        val newListOfUsers = initUserList()
        newListOfUsers.forEach { listOfUsers.add(it) }
        assertEquals(listOfUsers, newListOfUsers)
        listOfUsers.clear()
    }

    @Test
    fun getIssuesBetweenDate() {

        val startDate = Date.from(Instant.parse("2016-12-03T10:15:30.00Z"))
        val endDate = Date.from(Instant.parse("2019-12-03T10:15:30.00Z"))

        val (issue1, issue2) = getPairOfIssuesFromDates(startDate, endDate)

        val listOfIssuesFromGet = getIssues(startDate = startDate, endDate = endDate)

        assertEquals(issue1.creationDate, listOfIssuesFromGet.first().creationDate)
        assertEquals(issue2.creationDate, listOfIssuesFromGet[1].creationDate)

        listOfIssues.clear()
    }

    @Test
    fun getIssueLightAfter() {

        val startDate = Date.from(Instant.parse("2018-12-03T10:15:30.00Z"))
        val endDate = Date.from(Instant.parse("2017-12-03T10:15:30.00Z"))

        val (issue1, _) = getPairOfIssuesFromDates(startDate, endDate)

        val issue = getIssues(startDate = startDate)

        assertEquals(issue1.creationDate, issue.first().creationDate)
        assertEquals(1, issue.size)

        listOfIssues.clear()

    }

    private fun getPairOfIssuesFromDates(firstIssueDate: Date, secondIssueDate: Date): Pair<Issue, Issue> {
        val issue1 = Issue(issueTitle, creationDate = firstIssueDate)
        val issue2 = Issue(issueTitle, creationDate = secondIssueDate)
        listOfIssues.clear()
        listOfIssues.add(issue1)
        listOfIssues.add(issue2)
        return Pair(issue1, issue2)
    }

    @Test
    fun getIssueLightBefore() {
        val dateToGetIssuesBefore = Date.from(Instant.parse("2018-12-03T10:15:30.00Z"))
        val creationDateIssue2 = Date.from(Instant.parse("2017-12-03T10:15:30.00Z"))

        val (issue1, issue2) = getPairOfIssuesFromDates(dateToGetIssuesBefore, creationDateIssue2)

        val issue = getIssues(endDate = dateToGetIssuesBefore)

        assertEquals(issue1.creationDate, issue.first().creationDate)
        assertEquals(issue2.creationDate, issue[1].creationDate)

        listOfIssues.clear()

    }

    @Test
    fun getIssueLightByState() {
        val stateToSearch = State.TODO
        val issue1 = Issue(issueTitle, state = stateToSearch)
        val issue2 = Issue(issueTitle, state = State.DONE)

        listOfIssues.clear()
        listOfIssues.add(issue1)
        listOfIssues.add(issue2)


        val issue = getIssues(state = stateToSearch)

        assertEquals(stateToSearch, issue.first().state)
        assertEquals(1, issue.size)

        listOfIssues.clear()
    }

    @Test
    fun multipleParameterFilter() {

        val stateToSearch = State.TODO
        val startDate = Date.from(Instant.parse("2018-12-03T10:15:30.00Z"))
        val userID = addUser(userName)
        val issue1 = Issue(issueTitle, userID = userID, creationDate = startDate, state = stateToSearch)
        val issue2 = Issue(issueTitle, state = State.DONE)

        listOfIssues.clear()
        listOfIssues.add(issue1)
        listOfIssues.add(issue2)

        val issue = getIssues(state = stateToSearch, userID = userID, startDate = startDate)

        assertEquals(stateToSearch, issue.first().state)
        assertEquals(userID, issue.first().userId)
        assertEquals(startDate, issue.first().creationDate)
        assertEquals(1, issue.size)

        listOfIssues.clear()
    }


    @Test
    fun getIssueLightByUserID() {

        listOfIssues.clear()
        listOfUsers.clear()

        val userID = addUser(userName)
        val issueID = addIssue(issueTitle)
        addIssue(issueTitle)
        assignUser(userID, issueID)

        val issue = getIssues(userID = userID)

        assertEquals(userID, issue.first().userId)
        assertEquals(1, issue.size)

        listOfIssues.clear()
    }

    @Test
    fun convert() {
        val issue = getIssue(issueTitle)
        if (issue != null) {
            val issueLight = convertIssueToIssueLight(issue)
            assertEquals(issue.creationDate, issueLight.creationDate)
            assertEquals(issue.ID, issueLight.ID)
            assertEquals(issue.state, issueLight.state)
            assertEquals(issue.title, issueLight.title)
            assertEquals(issue.userID, issueLight.userId)
        }
    }
}