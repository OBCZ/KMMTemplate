package com.baarton.runweather.android.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

private val tabs = listOf(
    TabItem.Home,
    TabItem.Settings,
    TabItem.Contacts
)

sealed class TabItem(
    val index:Int,
    val icon: ImageVector,
    val title: String,
    val screenToLoad: @Composable () -> Unit
) {
    //TODO change according to my needs
    object Home : TabItem(0, Icons.Default.Home, "Home", {
        WeatherFragmentScreen()
    })
    object Contacts : TabItem(2, Icons.Default.ShoppingCart, "Cart", {
        ContactScreenForTab()
    })
    object Settings : TabItem(1, Icons.Default.Settings, "Settings", {
        SettingsScreenForTab()
    })
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainFragmentScreen() {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    Column(content = {

        IconWithTextTabLayout(
            tabs,
            selectedIndex = pagerState.currentPage,
            onPageSelected = { tabItem: TabItem ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(tabItem.index)
                }
            })
        TabPage(tabItems = tabs, pagerState = pagerState)
    })
}

@ExperimentalPagerApi
@Composable
fun IconWithTextTabLayout(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onPageSelected: ((tabItem: TabItem) -> Unit)
) {
    TabRow(selectedTabIndex = selectedIndex) {
        tabs.forEachIndexed { index, tabItem ->
            Tab(selected = index == selectedIndex, onClick = {
                onPageSelected(tabItem)
            }, text = {
                Text(text = tabItem.title)
            }, icon = {
                Icon(tabItem.icon, "")
            })
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabPage(pagerState: PagerState, tabItems: List<TabItem>) {
    HorizontalPager(
        count = tabs.size,
        state = pagerState
    ) { index ->
        tabItems[index].screenToLoad()
    }
}

@Composable
fun ContactScreenForTab() {
    Column(
        content = {
            Text(text = "You are in Contact Us Screen")
        }, modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
}

@Composable
fun SettingsScreenForTab() {
    Column(
        content = {
            Text(text = "You are in Settings Screen")
        }, modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
}

@ExperimentalPagerApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainFragmentScreen()
}