package com.androstays.kidsdrawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    private var ibBrush: ImageButton? = null
    private var mImageButtonCurrentPaint: ImageButton? = null
    private var ibImage: ImageButton? = null
    private var ibUndo: ImageButton? = null
    private var ibRedo: ImageButton? = null


    private val openGalleryLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == RESULT_OK && result.data!=null){
            val ibBackground: ImageView = findViewById(R.id.ib_background)
            ibBackground.setImageURI(result.data?.data)
        }
    }


    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value

                if (isGranted) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(intent)
                } else if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    Toast.makeText(
                        this,
                        "Permission not granted to your external storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById(R.id.drawingView)
        drawingView?.setSizeForBrush(20.toFloat())
        ibBrush = findViewById(R.id.ibBrush)
        ibImage = findViewById(R.id.ibImage)
        ibUndo = findViewById(R.id.ibUndo)
        ibRedo = findViewById(R.id.ibRedo)

        var linearLayoutPaintColors = findViewById<LinearLayout>(R.id.paintColors)
        mImageButtonCurrentPaint = linearLayoutPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_selected)

        )

        ibBrush?.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        ibImage?.setOnClickListener {
            requestStoragePermissions()
        }

        ibUndo?.setOnClickListener {
            drawingView?.onClickUndo()
        }

        ibRedo?.setOnClickListener {
            drawingView?.onClickRedo()
        }

    }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialogue_brush_size)
        brushDialog.setTitle("Select Brush: ")
        brushDialog.show()
        val smallBtn: ImageButton = brushDialog.findViewById(R.id.ibSmallBrush)
        smallBtn.setOnClickListener {
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.ibMediumBrush)
        mediumBtn.setOnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        val largeBtn: ImageButton = brushDialog.findViewById(R.id.ibLargeBrush)
        largeBtn.setOnClickListener {
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }

    }

    fun paintClicked(view: View) {
        val imageButton = view as ImageButton
        drawingView?.setColour(imageButton.tag.toString())

        imageButton!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_selected)

        )
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_normal)

        )

        mImageButtonCurrentPaint = imageButton
    }


    private fun requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showRationaleDialog(
                "Kids Drawing App",
                "Kids Drawing App needs access to your external storage"
            )
        } else {
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private fun showRationaleDialog(
        title: String,
        message: String
    ) {
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title).setMessage(message).setPositiveButton("Cancel"){dialog, _->
            dialog.dismiss()
        }
        builder.create().show()
    }
}