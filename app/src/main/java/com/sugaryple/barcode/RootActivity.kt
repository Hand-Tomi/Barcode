package com.sugaryple.barcode

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class RootActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
    }

    override fun onResume() {
        super.onResume()
        Timber.v("onResume")
        if (hasPermissions().not()) {
            requestPermission()
        }
    }

    private val requiredPermissions = arrayOf(Manifest.permission.CAMERA)

    private fun hasPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, *requiredPermissions)
    }

    private fun requestPermission() {
        Timber.v("requestPermission")
        EasyPermissions.requestPermissions(
            this,
            "QRコードスキャンのためカメラ権限の許可が必要です",
            0,
            *requiredPermissions
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Timber.v("onPermissionsDenied")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Timber.v("AppSettingsDialog.Builder")
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Timber.v("onPermissionsGranted")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.v("onActivityResult")
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Timber.v("DEFAULT_SETTINGS_REQ_CODE")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Timber.v("onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
