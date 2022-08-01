package com.baarton.runweather.ui

//TODO this should be deleted after migrated
// class WeatherFragment : Fragment() {
//
//     //UPGRADE I might consider extracting the top views into some common class container, similar to below ones
//     private lateinit var locationIndicatorImageView: ImageView
//     private lateinit var networkIndicatorImageView: ImageView
//     private lateinit var lastUpdatedTextView: TextView
//     private lateinit var infoTextView: TextView
//     private lateinit var refreshButton: MaterialButton
//     private lateinit var progressBar: ProgressBar
//
//     private lateinit var mainInfoPane: MainInfoPane
//     private lateinit var warningPane: WarningPane
//     private lateinit var midDetailPane: MidDetailPane
//
//     private val weatherViewModel: WeatherViewModel by viewModel()
//
//     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//         return inflater.inflate(R.layout.fragment_weather, container, false)
//     }
//
//     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//         super.onViewCreated(view, savedInstanceState)
//
//         locationIndicatorImageView = view.findViewById(R.id.weather_location_indicator)
//         networkIndicatorImageView = view.findViewById(R.id.weather_network_indicator)
//         lastUpdatedTextView = view.findViewById(R.id.weather_last_updated)
//         infoTextView = view.findViewById(R.id.weather_info_text)
//         refreshButton = view.findViewById(R.id.weather_refresh_button)
//         progressBar = view.findViewById(R.id.weather_progress_bar)
//
//         midDetailPane = MidDetailPane(view)
//         warningPane = WarningPane(view)
//         mainInfoPane = MainInfoPane(view)
//
//         refreshButton.setOnClickListener { weatherViewModel.refreshWeatherDataOneTime() }
//
//         observeLiveData()
//     }
//
//     @OptIn(ExperimentalCoroutinesApi::class)
//     override fun onResume() {
//         super.onResume()
//         weatherViewModel.loadData(requireContext())
//     }
//
//     private fun observeLiveData() {
//         weatherViewModel.apply {
//             weatherData.observe(viewLifecycleOwner) { refreshData(it) }
//             runnersInfoData.observe(viewLifecycleOwner) { refreshRunnersInfoData(it) }
//
//             infoTextRes.observe(viewLifecycleOwner) { infoTextView.text = getString(it) }
//             infoTextVisibility.observe(viewLifecycleOwner) { infoTextView.visibility = it }
//             refreshButtonVisibility.observe(viewLifecycleOwner) { refreshButton.visibility = it }
//             progressBarVisibility.observe(viewLifecycleOwner) { progressBar.visibility = it }
//
//             locationIndicatorViewVisibility.observe(viewLifecycleOwner) { locationIndicatorImageView.visibility = it }
//             locationIndicatorImageViewRes.observe(viewLifecycleOwner) { locationIndicatorImageView.setImageResource(it) }
//             networkIndicatorViewVisibility.observe(viewLifecycleOwner) { networkIndicatorImageView.visibility = it }
//             networkIndicatorImageViewRes.observe(viewLifecycleOwner) { networkIndicatorImageView.setImageResource(it) }
//             lastUpdatedViewVisibility.observe(viewLifecycleOwner) { lastUpdatedTextView.visibility = it }
//             resultViewVisibility.observe(viewLifecycleOwner) { refreshVisibility(it) }
//         }
//     }
//
//     private fun refreshData(newData: WeatherData?) {
//         if (newData == null) {
//             weatherViewModel.setLoadingError()
//         } else {
//             if (newData.weatherList.isNotEmpty()) {
//                 lastUpdatedTextView.text = getLastUpdatedText(newData.timestamp)
//
//                 midDetailPane.refresh(newData)
//                 mainInfoPane.refresh(newData)
//             }
//         }
//     }
//
//     private fun getLastUpdatedText(dataTimestamp: DateTime): CharSequence {
//         val now = DateTime.now(DateTimeZone.UTC)
//
//         val timeText = when(val timestampAge = Duration(dataTimestamp, now).standardMinutes) {
//             0L -> getString(R.string.fragment_weather_last_updated_now)
//             else -> getString(R.string.fragment_weather_last_updated_time, timestampAge)
//         }
//
//         return getString(R.string.fragment_weather_last_updated_text, timeText)
//     }
//
//     private fun refreshRunnersInfoData(newData: RunnersInfo?) {
//         if (newData == null) {
//             weatherViewModel.setLoadingError()
//         } else {
//             midDetailPane.refresh(newData)
//             warningPane.refresh(newData)
//         }
//     }
//
//     private fun refreshVisibility(visibility: Int) {
//         midDetailPane.setVisibility(visibility)
//         warningPane.setVisibility(visibility)
//         mainInfoPane.setVisibility(visibility)
//     }
//
//     override fun onPause() {
//         super.onPause()
//         weatherViewModel.closePolling()
//     }
//
// }