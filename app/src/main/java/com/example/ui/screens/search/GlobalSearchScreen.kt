package com.example.ui.screens.search

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DualLanguageText
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun GlobalSearchScreen(
    viewModel: ShopViewModel,
    onBack: (() -> Unit)? = null
) {
    val sales by viewModel.allSales.collectAsState()
    val repairs by viewModel.allRepairs.collectAsState()
    val stock by viewModel.allStockItems.collectAsState()
    val customers by viewModel.customerRecords.collectAsState()
    val isUrduEnabled by viewModel.isUrduEnabled.collectAsState()

    var query by remember { mutableStateOf("") }

    val matchingSales = if (query.length < 2) emptyList() else sales.filter {
        it.customerName.contains(query, ignoreCase = true) ||
                it.customerPhone.contains(query) ||
                it.modelName.contains(query, ignoreCase = true) ||
                it.imeiNumber.contains(query) ||
                it.brandName.contains(query, ignoreCase = true)
    }

    val matchingRepairs = if (query.length < 2) emptyList() else repairs.filter {
        it.customerName.contains(query, ignoreCase = true) ||
                it.customerPhone.contains(query) ||
                it.mobileModel.contains(query, ignoreCase = true) ||
                it.problemDetails.contains(query, ignoreCase = true) ||
                it.imeiNumber.contains(query)
    }

    val matchingStock = if (query.length < 2) emptyList() else stock.filter {
        it.itemName.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            DualLanguageText(
                english = "Global Search",
                urdu = "تلاش کریں (نام، فون، IMEI، ماڈل)",
                isUrduEnabled = isUrduEnabled,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Type Customer Name, Phone #, IMEI, or Model...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = if (query.isNotEmpty()) {
                    {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_search_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        if (query.length < 2) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Instant Search Bar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Type at least 2 characters to search across Sales, Repairs, Stock, and Customer Records.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sales Results
                if (matchingSales.isNotEmpty()) {
                    item {
                        Text(text = "MOBILE SALES (${matchingSales.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                    }
                    items(matchingSales) { sale ->
                        SearchResultCard(
                            title = "${sale.brandName} ${sale.modelName}",
                            subtitle = "Customer: ${sale.customerName} (${sale.customerPhone})",
                            detail = "IMEI: ${sale.imeiNumber}",
                            amount = "Rs. ${String.format("%.0f", sale.salePrice)}",
                            icon = Icons.Default.PhoneAndroid,
                            iconColor = Color(0xFF0284C7)
                        )
                    }
                }

                // Repairs Results
                if (matchingRepairs.isNotEmpty()) {
                    item {
                        Text(text = "MOBILE REPAIRS (${matchingRepairs.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                    }
                    items(matchingRepairs) { repair ->
                        SearchResultCard(
                            title = "${repair.mobileModel} (${repair.status})",
                            subtitle = "Customer: ${repair.customerName} (${repair.customerPhone})",
                            detail = "Problem: ${repair.problemDetails}",
                            amount = "Charges: Rs. ${String.format("%.0f", repair.repairCharges)}",
                            icon = Icons.Default.Build,
                            iconColor = Color(0xFFD97706)
                        )
                    }
                }

                // Stock Results
                if (matchingStock.isNotEmpty()) {
                    item {
                        Text(text = "STOCK ACCESSORIES (${matchingStock.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                    }
                    items(matchingStock) { item ->
                        SearchResultCard(
                            title = item.itemName,
                            subtitle = "Category: ${item.category}",
                            detail = "Available Quantity: ${item.quantity} units",
                            amount = "Price: Rs. ${String.format("%.0f", item.salePrice)}",
                            icon = Icons.Default.Inventory2,
                            iconColor = Color(0xFF059669)
                        )
                    }
                }

                if (matchingSales.isEmpty() && matchingRepairs.isEmpty() && matchingStock.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No matching records found for '$query'.", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    title: String,
    subtitle: String,
    detail: String,
    amount: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text(text = detail, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }

            Text(text = amount, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = iconColor)
        }
    }
}
