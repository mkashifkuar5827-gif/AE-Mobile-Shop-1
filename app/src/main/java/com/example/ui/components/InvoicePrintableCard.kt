package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MobileRepairEntity
import com.example.data.local.entity.MobileSaleEntity
import com.example.data.local.entity.ShopProfileEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoicePrintableCard(
    shopProfile: ShopProfileEntity?,
    saleItem: MobileSaleEntity? = null,
    repairItem: MobileRepairEntity? = null,
    isUrduEnabled: Boolean = true,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val shopName = shopProfile?.shopName ?: "KASHIF MOBILE AND REPAIR"
    val owner = shopProfile?.ownerName ?: "Muhammad Kashif"
    val phone = shopProfile?.phone ?: "0300-1234567"
    val address = shopProfile?.address ?: "Main Mobile Market"
    val city = shopProfile?.city ?: "Lahore"
    val tagline = shopProfile?.tagline ?: "Mobile Sales, Repairing & Accessories"

    val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(
        Date(saleItem?.saleDate ?: repairItem?.receivedDate ?: System.currentTimeMillis())
    )

    val invoiceNo = saleItem?.invoiceNumber ?: repairItem?.invoiceNumber ?: "INV-0001"
    val customerName = saleItem?.customerName ?: repairItem?.customerName ?: "Valued Customer"
    val customerPhone = saleItem?.customerPhone ?: repairItem?.customerPhone ?: "N/A"

    val totalAmt = saleItem?.salePrice ?: repairItem?.repairCharges ?: 0.0
    val paidAmt = saleItem?.salePrice ?: repairItem?.advancePayment ?: 0.0
    val remainingAmt = if (saleItem != null) 0.0 else (repairItem?.remainingPayment ?: 0.0)

    val invoiceText = buildString {
        appendLine("===============================")
        appendLine("    $shopName")
        appendLine("    $tagline")
        appendLine("Owner: $owner | Tel: $phone")
        appendLine("Address: $address, $city")
        appendLine("===============================")
        appendLine("INVOICE NO: $invoiceNo")
        appendLine("DATE: $dateFormatted")
        appendLine("-------------------------------")
        appendLine("CUSTOMER DETAILS / گاہک کی تفصیل:")
        appendLine("Name: $customerName")
        appendLine("Phone: $customerPhone")
        appendLine("-------------------------------")
        if (saleItem != null) {
            appendLine("ITEM SOLD / موبائل فروخت:")
            appendLine("Brand: ${saleItem.brandName}")
            appendLine("Model: ${saleItem.modelName}")
            appendLine("IMEI: ${saleItem.imeiNumber}")
            if (saleItem.notes.isNotBlank()) appendLine("Notes: ${saleItem.notes}")
        } else if (repairItem != null) {
            appendLine("REPAIR JOB / موبائل مرمت:")
            appendLine("Model: ${repairItem.mobileModel}")
            if (repairItem.imeiNumber.isNotBlank()) appendLine("IMEI: ${repairItem.imeiNumber}")
            appendLine("Problem: ${repairItem.problemDetails}")
            if (repairItem.partsUsed.isNotBlank()) appendLine("Parts: ${repairItem.partsUsed}")
            appendLine("Status: ${repairItem.status}")
        }
        appendLine("-------------------------------")
        appendLine("Total Amount (کل رقم): Rs. ${String.format("%.0f", totalAmt)}")
        appendLine("Paid Amount (ادا شدہ): Rs. ${String.format("%.0f", paidAmt)}")
        appendLine("Remaining Balance (بقایا): Rs. ${String.format("%.0f", remainingAmt)}")
        appendLine("===============================")
        appendLine("Thank you for visiting! / شکریہ")
        appendLine("Software by Kashif Mobile System")
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("invoice_dialog_surface"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Receipt Canvas Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = shopName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = tagline,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$phone | $address, $city", fontSize = 11.sp, color = Color(0xFF64748B))
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFCBD5E1))

                    // Invoice Metadata
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Invoice # $invoiceNo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text(text = "Date: $dateFormatted", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Text(
                            text = if (saleItem != null) "MOBILE SALE" else "REPAIR JOB",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7),
                            modifier = Modifier
                                .background(Color(0xFFE0F2FE), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Customer Info
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Customer Details / گاہک کی تفصیل", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0284C7))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Name: $customerName", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                            Text(text = "Phone: $customerPhone", fontSize = 12.sp, color = Color(0xFF475569))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Product or Repair Details
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            if (saleItem != null) {
                                Text(text = "Product Details / پروڈکٹ تفصیل", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0284C7))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "${saleItem.brandName} ${saleItem.modelName}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text(text = "IMEI: ${saleItem.imeiNumber}", fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF475569))
                                if (saleItem.notes.isNotBlank()) {
                                    Text(text = "Note: ${saleItem.notes}", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            } else if (repairItem != null) {
                                Text(text = "Repair Details / مرمت تفصیل", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0284C7))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Model: ${repairItem.mobileModel}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                if (repairItem.imeiNumber.isNotBlank()) {
                                    Text(text = "IMEI: ${repairItem.imeiNumber}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF475569))
                                }
                                Text(text = "Problem: ${repairItem.problemDetails}", fontSize = 12.sp, color = Color(0xFF334155))
                                if (repairItem.partsUsed.isNotBlank()) {
                                    Text(text = "Parts Used: ${repairItem.partsUsed}", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Totals Breakdown
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total Charges / کل رقم:", fontSize = 13.sp, color = Color(0xFF475569))
                            Text(text = "Rs. ${String.format("%.0f", totalAmt)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Paid Amount / ادا شدہ:", fontSize = 13.sp, color = Color(0xFF059669))
                            Text(text = "Rs. ${String.format("%.0f", paidAmt)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                        }
                        if (remainingAmt > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Remaining Balance / بقایا:", fontSize = 13.sp, color = Color(0xFFDC2626))
                                Text(text = "Rs. ${String.format("%.0f", remainingAmt)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• No Return / Refund without receipt. Thank you for your business!",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        InvoicePdfHelper.shareTextInvoice(context, shopProfile, saleItem, repairItem)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_invoice_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Share / شیئر")
                }

                Button(
                    onClick = {
                        InvoicePdfHelper.printInvoice(context, shopProfile, saleItem, repairItem)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("print_invoice_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Print / پرنٹ")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("close_invoice_btn"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Close / بند کریں")
            }
        }
    }
}

private fun shareTextInvoice(context: Context, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Invoice via")
    context.startActivity(shareIntent)
}
