package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xingheyuzhuan.shiguangschedule.Destination

@Composable
internal expect fun PlatformBottomNavigationLayout(
    navItems: List<NavItemData>,
    currentDestination: Destination,
    onTabSelected: (Destination) -> Unit,
    showNavigation: Boolean,
    navHideFractionProvider: () -> Float,
    isTransparent: Boolean,
    navigationModifier: Modifier,
    content: @Composable (PaddingValues) -> Unit
)

/** Keeps the pre-existing non-glass navigation on non-Android targets. */
@Composable
internal fun DefaultBottomNavigationLayout(
    navItems: List<NavItemData>,
    currentDestination: Destination,
    onTabSelected: (Destination) -> Unit,
    showNavigation: Boolean,
    navHideFractionProvider: () -> Float,
    isTransparent: Boolean,
    navigationModifier: Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val density = LocalDensity.current
    val navBarBottomInsetPx = with(density) {
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx()
    }
    val navBarTotalHeightPx = with(density) { 88.dp.toPx() } + navBarBottomInsetPx

    Box(modifier = Modifier.fillMaxSize()) {
        content(PaddingValues(0.dp))

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
                Surface(
                    shape = CircleShape,
                    color = if (isTransparent) {
                        Color.Transparent
                    } else {
                        NavigationBarDefaults.containerColor
                    },
                    shadowElevation = 0.dp,
                    tonalElevation = 3.dp
                ) {
                    FixedWidthNavBarContainer(
                        navItems = navItems,
                        currentDestination = currentDestination,
                        onTabSelected = onTabSelected,
                        minSpacing = 8.dp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FixedWidthNavBarContainer(
    navItems: List<NavItemData>,
    currentDestination: Destination,
    onTabSelected: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    minSpacing: Dp = 8.dp
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val minSpacingPx = minSpacing.roundToPx()

        val unselectedWidths = navItems.mapIndexed { index, item ->
            subcompose("measure_unselected_$index") {
                TabContent(item = item, isSelected = false, textAlpha = 0f)
            }.first().measure(constraints).width
        }

        val selectedWidths = navItems.mapIndexed { index, item ->
            subcompose("measure_selected_$index") {
                TabContent(item = item, isSelected = true, textAlpha = 1f)
            }.first().measure(constraints).width
        }

        val maxCapsuleWidth = navItems.indices.maxOfOrNull { selectedIndex ->
            navItems.indices.sumOf { index ->
                if (index == selectedIndex) selectedWidths[index] else unselectedWidths[index]
            } + (navItems.size - 1) * minSpacingPx
        } ?: 0

        val placeables = navItems.mapIndexed { index, item ->
            val isSelected = currentDestination::class == item.destination::class
            subcompose("real_$index") {
                key(item.destination) {
                    AnimatedTabItem(
                        item = item,
                        isSelected = isSelected,
                        collapsedWidth = with(this@SubcomposeLayout) {
                            unselectedWidths[index].toDp()
                        },
                        expandedWidth = with(this@SubcomposeLayout) {
                            selectedWidths[index].toDp()
                        },
                        onSelect = { onTabSelected(item.destination) }
                    )
                }
            }.first().measure(constraints)
        }

        val maxHeight = placeables.maxOfOrNull { it.height } ?: 0
        val remainingSpace = maxCapsuleWidth - placeables.sumOf { it.width }
        val gapCount = (placeables.size - 1).coerceAtLeast(1)
        val dynamicGap = remainingSpace.toFloat() / gapCount

        layout(maxCapsuleWidth, maxHeight) {
            var xCursor = 0f
            placeables.forEach { placeable ->
                placeable.placeRelative(xCursor.toInt(), 0)
                xCursor += placeable.width + dynamicGap
            }
        }
    }
}

@Composable
private fun AnimatedTabItem(
    item: NavItemData,
    isSelected: Boolean,
    collapsedWidth: Dp,
    expandedWidth: Dp,
    onSelect: () -> Unit
) {
    val animatedWidth by animateDpAsState(
        targetValue = if (isSelected) expandedWidth else collapsedWidth,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "TabWidthAnimation"
    )
    val totalDelta = (expandedWidth - collapsedWidth).value
    val currentDelta = (animatedWidth - collapsedWidth).value
    val progress = if (totalDelta > 0f) {
        (currentDelta / totalDelta).coerceIn(0f, 1f)
    } else if (isSelected) {
        1f
    } else {
        0f
    }
    val pillBgColor = if (isSelected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .width(animatedWidth)
            .height(40.dp)
            .clip(CircleShape)
            .background(pillBgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isSelected) onSelect()
            },
        contentAlignment = Alignment.CenterStart
    ) {
        TabContent(item = item, isSelected = isSelected, textAlpha = progress)
    }
}

@Composable
private fun TabContent(
    item: NavItemData,
    isSelected: Boolean,
    textAlpha: Float
) {
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            tint = Color.Unspecified,
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
                .graphicsLayer {
                    colorFilter = ColorFilter.tint(contentColor)
                }
        )

        if (textAlpha > 0f) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = item.label,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.graphicsLayer { alpha = textAlpha }
            )
        }
    }
}
