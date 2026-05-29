package com.vonage.android.compose.layout

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class GridLayoutCalculatorTest(
    private val participantCount: Int,
    private val isLandscape: Boolean,
    private val expectedColumns: Int,
    private val expectedRows: Int,
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "count={0}, landscape={1} → {2}×{3}")
        fun data(): Collection<Array<Any>> = listOf(
            // Portrait
            arrayOf(1, false, 1, 1),
            arrayOf(2, false, 1, 2),
            arrayOf(3, false, 2, 2),
            arrayOf(4, false, 2, 2),
            arrayOf(5, false, 2, 3),
            arrayOf(6, false, 2, 3),
            // Landscape (columns and rows flipped)
            arrayOf(1, true, 1, 1),
            arrayOf(2, true, 2, 1),
            arrayOf(3, true, 2, 2),
            arrayOf(4, true, 2, 2),
            arrayOf(5, true, 3, 2),
            arrayOf(6, true, 3, 2),
        )
    }

    @Test
    fun `gridLayoutFor returns correct dimensions`() {
        val result = gridLayoutFor(participantCount, isLandscape)
        assertEquals(expectedColumns, result.columns)
        assertEquals(expectedRows, result.rows)
    }
}

class GridLayoutCalculatorBoundaryTest {

    @Test
    fun `counts above 6 portrait produce same dimensions as 6`() {
        assertEquals(gridLayoutFor(6, false), gridLayoutFor(7, false))
        assertEquals(gridLayoutFor(6, false), gridLayoutFor(100, false))
    }

    @Test
    fun `counts above 6 landscape produce same dimensions as 6`() {
        assertEquals(gridLayoutFor(6, true), gridLayoutFor(7, true))
        assertEquals(gridLayoutFor(6, true), gridLayoutFor(100, true))
    }

    @Test
    fun `single participant is always 1x1 regardless of orientation`() {
        assertEquals(GridDimensions(columns = 1, rows = 1), gridLayoutFor(1, false))
        assertEquals(GridDimensions(columns = 1, rows = 1), gridLayoutFor(1, true))
    }
}
