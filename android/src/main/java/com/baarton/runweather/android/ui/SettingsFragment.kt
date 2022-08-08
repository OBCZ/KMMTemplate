package com.baarton.runweather.android.ui

//TODO this should be fixed
// class SettingsFragment : PreferenceFragmentCompat() {
//
//     private val viewModel: SettingsViewModel by viewModel()
//
//     override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//         setPreferencesFromResource(R.xml.preferences_settings, rootKey)
//         setUnitPreference()
//         setAboutText()
//     }
//
//     private fun setUnitPreference() {
//         val unitPreference: ListPreference? = findPreference(getString(R.string.setting_units_key))
//         unitPreference?.entries = arrayOf(
//             getString(R.string.setting_units_entry_metric),
//             getString(R.string.setting_units_entry_imperial)
//         )
//
//         unitPreference?.entryValues = arrayOf(
//             Units.UnitSetting.METRIC.name,
//             Units.UnitSetting.IMPERIAL.name
//         )
//
//         unitPreference?.setDefaultValue(Units.UnitSetting.METRIC.name)
//     }
//
//     private fun setAboutText() {
//         val aboutPreference: Preference? = findPreference(getString(R.string.setting_about_key))
//         aboutPreference?.summary = getString(R.string.setting_about_summary, BuildConfig.VERSION_NAME, getString(R.string.app_copyright))
//     }
//
//     override fun onResume() {
//         super.onResume()
//         viewModel.registerSharedPrefsListener(preferenceScreen)
//     }
//
//     override fun onPause() {
//         super.onPause()
//         viewModel.unregisterSharedPrefsListener(preferenceScreen)
//     }
//
// }