package com.example.smarthome_monitoring_system.data.firebase

import android.util.Log
import com.example.smarthome_monitoring_system.data.model.UsageRecord
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class UsageRecordFirebaseDataSource {

    private val usageRecordsReference =
        FirebaseDataSource.usageRecordsReference

    fun observeUsageRecords(
        onSuccess: (List<UsageRecord>) -> Unit,
        onError: (String) -> Unit
    ) {
        usageRecordsReference
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val records = mutableListOf<UsageRecord>()
                        for (child in snapshot.children) {
                            val record = child.getValue(UsageRecord::class.java)
                            if (record != null) {
                                record.id = child.key ?: ""
                                records.add(record)
                            }
                        }
                        
                        // Newest records first
                        records.sortByDescending { it.timestamp }
                        
                        onSuccess(records)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("UsageRecordFirebase", error.message)
                        onError(error.message)
                    }
                }
            )
    }
}
