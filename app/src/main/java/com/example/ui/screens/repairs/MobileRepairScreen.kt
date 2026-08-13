package com.example.ui.screens.repairs

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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Print
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MobileRepairEntity
import com.example.ui.components.DualLanguageText
import com.example.ui.components.InvoicePrintableCard
import com.example.ui.viewmodel.ShopViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileRepairScreen(
    viewModel: ShopViewModel
) {
    val repairs by viewModel.allRepairs.collectAsState()
    val shopProfile by viewModel.shopProfile.collectAsState()
    val isUrduEnabled by viewModel.isUrduEnabled.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedInvoiceRepair by remember { mutableStateOf<MobileRepairEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }

    val statusOptions = listOf("All", "Received", "Working", "Ready", "Delivered")

    val filteredRepairs = repairs.filter { r ->
        val matchesSearch = r.customerName.contains(searchQuery, ignoreCase = true) ||
                r.customerPhone.contains(searchQuery) ||
                r.mobileModel.contains(searchQuery, ignoreCase = true) ||
                r.problemDetails.contains(searchQuery, ignoreCase = true) ||
                r.imeiNumber.contains(searchQuery)
        val matchesStatus = selectedStatusFilter == "All" || r.status.equals(selectedStatusFilter, ignoreCase = true)
        matchesSearch && matchesStatus
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFD97706),
                contentColor = Color.White,
                modifier = Modifier.testTag("add_repair_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "New Repair")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "New Repair", fontWeight = FontWeight.Bold)
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
                    english = "Mobile Repair Management",
                    urdu = "موبائل مرمت و سروس",
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
                    placeholder = { Text("Search Customer Name, Phone, Model, Problem...") },
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
                        .testTag("repairs_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Status Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(statusOptions) { status ->
                        val count = if (status == "All") repairs.size else repairs.count { it.status == status }
                        FilterChip(
                            selected = selectedStatusFilter == status,
                            onClick = { selectedStatusFilter = status },
                            label = { Text("$status ($count)") }
                        )
                    }
                }
            }

            // Summary Bar
            val totalRepairCharges = filteredRepairs.sumOf { it.repairCharges }
            val totalAdvance = filteredRepairs.sumOf { it.advancePayment }
            val totalRemaining = filteredRepairs.sumOf { it.remainingPayment }

            Surface(
                color = Color(0xFFFEF3C7),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Repairs: ${filteredRepairs.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                    Text(
                        text = "Coll: Rs. ${String.format("%.0f", totalAdvance)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                    Text(
                        text = "Pending Bal: Rs. ${String.format("%.0f", totalRemaining)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFDC2626)
                    )
                }
            }

            // List of Repairs
            if (filteredRepairs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Repair Jobs Found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Tap button below to register a new repair ticket.",
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
                    items(filteredRepairs, key = { it.id }) { repair ->
                        RepairItemCard(
                            repair = repair,
                            isUrduEnabled = isUrduEnabled,
                            onUpdateStatus = { newStatus ->
                                viewModel.updateRepairStatus(repair, newStatus)
                            },
                            onInvoiceClick = { selectedInvoiceRepair = repair },
                            onDeleteClick = { viewModel.deleteMobileRepair(repair) }
                        )
                    }
                }
            }
        }
    }

    // Add Mobile Repair Dialog
    if (showAddDialog) {
        AddMobileRepairDialog(
            isUrduEnabled = isUrduEnabled,
            onDismiss = { showAddDialog = false },
            onSave = { newRepair ->
                viewModel.addMobileRepair(newRepair) { id ->
                    showAddDialog = false
                    selectedInvoiceRepair = newRepair.copy(id = id)
                }
            }
        )
    }

    // Invoice Dialog
    if (selectedInvoiceRepair != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedInvoiceRepair = null }
        ) {
            InvoicePrintableCard(
                shopProfile = shopProfile,
                repairItem = selectedInvoiceRepair,
                isUrduEnabled = isUrduEnabled,
                onDismiss = { selectedInvoiceRepair = null }
            )
        }
    }
}

