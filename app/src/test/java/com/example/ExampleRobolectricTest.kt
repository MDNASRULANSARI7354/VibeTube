package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.VibeTubeRepository
import com.example.ui.components.NavTab
import com.example.ui.viewmodel.ScreenState
import com.example.ui.viewmodel.VibeTubeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("VibeTube", appName)
    }

    @Test
    fun `repository initial data loads successfully`() {
        val repository = VibeTubeRepository()
        val videos = repository.videos.value
        val shorts = repository.shorts.value

        assertTrue(videos.isNotEmpty())
        assertTrue(shorts.isNotEmpty())
        assertNotNull(repository.currentUser.value)
    }

    @Test
    fun `viewmodel navigation and video selection works`() {
        val repository = VibeTubeRepository()
        val viewModel = VibeTubeViewModel(repository)

        assertEquals(NavTab.HOME, viewModel.currentTab.value)
        assertEquals(ScreenState.FEED, viewModel.screenState.value)

        val firstVideo = repository.videos.value.first()
        viewModel.playVideo(firstVideo)

        assertEquals(ScreenState.VIDEO_DETAIL, viewModel.screenState.value)
        assertEquals(firstVideo.id, viewModel.selectedVideo.value?.id)

        // Test like action
        val initialLikes = firstVideo.likesCount
        viewModel.toggleLike(firstVideo.id)
        val updatedLikes = viewModel.selectedVideo.value?.likesCount ?: 0
        assertTrue(updatedLikes != initialLikes)
    }
}
