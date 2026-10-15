package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.xingheyuzhuan.shiguangschedule.Destination
import com.xingheyuzhuan.shiguangschedule.ui.theme.LocalIsDarkTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val IndicatorSpring = spring<Float>(
    dampingRatio = 0.72f,
    stiffness = 380f,
    visibilityThreshold = 0.001f
)

/**
 * Android floating bottom navigation that samples the real screen content behind it.
 *
 * The visual treatment is based on the Backdrop catalog's LiquidBottomTabs,
 * while selection and navigation remain owned by the app's existing back stack.
 */
@Composable
internal fun LiquidGlassNavigationBar(
    navItems: List<NavItemData>,
    currentDestination: Destination,
    onTabSelected: (Destination) -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isTransparent: Boolean = false
) {
    val selectedIndex = navItems.indexOfFirst {
        currentDestination::class == it.destination::class
    }.coerceAtLeast(0)
    val isDarkTheme = LocalIsDarkTheme.current
    val layoutDirection = LocalLayoutDirection.current
    val latestOnTabSelected by rememberUpdatedState(onTabSelected)

    val containerColor = when {
        isTransparent -> Color.Transparent
        isDarkTheme -> MaterialTheme.colorScheme.surface.copy(alpha = 0.58f)
        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.42f)
    }
    val selectionTint = if (isDarkTheme) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
    }
    val innerShadowColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.09f)

    BoxWithConstraints(
        modifier = modifier
            .height(64.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { CircleShape },
                effects = {
                    vibrancy()
                    blur(8.dp.toPx())
                    lens(
                        refractionHeight = 18.dp.toPx(),
                        refractionAmount = 12.dp.toPx()
                    )
                },
                highlight = { Highlight.Default.copy(alpha = if (isDarkTheme) 0.55f else 0.7f) },
                innerShadow = {
                    InnerShadow(
                        radius = 8.dp,
                        color = innerShadowColor
                    )
                },
                onDrawSurface = { drawRect(containerColor) }
            )
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val tabCount = navItems.size.coerceAtLeast(1)
        val tabWidthPx = constraints.maxWidth.toFloat() / tabCount
        val direction = if (layoutDirection == LayoutDirection.Ltr) 1f else -1f
        val indicatorPosition = remember { Animatable(selectedIndex.toFloat()) }
        val animationScope = rememberCoroutineScope()
        var isDragging by remember { mutableStateOf(false) }

        LaunchedEffect(selectedIndex, isDragging) {
            if (!isDragging) {
                indicatorPosition.animateTo(selectedIndex.toFloat(), IndicatorSpring)
            }
        }

        val scaleX by animateFloatAsState(
            targetValue = if (isDragging) 1.16f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "LiquidNavIndicatorScaleX"
        )
        val scaleY by animateFloatAsState(
            targetValue = if (isDragging) 0.94f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "LiquidNavIndicatorScaleY"
        )

        Box(
            modifier = Modifier
                .clearAndSetSemantics { }
                .graphicsLayer {
                    translationX = direction * indicatorPosition.value * tabWidthPx
                    this.scaleX = scaleX
                    this.scaleY = scaleY
                }
                .fillMaxWidth(1f / tabCount)
                .fillMaxHeight()
                .padding(vertical = 2.dp)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { CircleShape },
                    effects = {
                        vibrancy()
                        blur(4.dp.toPx())
                        lens(
                            refractionHeight = 12.dp.toPx(),
                            refractionAmount = 10.dp.toPx()
                        )
                    },
                    highlight = { Highlight.Default.copy(alpha = if (isDragging) 0.95f else 0.72f) },
                    innerShadow = {
                        InnerShadow(
                            radius = 6.dp,
                            color = innerShadowColor
                        )
                    },
                    onDrawSurface = { drawRect(selectionTint) }
                )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val contentColor = if (isSelected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .selectable(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) latestOnTabSelected(item.destination)
                            },
                            role = Role.Tab
                        )
                        .semantics(mergeDescendants = true) {
                            contentDescription = item.label
                        }
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                colorFilter = ColorFilter.tint(contentColor)
                            }
                    )

                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                        exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.label,
                                color = contentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }

        // Keep gestures above the tab row while the glass indicator itself stays
        // behind the icon and label content.
        Box(
            modifier = Modifier
                .clearAndSetSemantics { }
                .graphicsLayer {
                    translationX = direction * indicatorPosition.value * tabWidthPx
                }
                .pointerInput(tabWidthPx, tabCount, layoutDirection) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                            animationScope.launch { indicatorPosition.stop() }
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onDragEnd = {
                            val targetIndex = indicatorPosition.value
                                .roundToInt()
                                .coerceIn(0, tabCount - 1)
                            isDragging = false
                            latestOnTabSelected(navItems[targetIndex].destination)
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val target = (
                                indicatorPosition.value + direction * dragAmount / tabWidthPx
                                ).coerceIn(0f, (tabCount - 1).toFloat())
                            animationScope.launch { indicatorPosition.snapTo(target) }
                        }
                    )
                }
                .fillMaxWidth(1f / tabCount)
                .fillMaxHeight()
        )
    }
}
