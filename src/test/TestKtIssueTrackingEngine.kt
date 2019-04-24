package test

import IssueTrackingEngine
import models.Comment
import models.Issue
import models.State
import models.User
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.Instant
import java.util.*

private class TestKtIssueTrackingEngine {


    private val issueTitle = "Test"
    private val userName = "Steve"
    private val issueComment = "Something went wrong"

    private val listOfIssues: MutableList<Issue> = mutableListOf()
    private val listOfUsers: MutableList<User> = mutableListOf()
    private val listOfComments: MutableList<Comment> = mutableListOf()

    private val issueTrackingEngine: IssueTrackingEngine =
        IssueTrackingEngine(listOfIssues, listOfUsers, listOfComments)


    //The rest is tested below
    @Test
    fun testScenario1() {
        //Add User
        val userID = issueTrackingEngine.addUser(userName)
        assertEquals(userID, listOfUsers.last().ID)
        //Get user
        val user = issueTrackingEngine.getUser(userID)
        assertEquals(user!!.ID, userID)
        //Add Issue
        val issueID = issueTrackingEngine.addIssue(issueTitle)
        assertEquals(issueID, listOfIssues.last().ID)
        //Get Issue
        val issue = issueTrackingEngine.getIssue(issueID)
        assertEquals(issueID, issue!!.ID)
        //AssignUser
        issueTrackingEngine.assignUserToIssue(userID, issueID)
        assertEquals(userID, listOfIssues.last().userID)
        //Set issue state
        issueTrackingEngine.setIssueState(issueID, State.IN_PROGRESS_STATE, issueComment)
        assertEquals(State.IN_PROGRESS_STATE, listOfIssues.last().state)
        assertEquals(issueComment, listOfIssues.last().stateChangedComment)
        //Add issue comment
        issueTrackingEngine.addIssueComment(issueID, issueComment, userID)
        assertEquals(issueComment, listOfComments.last().comment)
        assertEquals(userID, listOfComments.last().userID)
        //GetUsers
        val listOfUser = issueTrackingEngine.getUsers()
        assertEquals(listOfUsers, listOfUser)
        //GetIssues
        val listOfIssue = issueTrackingEngine.getIssues()
        assertEquals(listOfIssues.size, listOfIssue.size)
        //Remove issue
        issueTrackingEngine.removeIssue(issueID)
        val removedIssue = issueTrackingEngine.getIssue(issueID)
        assertEquals(null, removedIssue)
    }


