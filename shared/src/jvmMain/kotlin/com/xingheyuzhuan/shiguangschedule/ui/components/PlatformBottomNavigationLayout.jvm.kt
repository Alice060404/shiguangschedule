package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.data.model.LiquidGlassNavigationSettings

internal actual val supportsLiquidGlassNavigation: Boolean = false

@Composable
internal actual fun PlatformBottomNavigationLayout(
    navItems: List<NavItemData>,
    currentDestination: Destination,
    onTabSelected: (Destination) -> Unit,
    showNavigation: Boolean,
    navHideFractionProvider: () -> Float,
    isTransparent: Boolean,
    liquidGlassSettings: LiquidGlassNavigationSettings,
    navigationModifier: Modifier,
    content: @Composable (PaddingValues) -> Unit
) = DefaultBottomNavigationLayout(
    navItems,
    currentDestination,
    onTabSelected,
    showNavigation,
    navHideFractionProvider,
    isTransparent,
    liquidGlassSettings,
    navigationModifier,
    content
)
