package com.gmail.hofmarchermatthias.toactive.model

import com.google.firebase.firestore.GeoPoint
import java.sql.Timestamp
import java.util.*

data class Appointment(
    var timestamp: com.google.firebase.Timestamp,
    var title: String,
    var description: String) {
    var location:GeoPoint? = null
    get
    set

    constructor():this(com.google.firebase.Timestamp(Date()), "", "")
}