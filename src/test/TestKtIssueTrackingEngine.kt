package test

import IssueTrackingEngine
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


    private val issueTrackingEngine: IssueTrackingEngine =
        IssueTrackingEngine()

    @Test
    fun testScenario1() {
        issueTrackingEngine.run {
            //Add User
            USER.run {
                addUser(this)
                assertEquals(ID, getUsers().last().ID)
                //Get user
                assertNotNull(getUser(ID)?.let {
                    assertEquals(it.ID, ID)
                })
                //Add Issue
                assertNotNull(addIssue(issueTitle).let { issueID ->
                    getAllIssues().run {
                        assertEquals(issueID, last().ID)
                        //Get Issue
                        assertNotNull(getIssue(issueID)?.run {
                            assertEquals(issueID, ID)
                        }
                        )
                        //AssignUser
                        assignUserToIssue(ID, issueID)
                        assertEquals(ID, last().userID)
                        //Set issue state
                        setIssueState(issueID, State.IN_PROGRESS_STATE, issueComment)
                        assertEquals(State.IN_PROGRESS_STATE, last().state)
                        assertEquals(issueComment, last().stateChangedComment)
                        //Add issue comment
                        addIssueComment(issueID, issueComment, ID)
                        assertEquals(issueComment, getComment(issueID)?.comment)
                        assertEquals(ID, last().userID)
                        //todo
                        //GetUsers
                        //GetIssues
                        //Remove issue
                        removeIssue(issueID)
                        assertNull(getIssue(issueID))
                    }
                })
            }
        }
    }

    private val name1 = "Johan"
    private val name2 = "Benny"
    private val name3 = "Anna"

    val userList = listOf(User(name1), User(name2), User(name3))
    val nameList = listOf(name1, name2, name3)

    @Test
    fun getListOfIssues() {

    }

    @Test
    fun getListOfUsers() {
        assertEquals(nameList.size, userList.size)
        nameList.forEachIndexed { i, element ->
            assertEquals(element, userList[i].name)
        }
    }

    @Test
    fun addAndGetIssue() {
        issueTrackingEngine.run {
            assertNotNull(addIssue(issueTitle).let {
                assertNotNull(getIssue(it)?.run {
                    assertEquals(it, ID)
                    assertEquals(issueTitle, title)
                })
            }
            )
        }
    }

    @Test
    fun addAndGetUser() {
        issueTrackingEngine.run {
            assertNotNull(addUser(userName).let {
                assertNotNull(getUser(it)?.run {
                    assertEquals(it, ID)
                    assertEquals(userName, userName)
                })
            })
        }
    }

    @Test
    fun removeIssue() {
        issueTrackingEngine.run {
            assertNotNull(addIssue(issueTitle).let {
                removeIssue(it)
                assertNull(getIssue(it))
            })
        }
    }

    @Test
    fun setIssueState() {
        issueTrackingEngine.run {
            addIssue(issueTitle).let {
                setIssueState(it, State.IN_PROGRESS_STATE)
                assertNotNull(getIssue(it)?.run {
                    assertEquals(State.IN_PROGRESS_STATE, state)
                })
            }
        }
    }

    @Test
    fun assignUser() {
        issueTrackingEngine.run {
            addUser(userName).let { newUserID ->
                addIssue(issueTitle).let {
                    assignUserToIssue(newUserID, it)
                    getIssue(it)?.run {
                        assertEquals(newUserID, userID)
                    }
                }
            }
        }
    }

    @Test
    fun addIssueComment() {
        issueTrackingEngine.run {
            addUser(userName).let { newUserID ->
                addIssue(issueTitle).let { issueID ->
                    addIssueComment(issueID, issueComment, newUserID)
                    getComments().run {
                        assertEquals(this[0], last())
                    }
                }
            }
        }
    }

    @Test
    fun getIssues() {
        issueTrackingEngine.run {
            clearAllIssues()
            for (i in 1..3)
                addIssue(issueTitle)

            getIssues(null).let {
                getAllIssues().run {
                    assertEquals(size, it.size)
                    for (i in it.indices) {
                        assertEquals(it[i].ID, this[i].ID)
                        assertEquals(it[i].creationDate, this[i].creationDate)
                        assertEquals(it[i].state, this[i].state)
                        assertEquals(it[i].title, this[i].title)
                        assertEquals(it[i].userId, this[i].userID)
                    }

                }
            }
        }
    }

    @Test
    fun getAndAddUser() {
        issueTrackingEngine.run {
            assertNotNull(addUser(userName).let {
                assertNotNull(getUser(it)?.run {
                    assertEquals(it, ID)
                    assertEquals(userName, name)
                })
            })
        }
    }

    @Test
    fun getIssuesBetweenDate() {
        getPairOfIssuesFromDates(creationDate2017, creationDate2018).run {
            issueTrackingEngine.getIssues(startDate = creationDate2017, endDate = creationDate2018).run {
                assertEquals(get(0).creationDate, first().creationDate)
                assertEquals(get(1).creationDate, last().creationDate)
            }
        }
    }

    val creationDate2017: Date = Date.from(Instant.parse("2017-12-03T10:15:30.00Z"))
    val creationDate2018: Date = Date.from(Instant.parse("2018-12-03T10:15:30.00Z"))

    @Test
    fun getIssueLightAfter() {
        ISSUE.let {
            issueTrackingEngine.run {
                addIssue(it)
                addIssue(Issue(issueTitle, creationDate = creationDate2017))
                issueTrackingEngine.getIssues(startDate = it.creationDate).run {
                    assertEquals(it.creationDate, first().creationDate)
                    assertEquals(1, size)
                }
            }

        }

    }

    private fun getPairOfIssuesFromDates(firstIssueDate: Date, secondIssueDate: Date): Pair<Issue, Issue> {
        val issue1 = Issue(issueTitle, creationDate = firstIssueDate)
        val issue2 = Issue(issueTitle, creationDate = secondIssueDate)
        issueTrackingEngine.run {
            addIssue(issue1)
            addIssue(issue2)
        }
        return Pair(issue1, issue2)
    }

    @Test
    fun getIssueLightBefore() {
        getPairOfIssuesFromDates(creationDate2018, creationDate2017).run {
            issueTrackingEngine.getIssues(endDate = creationDate2018).run {
                assertEquals(get(0).creationDate, first().creationDate)
                assertEquals(get(1).creationDate, last().creationDate)
                assertEquals(2, size)
            }
        }
    }

    val stateToSearch = State.TODO

    @Test
    fun getIssueLightByState() {
        issueTrackingEngine.run {
            assertNotNull(addIssue(Issue(issueTitle, state = stateToSearch)))
            assertNotNull(addIssue(Issue(issueTitle, state = State.DONE)))
            assertNotNull(getIssues(state = stateToSearch).run {
                assertEquals(stateToSearch, last().state)
                assertEquals(1, size)
            })
        }


    }

    @Test
    fun multipleParameterFilter() {
        issueTrackingEngine.run {
            assertNotNull(addUser(userName).let { userID ->
                addIssue(Issue(issueTitle, userID = userID, creationDate = creationDate2018, state = stateToSearch))
                addIssue(Issue(issueTitle, state = State.DONE))
                assertNotNull(
                    getIssues(
                        state = stateToSearch,
                        userID = userID,
                        startDate = creationDate2018
                    ).run {
                        assertEquals(stateToSearch, last().state)
                        assertEquals(userID, last().userId)
                        assertEquals(creationDate2018, last().creationDate)
                        assertEquals(1, size)
                    })
            })
        }

    }

    @Test
    fun getIssueLightByUserID() {
        issueTrackingEngine.run {
            assertNotNull(addUser(userName).let { userID ->
                assertNotNull(addIssue(issueTitle).let {
                    addIssue(issueTitle)
                    assignUserToIssue(userID, it)
                    assertNotNull(getIssues(userID = userID).run {
                        assertEquals(userID, last().userId)
                        assertEquals(1, size)
                    })
                })
            }
            )
        }
    }

    @Test
    fun convert() {
        ISSUE.let {
            assertNotNull(issueTrackingEngine.convertIssueToIssueLight(it).run {
                assertEquals(it.creationDate, creationDate)
                assertEquals(it.ID, ID)
                assertEquals(it.state, state)
                assertEquals(it.title, title)
                assertEquals(it.userID, userId)
            })
        }
    }

    companion object {
        private const val userName = "Steve"
        private val USER = User(userName)
        private val ISSUE = Issue("id", "username")
    }
}