    private val name1 = "Johan"
    private val name2 = "Benny"
    private val name3 = "Anna"

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
        val issueID = issueTrackingEngine.addIssue(issueTitle)
        val issue = issueTrackingEngine.getIssue(issueID)
        assertNotEquals(null, issue)
        assertEquals(issueID, issue!!.ID)
        assertEquals(issueTitle, issue.title)
    }

    @Test
    fun addAndGetUser() {
        val userID = issueTrackingEngine.addUser(userName)
        val user = issueTrackingEngine.getUser(userID)
        assertNotEquals(null, user)
        assertEquals(userID, user!!.ID)
        assertEquals(userName, user.name)
    }

    @Test
    fun removeIssue() {
        val issueID = issueTrackingEngine.addIssue(issueTitle)
        //Remove issue
        issueTrackingEngine.removeIssue(issueID)
        //Get issue
        val removedIssue = issueTrackingEngine.getIssue(issueID)
        assertEquals(null, removedIssue)
    }

    @Test
    fun setIssueState() {
        val issueID = issueTrackingEngine.addIssue(issueTitle)
        issueTrackingEngine.setIssueState(issueID, State.IN_PROGRESS_STATE, issueComment)
        val issue = issueTrackingEngine.getIssue(issueID)
        assertEquals(State.IN_PROGRESS_STATE, issue!!.state)
        assertEquals(issueComment, issue.stateChangedComment)
    }

    @Test
    fun assignUser() {
        val userID = issueTrackingEngine.addUser(userName)
        val issueID = issueTrackingEngine.addIssue(issueTitle)
        issueTrackingEngine.assignUserToIssue(userID, issueID)
        val issue = issueTrackingEngine.getIssue(issueID)
        assertEquals(issue!!.userID, userID)
    }


    @Test
    fun addIssueComment() {
        val userID = issueTrackingEngine.addUser(userName)
        val issueID = issueTrackingEngine.addIssue(issueTitle)
        issueTrackingEngine.addIssueComment(issueID, issueComment, userID)
        assertEquals(issueComment, listOfComments.last().comment)
        assertEquals(listOfComments.last().issueID, issueID)
        assertEquals(listOfComments.last().userID, userID)
    }

    @Test
    fun getIssues() {
        listOfIssues.clear()
        for (i in 1..3)
            issueTrackingEngine.addIssue(issueTitle)

        val issues = issueTrackingEngine.getIssues(null)
        val listOfIssues = issueTrackingEngine.getAllIssues()
        //TODO make method to check all properties
        for (i in issues.indices)
            assertEquals(issues[i].ID, listOfIssues[i].ID)

    }

    @Test
    fun getAndAddUser() {
        val userID = issueTrackingEngine.addUser(userName)
        val user = issueTrackingEngine.getUser(userID)
        assertEquals(userID, user!!.ID)
        assertEquals(userName, user.name)
    }


    @Test
    fun getUsers() {
        listOfUsers.clear()
        val newListOfUsers = initUserList()
        newListOfUsers.forEach { listOfUsers.add(it) }
        val listOfUsers = issueTrackingEngine.getUsers()

        assertEquals(listOfUsers, newListOfUsers)
    }

    @Test
    fun getIssuesBetweenDate() {

        val startDate = Date.from(Instant.parse("2016-12-03T10:15:30.00Z"))
        val endDate = Date.from(Instant.parse("2019-12-03T10:15:30.00Z"))

        val (issue1, issue2) = getPairOfIssuesFromDates(startDate, endDate)

        val listOfIssuesFromGet = issueTrackingEngine.getIssues(startDate = startDate, endDate = endDate)

        assertEquals(issue1.creationDate, listOfIssuesFromGet.first().creationDate)
        assertEquals(issue2.creationDate, listOfIssuesFromGet[1].creationDate)

    }

    @Test
    fun getIssueLightAfter() {

        val startDate = Date.from(Instant.parse("2018-12-03T10:15:30.00Z"))
        val endDate = Date.from(Instant.parse("2017-12-03T10:15:30.00Z"))

        val (issue1, _) = getPairOfIssuesFromDates(startDate, endDate)

        val issue = issueTrackingEngine.getIssues(startDate = startDate)

        assertEquals(issue1.creationDate, issue.first().creationDate)
        assertEquals(1, issue.size)
    }

    private fun getPairOfIssuesFromDates(firstIssueDate: Date, secondIssueDate: Date): Pair<Issue, Issue> {
        val issue1 = Issue(issueTitle, creationDate = firstIssueDate)
        val issue2 = Issue(issueTitle, creationDate = secondIssueDate)

        listOfIssues.add(issue1)
        listOfIssues.add(issue2)
        return Pair(issue1, issue2)
    }

    @Test
    fun getIssueLightBefore() {
        val dateToGetIssuesBefore = Date.from(Instant.parse("2018-12-03T10:15:30.00Z"))
        val creationDateIssue2 = Date.from(Instant.parse("2017-12-03T10:15:30.00Z"))

        val (issue1, issue2) = getPairOfIssuesFromDates(dateToGetIssuesBefore, creationDateIssue2)

        val issue = issueTrackingEngine.getIssues(endDate = dateToGetIssuesBefore)

        assertEquals(issue1.creationDate, issue.first().creationDate)
        assertEquals(issue2.creationDate, issue[1].creationDate)
        assertEquals(2, issue.size)
    }

    @Test
    fun getIssueLightByState() {
        val stateToSearch = State.TODO
        val issue1 = Issue(issueTitle, state = stateToSearch)
        val issue2 = Issue(issueTitle, state = State.DONE)

        listOfIssues.add(issue1)
        listOfIssues.add(issue2)

        val listOfIssueLight = issueTrackingEngine.getIssues(state = stateToSearch)

        assertEquals(stateToSearch, listOfIssueLight.last().state)
        assertEquals(1, listOfIssueLight.size)
    }

    @Test
    fun multipleParameterFilter() {
        val stateToSearch = State.TODO
        val startDate = Date.from(Instant.parse("2018-12-03T10:15:30.00Z"))
        val userID = issueTrackingEngine.addUser(userName)
        val issue1 = Issue(issueTitle, userID = userID, creationDate = startDate, state = stateToSearch)
        val issue2 = Issue(issueTitle, state = State.DONE)

        listOfIssues.add(issue1)
        listOfIssues.add(issue2)

        val listOfIssueLight =
            issueTrackingEngine.getIssues(state = stateToSearch, userID = userID, startDate = startDate)

        assertEquals(stateToSearch, listOfIssueLight.last().state)
        assertEquals(userID, listOfIssueLight.last().userId)
        assertEquals(startDate, listOfIssueLight.last().creationDate)
        assertEquals(1, listOfIssueLight.size)
    }


    @Test
    fun getIssueLightByUserID() {
        val userID = issueTrackingEngine.addUser(userName)
        val issueID = issueTrackingEngine.addIssue(issueTitle)
        issueTrackingEngine.addIssue(issueTitle)
        issueTrackingEngine.assignUserToIssue(userID, issueID)

        val listOfIssueLight = issueTrackingEngine.getIssues(userID = userID)

        assertEquals(userID, listOfIssueLight.last().userId)
        assertEquals(1, listOfIssueLight.size)
    }

    @Test
    fun convert() {
        val issue = issueTrackingEngine.getIssue(issueTitle)
        if (issue != null) {
            val issueLight = issueTrackingEngine.convertIssueToIssueLight(issue)
            assertEquals(issue.creationDate, issueLight.creationDate)
            assertEquals(issue.ID, issueLight.ID)
            assertEquals(issue.state, issueLight.state)
            assertEquals(issue.title, issueLight.title)
            assertEquals(issue.userID, issueLight.userId)
        }
    }

}