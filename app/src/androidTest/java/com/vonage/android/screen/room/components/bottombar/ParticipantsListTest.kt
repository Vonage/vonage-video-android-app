package com.vonage.android.screen.room.components.bottombar

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.compose.preview.buildParticipants
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.collections.immutable.toImmutableList
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)

class ParticipantsListTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun given_5_participants_with_no_search_param_then_participants_in_list_5() {
        compose.setContent {ParticipantsList(buildParticipants(5).toImmutableList(), Modifier)}
        compose
            .onAllNodes(hasTestTag(PARTICIPANT_ITEM_TAG))
            .assertCountEquals(5)
    }

    @Test
    fun given_5_participants_searching_test_then_participants_in_list_0() {
        compose.setContent {ParticipantsList(buildParticipants(5).toImmutableList(), Modifier)}
        compose.onNodeWithTag(SEARCH_TAG).performTextInput("test")
        compose
            .onAllNodes(hasTestTag(PARTICIPANT_ITEM_TAG))
            .assertCountEquals(0)
    }

    @Test
    fun given_5_participants_searching_Name_Sample_5_then_participants_in_list_1() {
        compose.setContent {ParticipantsList(buildParticipants(5).toImmutableList(), Modifier)}
        compose.onNodeWithTag(SEARCH_TAG).performTextInput("Name Sample 5")
        compose
            .onAllNodes(hasTestTag(PARTICIPANT_ITEM_TAG))
            .assertCountEquals(1)
    }
}
