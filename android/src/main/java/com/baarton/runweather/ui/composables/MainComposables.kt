package com.baarton.runweather.ui.composables

import android.Manifest
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.baarton.runweather.ui.AndroidVector.build
import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.ui.Vector
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    if (locationPermissionsState.allPermissionsGranted) {
        MainContent()
    } else {
        PermissionContent(locationPermissionsState)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionContent(locationPermissionsState: MultiplePermissionsState) {
    val allPermissionsRevoked =
        locationPermissionsState.permissions.size ==
            locationPermissionsState.revokedPermissions.size

    val textToShow = if (!allPermissionsRevoked) {
        // If not all the permissions are revoked, it's because the user accepted the COARSE
        // location permission, but not the FINE one.
        stringResource(id = SharedRes.strings.permission_screen_fine_location_text.resourceId)
    } else if (locationPermissionsState.shouldShowRationale) {
        // Both location permissions have been denied
        stringResource(id = SharedRes.strings.permission_screen_denied_rationale.resourceId)
    } else {
        // First time the user sees this feature or the user doesn't want to be asked again
        stringResource(id = SharedRes.strings.permission_screen_main_text.resourceId)
    }

    val buttonText = if (!allPermissionsRevoked) {
        stringResource(id = SharedRes.strings.permission_button_fine_location.resourceId)
    } else {
        stringResource(id = SharedRes.strings.permission_button.resourceId)
    }

    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = textToShow,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
            Text(
                text = buttonText,
                modifier = Modifier
                    .padding(2.dp),
                style = MaterialTheme.typography.button,
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun MainContent() {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    Column(
        content = {
            IconWithTextTabLayout(
                selectedIndex = pagerState.currentPage,
                onPageSelected = { tabItem: TabItem ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(tabItem.index)
                    }
                }
            )
            TabPage(pagerState = pagerState)
        }
    )
}

@ExperimentalPagerApi
@Composable
private fun IconWithTextTabLayout(
    selectedIndex: Int,
    onPageSelected: ((tabItem: TabItem) -> Unit)
) {
    TabRow(selectedTabIndex = selectedIndex, backgroundColor = MaterialTheme.colors.primary) {
        TabItem.values().forEachIndexed { index, tabItem ->
            Tab(selected = index == selectedIndex, onClick = {
                onPageSelected(tabItem)
            }, text = {
                Text(text = stringResource(id = tabItem.titleResId))
            }, icon = {
                Icon(
                    tabItem.vector.build(),
                    contentDescription = "TODO",
                )
            })
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun TabPage(pagerState: PagerState) {
    HorizontalPager(
        count = TabItem.values().size,
        state = pagerState
    ) { index ->
        TabItem.values().first { index == it.index }.screenToLoad()
    }
}

private enum class TabItem(
    val index: Int,
    val vector: Vector,
    @StringRes val titleResId: Int,
    val screenToLoad: @Composable () -> Unit
) {
    WEATHER(0, Vector.SUN, SharedRes.strings.main_tab_today.resourceId, {
        WeatherScreen()
    }),
    SETTINGS(1, Vector.SETTINGS, SharedRes.strings.main_tab_settings.resourceId, {
        SettingsScreen()
    })
}

@ExperimentalPagerApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen()
}

@ExperimentalPermissionsApi
@Preview(showBackground = true)
@Composable
fun PermissionPreview() {
    PermissionContent(
        locationPermissionsState = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )
    )
}