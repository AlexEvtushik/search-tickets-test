package android.content

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import ua.searchtickets.common.R

fun Context.generateBitmapDescriptorWith(
    text: String,
    textSizeSp: Float
): BitmapDescriptor? =
    ContextCompat.getDrawable(this, R.drawable.ic_city_marker)
        ?.let { background ->
            background.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)

            BitmapDescriptorFactory.fromBitmap(
                Bitmap.createBitmap(
                    background.intrinsicWidth,
                    background.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                    .also { bitmap ->
                        Canvas(bitmap).also { canvas ->
                            background.draw(canvas)
                            canvas.drawText(
                                text,
                                (bitmap.width / 2).toFloat(),
                                (bitmap.height / 1.4).toFloat(),
                                Paint().apply {
                                    color = Color.WHITE
                                    textSize = textSizeSp
                                    isFakeBoldText = true
                                    textAlign = Paint.Align.CENTER
                                }
                            )
                        }
                    }
            )
        }