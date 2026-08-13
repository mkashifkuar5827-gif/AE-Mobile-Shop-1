package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mobile_sales")
data class MobileSaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brandName: String,
    val modelName: String,
    val imeiNumber: String,
    val purchasePrice: Double,
    val salePrice: Double,
    val customerName: String,
    val customerPhone: String,
    val saleDate: Long = System.currentTimeMillis(),
    val invoiceNumber: String = "INV-${System.currentTimeMillis().toString().takeLast(6)}",
    val notes: String = ""
)
