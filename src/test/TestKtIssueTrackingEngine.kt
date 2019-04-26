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
    //var user : User? = null

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

    private fun initUserList(): List<User> {
        return listOf(User(name1), User(name2), User(name3))
    }

    private fun initNameList(): List<String> {
        return listOf(name1, name2, name3)
    }

    @Test
    fun getListOfIssues() {
        //TODO fix
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
        assertEquals(nameList.size, userList.size)
        nameList.forEachIndexed { i, element ->
            assertEquals(element, userList[i].name)
        }
    }

    @Test
    fun addAndGetIssue() {
        issueTrackingEngine.addIssue(issueTitle).let {
            assertNotNull(issueTrackingEngine.getIssue(it)?.run {
                assertEquals(it, ID)
                assertEquals(issueTitle, title)
            })
        }
    }

    @Test
    fun addAndGetUser() {
        issueTrackingEngine.addUser(userName).let {
            assertNotNull(issueTrackingEngine.getUser(it)?.run {
                assertEquals(it, ID)
                assertEquals(userName, userName)
            })
        }
    }

    @Test
    fun removeIssue() {
        assertNotNull(issueTrackingEngine.addIssue(issueTitle).let {
            issueTrackingEngine.removeIssue(it)
            assertNull(issueTrackingEngine.getIssue(it))
        })
    }

    @Test
    fun setIssueState() {
        issueTrackingEngine.addIssue(issueTitle).let {
            issueTrackingEngine.setIssueState(it, State.IN_PROGRESS_STATE, issueComment)
            assertNotNull(issueTrackingEngine.getIssue(it)?.run {
                assertEquals(State.IN_PROGRESS_STATE, state)
                assertEquals(issueComment, stateChangedComment)
            })
        }
    }

    @Test
    fun assignUser() {
        issueTrackingEngine.addUser(userName).let { newUserID ->
            issueTrackingEngine.addIssue(issueTitle).let {
                issueTrackingEngine.assignUserToIssue(newUserID, it)
                issueTrackingEngine.getIssue(it)?.run {
                    assertEquals(newUserID, userID)
                }
            }
        }
    }


    @Test
    fun addIssueComment() {
        issueTrackingEngine.addUser(userName).let { newUserID ->
            issueTrackingEngine.addIssue(issueTitle).let { issueID ->
                issueTrackingEngine.addIssueComment(issueID, issueComment, newUserID)
                assertEquals(issueComment, listOfComments.last().comment)
                assertEquals(listOfComments.last().issueID, issueID)
                assertEquals(listOfComments.last().userID, newUserID)
            }
        }
    }

    @Test
    fun getIssues() {
        listOfIssues.clear()
        for (i in 1..3)
            issueTrackingEngine.addIssue(issueTitle)

        issueTrackingEngine.getIssues(null).let { allIssuesLight ->
            issueTrackingEngine.getAllIssues().run {
                assertEquals(size, allIssuesLight.size)
                for (i in allIssuesLight.indices)
                    assertEquals(allIssuesLight[i].ID, this[i].ID)
            }
        }
    }

    @Test
    fun getAndAddUser() {
        issueTrackingEngine.addUser(userName).let {
            issueTrackingEngine.getUser(it)?.run {
                assertEquals(it, ID)
                assertEquals(userName, name)
            }
        }
    }


    @Test
    fun getUsers() {
        listOfUsers.clear()
        initUserList().run {
            forEach { listOfUsers.add(it) }
            assertEquals(issueTrackingEngine.getUsers(), this)
        }
    }

    @Test
    fun getIssuesBetweenDate() {

        val startDate = Date.from(Instant.parse("2016-12-03T10:15:30.00Z"))
        val endDate = Date.from(Instant.parse("2019-12-03T10:15:30.00Z"))

        val (issue1, issue2) = getPairOfIssuesFromDates(startDate, endDate)

        issueTrackingEngine.getIssues(startDate = startDate, endDate = endDate).run {
            assertEquals(issue1.creationDate, first().creationDate)
            assertEquals(issue2.creationDate, last().creationDate)
        }


    }

    @Test
    fun getIssueLightAfter() {
        val startDate = Date.from(Instant.parse("2018-12-03T10:15:30.00Z"))
        //TODO fix
        ISSUE.let {
            listOfIssues.add(it)
            issueTrackingEngine.getIssues(startDate = startDate).run {
                assertEquals(startDate, first().creationDate)
                assertEquals(1, size)
            }

        }

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

        issueTrackingEngine.getIssues(endDate = dateToGetIssuesBefore).run {
            assertEquals(issue1.creationDate, first().creationDate)
            assertEquals(issue2.creationDate, last().creationDate)
            assertEquals(2, size)
        }

    }

    @Test
    fun getIssueLightByState() {
        val stateToSearch = State.TODO

        listOfIssues.add(Issue(issueTitle, state = stateToSearch))
        listOfIssues.add(Issue(issueTitle, state = State.DONE))

        issueTrackingEngine.getIssues(state = stateToSearch).run {
            assertEquals(stateToSearch, last().state)
            assertEquals(1, size)
        }
    }

    @Test
    fun multipleParameterFilter() {

        val stateToSearch = State.TODO
        val startDate = Date.from(Instant.parse("2018-12-03T10:15:30.00Z"))
        val userID = issueTrackingEngine.addUser(userName)

        listOfIssues.add(Issue(issueTitle, userID = userID, creationDate = startDate, state = stateToSearch))
        listOfIssues.add(Issue(issueTitle, state = State.DONE))

        assertNotNull(issueTrackingEngine.getIssues(state = stateToSearch, userID = userID, startDate = startDate).run {
            assertEquals(stateToSearch, last().state)
            assertEquals(userID, last().userId)
            assertEquals(startDate, last().creationDate)
            assertEquals(1, size)
        })

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
        ISSUE.let {
            issueTrackingEngine.convertIssueToIssueLight(it).run {
                assertEquals(it.creationDate, creationDate)
                assertEquals(it.ID, ID)
                assertEquals(it.state, state)
                assertEquals(it.title, title)
                assertEquals(it.userID, userId)
            }
        }
    }

    companion object {
        //private const val userName = "Steve"
        //private val USER = User(userName)
        private val ISSUE = Issue("id", "username")
    }

}