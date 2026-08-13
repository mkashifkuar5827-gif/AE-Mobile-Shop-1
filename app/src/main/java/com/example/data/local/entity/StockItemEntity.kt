package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_items")
data class StockItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemName: String,
    val category: String, // "Chargers", "Handsfree", "Covers", "Screen Protectors", "Batteries", "Mobiles", "Other Items"
    val quantity: Int,
    val purchasePrice: Double,
    val salePrice: Double,
    val lowStockThreshold: Int = 5,
    val updatedAt: Long = System.currentTimeMillis()
)
