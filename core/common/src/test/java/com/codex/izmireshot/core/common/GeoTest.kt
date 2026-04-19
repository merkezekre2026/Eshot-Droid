package com.codex.izmireshot.core.common

import org.junit.Assert.assertTrue
import org.junit.Test

class GeoTest {
    @Test
    fun computesApproximateDistanceInIzmir() {
        val meters = Geo.distanceMeters(38.4237, 27.1428, 38.4189, 27.1287)

        assertTrue(meters in 1_000.0..1_500.0)
    }
}
