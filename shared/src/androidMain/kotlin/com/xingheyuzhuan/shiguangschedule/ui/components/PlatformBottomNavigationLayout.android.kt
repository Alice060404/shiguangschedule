package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
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
) {
    val backdrop = rememberLayerBackdrop()
    val density = LocalDensity.current
    val navBarBottomInsetPx = with(density) {
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx()
    }
    val navBarTotalHeightPx = with(density) { 88.dp.toPx() } + navBarBottomInsetPx

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop)
        ) {
            content(PaddingValues(0.dp))
        }

        AnimatedVisibility(
            visible = showNavigation,
            enter = slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(300)
            ) + fadeIn(tween(300)),
            exit = slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(300)
            ) + fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .then(navigationModifier)
                    .graphicsLayer {
                        val fraction = navHideFractionProvider()
                        translationY = navBarTotalHeightPx * fraction
                        alpha = (1f - fraction).coerceIn(0f, 1f)
                    }
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                LiquidGlassNavigationBar(
                    navItems = navItems,
                    currentDestination = currentDestination,
                    onTabSelected = onTabSelected,
                    backdrop = backdrop,
                    isTransparent = isTransparent,
                    modifier = Modifier
                        .widthIn(max = 320.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
