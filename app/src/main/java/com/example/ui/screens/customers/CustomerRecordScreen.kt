package com.example.ui.screens.customers

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.CustomerRecord
import com.example.ui.components.DualLanguageText
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerRecordScreen(
    viewModel: ShopViewModel
) {
    val customerRecords by viewModel.customerRecords.collectAsState()
    val isUrduEnabled by viewModel.isUrduEnabled.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCustomer by remember { mutableStateOf<CustomerRecord?>(null) }

    val filteredCustomers = customerRecords.filter { c ->
        c.name.contains(searchQuery, ignoreCase = true) ||
                c.phone.contains(searchQuery)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                english = "Customer Directory & History",
                urdu = "گاہک ریکارڈ اور ہسٹری",
                isUrduEnabled = isUrduEnabled,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Customer Name or Phone Number...") },
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
                    .testTag("customer_search_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Summary Bar
        Surface(
            color = Color(0xFFF3E8FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Customers: ${filteredCustomers.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B21A8)
                )
                Text(
                    text = "Pending Receivables: Rs. ${String.format("%.0f", filteredCustomers.sumOf { it.remainingBalance })}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
        }

        if (filteredCustomers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Customer History Available",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "New customer records will automatically appear when sales or repairs are created.",
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
                items(filteredCustomers) { record ->
                    CustomerCard(
                        record = record,
                        onClick = { selectedCustomer = record },
                        onCallClick = { dialPhone(context, record.phone) },
                        onWhatsAppClick = { openWhatsApp(context, record.phone) }
                    )
                }
            }
        }
    }

    // Customer Detail History BottomSheet
    if (selectedCustomer != null) {
        ModalBottomSheet(onDismissRequest = { selectedCustomer = null }) {
            CustomerHistorySheet(
                record = selectedCustomer!!,
                onDismiss = { selectedCustomer = null },
                onCallClick = { dialPhone(context, selectedCustomer!!.phone) },
                onWhatsAppClick = { openWhatsApp(context, selectedCustomer!!.phone) }
            )
        }
    }
}

@Composable
fun CustomerCard(
    record: CustomerRecord,
    onClick: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("customer_card_${record.name.lowercase()}"),
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
                            .size(44.dp)
                            .background(Color(0xFF7C3AED).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = record.name.take(1).uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7C3AED)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = record.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = record.phone,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Row {
                    if (record.phone.isNotBlank()) {
                        IconButton(onClick = onCallClick) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF0284C7))
                        }
                        IconButton(onClick = onWhatsAppClick) {
                            Icon(Icons.Default.Send, contentDescription = "WhatsApp", tint = Color(0xFF059669))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${record.purchases.size} Mobile Purchases",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "${record.repairs.size} Repair Jobs",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Spent: Rs. ${String.format("%.0f", record.totalSpent)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669)
                )
            }

            if (record.remainingBalance > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pending Repair Balance: Rs. ${String.format("%.0f", record.remainingBalance)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }
        }
    }
}

@Composable
fun CustomerHistorySheet(
    record: CustomerRecord,
    onDismiss: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .testTag("customer_history_sheet")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = record.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = record.phone, fontSize = 13.sp, color = Color.Gray)
            }

            Row {
                IconButton(onClick = onCallClick) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF0284C7))
                }
                IconButton(onClick = onWhatsAppClick) {
                    Icon(Icons.Default.Send, contentDescription = "WhatsApp", tint = Color(0xFF059669))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Previous Purchases / خرید ہسٹری", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
        Spacer(modifier = Modifier.height(6.dp))

        if (record.purchases.isEmpty()) {
            Text(text = "No mobile purchases recorded.", fontSize = 12.sp, color = Color.Gray)
        } else {
            record.purchases.forEach { sale ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "${sale.brandName} ${sale.modelName}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "IMEI: ${sale.imeiNumber}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Text(text = "Rs. ${String.format("%.0f", sale.salePrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Previous Repairs / مرمت ہسٹری", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
        Spacer(modifier = Modifier.height(6.dp))

        if (record.repairs.isEmpty()) {
            Text(text = "No repair jobs recorded.", fontSize = 12.sp, color = Color.Gray)
        } else {
            record.repairs.forEach { repair ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "${repair.mobileModel} (${repair.status})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Problem: ${repair.problemDetails}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Charges: Rs. ${String.format("%.0f", repair.repairCharges)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Rem: Rs. ${String.format("%.0f", repair.remainingPayment)}", fontSize = 11.sp, color = if (repair.remainingPayment > 0) Color.Red else Color.Green)
                        }
                    }
                }
            }
        }
    }
}

private fun dialPhone(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    context.startActivity(intent)
}

private fun openWhatsApp(context: Context, phone: String) {
    val cleanPhone = phone.replace("-", "").replace(" ", "")
    val url = "https://api.whatsapp.com/send?phone=$cleanPhone"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}
