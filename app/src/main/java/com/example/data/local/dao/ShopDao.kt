package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.MobileRepairEntity
import com.example.data.local.entity.MobileSaleEntity
import com.example.data.local.entity.ShopProfileEntity
import com.example.data.local.entity.StockItemEntity
import com.example.data.local.entity.StockTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {

    // --- Shop Profile ---
    @Query("SELECT * FROM shop_profile WHERE id = 1")
    fun getShopProfile(): Flow<ShopProfileEntity?>

    @Query("SELECT * FROM shop_profile WHERE id = 1")
    suspend fun getShopProfileDirect(): ShopProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveShopProfile(profile: ShopProfileEntity)

    // --- Mobile Sales ---
    @Query("SELECT * FROM mobile_sales ORDER BY saleDate DESC")
    fun getAllMobileSales(): Flow<List<MobileSaleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMobileSale(sale: MobileSaleEntity): Long

    @Update
    suspend fun updateMobileSale(sale: MobileSaleEntity)

    @Delete
    suspend fun deleteMobileSale(sale: MobileSaleEntity)

    @Query("SELECT * FROM mobile_sales WHERE saleDate BETWEEN :startTime AND :endTime ORDER BY saleDate DESC")
    fun getSalesBetween(startTime: Long, endTime: Long): Flow<List<MobileSaleEntity>>

    // --- Mobile Repairs ---
    @Query("SELECT * FROM mobile_repairs ORDER BY receivedDate DESC")
    fun getAllMobileRepairs(): Flow<List<MobileRepairEntity>>

    @Query("SELECT * FROM mobile_repairs WHERE status = :status ORDER BY receivedDate DESC")
    fun getRepairsByStatus(status: String): Flow<List<MobileRepairEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMobileRepair(repair: MobileRepairEntity): Long

    @Update
    suspend fun updateMobileRepair(repair: MobileRepairEntity)

    @Delete
    suspend fun deleteMobileRepair(repair: MobileRepairEntity)

    @Query("SELECT * FROM mobile_repairs WHERE receivedDate BETWEEN :startTime AND :endTime ORDER BY receivedDate DESC")
    fun getRepairsBetween(startTime: Long, endTime: Long): Flow<List<MobileRepairEntity>>

    // --- Stock Items ---
    @Query("SELECT * FROM stock_items ORDER BY itemName ASC")
    fun getAllStockItems(): Flow<List<StockItemEntity>>

    @Query("SELECT * FROM stock_items WHERE category = :category ORDER BY itemName ASC")
    fun getStockByCategory(category: String): Flow<List<StockItemEntity>>

    @Query("SELECT * FROM stock_items WHERE quantity <= lowStockThreshold ORDER BY quantity ASC")
    fun getLowStockItems(): Flow<List<StockItemEntity>>

    @Query("SELECT * FROM stock_items WHERE id = :id")
    suspend fun getStockItemById(id: Long): StockItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockItem(item: StockItemEntity): Long

    @Update
    suspend fun updateStockItem(item: StockItemEntity)

    @Delete
    suspend fun deleteStockItem(item: StockItemEntity)

    // --- Stock Transactions ---
    @Query("SELECT * FROM stock_transactions ORDER BY date DESC")
    fun getAllStockTransactions(): Flow<List<StockTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockTransaction(transaction: StockTransactionEntity)

    // --- Expenses ---
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startTime AND :endTime ORDER BY date DESC")
    fun getExpensesBetween(startTime: Long, endTime: Long): Flow<List<ExpenseEntity>>

    // --- Direct list getters for Export/Backup ---
    @Query("SELECT * FROM mobile_sales")
    suspend fun getAllMobileSalesDirect(): List<MobileSaleEntity>

    @Query("SELECT * FROM mobile_repairs")
    suspend fun getAllMobileRepairsDirect(): List<MobileRepairEntity>

    @Query("SELECT * FROM stock_items")
    suspend fun getAllStockItemsDirect(): List<StockItemEntity>

    @Query("SELECT * FROM expenses")
    suspend fun getAllExpensesDirect(): List<ExpenseEntity>

    @Query("SELECT * FROM stock_transactions")
    suspend fun getAllStockTransactionsDirect(): List<StockTransactionEntity>

    // Restore inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSales(sales: List<MobileSaleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRepairs(repairs: List<MobileRepairEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStockItems(items: List<StockItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExpenses(expenses: List<ExpenseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(transactions: List<StockTransactionEntity>)
}
