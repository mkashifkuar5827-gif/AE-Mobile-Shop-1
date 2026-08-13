package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.MobileRepairEntity
import com.example.data.local.entity.MobileSaleEntity
import com.example.data.local.entity.ShopProfileEntity
import com.example.data.local.entity.StockItemEntity
import com.example.data.repository.CustomerRecord
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppLanguage(val labelEn: String, val labelUr: String, val labelAr: String = "") {
    ENGLISH("English", "انگریزی", "الإنجليزية"),
    URDU("Urdu", "اردو", "الأردية"),
    ARABIC("Arabic", "عربی", "العربية"),
    BILINGUAL("English + Urdu", "انگریزی + اردو", "الإنجليزية + الأردية")
}

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShopRepository

    init {
        val dao = AppDatabase.getDatabase(application).shopDao()
        repository = ShopRepository(dao)
    }

    val shopProfile: StateFlow<ShopProfileEntity?> = repository.shopProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allSales: StateFlow<List<MobileSaleEntity>> = repository.allMobileSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRepairs: StateFlow<List<MobileRepairEntity>> = repository.allMobileRepairs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStockItems: StateFlow<List<StockItemEntity>> = repository.allStockItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockItems: StateFlow<List<StockItemEntity>> = repository.lowStockItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions = repository.allStockTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Theme & Language
    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private val _appLanguage = MutableStateFlow(AppLanguage.BILINGUAL)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _isUrduEnabled = MutableStateFlow(true)
    val isUrduEnabled: StateFlow<Boolean> = _isUrduEnabled.asStateFlow()

    // Global Search Query
    val searchQuery = MutableStateFlow("")

    // Unified Customer Records
    val customerRecords: StateFlow<List<CustomerRecord>> = combine(allSales, allRepairs) { sales, repairs ->
        val customerMap = mutableMapOf<String, Pair<MutableList<MobileSaleEntity>, MutableList<MobileRepairEntity>>>()

        sales.forEach { s ->
            val key = s.customerPhone.ifBlank { s.customerName.trim().lowercase() }
            if (key.isNotBlank()) {
                val pair = customerMap.getOrPut(key) { mutableListOf<MobileSaleEntity>() to mutableListOf<MobileRepairEntity>() }
                pair.first.add(s)
            }
        }

        repairs.forEach { r ->
            val key = r.customerPhone.ifBlank { r.customerName.trim().lowercase() }
            if (key.isNotBlank()) {
                val pair = customerMap.getOrPut(key) { mutableListOf<MobileSaleEntity>() to mutableListOf<MobileRepairEntity>() }
                pair.second.add(r)
            }
        }

        customerMap.map { (key, pair) ->
            val firstSale = pair.first.firstOrNull()
            val firstRepair = pair.second.firstOrNull()
            val name = firstSale?.customerName ?: firstRepair?.customerName ?: "Customer"
            val phone = firstSale?.customerPhone ?: firstRepair?.customerPhone ?: key

            val totalSaleAmt = pair.first.sumOf { it.salePrice }
            val totalRepairAmt = pair.second.sumOf { it.repairCharges }
            val totalSpent = totalSaleAmt + totalRepairAmt
            val remainingBal = pair.second.sumOf { it.remainingPayment }

            CustomerRecord(
                name = name,
                phone = phone,
                purchases = pair.first,
                repairs = pair.second,
                totalSpent = totalSpent,
                remainingBalance = remainingBal
            )
        }.sortedByDescending { it.totalSpent }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun toggleTheme(dark: Boolean) {
        _isDarkMode.value = dark
    }

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
        _isUrduEnabled.value = (language == AppLanguage.URDU || language == AppLanguage.BILINGUAL)
    }

    fun toggleUrduSupport(enabled: Boolean) {
        _isUrduEnabled.value = enabled
        if (!enabled) {
            _appLanguage.value = AppLanguage.ENGLISH
        } else if (_appLanguage.value == AppLanguage.ENGLISH) {
            _appLanguage.value = AppLanguage.BILINGUAL
        }
    }

    fun saveProfile(profile: ShopProfileEntity) {
        viewModelScope.launch { repository.saveShopProfile(profile) }
    }

    fun addMobileSale(sale: MobileSaleEntity, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.addMobileSale(sale)
            onComplete(id)
        }
    }

    fun deleteMobileSale(sale: MobileSaleEntity) {
        viewModelScope.launch { repository.deleteMobileSale(sale) }
    }

    fun addMobileRepair(repair: MobileRepairEntity, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.addMobileRepair(repair)
            onComplete(id)
        }
    }

    fun updateRepairStatus(repair: MobileRepairEntity, newStatus: String, newAdvance: Double = repair.advancePayment) {
        val remaining = (repair.repairCharges - newAdvance).coerceAtLeast(0.0)
        val updated = repair.copy(
            status = newStatus,
            advancePayment = newAdvance,
            remainingPayment = remaining,
            deliveredDate = if (newStatus == "Delivered") System.currentTimeMillis() else repair.deliveredDate
        )
        viewModelScope.launch { repository.updateMobileRepair(updated) }
    }

    fun deleteMobileRepair(repair: MobileRepairEntity) {
        viewModelScope.launch { repository.deleteMobileRepair(repair) }
    }

    fun addStockItem(item: StockItemEntity) {
        viewModelScope.launch { repository.addStockItem(item) }
    }

    fun updateStockItem(item: StockItemEntity) {
        viewModelScope.launch { repository.updateStockItem(item) }
    }

    fun sellStockItem(item: StockItemEntity, sellQty: Int, customerName: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.sellStockItemQuantity(item, sellQty, customerName)
            onResult(success)
        }
    }

    fun restockItem(item: StockItemEntity, addQty: Int, supplierName: String) {
        viewModelScope.launch { repository.addStockQuantity(item, addQty, supplierName) }
    }

    fun deleteStockItem(item: StockItemEntity) {
        viewModelScope.launch { repository.deleteStockItem(item) }
    }

    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.addExpense(expense) }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    // Backup & Restore
    fun createBackup(onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val json = repository.createBackupJson()
            onResult(json)
        }
    }

    fun restoreBackup(jsonString: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.restoreBackupJson(jsonString)
            onResult(success)
        }
    }

    // Date Utilities
    fun isToday(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = Calendar.getInstance()
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isThisMonth(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = Calendar.getInstance()
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    }
}
