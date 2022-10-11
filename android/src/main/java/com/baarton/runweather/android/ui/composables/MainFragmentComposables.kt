package com.baarton.runweather.android.ui.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.baarton.runweather.android.ui.AndroidVector.build
import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.ui.Vector
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

enum class TabItem(
    val index: Int,
    val vector: Vector,
    @StringRes val titleResId: Int,
    val screenToLoad: @Composable () -> Unit
) {
    WEATHER(0, Vector.SUN, SharedRes.strings.main_tab_today.resourceId, {
        WeatherFragmentScreen()
    }),
    SETTINGS(1, Vector.SETTINGS, SharedRes.strings.main_tab_settings.resourceId, {
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