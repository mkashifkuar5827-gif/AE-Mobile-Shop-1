package com.example.ui.screens.accounts

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.entity.ExpenseEntity
import com.example.ui.components.DualLanguageText
import com.example.ui.components.StatCard
import com.example.ui.viewmodel.ShopViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyAccountsScreen(
    viewModel: ShopViewModel
) {
    val sales by viewModel.allSales.collectAsState()
    val repairs by viewModel.allRepairs.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()
    val isUrduEnabled by viewModel.isUrduEnabled.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Daily Accounts, 1: Monthly Report
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (selectedTabIndex == 0) {
                FloatingActionButton(
                    onClick = { showAddExpenseDialog = true },
                    containerColor = Color(0xFFDC2626),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_expense_fab")
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Add Expense")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Add Expense", fontWeight = FontWeight.Bold)
                    }
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
                    english = "Daily Accounts & Profit Reports",
                    urdu = "روزنامچہ اور منافع رپورٹ",
                    isUrduEnabled = isUrduEnabled,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Daily Cashbook / روزانہ") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Monthly Report / ماہانہ") }
                    )
                }
            }

            if (selectedTabIndex == 0) {
                // Today's Cashbook View
                TodayCashbookView(
                    viewModel = viewModel,
                    isUrduEnabled = isUrduEnabled,
                    onDeleteExpense = { viewModel.deleteExpense(it) }
                )
            } else {
                // Monthly Report View
                MonthlyReportView(
                    viewModel = viewModel,
                    isUrduEnabled = isUrduEnabled
                )
            }
        }
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            isUrduEnabled = isUrduEnabled,
            onDismiss = { showAddExpenseDialog = false },
            onSave = { newExpense ->
                viewModel.addExpense(newExpense)
                showAddExpenseDialog = false
            }
        )
    }
}

@Composable
fun TodayCashbookView(
    viewModel: ShopViewModel,
    isUrduEnabled: Boolean,
    onDeleteExpense: (ExpenseEntity) -> Unit
) {
    val sales by viewModel.allSales.collectAsState()
    val repairs by viewModel.allRepairs.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()

    val todaySales = sales.filter { viewModel.isToday(it.saleDate) }
    val todaySalesTotal = todaySales.sumOf { it.salePrice }
    val todaySalesProfit = todaySales.sumOf { it.salePrice - it.purchasePrice }

    val todayRepairs = repairs.filter { viewModel.isToday(it.receivedDate) }
    val todayRepairIncome = todayRepairs.sumOf { it.repairCharges }

    val todayExpenses = expenses.filter { viewModel.isToday(it.date) }
    val todayExpenseTotal = todayExpenses.sumOf { it.amount }

    val todayNetProfit = (todaySalesProfit + todayRepairIncome) - todayExpenseTotal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Main Net Profit Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("accounts_today_net_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (todayNetProfit >= 0) Color(0xFF047857) else Color(0xFFB91C1C)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "TODAY'S NET PROFIT / آج کا صاف منافع", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Rs. ${String.format("%.0f", todayNetProfit)}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Sales Income", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        Text(text = "Rs. ${String.format("%.0f", todaySalesTotal)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text(text = "Repair Income", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        Text(text = "Rs. ${String.format("%.0f", todayRepairIncome)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text(text = "Shop Expenses", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        Text(text = "Rs. ${String.format("%.0f", todayExpenseTotal)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Expenses List Section
        DualLanguageText(
            english = "Today's Shop Expenses",
            urdu = "آج کے دکان کے اخراجات",
            isUrduEnabled = isUrduEnabled,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (todayExpenses.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = "No expenses recorded today.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            todayExpenses.forEach { exp ->
                ExpenseItemRow(expense = exp, onDelete = { onDeleteExpense(exp) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MonthlyReportView(
    viewModel: ShopViewModel,
    isUrduEnabled: Boolean
) {
    val sales by viewModel.allSales.collectAsState()
    val repairs by viewModel.allRepairs.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()

    val monthSales = sales.filter { viewModel.isThisMonth(it.saleDate) }
    val monthSalesTotal = monthSales.sumOf { it.salePrice }
    val monthSalesProfit = monthSales.sumOf { it.salePrice - it.purchasePrice }

    val monthRepairs = repairs.filter { viewModel.isThisMonth(it.receivedDate) }
    val monthRepairTotal = monthRepairs.sumOf { it.repairCharges }

    val monthExpenses = expenses.filter { viewModel.isThisMonth(it.date) }
    val monthExpenseTotal = monthExpenses.sumOf { it.amount }

    val monthNetProfit = (monthSalesProfit + monthRepairTotal) - monthExpenseTotal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Monthly Highlight Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("monthly_net_profit_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DualLanguageText(
                        english = "This Month's Summary",
                        urdu = "اس مہینے کا خلا صہ",
                        isUrduEnabled = isUrduEnabled,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF38BDF8))
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Rs. ${String.format("%.0f", monthNetProfit)}",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (monthNetProfit >= 0) Color(0xFF34D399) else Color(0xFFF87171)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReportRow("Total Mobile Sales Revenue", "Rs. ${String.format("%.0f", monthSalesTotal)}", Color(0xFF38BDF8))
                    ReportRow("Mobile Sales Profit", "Rs. ${String.format("%.0f", monthSalesProfit)}", Color(0xFF34D399))
                    ReportRow("Mobile Repairs Income", "Rs. ${String.format("%.0f", monthRepairTotal)}", Color(0xFFFBBF24))
                    ReportRow("Total Shop Expenses", "Rs. ${String.format("%.0f", monthExpenseTotal)}", Color(0xFFF87171))
                }
            }
        }
    }
}

@Composable
fun ReportRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun ExpenseItemRow(expense: ExpenseEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = expense.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "Category: ${expense.category}", fontSize = 11.sp, color = Color.Gray)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Rs. ${String.format("%.0f", expense.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    isUrduEnabled: Boolean,
    onDismiss: () -> Unit,
    onSave: (ExpenseEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tea & Food") }
    var amountStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var errorMsg by remember { mutableStateOf("") }

    val categories = listOf("Tea & Food", "Rent", "Electricity", "Salaries", "Parts Purchase", "Other")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("add_expense_sheet")
        ) {
            DualLanguageText(
                english = "Record Shop Expense",
                urdu = "دکان کا خرچہ درج کریں",
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
                value = title,
                onValueChange = { title = it },
                label = { Text("Expense Title (e.g. Tea for Guests)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_title_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("Expense Amount (Rs.)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_amount_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMsg.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (title.isBlank()) {
                        errorMsg = "Please enter expense title."
                        return@Button
                    }
                    if (amt <= 0) {
                        errorMsg = "Please enter valid expense amount."
                        return@Button
                    }

                    onSave(
                        ExpenseEntity(
                            title = title.trim(),
                            category = category,
                            amount = amt,
                            notes = notes.trim()
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_expense_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Save Expense", fontWeight = FontWeight.Bold)
            }
        }
    }
}
