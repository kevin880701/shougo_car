package com.clockworkorange.shou

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.database.*
import android.net.Uri
import android.os.Bundle
import com.clockworkorange.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class MyContentProvider : ContentProvider() {

    private var userRepository: UserRepository? = null

    private val targetURI = "content://com.clockworkorange.shou/device_id"

    override fun onCreate(): Boolean {
        userRepository = (context?.applicationContext as? APP)?.appContainer?.userRepository
        return userRepository != null
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return if (uri.toString().contentEquals(targetURI)){
            val deviceId = userRepository?.getDeviceId()
            if (deviceId != null){
                SHOUDeviceIDCursor(deviceId)
            }else{
                null
            }
        }else{
            null
        }
    }

    override fun getType(uri: Uri): String? {
        return userRepository?.getDeviceId()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return -1
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return -1
    }
}
class SHOUDeviceIDCursor(private val deviceId: String): AbstractCursor(){

    private val columnName = arrayOf("device_id")

    override fun getCount(): Int {
        return 1
    }

    override fun getColumnNames(): Array<String> {
        return columnName
    }

    override fun getString(columnIndex: Int): String {
        if (columnIndex == 0) return deviceId
        return ""
    }

    override fun getShort(column: Int): Short {
        return 0
    }

    override fun getInt(column: Int): Int {
        return 0
    }

    override fun getLong(column: Int): Long {
        return 0
    }

    override fun getFloat(column: Int): Float {
        return 0f
    }

    override fun getDouble(column: Int): Double {
        return 0.0
    }

    override fun isNull(column: Int): Boolean {
        return column != 0
    }
}
