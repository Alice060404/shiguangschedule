package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xingheyuzhuan.shiguangschedule.Destination

@Composable
internal actual fun PlatformBottomNavigationLayout(
    navItems: List<NavItemData>,
    currentDestination: Destination,
    onTabSelected: (Destination) -> Unit,
    showNavigation: Boolean,
    navHideFractionProvider: () -> Float,
    isTransparent: Boolean,
    navigationModifier: Modifier,
    content: @Composable (PaddingValues) -> Unit
) = DefaultBottomNavigationLayout(
    navItems,
    currentDestination,
    onTabSelected,
    showNavigation,
    navHideFractionProvider,
    isTransparent,
    navigationModifier,
    content
)
