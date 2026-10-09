package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.StarPulseRepository
import com.example.model.PostType
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AKASH SOCIAL NAGOD", appName)
  }

  @Test
  fun `fresh zero state and developer profile are verified`() {
    val repo = StarPulseRepository()
    val me = repo.currentUser.value

    assertEquals("Akash", me.name)
    assertEquals("@akash_dev", me.username)
    assertTrue(me.isDeveloper)
    assertEquals(0, me.fansCount)
    assertEquals(0, me.friendsCount)
    assertEquals(0, me.postsCount)
    assertEquals(0L, me.totalReach)
    assertEquals(0, me.streakDays)
    assertEquals(0, me.completedTasksCount)

    // All tasks start at 0 progress
    repo.tasks.value.forEach { task ->
      assertEquals(0, task.progress)
      assertFalse(task.isClaimed)
    }

    // All achievements start locked at 0
    repo.achievements.value.forEach { ach ->
      assertEquals(0, ach.progress)
      assertFalse(ach.isUnlocked)
      assertFalse(ach.isClaimed)
    }
  }

  @Test
  fun `fans and friends synergy works correctly with Indian community`() {
    val repo = StarPulseRepository()
    val aaravInitial = repo.creators.value.first { it.id == "user_aarav" }
    assertFalse(aaravInitial.isCurrentUserFan)
    assertFalse(aaravInitial.isFriend)

    repo.toggleFan("user_aarav")

    val aaravAfter = repo.creators.value.first { it.id == "user_aarav" }
    assertTrue(aaravAfter.isCurrentUserFan)
    assertEquals(aaravInitial.fansCount + 1, aaravAfter.fansCount)
  }

  @Test
  fun `create post advances total reach and creator counts organically`() {
    val repo = StarPulseRepository()
    val initialReach = repo.currentUser.value.totalReach
    val initialPosts = repo.currentUser.value.postsCount

    repo.createPost(
      type = PostType.PHOTO,
      caption = "Test art showcase",
      hashtags = listOf("#AkashSocial", "#Reach"),
      mentions = emptyList()
    )

    val updatedUser = repo.currentUser.value
    assertEquals(initialPosts + 1, updatedUser.postsCount)
    assertTrue(updatedUser.totalReach > initialReach)
    assertEquals(1, updatedUser.streakDays)
  }
}
