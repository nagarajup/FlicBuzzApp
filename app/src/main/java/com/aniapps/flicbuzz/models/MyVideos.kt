package com.aniapps.flicbuzz.models

data class MyVideos(val id:String, val headline:String, val description:String, val category:String,
                    val category_id:String, val video_filename:String,val short_video_filename:String,val
                    thumb:String,val views:Int,val fav_video:String) {
}

