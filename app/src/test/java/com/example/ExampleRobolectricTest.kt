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

    assertEquals("Akash Singh Parihar", me.name)
    assertEquals("akash_dev", me.username.removePrefix("@"))
    assertTrue(me.isDeveloper)
    assertTrue(me.isOwner)
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

  @Test
  fun `create reel and story with CameraX duration advances video counts`() {
    val repo = StarPulseRepository()
    val initialVideos = repo.currentUser.value.videosCount

    repo.createPost(
      type = PostType.REEL,
      caption = "Fresh CameraX Reel recording",
      hashtags = listOf("#Reels", "#AkashSocial"),
      mentions = emptyList(),
      durationSeconds = 24
    )

    val userAfterReel = repo.currentUser.value
    assertEquals(initialVideos + 1, userAfterReel.videosCount)

    repo.createPost(
      type = PostType.STORY,
      caption = "Daily story snap",
      hashtags = listOf("#DailyStory"),
      mentions = emptyList(),
      durationSeconds = 0
    )

    val latestPost = repo.posts.value.first()
    assertEquals(PostType.STORY, latestPost.type)
  }

  @Test
  fun `share post and send post attachment to friend updates metrics`() {
    val repo = StarPulseRepository()
    val initialShares = repo.currentUser.value.sharesCount
    val targetPost = repo.posts.value.first()
    val initialPostShares = targetPost.sharesCount

    repo.sharePost(targetPost.id)

    val userAfterShare = repo.currentUser.value
    assertEquals(initialShares + 1, userAfterShare.sharesCount)

    val updatedPost = repo.posts.value.first { it.id == targetPost.id }
    assertEquals(initialPostShares + 1, updatedPost.sharesCount)

    // Direct message with shared post attachment
    repo.sendMessage("user_aarav", "Check out this reel!", sharedPostId = targetPost.id)
    val msgs = repo.activeChatMessages.value["user_aarav"]
    assertNotNull(msgs)
    assertTrue(msgs!!.any { it.sharedPostId == targetPost.id })
  }

  @Test
  fun `registration requires only username and password without mandatory personal info`() {
    val repo = StarPulseRepository()
    val testUsername = "newcreator_${System.currentTimeMillis() % 10000}"

    assertTrue(repo.isUsernameAvailable(testUsername))

    // Register without email, phone, or real name
    val regResult = repo.registerWithUsernameAndPassword(testUsername, "password123")
    assertTrue(regResult.isSuccess)
    val registeredUser = regResult.getOrThrow()
    assertEquals(testUsername, registeredUser.username)
    assertNull(registeredUser.phoneNumber)
    assertNull(registeredUser.email)
    assertEquals(registeredUser.id, repo.currentUser.value.id)

    // Duplicate username is rejected
    val dupResult = repo.registerWithUsernameAndPassword(testUsername, "password999")
    assertTrue(dupResult.isFailure)
    assertTrue(dupResult.exceptionOrNull()?.message?.contains("already taken") == true)
  }

  @Test
  fun `login with username and password validates credentials`() {
    val repo = StarPulseRepository()
    val username = "authuser_${System.currentTimeMillis() % 10000}"

    repo.registerWithUsernameAndPassword(username, "mysecretpass")

    // Successful login
    val loginSuccess = repo.loginWithUsernameAndPassword(username, "mysecretpass")
    assertTrue(loginSuccess.isSuccess)

    // Wrong password fails
    val loginWrongPass = repo.loginWithUsernameAndPassword(username, "wrongpass")
    assertTrue(loginWrongPass.isFailure)
  }

  @Test
  fun `update checker verifies manifest version and returns appropriate result`() {
    val repo = StarPulseRepository()
    val checkResult = repo.checkForUpdates(manualCheck = true)
    // Manifest has latestVersionCode = 2 while installed baseline is 1
    assertTrue(checkResult is com.example.model.UpdateCheckResult.Available)
    val available = checkResult as com.example.model.UpdateCheckResult.Available
    assertTrue(available.manifest.latestVersionCode > 1)
    assertNotNull(available.manifest.apkDownloadUrl)
  }

  @Test
  fun `community activity popups consent and demo trigger`() {
    val repo = StarPulseRepository()
    assertFalse(repo.isCommunityPopupsEnabled.value)

    repo.setCommunityPopupsConsent(true, com.example.model.CommunityPopupsFrequency.NORMAL)
    assertTrue(repo.isCommunityPopupsEnabled.value)
    assertEquals(com.example.model.CommunityPopupsFrequency.NORMAL, repo.communityPopupsFrequency.value)

    repo.triggerCommunityActivityPulse()
    val popup = repo.activeCommunityPopup.value
    assertNotNull(popup)
    assertTrue(popup!!.isDemo)
  }

  @Test
  fun `milestone notifications trigger upon reaching thresholds without duplicates`() {
    val repo = StarPulseRepository()
    val me = repo.currentUser.value

    repo.createPost(
      type = PostType.REEL,
      caption = "Milestone test reel",
      hashtags = listOf("#Test"),
      mentions = emptyList()
    )

    val post = repo.posts.value.first()

    // Simulate 12 views
    repeat(12) {
      repo.recordContentView(post.id, viewerUserId = "other_user_$it")
    }

    val notifications = repo.notifications.value
    val reelNotif = notifications.firstOrNull { it.type == com.example.model.NotifType.REEL_MILESTONE && it.postId == post.id }
    assertNotNull(reelNotif)
    assertEquals("Your Reel is gaining attention! 🔥", reelNotif!!.title)

    // Ensure deduplication: another view should not create a duplicate
    repo.recordContentView(post.id, viewerUserId = "another_viewer")
    val count = repo.notifications.value.count { it.type == com.example.model.NotifType.REEL_MILESTONE && it.postId == post.id }
    assertEquals(1, count)
  }
}
