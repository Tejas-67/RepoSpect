package com.example.repospect.Database

import com.example.repospect.DataModel.Owner
import com.google.gson.Gson

class TypeConverter {
    @androidx.room.TypeConverter
    fun fromOwner(owner: Owner?): String? {
        // Convert Owner to String representation for storage in the database
        return owner?.let { Gson().toJson(it) }
    }

    @androidx.room.TypeConverter
    fun toOwner(ownerString: String?): Owner? {
        // Convert String representation from the database to Owner object
        return ownerString?.let { Gson().fromJson(it, Owner::class.java) }
    }
}