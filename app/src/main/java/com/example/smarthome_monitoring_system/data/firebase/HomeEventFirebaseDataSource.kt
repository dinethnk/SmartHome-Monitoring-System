package com.example.smarthome_monitoring_system.data.firebase

import android.util.Log
import com.example.smarthome_monitoring_system.data.model.HomeEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeEventFirebaseDataSource {

    private val eventsReference =
        FirebaseDataSource.eventsReference

    fun logEvent(
        event: HomeEvent,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val eventId = eventsReference.push().key
        if (eventId == null) {
            onError("Unable to generate event ID")
            return
        }

        event.id = eventId

        eventsReference
            .child(eventId)
            .setValue(event)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to log event")
            }
    }

    fun observeEvents(
        onSuccess: (List<HomeEvent>) -> Unit,
        onError: (String) -> Unit
    ) {
        eventsReference
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val events = mutableListOf<HomeEvent>()
                        for (child in snapshot.children) {
                            val event = child.getValue(HomeEvent::class.java)
                            if (event != null) {
                                event.id = child.key ?: ""
                                events.add(event)
                            }
                        }
                        
                        // Newest first
                        events.sortByDescending { it.timestamp }
                        
                        onSuccess(events)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onError(error.message)
                    }
                }
            )
    }
}
