package net.solvetheriddle.fermentlog.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

/**
 * Helper class for Firebase serialization/deserialization
 */
object FirebaseTypeConverters {
    
    /**
     * Converts a Date to a Long for Firebase storage
     */
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay()
            ?.atZone(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()
    }
    
    /**
     * Converts a Long from Firebase to a Date
     */
    fun timestampToDate(timestamp: Long?): LocalDate? {
        return timestamp?.let { Date(it).toInstant().atZone(ZoneId.systemDefault()).toLocalDate() }
    }
    
    /**
     * Extension function to set a Date value in Firebase
     */
    fun DatabaseReference.setDateValue(date: LocalDate?) {
        this.setValue(dateToTimestamp(date))
    }
    
    /**
     * Extension function to read a Date value from Firebase
     */
    fun DataSnapshot.getDateValue(): LocalDate? {
        val timestamp = this.getValue(Long::class.java)
        return timestampToDate(timestamp)
    }
}