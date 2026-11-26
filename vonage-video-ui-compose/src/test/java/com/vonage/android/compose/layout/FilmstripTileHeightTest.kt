package com.vonage.android.compose.layout

import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FilmstripTileHeightTest {

    @Test
    fun `keeps the preferred height when the cell is large enough`() {
        val height = filmstripTileHeight(200.dp, 1f, maxTileWidth = 400.dp, maxTileHeight = 300.dp)
        assertEquals(200.dp, height)
    }

    @Test
    fun `shrinks to the cell height in a short window`() {
        val height = filmstripTileHeight(200.dp, 1f, maxTileWidth = 400.dp, maxTileHeight = 90.dp)
        assertEquals(90.dp, height)
    }

    @Test
    fun `shrinks so a square tile fits a narrow cell`() {
        val height = filmstripTileHeight(200.dp, 1f, maxTileWidth = 130.dp, maxTileHeight = 300.dp)
        assertEquals(130.dp, height)
    }

    @Test
    fun `shrinks so a 16 by 9 tile fits a narrow strip`() {
        val height = filmstripTileHeight(200.dp, 16f / 9f, maxTileWidth = 160.dp, maxTileHeight = 300.dp)
        assertEquals(90f, height.value, 0.01f)
    }

    @Test
    fun `never returns a negative height`() {
        val height = filmstripTileHeight(200.dp, 1f, maxTileWidth = (-10).dp, maxTileHeight = 300.dp)
        assertEquals(0.dp, height)
    }
}
