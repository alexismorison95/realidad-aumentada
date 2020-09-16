package com.example.readqr

import android.graphics.Bitmap
import android.util.Log
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer

object Utils {

    fun scanQRImage(bMap: Bitmap): String? {

        var contents: String? = null
        val intArray = IntArray(bMap.width * bMap.height)

        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)
        val source: LuminanceSource = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader: Reader = MultiFormatReader()

        try {
            val result = reader.decode(bitmap)
            contents = result.text

        } catch (e: Exception) {
            Log.e("QrTest", "Error decoding barcode", e)
        }
        return contents
    }
}