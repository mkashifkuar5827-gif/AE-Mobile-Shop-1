package com.example.data.repository

import com.example.data.local.dao.ShopDao
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.MobileRepairEntity
import com.example.data.local.entity.MobileSaleEntity
import com.example.data.local.entity.ShopProfileEntity
import com.example.data.local.entity.StockItemEntity
import com.example.data.local.entity.StockTransactionEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

data class CustomerRecord(
    val name: String,
    val phone: String,
    val purchases: List<MobileSaleEntity>,
    val repairs: List<MobileRepairEntity>,
    val totalSpent: Double,
    val remainingBalance: Double
)

data class BackupData(
    val profile: ShopProfileEntity?,
    val sales: List<MobileSaleEntity>,
    val repairs: List<MobileRepairEntity>,
    val stockItems: List<StockItemEntity>,
    val expenses: List<ExpenseEntity>,
    val transactions: List<StockTransactionEntity>
)

class ShopRepository(private val dao: ShopDao) {

    val shopProfile: Flow<ShopProfileEntity?> = dao.getShopProfile()
    val allMobileSales: Flow<List<MobileSaleEntity>> = dao.getAllMobileSales()
    val allMobileRepairs: Flow<List<MobileRepairEntity>> = dao.getAllMobileRepairs()
    val allStockItems: Flow<List<StockItemEntity>> = dao.getAllStockItems()
    val lowStockItems: Flow<List<StockItemEntity>> = dao.getLowStockItems()
    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()
    val allStockTransactions: Flow<List<StockTransactionEntity>> = dao.getAllStockTransactions()

    suspend fun saveShopProfile(profile: ShopProfileEntity) {
        dao.saveShopProfile(profile)
    }

    suspend fun addMobileSale(sale: MobileSaleEntity): Long {
        return dao.insertMobileSale(sale)
    }

    suspend fun updateMobileSale(sale: MobileSaleEntity) {
        dao.updateMobileSale(sale)
    }

    suspend fun deleteMobileSale(sale: MobileSaleEntity) {
        dao.deleteMobileSale(sale)
    }

    suspend fun addMobileRepair(repair: MobileRepairEntity): Long {
        return dao.insertMobileRepair(repair)
    }

    suspend fun updateMobileRepair(repair: MobileRepairEntity) {
        dao.updateMobileRepair(repair)
    }

    suspend fun deleteMobileRepair(repair: MobileRepairEntity) {
        dao.deleteMobileRepair(repair)
    }

    suspend fun addStockItem(item: StockItemEntity): Long {
        return dao.insertStockItem(item)
    }

    suspend fun updateStockItem(item: StockItemEntity) {
        dao.updateStockItem(item)
    }

    suspend fun deleteStockItem(item: StockItemEntity) {
        dao.deleteStockItem(item)
    }

    suspend fun sellStockItemQuantity(stockItem: StockItemEntity, sellQty: Int, customerName: String): Boolean {
        if (stockItem.quantity < sellQty) return false
        val newQty = stockItem.quantity - sellQty
        dao.updateStockItem(stockItem.copy(quantity = newQty, updatedAt = System.currentTimeMillis()))
        dao.insertStockTransaction(
            StockTransactionEntity(
                stockItemId = stockItem.id,
                itemName = stockItem.itemName,
                category = stockItem.category,
                transactionType = "SALE",
                quantity = sellQty,
                unitPrice = stockItem.salePrice,
                totalPrice = stockItem.salePrice * sellQty,
                customerOrSupplier = customerName
            )
        )
        return true
    }

    suspend fun addStockQuantity(stockItem: StockItemEntity, addQty: Int, supplierName: String) {
        val newQty = stockItem.quantity + addQty
        dao.updateStockItem(stockItem.copy(quantity = newQty, updatedAt = System.currentTimeMillis()))
        dao.insertStockTransaction(
            StockTransactionEntity(
                stockItemId = stockItem.id,
                itemName = stockItem.itemName,
                category = stockItem.category,
                transactionType = "PURCHASE",
                quantity = addQty,
                unitPrice = stockItem.purchasePrice,
                totalPrice = stockItem.purchasePrice * addQty,
                customerOrSupplier = supplierName
            )
        )
    }

    suspend fun addExpense(expense: ExpenseEntity): Long {
        return dao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        dao.deleteExpense(expense)
    }

