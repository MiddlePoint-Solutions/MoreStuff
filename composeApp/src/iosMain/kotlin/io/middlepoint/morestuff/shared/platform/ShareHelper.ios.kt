package io.middlepoint.morestuff.shared.platform

import platform.Foundation.NSArray
import platform.Foundation.arrayWithObject
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

class ShareHelperImpl : ShareHelper {
    override fun shareMessage(content: String) {
        val items = NSArray.arrayWithObject(content)
        // Instantiate the UIActivityViewController with the items to share
        val activityViewController =
            UIActivityViewController(activityItems = items, applicationActivities = null)

        // Present the activity view controller on the provided view controller
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }
}