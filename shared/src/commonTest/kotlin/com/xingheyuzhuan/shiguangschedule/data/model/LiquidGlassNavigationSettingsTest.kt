package com.xingheyuzhuan.shiguangschedule.data.model

import kotlin.test.Test
import kotlin.test.assertEquals

class LiquidGlassNavigationSettingsTest {
    @Test
    fun defaultsMatchCurrentLiquidGlassNavigation() {
        val settings = LiquidGlassNavigationSettings()

        assertEquals(320f, settings.widthDp)
        assertEquals(64f, settings.heightDp)
        assertEquals(8f, settings.blurRadiusDp)
        assertEquals(0.40f, settings.opacity.light)
        assertEquals(0.46f, settings.opacity.dark)
        assertEquals(1f, settings.refractionStrength)
        assertEquals(1f, settings.animationElasticity)
        assertEquals(true, settings.chromaticAberration)
    }

    @Test
    fun sanitizedClampsEveryNumericSettingToSupportedRange() {
        val settings = LiquidGlassNavigationSettings(
            widthDp = 500f,
            heightDp = 40f,
            blurRadiusDp = 20f,
            opacity = LightDarkValue(light = 0.1f, dark = 0.8f),
            refractionStrength = -1f,
            animationElasticity = 2f
        ).sanitized()

        assertEquals(360f, settings.widthDp)
        assertEquals(56f, settings.heightDp)
        assertEquals(12f, settings.blurRadiusDp)
        assertEquals(0.20f, settings.opacity.light)
        assertEquals(0.65f, settings.opacity.dark)
        assertEquals(0f, settings.refractionStrength)
        assertEquals(1.2f, settings.animationElasticity)
    }
}