    // Export JSON Backup
    suspend fun createBackupJson(): String {
        val profile = dao.getShopProfileDirect()
        val sales = dao.getAllMobileSalesDirect()
        val repairs = dao.getAllMobileRepairsDirect()
        val stock = dao.getAllStockItemsDirect()
        val expenses = dao.getAllExpensesDirect()
        val txs = dao.getAllStockTransactionsDirect()

        val json = JSONObject()
        json.put("version", 1)
        json.put("app", "KASHIF_MOBILE_AND_REPAIR")
        json.put("backupDate", System.currentTimeMillis())

        if (profile != null) {
            val pObj = JSONObject().apply {
                put("shopName", profile.shopName)
                put("ownerName", profile.ownerName)
                put("phone", profile.phone)
                put("whatsapp", profile.whatsapp)
                put("address", profile.address)
                put("city", profile.city)
                put("tagline", profile.tagline)
            }
            json.put("profile", pObj)
        }

        val salesArr = JSONArray()
        sales.forEach { s ->
            salesArr.put(JSONObject().apply {
                put("brandName", s.brandName)
                put("modelName", s.modelName)
                put("imeiNumber", s.imeiNumber)
                put("purchasePrice", s.purchasePrice)
                put("salePrice", s.salePrice)
                put("customerName", s.customerName)
                put("customerPhone", s.customerPhone)
                put("saleDate", s.saleDate)
                put("invoiceNumber", s.invoiceNumber)
                put("notes", s.notes)
            })
        }
        json.put("sales", salesArr)

        val repairsArr = JSONArray()
        repairs.forEach { r ->
            repairsArr.put(JSONObject().apply {
                put("customerName", r.customerName)
                put("customerPhone", r.customerPhone)
                put("mobileModel", r.mobileModel)
                put("imeiNumber", r.imeiNumber)
                put("problemDetails", r.problemDetails)
                put("repairCharges", r.repairCharges)
                put("partsUsed", r.partsUsed)
                put("advancePayment", r.advancePayment)
                put("remainingPayment", r.remainingPayment)
                put("status", r.status)
                put("receivedDate", r.receivedDate)
                put("invoiceNumber", r.invoiceNumber)
            })
        }
        json.put("repairs", repairsArr)

        val stockArr = JSONArray()
        stock.forEach { st ->
            stockArr.put(JSONObject().apply {
                put("itemName", st.itemName)
                put("category", st.category)
                put("quantity", st.quantity)
                put("purchasePrice", st.purchasePrice)
                put("salePrice", st.salePrice)
                put("lowStockThreshold", st.lowStockThreshold)
            })
        }
        json.put("stock", stockArr)

        val expArr = JSONArray()
        expenses.forEach { ex ->
            expArr.put(JSONObject().apply {
                put("title", ex.title)
                put("category", ex.category)
                put("amount", ex.amount)
                put("date", ex.date)
                put("notes", ex.notes)
            })
        }
        json.put("expenses", expArr)

        return json.toString(2)
    }

    // Restore Backup JSON
    suspend fun restoreBackupJson(jsonString: String): Boolean {
        return try {
            val json = JSONObject(jsonString)
            if (json.has("profile")) {
                val pObj = json.getJSONObject("profile")
                dao.saveShopProfile(
                    ShopProfileEntity(
                        id = 1,
                        shopName = pObj.optString("shopName", "KASHIF MOBILE AND REPAIR"),
                        ownerName = pObj.optString("ownerName", "Muhammad Kashif"),
                        phone = pObj.optString("phone", ""),
                        whatsapp = pObj.optString("whatsapp", ""),
                        address = pObj.optString("address", ""),
                        city = pObj.optString("city", ""),
                        tagline = pObj.optString("tagline", "")
                    )
                )
            }

            if (json.has("sales")) {
                val salesArr = json.getJSONArray("sales")
                val salesList = mutableListOf<MobileSaleEntity>()
                for (i in 0 until salesArr.length()) {
                    val s = salesArr.getJSONObject(i)
                    salesList.add(
                        MobileSaleEntity(
                            brandName = s.optString("brandName"),
                            modelName = s.optString("modelName"),
                            imeiNumber = s.optString("imeiNumber"),
                            purchasePrice = s.optDouble("purchasePrice", 0.0),
                            salePrice = s.optDouble("salePrice", 0.0),
                            customerName = s.optString("customerName"),
                            customerPhone = s.optString("customerPhone"),
                            saleDate = s.optLong("saleDate", System.currentTimeMillis()),
                            invoiceNumber = s.optString("invoiceNumber", "INV-${i}"),
                            notes = s.optString("notes", "")
                        )
                    )
                }
                dao.insertAllSales(salesList)
            }

            if (json.has("repairs")) {
                val repArr = json.getJSONArray("repairs")
                val repList = mutableListOf<MobileRepairEntity>()
                for (i in 0 until repArr.length()) {
                    val r = repArr.getJSONObject(i)
                    repList.add(
                        MobileRepairEntity(
                            customerName = r.optString("customerName"),
                            customerPhone = r.optString("customerPhone"),
                            mobileModel = r.optString("mobileModel"),
                            imeiNumber = r.optString("imeiNumber"),
                            problemDetails = r.optString("problemDetails"),
                            repairCharges = r.optDouble("repairCharges", 0.0),
                            partsUsed = r.optString("partsUsed"),
                            advancePayment = r.optDouble("advancePayment", 0.0),
                            remainingPayment = r.optDouble("remainingPayment", 0.0),
                            status = r.optString("status", "Received"),
                            receivedDate = r.optLong("receivedDate", System.currentTimeMillis()),
                            invoiceNumber = r.optString("invoiceNumber", "REP-${i}")
                        )
                    )
                }
                dao.insertAllRepairs(repList)
            }

            if (json.has("stock")) {
                val stArr = json.getJSONArray("stock")
                val stockList = mutableListOf<StockItemEntity>()
                for (i in 0 until stArr.length()) {
                    val st = stArr.getJSONObject(i)
                    stockList.add(
                        StockItemEntity(
                            itemName = st.optString("itemName"),
                            category = st.optString("category"),
                            quantity = st.optInt("quantity", 0),
                            purchasePrice = st.optDouble("purchasePrice", 0.0),
                            salePrice = st.optDouble("salePrice", 0.0),
                            lowStockThreshold = st.optInt("lowStockThreshold", 5)
                        )
                    )
                }
                dao.insertAllStockItems(stockList)
            }

            if (json.has("expenses")) {
                val expArr = json.getJSONArray("expenses")
                val expList = mutableListOf<ExpenseEntity>()
                for (i in 0 until expArr.length()) {
                    val ex = expArr.getJSONObject(i)
                    expList.add(
                        ExpenseEntity(
                            title = ex.optString("title"),
                            category = ex.optString("category", "Other"),
                            amount = ex.optDouble("amount", 0.0),
                            date = ex.optLong("date", System.currentTimeMillis()),
                            notes = ex.optString("notes")
                        )
                    )
                }
                dao.insertAllExpenses(expList)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
