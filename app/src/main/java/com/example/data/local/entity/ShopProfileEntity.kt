package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_profile")
data class ShopProfileEntity(
    @PrimaryKey val id: Int = 1,
    val shopName: String = "KASHIF MOBILE AND REPAIR",
    val ownerName: String = "Kashif Mobile Owner",
    val phone: String = "0300-0000000",
    val whatsapp: String = "0300-0000000",
    val address: String = "Main Mobile Market, Shop #12",
    val city: String = "City Center",
    val tagline: String = "Mobile Sales, Repairing & Accessories",
    val logoUri: String? = null
)
