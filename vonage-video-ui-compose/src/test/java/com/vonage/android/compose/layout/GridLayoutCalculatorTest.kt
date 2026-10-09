package com.vonage.android.compose.layout

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.api.Test

class GridLayoutCalculatorTest {

    @ParameterizedTest(name = "count={0}, landscape={1} → {2}×{3}")
    @MethodSource("data")
    fun `gridLayoutFor returns correct dimensions`(
        participantCount: Int,
        isLandscape: Boolean,
        expectedColumns: Int,
        expectedRows: Int,
    ) {
        val result = gridLayoutFor(participantCount, isLandscape)
        assertEquals(expectedColumns, result.columns)
        assertEquals(expectedRows, result.rows)
    }

    companion object {
        @JvmStatic
        fun data(): List<Arguments> = listOf(
            // Portrait
            Arguments.of(1, false, 1, 1),
            Arguments.of(2, false, 1, 2),
            Arguments.of(3, false, 1, 3),
            Arguments.of(4, false, 2, 2),
            Arguments.of(5, false, 2, 3),
            Arguments.of(6, false, 2, 3),
            // Landscape (columns and rows flipped)
            Arguments.of(1, true, 1, 1),
            Arguments.of(2, true, 2, 1),
            Arguments.of(3, true, 3, 1),
            Arguments.of(4, true, 2, 2),
            Arguments.of(5, true, 3, 2),
            Arguments.of(6, true, 3, 2),
        )
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
