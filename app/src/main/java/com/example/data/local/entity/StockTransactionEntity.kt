package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_transactions")
data class StockTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stockItemId: Long,
    val itemName: String,
    val category: String,
    val transactionType: String, // "PURCHASE" or "SALE"
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double = unitPrice * quantity,
    val date: Long = System.currentTimeMillis(),
    val customerOrSupplier: String = ""
)
