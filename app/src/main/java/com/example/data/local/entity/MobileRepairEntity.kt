package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mobile_repairs")
data class MobileRepairEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val customerPhone: String,
    val mobileModel: String,
    val imeiNumber: String = "",
    val problemDetails: String,
    val repairCharges: Double,
    val partsUsed: String = "",
    val advancePayment: Double = 0.0,
    val remainingPayment: Double = repairCharges - advancePayment,
    val status: String = "Received", // "Received", "Working", "Ready", "Delivered"
    val receivedDate: Long = System.currentTimeMillis(),
    val deliveredDate: Long? = null,
    val invoiceNumber: String = "REP-${System.currentTimeMillis().toString().takeLast(6)}"
)
