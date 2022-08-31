package com.baarton.runweather.android.ui.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.baarton.runweather.android.R
import com.baarton.runweather.res.SharedRes
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

enum class TabItem(
    val index: Int,
    @DrawableRes val iconResId: Int,
    @StringRes val titleResId: Int,
    val screenToLoad: @Composable () -> Unit
) {
    WEATHER(0, R.drawable.ic_sunny_24_secondary, SharedRes.strings.main_tab_today.resourceId, {
        WeatherFragmentScreen()
    }),
    SETTINGS(1, R.drawable.ic_settings_24_secondary, SharedRes.strings.main_tab_settings.resourceId, {
        SettingsFragmentScreen()
    })
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainFragmentScreen() {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    Column(content = {

        IconWithTextTabLayout(
            selectedIndex = pagerState.currentPage,
            onPageSelected = { tabItem: TabItem ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(tabItem.index)
                }
            })
        TabPage(pagerState = pagerState)
    })
}

@ExperimentalPagerApi
@Composable
fun IconWithTextTabLayout(
    selectedIndex: Int,
    onPageSelected: ((tabItem: TabItem) -> Unit)
) {
    TabRow(selectedTabIndex = selectedIndex) {
        TabItem.values().forEachIndexed { index, tabItem ->
            Tab(selected = index == selectedIndex, onClick = {
                onPageSelected(tabItem)
            }, text = {
                Text(text = stringResource(id = tabItem.titleResId))
            }, icon = {
                Icon(ImageVector.vectorResource(id = tabItem.iconResId), "TODO")
            })
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabPage(pagerState: PagerState) {
    HorizontalPager(
        count = TabItem.values().size,
        state = pagerState
    ) { index ->
        TabItem.values().first { index == it.index }.screenToLoad()
    }
}

@ExperimentalPagerApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainFragmentScreen()
}