package com.farshidrezaei.ccloudplus

import android.app.Application
import com.farshidrezaei.ccloudplus.core.error.CrashHandler

class CCloudPlusApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Install global crash guard
        CrashHandler.install(this)
    }
}
