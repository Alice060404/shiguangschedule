package com.xingheyuzhuan.shiguangschedule.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xingheyuzhuan.shiguangschedule.Destination
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import shiguangschedule.shared.generated.resources.Res
import shiguangschedule.shared.generated.resources.account_circle_24px
import shiguangschedule.shared.generated.resources.account_circle_filled_24px
import shiguangschedule.shared.generated.resources.nav_course_schedule
import shiguangschedule.shared.generated.resources.nav_settings
import shiguangschedule.shared.generated.resources.nav_today_schedule
import shiguangschedule.shared.generated.resources.view_agenda_24px
import shiguangschedule.shared.generated.resources.view_agenda_filled_24px
import shiguangschedule.shared.generated.resources.view_week_24px
import shiguangschedule.shared.generated.resources.view_week_filled_24px

@Immutable
internal data class NavItemData(
    val label: String,
    val destination: Destination,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * 自适应导航栏组件
 */
@Composable
fun AdaptiveNavigationScaffold(
    currentDestination: Destination,
    onTabSelected: (Destination) -> Unit,
    modifier: Modifier = Modifier,
    showNavigation: Boolean = true,
    navHideFractionProvider: () -> Float = { 0f },
    isTransparent: Boolean = false,
    navigationModifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val todayLabel = stringResource(Res.string.nav_today_schedule)
    val courseLabel = stringResource(Res.string.nav_course_schedule)
    val settingsLabel = stringResource(Res.string.nav_settings)

    val todaySelectedIcon = vectorResource(Res.drawable.view_agenda_filled_24px)
    val todayUnselectedIcon = vectorResource(Res.drawable.view_agenda_24px)
    val courseSelectedIcon = vectorResource(Res.drawable.view_week_filled_24px)
    val courseUnselectedIcon = vectorResource(Res.drawable.view_week_24px)
    val settingsSelectedIcon = vectorResource(Res.drawable.account_circle_filled_24px)
    val settingsUnselectedIcon = vectorResource(Res.drawable.account_circle_24px)

    val navItems = remember(
        todayLabel, courseLabel, settingsLabel,
        todaySelectedIcon, todayUnselectedIcon,
        courseSelectedIcon, courseUnselectedIcon,
        settingsSelectedIcon, settingsUnselectedIcon
    ) {
        listOf(
            NavItemData(
                label = todayLabel,
                destination = Destination.TodaySchedule,
                selectedIcon = todaySelectedIcon,
                unselectedIcon = todayUnselectedIcon
            ),
            NavItemData(
                label = courseLabel,
                destination = Destination.CourseSchedule,
                selectedIcon = courseSelectedIcon,
                unselectedIcon = courseUnselectedIcon
            ),
            NavItemData(
                label = settingsLabel,
                destination = Destination.Settings,
                selectedIcon = settingsSelectedIcon,
                unselectedIcon = settingsUnselectedIcon
            )
        )
    }

    val layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

    Box(modifier = modifier.fillMaxSize()) {
        when (layoutType) {
            NavigationSuiteType.NavigationRail -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    AnimatedVisibility(
                        visible = showNavigation,
                        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(tween(300)),
                        exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(tween(300))
                    ) {
                        NavigationRail(
                            containerColor = if (isTransparent) Color.Transparent else NavigationRailDefaults.ContainerColor,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            navItems.forEach { item ->
                                key(item.destination) {
                                    val isSelected = currentDestination::class == item.destination::class
                                    NavigationRailItem(
                                        selected = isSelected,
                                        onClick = { if (!isSelected) onTabSelected(item.destination) },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.label,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        },
                                        label = { Text(item.label, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        content(PaddingValues(0.dp))
                    }
                }
            }

            NavigationSuiteType.NavigationBar -> {
                PlatformBottomNavigationLayout(
                    navItems = navItems,
                    currentDestination = currentDestination,
                    onTabSelected = onTabSelected,
                    showNavigation = showNavigation,
                    navHideFractionProvider = navHideFractionProvider,
                    isTransparent = isTransparent,
                    navigationModifier = navigationModifier,
                    content = content
                )
            }

            else -> {
                content(PaddingValues(0.dp))
            }
        }
    }
}
