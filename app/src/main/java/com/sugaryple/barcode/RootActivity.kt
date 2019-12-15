package com.sugaryple.barcode

import android.Manifest
import android.graphics.Matrix
import android.os.Bundle
import android.util.Size
import android.view.Surface
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import kotlinx.android.synthetic.main.activity_root.*
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class RootActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        view_finder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            Timber.v("addOnLayoutChangeListener")
            updateTransform()
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.v("onResume")
        if (hasPermissions()) {
            view_finder.post { startCamera() }
        } else {
            requestPermission()
        }
    }

    private fun startCamera() {
        Timber.v("startCamera")
        // 미리보기를 위한 환경설정 객체를 만듭니다.
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(640, 480))
        }.build()

        // 미리보기 객체를 만듭니다.
        val preview = Preview(previewConfig)

        // viewfinder가 업데이트 될 때마다 레이아웃을 다시 계산
        preview.setOnPreviewOutputUpdateListener {
            Timber.v("setOnPreviewOutputUpdateListener")

            // SurfaceTexture를 업데이트하려면 제거하고 다시 추가해야 합니다.
            val parent = view_finder.parent as ViewGroup
            parent.removeView(view_finder)
            parent.addView(view_finder, 0)

            view_finder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        // Use Case를 라이프 사이클에 바인딩
        // "this"가 LifecycleOwner가 아니여서 Android Studio가 컴플레인을 걸 경우
        // 프로젝트를 다시 빌드하거나, appcompat 종속성을
        // version 1.1.0 올리세요
        CameraX.bindToLifecycle(this, preview)
    }

    private fun updateTransform() {
        Timber.v("updateTransform")
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = view_finder.width / 2f
        val centerY = view_finder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(view_finder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        view_finder.setTransform(matrix)
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