@Composable
fun RepairItemCard(
    repair: MobileRepairEntity,
    isUrduEnabled: Boolean,
    onUpdateStatus: (String) -> Unit,
    onInvoiceClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val statusColor = when (repair.status) {
        "Received" -> Color(0xFF2563EB) // Blue
        "Working" -> Color(0xFFD97706)  // Amber
        "Ready" -> Color(0xFF059669)    // Green
        "Delivered" -> Color(0xFF4B5563)// Gray
        else -> Color.Gray
    }

    val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(repair.receivedDate))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("repair_item_${repair.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(statusColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = statusColor, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = repair.mobileModel,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Customer: ${repair.customerName} (${repair.customerPhone})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Status Badge
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = repair.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Problem & Details Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Problem: ${repair.problemDetails}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (repair.partsUsed.isNotBlank()) {
                    Text(
                        text = "Parts Used: ${repair.partsUsed}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                if (repair.imeiNumber.isNotBlank()) {
                    Text(
                        text = "IMEI: ${repair.imeiNumber}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Charges & Payment Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total: Rs. ${String.format("%.0f", repair.repairCharges)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = "Adv: Rs. ${String.format("%.0f", repair.advancePayment)}", fontSize = 12.sp, color = Color(0xFF059669))
                Text(
                    text = "Rem: Rs. ${String.format("%.0f", repair.remainingPayment)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (repair.remainingPayment > 0) Color(0xFFDC2626) else Color(0xFF059669)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Status Transition Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Rec: $dateFormatted", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (repair.status != "Delivered") {
                        val nextStatus = when (repair.status) {
                            "Received" -> "Working"
                            "Working" -> "Ready"
                            "Ready" -> "Delivered"
                            else -> "Delivered"
                        }
                        OutlinedButton(
                            onClick = { onUpdateStatus(nextStatus) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Set $nextStatus", fontSize = 11.sp)
                        }
                    }

                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                    }

                    Button(
                        onClick = onInvoiceClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Receipt", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Repair Ticket?") },
            text = { Text("Are you sure you want to delete repair ticket for ${repair.mobileModel}?") },
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
fun AddMobileRepairDialog(
    isUrduEnabled: Boolean,
    onDismiss: () -> Unit,
    onSave: (MobileRepairEntity) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var mobileModel by remember { mutableStateOf("") }
    var imeiNumber by remember { mutableStateOf("") }
    var problemDetails by remember { mutableStateOf("") }
    var repairChargesStr by remember { mutableStateOf("") }
    var partsUsed by remember { mutableStateOf("") }
    var advancePaymentStr by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("add_repair_sheet")
        ) {
            DualLanguageText(
                english = "New Repair Ticket",
                urdu = "موبائل مرمت کی ٹکٹ بنائیں",
                isUrduEnabled = isUrduEnabled,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("repair_customer_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("repair_customer_phone_input"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = mobileModel,
                    onValueChange = { mobileModel = it },
                    label = { Text("Mobile Model (e.g. Vivo Y20)") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("repair_model_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = imeiNumber,
                    onValueChange = { imeiNumber = it },
                    label = { Text("IMEI (Optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("repair_imei_input"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = problemDetails,
                onValueChange = { problemDetails = it },
                label = { Text("Problem Details (e.g. Broken Screen / Charging)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("repair_problem_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = partsUsed,
                onValueChange = { partsUsed = it },
                label = { Text("Parts Used (e.g. Panel, Battery, Charging Jack)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = repairChargesStr,
                    onValueChange = { repairChargesStr = it },
                    label = { Text("Total Repair Charges (Rs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("repair_charges_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = advancePaymentStr,
                    onValueChange = { advancePaymentStr = it },
                    label = { Text("Advance Paid (Rs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("repair_advance_input"),
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
                    val charges = repairChargesStr.toDoubleOrNull() ?: 0.0
                    val advance = advancePaymentStr.toDoubleOrNull() ?: 0.0

                    if (customerName.isBlank()) {
                        errorMessage = "Please enter customer name."
                        return@Button
                    }
                    if (mobileModel.isBlank()) {
                        errorMessage = "Please enter mobile model."
                        return@Button
                    }
                    if (problemDetails.isBlank()) {
                        errorMessage = "Please specify repair problem details."
                        return@Button
                    }

                    val newRepair = MobileRepairEntity(
                        customerName = customerName.trim(),
                        customerPhone = customerPhone.trim(),
                        mobileModel = mobileModel.trim(),
                        imeiNumber = imeiNumber.trim(),
                        problemDetails = problemDetails.trim(),
                        repairCharges = charges,
                        partsUsed = partsUsed.trim(),
                        advancePayment = advance,
                        remainingPayment = (charges - advance).coerceAtLeast(0.0),
                        status = "Received"
                    )
                    onSave(newRepair)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_repair_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Register Ticket & Generate Receipt", fontWeight = FontWeight.Bold)
            }
        }
    }
}
