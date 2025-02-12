package com.example.android.politicalpreparedness.network.jsonadapter


import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date

class DateAdapter {
    private val format = SimpleDateFormat("yyyy-MM-dd")

    @FromJson
    fun dateFromJson(dateStr: String): Date {
        return format.parse(dateStr)
    }

    @ToJson
    fun dateToJson(date: Date): String {
        return format.format(date)
    }
}