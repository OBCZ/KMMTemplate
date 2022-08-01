package com.baarton.runweather.models

//TODO if not needed, delete

// class MainFragmentViewModel : ViewModel() {
//
//
//     private enum class MainFragments {
//         WEATHER,
//         SETTINGS
//     }
//
//     fun fragmentsSize(): Int {
//         return MainFragments.values().size
//     }
//
//     fun createFragment(fragmentPosition: Int): Fragment {
//         return when (MainFragments.values()[fragmentPosition]) {
//             MainFragments.WEATHER -> { WeatherFragment() }
//             MainFragments.SETTINGS -> { SettingsFragment() }
//         }
//     }
//
//     fun createTabConfiguration(applicationContext: Context, tab: TabLayout.Tab, position: Int) {
//         tab.text = when (MainFragments.values()[position]) {
//             MainFragments.WEATHER -> { applicationContext.getString(R.string.main_tab_today) }
//             MainFragments.SETTINGS -> { applicationContext.getString(R.string.main_tab_settings) }
//         }
//         tab.icon = when (MainFragments.values()[position]) {
//             MainFragments.WEATHER -> { AppCompatResources.getDrawable(applicationContext, R.drawable.ic_sunny_24_secondary) }
//             MainFragments.SETTINGS -> { AppCompatResources.getDrawable(applicationContext, R.drawable.ic_settings_24_secondary) }
//         }
//     }
//
// }
