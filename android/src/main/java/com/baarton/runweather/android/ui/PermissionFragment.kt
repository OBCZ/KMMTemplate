package com.baarton.runweather.ui

//TODO this should be fixed
// class PermissionFragment : Fragment() {
//
//     companion object {
//         const val TAG = "PermissionFragment"
//
//         private val logger: Logger = Logger.getLogger(PermissionFragment::class.java.name)
//     }
//
//     interface PermissionListener {
//         fun onPermissionGranted()
//     }
//
//     private lateinit var root: View
//     private var permissionListener: PermissionListener? = null
//
//     private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//         if (isGranted) {
//             activity?.supportFragmentManager?.popBackStackImmediate()
//             logger.info("Permission granted.")
//             permissionListener?.onPermissionGranted()
//         } else {
//             logger.info("Permission denied.")
//             Snackbar.make(root, R.string.fragment_permission_denied_rationale, Snackbar.LENGTH_LONG)
//                 .setAction(R.string.app_settings) {
//                     val intent = Intent()
//                     intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                     val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//                     intent.data = uri
//                     intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                     startActivity(intent)
//                 }
//                 .setMaxLines(5)
//                 .show()
//         }
//     }
//
//     override fun onAttach(context: Context) {
//         super.onAttach(context)
//
//         if (context is PermissionListener) {
//             permissionListener = context
//         } else {
//             throw RuntimeException("$context must implement ${PermissionListener::class.java.simpleName}. This is dev error.")
//         }
//     }
//
//     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//         root = inflater.inflate(R.layout.fragment_permissions, container, false).apply {
//             with(findViewById<TextView>(R.id.permissions_request_button)) {
//                 setOnClickListener {
//                     requestFineLocationPermission()
//                 }
//             }
//         }
//         return root
//     }
//
//     private fun requestFineLocationPermission() {
//         logger.info("Requesting permission check.")
//         val permissionApproved = context?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ?: return
//
//         if (permissionApproved) {
//             logger.info("Permission granted already.")
//             activity?.supportFragmentManager?.popBackStackImmediate()
//             permissionListener?.onPermissionGranted()
//         } else {
//             logger.info("Permission not granted yet. Start permission request.")
//             activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//         }
//     }
//
//     override fun onDetach() {
//         super.onDetach()
//
//         permissionListener = null
//     }
//
// }