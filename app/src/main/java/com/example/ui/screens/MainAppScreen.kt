package com.example.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.accounts.DailyAccountsScreen
import com.example.ui.screens.backup.BackupRestoreScreen
import com.example.ui.screens.customers.CustomerRecordScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.profile.ShopProfileScreen
import com.example.ui.screens.repairs.MobileRepairScreen
import com.example.ui.screens.sales.MobileSalesScreen
import com.example.ui.screens.search.GlobalSearchScreen
import com.example.ui.screens.stock.StockManagementScreen
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.ShopViewModel

enum class NavigationTab(val titleEn: String, val titleUr: String, val titleAr: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", "ڈیش بورڈ", "الرئيسية", Icons.Default.Dashboard),
    SALES("Sales", "سیل", "المبيعات", Icons.Default.PointOfSale),
    REPAIRS("Repairs", "مرمت", "الصيانة", Icons.Default.Build),
    STOCK("Stock", "اسٹاک", "المخزون", Icons.Default.Inventory),
    CUSTOMERS("Customers", "کسٹمرز", "العملاء", Icons.Default.People),
    ACCOUNTS("Accounts", "اکاؤنٹس", "الحسابات", Icons.Default.AccountBalanceWallet),
    PROFILE("Profile", "پروفائل", "الملف", Icons.Default.Person)
}

@Composable
fun MainAppScreen(
    viewModel: ShopViewModel
) {
    var selectedTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }
    var currentSubScreen by remember { mutableStateOf<String?>(null) } // "search", "backup"
    val isUrduEnabled by viewModel.isUrduEnabled.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = currentSubScreen == null && selectedTab == tab,
                        onClick = {
                            currentSubScreen = null
                            selectedTab = tab
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.titleEn) },
                        label = {
                            val title = when (appLanguage) {
                                AppLanguage.ENGLISH -> tab.titleEn
                                AppLanguage.URDU -> tab.titleUr
                                AppLanguage.ARABIC -> tab.titleAr
                                AppLanguage.BILINGUAL -> tab.titleUr
                            }
                            Text(title)
                        },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { paddingValues ->
        val modifier = Modifier.padding(paddingValues)

        when {
            currentSubScreen == "search" -> {
                GlobalSearchScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = null }
                )
            }
            currentSubScreen == "backup" -> {
                BackupRestoreScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = null }
                )
            }
            else -> {
                when (selectedTab) {
                    NavigationTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToSales = { selectedTab = NavigationTab.SALES },
                        onNavigateToRepairs = { selectedTab = NavigationTab.REPAIRS },
                        onNavigateToStock = { selectedTab = NavigationTab.STOCK },
                        onNavigateToCustomers = { selectedTab = NavigationTab.CUSTOMERS },
                        onNavigateToAccounts = { selectedTab = NavigationTab.ACCOUNTS },
                        onNavigateToSearch = { currentSubScreen = "search" },
                        onNavigateToProfile = { selectedTab = NavigationTab.PROFILE }
                    )
                    NavigationTab.SALES -> MobileSalesScreen(viewModel = viewModel)
                    NavigationTab.REPAIRS -> MobileRepairScreen(viewModel = viewModel)
                    NavigationTab.STOCK -> StockManagementScreen(viewModel = viewModel)
                    NavigationTab.CUSTOMERS -> CustomerRecordScreen(viewModel = viewModel)
                    NavigationTab.ACCOUNTS -> DailyAccountsScreen(viewModel = viewModel)
                    NavigationTab.PROFILE -> ShopProfileScreen(
                        viewModel = viewModel,
                        onNavigateToBackup = { currentSubScreen = "backup" }
                    )
                }
            }
        }
    }
}
