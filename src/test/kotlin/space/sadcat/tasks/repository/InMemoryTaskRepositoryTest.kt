package space.sadcat.tasks.repository

import kotlin.test.*
import space.sadcat.tasks.models.*
import space.sadcat.tasks.enums.Status
import kotlinx.coroutines.test.runTest

class InMemoryTaskRepositoryTest {
    @Test fun `create-find-update-delete`() = runTest {
        val repo: TaskRepository = InMemoryTaskRepository()
        val c = repo.create(CreateTaskRequest("A", status = Status.TODO))
        assertEquals("A", c.title)
        assertNotNull(repo.find(c.id))
        val u = repo.update(c.id, UpdateTaskRequest("B", Status.DONE))
        assertEquals("B", u?.title); assertEquals(Status.DONE, u?.status)
        assertTrue(repo.delete(c.id)); assertNull(repo.find(c.id))
    }

    @Test fun `all returns in insertion order`() = runTest {
        val repo: TaskRepository = InMemoryTaskRepository()
        val a = repo.create(CreateTaskRequest("A", Status.TODO))
        val b = repo.create(CreateTaskRequest("B", Status.TODO))
        val c = repo.create(CreateTaskRequest("C", Status.TODO))
        val titles = repo.all().map { it.title }
        assertEquals(listOf("A","B","C"), titles)
        assertEquals(listOf(a.id, b.id, c.id), repo.all().map { it.id })
    }

    @Test fun `update missing returns null and delete missing returns false`() = runTest {
        val repo: TaskRepository = InMemoryTaskRepository()
        assertNull(repo.update(999, UpdateTaskRequest("X", Status.DONE)))
        assertFalse(repo.delete(999))
    }
}


