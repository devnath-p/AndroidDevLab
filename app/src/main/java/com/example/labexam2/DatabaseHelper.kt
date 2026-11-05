package com.example.labexam2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "TripJournal.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE trips (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                date TEXT NOT NULL,
                notes TEXT,
                latitude REAL,
                longitude REAL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE photos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                trip_id INTEGER,
                uri TEXT,
                FOREIGN KEY(trip_id) REFERENCES trips(id)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS photos")
        db.execSQL("DROP TABLE IF EXISTS trips")
        onCreate(db)
    }

    fun addTrip(trip: Trip): Long {

        val values = ContentValues().apply {
            put("title", trip.title)
            put("date", trip.date)
            put("notes", trip.notes)
            put("latitude", trip.latitude)
            put("longitude", trip.longitude)
        }

        return writableDatabase.insert("trips", null, values)
    }

    fun getAllTrips(): ArrayList<Trip> {

        val trips = ArrayList<Trip>()

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM trips ORDER BY id DESC",
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                trips.add(
                    Trip(
                        id = it.getInt(0),
                        title = it.getString(1),
                        date = it.getString(2),
                        notes = it.getString(3) ?: "",
                        latitude = it.getDouble(4),
                        longitude = it.getDouble(5)
                    )
                )
            }
        }

        return trips
    }

    fun getTrip(id: Int): Trip? {
        return getAllTrips().find { it.id == id }
    }

    fun updateTrip(trip: Trip): Int {

        val values = ContentValues().apply {
            put("title", trip.title)
            put("date", trip.date)
            put("notes", trip.notes)
            put("latitude", trip.latitude)
            put("longitude", trip.longitude)
        }

        return writableDatabase.update(
            "trips",
            values,
            "id=?",
            arrayOf(trip.id.toString())
        )
    }

    fun deleteTrip(id: Int): Int {

        val db = writableDatabase

        db.beginTransaction()

        return try {

            db.delete(
                "photos",
                "trip_id=?",
                arrayOf(id.toString())
            )

            val result = db.delete(
                "trips",
                "id=?",
                arrayOf(id.toString())
            )

            db.setTransactionSuccessful()
            result

        } finally {
            db.endTransaction()
        }
    }

    fun addPhoto(tripId: Int, uri: String): Long {

        val values = ContentValues().apply {
            put("trip_id", tripId)
            put("uri", uri)
        }

        return writableDatabase.insert("photos", null, values)
    }

    fun getPhotos(tripId: Int): ArrayList<TripPhoto> {

        val photos = ArrayList<TripPhoto>()

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM photos WHERE trip_id=?",
            arrayOf(tripId.toString())
        )

        cursor.use {

            while (it.moveToNext()) {

                photos.add(
                    TripPhoto(
                        id = it.getInt(0),
                        tripId = it.getInt(1),
                        uri = it.getString(2)
                    )
                )
            }
        }

        return photos
    }

    fun updatePhoto(photoId: Int, uri: String): Int {

        val values = ContentValues().apply {
            put("uri", uri)
        }

        return writableDatabase.update(
            "photos",
            values,
            "id=?",
            arrayOf(photoId.toString())
        )
    }

    fun deletePhoto(photoId: Int): Int {

        return writableDatabase.delete(
            "photos",
            "id=?",
            arrayOf(photoId.toString())
        )
    }
}