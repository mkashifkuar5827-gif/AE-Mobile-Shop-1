package com.example.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.DualLanguageText
import com.example.ui.components.StatCard
import com.example.ui.theme.ShopPrimaryLight
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun DashboardScreen(
    viewModel: ShopViewModel,
    onNavigateToSales: () -> Unit,
    onNavigateToRepairs: () -> Unit,
    onNavigateToStock: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val shopProfile by viewModel.shopProfile.collectAsState()
    val sales by viewModel.allSales.collectAsState()
    val repairs by viewModel.allRepairs.collectAsState()
    val stockItems by viewModel.allStockItems.collectAsState()
    val lowStockItems by viewModel.lowStockItems.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()
    val isUrduEnabled by viewModel.isUrduEnabled.collectAsState()

    val todaySales = sales.filter { viewModel.isToday(it.saleDate) }
    val todaySalesTotal = todaySales.sumOf { it.salePrice }
    val todaySalesProfit = todaySales.sumOf { it.salePrice - it.purchasePrice }

    val todayRepairs = repairs.filter { viewModel.isToday(it.receivedDate) }
    val todayRepairTotal = todayRepairs.sumOf { it.repairCharges }

    val todayExpenses = expenses.filter { viewModel.isToday(it.date) }
    val todayExpenseTotal = todayExpenses.sumOf { it.amount }

    val todayNetProfit = (todaySalesProfit + todayRepairTotal) - todayExpenseTotal

    val workingRepairs = repairs.filter { it.status == "Working" || it.status == "Received" }
    val readyRepairs = repairs.filter { it.status == "Ready" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // Top Banner / Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Banner Background Image
            Image(
                painter = painterResource(id = R.drawable.shop_banner_1786602013038),
                contentDescription = "Shop Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Shop Info Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: App Title & Search
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF0EA5E9),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "100% Offline Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    IconButton(
                        onClick = onNavigateToSearch,
                        modifier = Modifier
                            .testTag("dashboard_search_btn")
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                }

                // Shop Name & Details
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigateToProfile() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.shop_app_icon_1786601992623),
                            contentDescription = "Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = shopProfile?.shopName ?: "KASHIF MOBILE AND REPAIR",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${shopProfile?.ownerName ?: "Muhammad Kashif"} • ${shopProfile?.city ?: "Lahore"}",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Actions Scroll Row
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            DualLanguageText(
                english = "Quick Actions",
                urdu = "فوری سرگرمیاں",
                isUrduEnabled = isUrduEnabled,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    QuickPillButton(
                        title = "New Mobile Sale",
                        urdu = "موبائل فروخت",
                        icon = Icons.Default.PhoneAndroid,
                        containerColor = Color(0xFF0284C7),
                        onClick = onNavigateToSales
                    )
                }
                item {
                    QuickPillButton(
                        title = "New Repair Job",
                        urdu = "موبائل مرمت",
                        icon = Icons.Default.Build,
                        containerColor = Color(0xFFD97706),
                        onClick = onNavigateToRepairs
                    )
                }
                item {
                    QuickPillButton(
                        title = "Stock & Items",
                        urdu = "اسٹاک سامان",
                        icon = Icons.Default.Inventory2,
                        containerColor = Color(0xFF059669),
                        onClick = onNavigateToStock
                    )
                }
                item {
                    QuickPillButton(
                        title = "Customers",
                        urdu = "گاہک ریکارڈ",
                        icon = Icons.Default.People,
                        containerColor = Color(0xFF7C3AED),
                        onClick = onNavigateToCustomers
                    )
                }
                item {
                    QuickPillButton(
                        title = "Daily Accounts",
                        urdu = "روزانہ روزنامچہ",
                        icon = Icons.Default.AttachMoney,
                        containerColor = Color(0xFFDC2626),
                        onClick = onNavigateToAccounts
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Low Stock Alert Banner (if any)
        AnimatedVisibility(visible = lowStockItems.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigateToStock() }
                    .testTag("low_stock_alert_banner"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Low Stock Alert! / اسٹاک الرٹ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = "${lowStockItems.size} items are running low on quantity. Tap to view.",
                            fontSize = 12.sp,
                            color = Color(0xFFB91C1C)
                        )
                    }
                }
            }
        }

        if (lowStockItems.isNotEmpty()) Spacer(modifier = Modifier.height(16.dp))

        // Today's Business Performance Summary
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            DualLanguageText(
                english = "Today's Business Summary",
                urdu = "آج کی کاروباری صورتحال",
                isUrduEnabled = isUrduEnabled,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Main Profit Highlight Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("today_profit_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (todayNetProfit >= 0) Color(0xFF065F46) else Color(0xFF991B1B)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DualLanguageText(
                            english = "Net Profit Today",
                            urdu = "آج کا صاف منافع",
                            isUrduEnabled = isUrduEnabled,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Rs. ${String.format("%.0f", todayNetProfit)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Sales Profit: Rs. ${String.format("%.0f", todaySalesProfit)}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Text(
                            text = "Repair Income: Rs. ${String.format("%.0f", todayRepairTotal)}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2x2 Grid of Stat Cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Today Sales",
                            urduTitle = "آج کی سیل",
                            value = "Rs. ${String.format("%.0f", todaySalesTotal)}",
                            icon = Icons.Default.PhoneAndroid,
                            iconBgColor = Color(0xFF0284C7),
                            isUrduEnabled = isUrduEnabled,
                            onClick = onNavigateToSales
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Today Repairs",
                            urduTitle = "آج کی مرمت",
                            value = "Rs. ${String.format("%.0f", todayRepairTotal)}",
                            icon = Icons.Default.Build,
                            iconBgColor = Color(0xFFD97706),
                            isUrduEnabled = isUrduEnabled,
                            onClick = onNavigateToRepairs
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Pending Repairs",
                            urduTitle = "زیرِ مرمت",
                            value = "${workingRepairs.size} Phones",
                            icon = Icons.Default.HourglassTop,
                            iconBgColor = Color(0xFFEAB308),
                            isUrduEnabled = isUrduEnabled,
                            onClick = onNavigateToRepairs
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Ready Repairs",
                            urduTitle = "تیار شدہ",
                            value = "${readyRepairs.size} Phones",
                            icon = Icons.Default.CheckCircle,
                            iconBgColor = Color(0xFF10B981),
                            isUrduEnabled = isUrduEnabled,
                            onClick = onNavigateToRepairs
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Inventory Overview Card
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToStock() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFF059669).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Category, contentDescription = null, tint = Color(0xFF059669))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            DualLanguageText(
                                english = "Accessories & Stock",
                                urdu = "موبائل لوازمات اسٹاک",
                                isUrduEnabled = isUrduEnabled,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${stockItems.size} Total Accessory Items Registered",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Manage",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun QuickPillButton(
    title: String,
    urdu: String,
    icon: ImageVector,
    containerColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .testTag("quick_action_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                if (urdu.isNotBlank()) {
                    Text(text = urdu, fontSize = 10.sp, color = Color.White.copy(alpha = 0.85f))
                }
            }
        }
    }
}
