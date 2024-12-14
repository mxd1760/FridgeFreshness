package com.marcusdoucette.fridgefreshness.data

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.marcusdoucette.fridgefreshness.MainActivity
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Base64

@Serializer(forClass= Bitmap::class)
object BitmapSerializer: KSerializer<Bitmap> {
    override fun serialize(encoder: Encoder, value: Bitmap){
        val stream = ByteArrayOutputStream()
        value.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        encoder.encodeString(Base64.getEncoder().encodeToString(bytes))
    }

    override fun deserialize(decoder: Decoder): Bitmap {
        val string = decoder.decodeString()
        val bytes = Base64.getDecoder().decode(string)
        val ans =  BitmapFactory.decodeByteArray(bytes,0,bytes.size)?:
        BitmapFactory.decodeResource(Resources.getSystem(), MainActivity.DEFAULT_IMAGE)
        Log.d(MainActivity.APP_NAME,"ans: ${ans}")
        return ans?: run{
            val conf = Bitmap.Config.ARGB_8888 // see other conf types
            val bmp = Bitmap.createBitmap(100, 100, conf) // this creates a MUTABLE bitmap
            return bmp
        }
    }
}

@Serializer(forClass= LocalDate::class)
object DateSerializer: KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    override fun serialize(encoder: Encoder, value: LocalDate){
        encoder.encodeString(value.format(formatter))
    }
    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}

@Serializable
data class FridgeItemData (
    @Serializable(with= BitmapSerializer::class)
    val image: Bitmap,
    val name: String,
    //var last_checked:LocalDate,//TODO belongs in subclass for check after days
    //var freshness:Freshness,
    @Serializable(with = DateSerializer::class)
    var next_reccomended_check: LocalDate, // TODO belongs in subclass for given date
):Comparable<FridgeItemData>{
    override fun compareTo(other: FridgeItemData):Int{
        return next_reccomended_check.compareTo(other.next_reccomended_check)
    }
}
