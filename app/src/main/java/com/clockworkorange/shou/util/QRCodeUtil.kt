package com.clockworkorange.shou.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import timber.log.Timber

object QRCodeUtil {

    fun createBitmap(content: String, size: Int = 128): Bitmap{
        val timeStart = System.currentTimeMillis()
        val hints = hashMapOf<EncodeHintType, Any>(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1//調整產生的QRCODE白邊
        )

        val bits = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size){
            for (y in 0 until size){
                val color = if(bits.get(x,y)) Color.BLACK else Color.WHITE
                bitmap.setPixel(x, y,  color)
            }
        }
        val timeEnd = System.currentTimeMillis()
        Timber.d("createBitmap $content ${timeEnd - timeStart} ms Thread:${Thread.currentThread().name}")
        return bitmap
    }

}