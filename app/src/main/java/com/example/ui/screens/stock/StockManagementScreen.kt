package com.example.ui.screens.stock

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.StockItemEntity
import com.example.ui.components.DualLanguageText
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockManagementScreen(
    viewModel: ShopViewModel
) {
    val stockItems by viewModel.allStockItems.collectAsState()
    val isUrduEnabled by viewModel.isUrduEnabled.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedItemForSell by remember { mutableStateOf<StockItemEntity?>(null) }
    var selectedItemForRestock by remember { mutableStateOf<StockItemEntity?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Chargers", "Handsfree", "Covers", "Screen Protectors", "Batteries", "Mobiles", "Other Items")

    val filteredStock = stockItems.filter { item ->
        val matchesSearch = item.itemName.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryFilter == "All" || item.category.equals(selectedCategoryFilter, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF059669),
                contentColor = Color.White,
                modifier = Modifier.testTag("add_stock_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Add Item", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                DualLanguageText(
                    english = "Accessories & Stock Management",
                    urdu = "سامان لوازمات اسٹاک",
                    isUrduEnabled = isUrduEnabled,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Charger, Handsfree, Cover, Glass...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stock_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val count = if (cat == "All") stockItems.size else stockItems.count { it.category == cat }
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text("$cat ($count)") }
                        )
                    }
                }
            }

            // Summary Bar
            val totalInventoryQty = filteredStock.sumOf { it.quantity }
            val totalValue = filteredStock.sumOf { it.purchasePrice * it.quantity }

            Surface(
                color = Color(0xFFD1FAE5),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Items: ${filteredStock.size} ($totalInventoryQty Units)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46)
                    )
                    Text(
                        text = "Stock Asset Value: Rs. ${String.format("%.0f", totalValue)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF047857)
                    )
                }
            }

            // List of Items
            if (filteredStock.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Stock Items Registered",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Tap button below to add new chargers, handsfree, covers or screens.",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredStock, key = { it.id }) { item ->
                        StockItemCard(
                            item = item,
                            isUrduEnabled = isUrduEnabled,
                            onSellClick = { selectedItemForSell = item },
                            onRestockClick = { selectedItemForRestock = item },
                            onDeleteClick = { viewModel.deleteStockItem(item) }
                        )
                    }
                }
            }
        }
    }

    // Add Stock Item Dialog
    if (showAddDialog) {
        AddStockItemDialog(
            isUrduEnabled = isUrduEnabled,
            onDismiss = { showAddDialog = false },
            onSave = { newItem ->
                viewModel.addStockItem(newItem)
                showAddDialog = false
            }
        )
    }

    // Sell Quantity Dialog
    if (selectedItemForSell != null) {
        SellStockQuantityDialog(
            item = selectedItemForSell!!,
            onDismiss = { selectedItemForSell = null },
            onConfirmSell = { sellQty, customer ->
                viewModel.sellStockItem(selectedItemForSell!!, sellQty, customer) { success ->
                    if (success) selectedItemForSell = null
                }
            }
        )
    }

    // Restock Quantity Dialog
    if (selectedItemForRestock != null) {
        RestockQuantityDialog(
            item = selectedItemForRestock!!,
            onDismiss = { selectedItemForRestock = null },
            onConfirmRestock = { addQty, supplier ->
                viewModel.restockItem(selectedItemForRestock!!, addQty, supplier)
                selectedItemForRestock = null
            }
        )
    }
}

