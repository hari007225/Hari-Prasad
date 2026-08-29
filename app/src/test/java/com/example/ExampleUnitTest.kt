package com.example

import com.example.ui.viewmodel.CountryInfo
import com.example.ui.viewmodel.POPULAR_COUNTRIES
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {
    @Test
    fun testCountryListConfiguration() {
        assertTrue("Country list should not be empty", POPULAR_COUNTRIES.isNotEmpty())
        val india = POPULAR_COUNTRIES.find { it.dialCode == "+91" }
        assertEquals("India", india?.name)
        assertEquals(10, india?.maskLength)
    }

    @Test
    fun testCountryFormatting() {
        val us = POPULAR_COUNTRIES.find { it.dialCode == "+1" }
        assertEquals("+1", us?.dialCode)
        assertEquals("🇺🇸", us?.flagEmoji)
    }
}

