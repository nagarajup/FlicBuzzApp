package com.aniapps.flicbuzzapp.models

import android.os.Parcel
import android.os.Parcelable

data class MyVideos(val id:String, val headline:String, val description:String, val category:String,
                    val category_id:String, val video_filename:String,val share_url:String,val
                    thumb:String,val views:Int,val fav_video:String, val short_desc:String,val video_date2:String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(headline)
        parcel.writeString(description)
        parcel.writeString(category)
        parcel.writeString(category_id)
        parcel.writeString(video_filename)
        parcel.writeString(share_url)
        parcel.writeString(thumb)
        parcel.writeInt(views)
        parcel.writeString(fav_video)
        parcel.writeString(short_desc)
        parcel.writeString(video_date2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyVideos> {
        override fun createFromParcel(parcel: Parcel): MyVideos {
            return MyVideos(parcel)
        }

        override fun newArray(size: Int): Array<MyVideos?> {
            return arrayOfNulls(size)
        }
    }

}