@Composable
fun StockItemCard(
    item: StockItemEntity,
    isUrduEnabled: Boolean,
    onSellClick: () -> Unit,
    onRestockClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val isLowStock = item.quantity <= item.lowStockThreshold

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stock_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF059669).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color(0xFF059669))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.itemName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Category: ${item.category}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Surface(
                    color = if (isLowStock) Color(0xFFFEF2F2) else Color(0xFFECFDF5),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLowStock) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = "${item.quantity} Qty",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLowStock) Color(0xFFDC2626) else Color(0xFF059669)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Cost: Rs. ${String.format("%.0f", item.purchasePrice)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text(text = "Price: Rs. ${String.format("%.0f", item.salePrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                Text(text = "Margin: Rs. ${String.format("%.0f", item.salePrice - item.purchasePrice)}", fontSize = 12.sp, color = Color(0xFF0284C7))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onRestockClick,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "+ Restock", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onSellClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.RemoveShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Quick Sell", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Item?") },
            text = { Text("Are you sure you want to remove ${item.itemName} from inventory?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick()
                    showDeleteConfirm = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockItemDialog(
    isUrduEnabled: Boolean,
    onDismiss: () -> Unit,
    onSave: (StockItemEntity) -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Chargers") }
    var quantityStr by remember { mutableStateOf("10") }
    var purchasePriceStr by remember { mutableStateOf("") }
    var salePriceStr by remember { mutableStateOf("") }
    var lowStockThresholdStr by remember { mutableStateOf("5") }

    var errorMessage by remember { mutableStateOf("") }

    val categories = listOf("Chargers", "Handsfree", "Covers", "Screen Protectors", "Batteries", "Mobiles", "Other Items")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("add_stock_sheet")
        ) {
            DualLanguageText(
                english = "Add Stock Accessory",
                urdu = "نیا سامان اسٹاک میں شامل کریں",
                isUrduEnabled = isUrduEnabled,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Category / قسم:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name (e.g. Samsung 25W Fast Charger)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stock_item_name_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stock_item_qty_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = lowStockThresholdStr,
                    onValueChange = { lowStockThresholdStr = it },
                    label = { Text("Alert Threshold") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = purchasePriceStr,
                    onValueChange = { purchasePriceStr = it },
                    label = { Text("Purchase Price (Rs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stock_purchase_price_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = salePriceStr,
                    onValueChange = { salePriceStr = it },
                    label = { Text("Selling Price (Rs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stock_selling_price_input"),
                    singleLine = true
                )
            }

            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val qty = quantityStr.toIntOrNull() ?: 0
                    val threshold = lowStockThresholdStr.toIntOrNull() ?: 5
                    val pPrice = purchasePriceStr.toDoubleOrNull() ?: 0.0
                    val sPrice = salePriceStr.toDoubleOrNull() ?: 0.0

                    if (itemName.isBlank()) {
                        errorMessage = "Please enter item name."
                        return@Button
                    }
                    if (sPrice <= 0) {
                        errorMessage = "Please enter valid selling price."
                        return@Button
                    }

                    val newItem = StockItemEntity(
                        itemName = itemName.trim(),
                        category = category,
                        quantity = qty,
                        purchasePrice = pPrice,
                        salePrice = sPrice,
                        lowStockThreshold = threshold
                    )
                    onSave(newItem)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_stock_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Save Stock Item", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SellStockQuantityDialog(
    item: StockItemEntity,
    onDismiss: () -> Unit,
    onConfirmSell: (Int, String) -> Unit
) {
    var sellQtyStr by remember { mutableStateOf("1") }
    var customerName by remember { mutableStateOf("Counter Customer") }
    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sell ${item.itemName}") },
        text = {
            Column {
                Text("Available Stock: ${item.quantity} units | Unit Price: Rs. ${item.salePrice}")
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = sellQtyStr,
                    onValueChange = { sellQtyStr = it },
                    label = { Text("Selling Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name / Note") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMsg.isNotBlank()) {
                    Text(text = errorMsg, color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = sellQtyStr.toIntOrNull() ?: 0
                    if (qty <= 0) {
                        errorMsg = "Enter valid quantity."
                        return@Button
                    }
                    if (qty > item.quantity) {
                        errorMsg = "Cannot sell more than available quantity."
                        return@Button
                    }
                    onConfirmSell(qty, customerName)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
            ) {
                Text("Confirm Sale")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun RestockQuantityDialog(
    item: StockItemEntity,
    onDismiss: () -> Unit,
    onConfirmRestock: (Int, String) -> Unit
) {
    var addQtyStr by remember { mutableStateOf("10") }
    var supplierName by remember { mutableStateOf("Market Purchase") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restock ${item.itemName}") },
        text = {
            Column {
                Text("Current Stock: ${item.quantity} units")
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = addQtyStr,
                    onValueChange = { addQtyStr = it },
                    label = { Text("Add Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = supplierName,
                    onValueChange = { supplierName = it },
                    label = { Text("Supplier / Dealer Note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = addQtyStr.toIntOrNull() ?: 0
                    if (qty > 0) {
                        onConfirmRestock(qty, supplierName)
                    }
                }
            ) {
                Text("Add Stock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
