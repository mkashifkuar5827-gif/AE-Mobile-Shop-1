package com.example.ui.screens.sales

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MobileSaleEntity
import com.example.ui.components.DualLanguageText
import com.example.ui.components.InvoicePrintableCard
import com.example.ui.viewmodel.ShopViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileSalesScreen(
    viewModel: ShopViewModel
) {
    val sales by viewModel.allSales.collectAsState()
    val shopProfile by viewModel.shopProfile.collectAsState()
    val isUrduEnabled by viewModel.isUrduEnabled.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedInvoiceSale by remember { mutableStateOf<MobileSaleEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedBrandFilter by remember { mutableStateOf("All") }

    val brands = listOf("All", "Samsung", "iPhone", "Vivo", "Oppo", "Xiaomi", "Realme", "Infinix", "Tecno")

    val filteredSales = sales.filter { s ->
        val matchesSearch = s.customerName.contains(searchQuery, ignoreCase = true) ||
                s.customerPhone.contains(searchQuery) ||
                s.modelName.contains(searchQuery, ignoreCase = true) ||
                s.imeiNumber.contains(searchQuery) ||
                s.brandName.contains(searchQuery, ignoreCase = true)
        val matchesBrand = selectedBrandFilter == "All" || s.brandName.equals(selectedBrandFilter, ignoreCase = true)
        matchesSearch && matchesBrand
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_sale_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Add Sale")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Sell Phone", fontWeight = FontWeight.Bold)
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
                    english = "Mobile Sales Management",
                    urdu = "موبائل خرید و فروخت",
                    isUrduEnabled = isUrduEnabled,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Model, IMEI, Customer Name or Phone...") },
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
                        .testTag("sales_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Brand Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(brands) { brand ->
                        FilterChip(
                            selected = selectedBrandFilter == brand,
                            onClick = { selectedBrandFilter = brand },
                            label = { Text(brand) }
                        )
                    }
                }
            }

            // Summary Bar
            val totalSalesAmt = filteredSales.sumOf { it.salePrice }
            val totalProfitAmt = filteredSales.sumOf { it.salePrice - it.purchasePrice }

            Surface(
                color = Color(0xFFE0F2FE),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Sales: ${filteredSales.size} Units (Rs. ${String.format("%.0f", totalSalesAmt)})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0369A1)
                    )
                    Text(
                        text = "Profit: Rs. ${String.format("%.0f", totalProfitAmt)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                }
            }

            // List of Sales
            if (filteredSales.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Mobile Sales Found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Tap the button below to add a new mobile sale.",
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
                    items(filteredSales, key = { it.id }) { sale ->
                        MobileSaleItemCard(
                            sale = sale,
                            isUrduEnabled = isUrduEnabled,
                            onInvoiceClick = { selectedInvoiceSale = sale },
                            onDeleteClick = { viewModel.deleteMobileSale(sale) }
                        )
                    }
                }
            }
        }
    }

    // Add Mobile Sale Sheet / Dialog
    if (showAddDialog) {
        AddMobileSaleDialog(
            isUrduEnabled = isUrduEnabled,
            onDismiss = { showAddDialog = false },
            onSave = { newSale ->
                viewModel.addMobileSale(newSale) { id ->
                    showAddDialog = false
                    selectedInvoiceSale = newSale.copy(id = id)
                }
            }
        )
    }

    // Invoice View Dialog
    if (selectedInvoiceSale != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedInvoiceSale = null }
        ) {
            InvoicePrintableCard(
                shopProfile = shopProfile,
                saleItem = selectedInvoiceSale,
                isUrduEnabled = isUrduEnabled,
                onDismiss = { selectedInvoiceSale = null }
            )
        }
    }
}

@Composable
fun MobileSaleItemCard(
    sale: MobileSaleEntity,
    isUrduEnabled: Boolean,
    onInvoiceClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(sale.saleDate))
    val profit = sale.salePrice - sale.purchasePrice

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sale_item_${sale.id}"),
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
                            .background(Color(0xFF0284C7).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF0284C7))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "${sale.brandName} ${sale.modelName}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "IMEI: ${sale.imeiNumber}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    text = "Rs. ${String.format("%.0f", sale.salePrice)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF059669)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details Table
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Customer: ${sale.customerName}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(text = "Phone: ${sale.customerPhone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Profit: Rs. ${String.format("%.0f", profit)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                    Text(text = dateFormatted, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inv: ${sale.invoiceNumber}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Row {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                    }

                    Button(
                        onClick = onInvoiceClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Invoice", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Sale Record?") },
            text = { Text("Are you sure you want to delete sale record for ${sale.brandName} ${sale.modelName}?") },
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
fun AddMobileSaleDialog(
    isUrduEnabled: Boolean,
    onDismiss: () -> Unit,
    onSave: (MobileSaleEntity) -> Unit
) {
    var brandName by remember { mutableStateOf("Samsung") }
    var modelName by remember { mutableStateOf("") }
    var imeiNumber by remember { mutableStateOf("") }
    var purchasePriceStr by remember { mutableStateOf("") }
    var salePriceStr by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }

    val popularBrands = listOf("Samsung", "iPhone", "Vivo", "Oppo", "Xiaomi", "Realme", "Infinix", "Tecno", "Other")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("add_sale_sheet")
        ) {
            DualLanguageText(
                english = "Add Mobile Sale",
                urdu = "نیا موبائل فروخت درج کریں",
                isUrduEnabled = isUrduEnabled,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Select Brand Chips
            Text(text = "Select Brand / برانڈ منتَخب کریں:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                items(popularBrands) { b ->
                    FilterChip(
                        selected = brandName == b,
                        onClick = { brandName = b },
                        label = { Text(b) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = modelName,
                onValueChange = { modelName = it },
                label = { Text("Model Name (e.g. Galaxy A14 128GB)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sale_model_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = imeiNumber,
                onValueChange = { imeiNumber = it },
                label = { Text("IMEI Number (15 Digits)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sale_imei_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = purchasePriceStr,
                    onValueChange = { purchasePriceStr = it },
                    label = { Text("Purchase Price (Rs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sale_purchase_price_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = salePriceStr,
                    onValueChange = { salePriceStr = it },
                    label = { Text("Selling Price (Rs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sale_selling_price_input"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sale_customer_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("Customer Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sale_customer_phone_input"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes / Warranty Info (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val pPrice = purchasePriceStr.toDoubleOrNull() ?: 0.0
                    val sPrice = salePriceStr.toDoubleOrNull() ?: 0.0

                    if (modelName.isBlank()) {
                        errorMessage = "Please enter mobile model name."
                        return@Button
                    }
                    if (sPrice <= 0) {
                        errorMessage = "Please enter valid sale price."
                        return@Button
                    }

                    val newSale = MobileSaleEntity(
                        brandName = brandName,
                        modelName = modelName.trim(),
                        imeiNumber = imeiNumber.trim(),
                        purchasePrice = pPrice,
                        salePrice = sPrice,
                        customerName = customerName.ifBlank { "Cash Customer" },
                        customerPhone = customerPhone.trim(),
                        notes = notes.trim()
                    )
                    onSave(newSale)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_sale_btn"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Save Sale & Generate Invoice", fontWeight = FontWeight.Bold)
            }
        }
    }
}
