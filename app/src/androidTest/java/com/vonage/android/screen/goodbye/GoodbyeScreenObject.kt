package com.vonage.android.screen.goodbye

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import com.vonage.android.screen.goodbye.GoodbyeScreenTestTags.GOODBYE_ARCHIVES_CONTAINER_TAG
import com.vonage.android.screen.goodbye.GoodbyeScreenTestTags.GOODBYE_GO_HOME_BUTTON_TAG
import com.vonage.android.screen.goodbye.GoodbyeScreenTestTags.GOODBYE_HEADER_TAG
import com.vonage.android.screen.goodbye.GoodbyeScreenTestTags.GOODBYE_REJOIN_BUTTON_TAG
import com.vonage.android.screen.goodbye.GoodbyeScreenTestTags.GOODBYE_REJOIN_CONTAINER_TAG

class GoodbyeScreenObject(compose: SemanticsNodeInteractionsProvider) {
    val header = compose.onNodeWithTag(GOODBYE_HEADER_TAG)
    val rejoinContainer = compose.onNodeWithTag(GOODBYE_REJOIN_CONTAINER_TAG)
    val archivesContainer = compose.onNodeWithTag(GOODBYE_ARCHIVES_CONTAINER_TAG)
    val rejoinButton = compose.onNodeWithTag(GOODBYE_REJOIN_BUTTON_TAG)
    val goHomeButton = compose.onNodeWithTag(GOODBYE_GO_HOME_BUTTON_TAG)
}
