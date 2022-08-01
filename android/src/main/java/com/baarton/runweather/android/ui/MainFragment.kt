package com.baarton.runweather.android.ui

//FIXME compose + viewmodel extraction to MainFragmentScreen
// class MainFragment : Fragment() {
//
//     companion object {
//         const val TAG = "MainFragment"
//     }
//
//     private val viewModel: MainFragmentViewModel by viewModel()
//
//     private lateinit var weatherPagerAdapter: WeatherPagerAdapter
//     private lateinit var tabLayout: TabLayout
//     private lateinit var viewPager: ViewPager2
//
//     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//         return inflater.inflate(R.layout.fragment_main, container, false)
//     }
//
//     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//         super.onViewCreated(view, savedInstanceState)
//
//         weatherPagerAdapter = WeatherPagerAdapter(this)
//         viewPager = view.findViewById(R.id.main_pager)
//         viewPager.adapter = weatherPagerAdapter
//
//         tabLayout = view.findViewById(R.id.main_tab_layout)
//         TabLayoutMediator(tabLayout, viewPager) { tab, position -> viewModel.createTabConfiguration(view.context, tab, position) }.attach()
//     }
//
//     inner class WeatherPagerAdapter(mainFragment: MainFragment) : FragmentStateAdapter(mainFragment) {
//
//         override fun getItemCount(): Int {
//             return viewModel.fragmentsSize()
//         }
//
//         override fun createFragment(position: Int): Fragment {
//             return viewModel.createFragment(position)
//         }
//
//     }
//
// }