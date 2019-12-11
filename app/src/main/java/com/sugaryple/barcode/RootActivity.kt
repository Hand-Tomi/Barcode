package com.sugaryple.barcode

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class RootActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
    }

    override fun onResume() {
        super.onResume()
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
            this.getString(R.string.permissions_dialog_rationale),
            REQUEST_CODE_REQUEST_PERMISSION,
            *requiredPermissions
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            // 必要な権限が永久に拒否された
        } else {
            // 必要な権限が拒否された
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Timber.v("onPermissionsGranted")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    companion object {
        private const val REQUEST_CODE_REQUEST_PERMISSION = 10
    }
}
