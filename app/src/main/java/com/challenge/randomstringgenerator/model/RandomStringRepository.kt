package com.challenge.randomstringgenerator.model

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson

class RandomStringRepository(private val context: Context) {

    fun fetchRandomString(maxLength: Int): Result<RandomString> {
        return try {
            val uri = Uri.parse("content://com.iav.contestdataprovider/text")

            // Create a bundle for query arguments
            val queryArgs = Bundle().apply {
                putInt(ContentResolver.QUERY_ARG_LIMIT, maxLength)
            }

            Log.d("QueryArgs","Bundles: $queryArgs")

            // Perform the query with queryArgs
            val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.contentResolver.query(
                    uri,
                    null,
                    queryArgs,
                    null
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            cursor?.use {
                if (it.moveToFirst()) {
                    val jsonData = it.getString(it.getColumnIndexOrThrow("data"))
                    Log.d("ProviderDataRepo","data : $jsonData")
                    val randomTextWrapper = Gson().fromJson(jsonData, RandomTextWrapper::class.java)
                    val randomString = randomTextWrapper.randomText
                    Log.d("RandomStringRepo","randomString : $randomString")
                    return Result.success(randomString)
                }
            }
            Result.failure(Exception("No data returned"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}