package io.middlepoint.morestuff.shared.ui.components

// TODO
//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun NotificationPermissionRequester() {
//
//    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        rememberPermissionState(
//            Manifest.permission.POST_NOTIFICATIONS
//        )
//    } else return
//
//    var dismiss by remember {
//        mutableStateOf(false)
//    }
//
//    when (permissionState.status) {
//        PermissionStatus.Granted -> {
//
//        }
//
//        is PermissionStatus.Denied -> {
//            if (!dismiss)
//                Dialog(onDismissRequest = { dismiss = true }) {
//                    Card {
//                        Column(modifier = Modifier.padding(8.dp)) {
//                            val textToShow = if (permissionState.status.shouldShowRationale) {
//                                "Notifications are important for this app. Please grant the permission."
//                            } else {
//                                "Notifications are required for this feature to be available. " +
//                                        "Please grant the permission"
//                            }
//                            Text(textToShow)
//                            Button(onClick = { permissionState.launchPermissionRequest() }) {
//                                Text("Request permission")
//                            }
//                        }
//                    }
//                }
//        }
//    }
//}