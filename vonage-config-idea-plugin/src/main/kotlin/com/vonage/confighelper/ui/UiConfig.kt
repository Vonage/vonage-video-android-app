package com.vonage.confighelper.ui

import java.awt.Font
import javax.swing.BorderFactory

@Suppress("MagicNumber")
object UiConfig {
    const val maxLogEntries = 25
    val jsonViewFont = Font("Monospaced", Font.PLAIN, 12)
    val defaultBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5)
}
