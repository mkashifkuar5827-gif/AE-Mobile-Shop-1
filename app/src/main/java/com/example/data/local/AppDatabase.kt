package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ShopDao
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.MobileRepairEntity
import com.example.data.local.entity.MobileSaleEntity
import com.example.data.local.entity.ShopProfileEntity
import com.example.data.local.entity.StockItemEntity
import com.example.data.local.entity.StockTransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ShopProfileEntity::class,
        MobileSaleEntity::class,
        MobileRepairEntity::class,
        StockItemEntity::class,
        ExpenseEntity::class,
        StockTransactionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shopDao(): ShopDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kashif_mobile_shop_db"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.shopDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(dao: ShopDao) {
            // Default Shop Profile
            dao.saveShopProfile(
                ShopProfileEntity(
                    id = 1,
                    shopName = "KASHIF MOBILE AND REPAIR",
                    ownerName = "Muhammad Kashif",
                    phone = "0300-1234567",
                    whatsapp = "0300-1234567",
                    address = "Shop #15, Mobile Market, Main Boulevard",
                    city = "Lahore",
                    tagline = "Mobile Sales, Repairing & Accessories"
                )
            )

            // Seed Stock Items
            val initialStock = listOf(
                StockItemEntity(itemName = "Samsung 25W Fast Charger", category = "Chargers", quantity = 15, purchasePrice = 800.0, salePrice = 1300.0, lowStockThreshold = 4),
                StockItemEntity(itemName = "iPhone 20W Type-C Adapter", category = "Chargers", quantity = 10, purchasePrice = 1200.0, salePrice = 1800.0, lowStockThreshold = 3),
                StockItemEntity(itemName = "Infinix Original Handsfree", category = "Handsfree", quantity = 25, purchasePrice = 250.0, salePrice = 500.0, lowStockThreshold = 5),
                StockItemEntity(itemName = "Realme AirBuds T100", category = "Handsfree", quantity = 8, purchasePrice = 2800.0, salePrice = 3800.0, lowStockThreshold = 2),
                StockItemEntity(itemName = "Matte Screen Protector (Universal)", category = "Screen Protectors", quantity = 50, purchasePrice = 60.0, salePrice = 200.0, lowStockThreshold = 10),
                StockItemEntity(itemName = "iPhone 13 9D Curved Glass", category = "Screen Protectors", quantity = 3, purchasePrice = 150.0, salePrice = 350.0, lowStockThreshold = 5),
                StockItemEntity(itemName = "Samsung A54 Silicone Case", category = "Covers", quantity = 12, purchasePrice = 180.0, salePrice = 400.0, lowStockThreshold = 4),
                StockItemEntity(itemName = "Redmi Note 12 Battery (Original)", category = "Batteries", quantity = 4, purchasePrice = 1500.0, salePrice = 2400.0, lowStockThreshold = 3)
            )

            initialStock.forEach { dao.insertStockItem(it) }

            // Seed Sample Mobile Sale
            val now = System.currentTimeMillis()
            dao.insertMobileSale(
                MobileSaleEntity(
                    brandName = "Samsung",
                    modelName = "Galaxy A14 (6GB/128GB)",
                    imeiNumber = "358912345678901",
                    purchasePrice = 38000.0,
                    salePrice = 42500.0,
                    customerName = "Ali Raza",
                    customerPhone = "0321-9876543",
                    saleDate = now - 86400000L,
                    invoiceNumber = "INV-100234",
                    notes = "New box open, warranty included"
                )
            )

            // Seed Sample Mobile Repairs
            dao.insertMobileRepair(
                MobileRepairEntity(
                    customerName = "Usman Ahmed",
                    customerPhone = "0333-5551212",
                    mobileModel = "Vivo Y20",
                    imeiNumber = "864201928374651",
                    problemDetails = "Display LCD broken & charging port issue",
                    repairCharges = 4500.0,
                    partsUsed = "OG Panel + Charging Strip",
                    advancePayment = 1000.0,
                    remainingPayment = 3500.0,
                    status = "Working",
                    receivedDate = now - 43200000L,
                    invoiceNumber = "REP-200101"
                )
            )

            dao.insertMobileRepair(
                MobileRepairEntity(
                    customerName = "Hamza Khan",
                    customerPhone = "0312-4443322",
                    mobileModel = "iPhone X",
                    imeiNumber = "359012384756291",
                    problemDetails = "Battery replacement & glass repair",
                    repairCharges = 7000.0,
                    partsUsed = "100% Health Battery",
                    advancePayment = 7000.0,
                    remainingPayment = 0.0,
                    status = "Ready",
                    receivedDate = now - 172800000L,
                    invoiceNumber = "REP-200098"
                )
            )

            // Seed Sample Expenses
            dao.insertExpense(
                ExpenseEntity(
                    title = "Shop Tea & Refreshments",
                    category = "Tea & Food",
                    amount = 350.0,
                    date = now - 10000000L,
                    notes = "Daily guests and tea"
                )
            )
            dao.insertExpense(
                ExpenseEntity(
                    title = "Soldering Wire & Tape",
                    category = "Parts Purchase",
                    amount = 800.0,
                    date = now - 50000000L,
                    notes = "Repair kit tools"
                )
            )
        }
    }
}
