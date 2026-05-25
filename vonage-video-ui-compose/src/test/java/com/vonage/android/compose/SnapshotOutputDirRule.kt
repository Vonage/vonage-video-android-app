package com.vonage.android.compose

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.InternalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziContext
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * JUnit4 rule that redirects Roborazzi golden images to
 * `src/test/snapshots/images/` (relative to the module directory).
 *
 * Roborazzi's default output dir is `build/outputs/roborazzi`; this rule
 * overrides it via [RoborazziContext.setRuleOverrideOutputDirectory] so
 * goldens land in source-control-tracked directory.
 */
@OptIn(ExperimentalRoborazziApi::class, InternalRoborazziApi::class)
class SnapshotOutputDirRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement =
        object : Statement() {
            override fun evaluate() {
                RoborazziContext.setRuleOverrideOutputDirectory("src/test/snapshots/images")
                try {
                    base.evaluate()
                } finally {
                    RoborazziContext.clearRuleOverrideOutputDirectory()
                }
            }
        }
}
