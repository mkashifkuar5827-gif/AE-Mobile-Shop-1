package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.data.local.entity.MobileRepairEntity
import com.example.data.local.entity.MobileSaleEntity
import com.example.data.local.entity.ShopProfileEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InvoicePdfHelper {

    fun generateInvoiceHtml(
        shopProfile: ShopProfileEntity?,
        saleItem: MobileSaleEntity? = null,
        repairItem: MobileRepairEntity? = null
    ): String {
        val shopName = shopProfile?.shopName?.ifBlank { "KASHIF MOBILE AND REPAIR" } ?: "KASHIF MOBILE AND REPAIR"
        val owner = shopProfile?.ownerName?.ifBlank { "Muhammad Kashif" } ?: "Muhammad Kashif"
        val phone = shopProfile?.phone?.ifBlank { "0300-1234567" } ?: "0300-1234567"
        val address = shopProfile?.address?.ifBlank { "Main Mobile Market" } ?: "Main Mobile Market"
        val city = shopProfile?.city?.ifBlank { "Lahore" } ?: "Lahore"
        val tagline = shopProfile?.tagline?.ifBlank { "Mobile Sales, Repairing & Accessories" } ?: "Mobile Sales, Repairing & Accessories"

        val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(
            Date(saleItem?.saleDate ?: repairItem?.receivedDate ?: System.currentTimeMillis())
        )

        val invoiceNo = saleItem?.invoiceNumber ?: repairItem?.invoiceNumber ?: "INV-0001"
        val customerName = saleItem?.customerName ?: repairItem?.customerName ?: "Valued Customer"
        val customerPhone = saleItem?.customerPhone ?: repairItem?.customerPhone ?: "N/A"

        val totalAmt = saleItem?.salePrice ?: repairItem?.repairCharges ?: 0.0
        val paidAmt = saleItem?.salePrice ?: repairItem?.advancePayment ?: 0.0
        val remainingAmt = if (saleItem != null) 0.0 else (repairItem?.remainingPayment ?: 0.0)

        val isSale = saleItem != null
        val invoiceTitle = if (isSale) "MOBILE SALE INVOICE / سیل رسید" else "MOBILE REPAIR RECEIPT / مرمت رسید"

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>$invoiceNo - $shopName</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        margin: 0;
                        padding: 16px;
                        color: #1e293b;
                        background-color: #ffffff;
                    }
                    .invoice-card {
                        max-width: 480px;
                        margin: 0 auto;
                        border: 2px solid #0284c7;
                        border-radius: 12px;
                        padding: 20px;
                        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
                    }
                    .header {
                        text-align: center;
                        border-bottom: 2px dashed #cbd5e1;
                        padding-bottom: 12px;
                        margin-bottom: 12px;
                    }
                    .shop-title {
                        font-size: 22px;
                        font-weight: 800;
                        color: #0284c7;
                        margin: 0;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }
                    .tagline {
                        font-size: 12px;
                        color: #64748b;
                        margin: 4px 0;
                    }
                    .contact-info {
                        font-size: 11px;
                        color: #334155;
                        font-weight: 600;
                    }
                    .meta-row {
                        display: flex;
                        justify-content: space-between;
                        font-size: 12px;
                        margin-bottom: 12px;
                        background: #f1f5f9;
                        padding: 8px 12px;
                        border-radius: 6px;
                    }
                    .section-title {
                        font-size: 12px;
                        font-weight: 700;
                        color: #0284c7;
                        margin-bottom: 6px;
                        text-transform: uppercase;
                        border-bottom: 1px solid #e2e8f0;
                        padding-bottom: 2px;
                    }
                    .info-box {
                        background: #f8fafc;
                        border: 1px solid #e2e8f0;
                        border-radius: 8px;
                        padding: 10px;
                        margin-bottom: 12px;
                        font-size: 12px;
                    }
                    .info-row {
                        display: flex;
                        justify-content: space-between;
                        margin-bottom: 4px;
                    }
                    .info-row:last-child {
                        margin-bottom: 0;
                    }
                    .label {
                        color: #64748b;
                    }
                    .value {
                        font-weight: 600;
                        color: #0f172a;
                    }
                    .amount-table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 8px;
                        margin-bottom: 12px;
                    }
                    .amount-table td {
                        padding: 6px 8px;
                        font-size: 13px;
                    }
                    .total-row {
                        background-color: #e0f2fe;
                        font-weight: bold;
                        font-size: 14px;
                        color: #0369a1;
                    }
                    .paid-row {
                        color: #15803d;
                        font-weight: 600;
                    }
                    .due-row {
                        color: #b91c1c;
                        font-weight: 700;
                    }
                    .footer {
                        text-align: center;
                        font-size: 10px;
                        color: #94a3b8;
                        border-top: 1px dashed #cbd5e1;
                        padding-top: 10px;
                        margin-top: 12px;
                    }
                    .urdu-sub {
                        direction: rtl;
                        font-family: system-ui, -apple-system, sans-serif;
                    }
                </style>
            </head>
            <body>
                <div class="invoice-card">
                    <div class="header">
                        <h1 class="shop-title">$shopName</h1>
                        <div class="tagline">$tagline</div>
                        <div class="contact-info">Owner: $owner | 📱 $phone</div>
                        <div class="contact-info">📍 $address, $city</div>
                    </div>

                    <div class="meta-row">
                        <div>
                            <strong>Invoice #:</strong> $invoiceNo<br>
                            <span style="font-size: 10px; color: #64748b;">$dateFormatted</span>
                        </div>
                        <div style="text-align: right; color: #0284c7; font-weight: bold;">
                            $invoiceTitle
                        </div>
                    </div>

                    <div class="info-box">
                        <div class="section-title">Customer Details / گاہک کی تفصیل</div>
                        <div class="info-row"><span class="label">Name / نام:</span> <span class="value">$customerName</span></div>
                        <div class="info-row"><span class="label">Phone / فون:</span> <span class="value">$customerPhone</span></div>
                    </div>

                    <div class="info-box">
                        <div class="section-title">Items / تفصیلات</div>
                        ${if (isSale) """
                            <div class="info-row"><span class="label">Brand:</span> <span class="value">${saleItem?.brandName}</span></div>
                            <div class="info-row"><span class="label">Model:</span> <span class="value">${saleItem?.modelName}</span></div>
                            <div class="info-row"><span class="label">IMEI:</span> <span class="value" style="font-family: monospace;">${saleItem?.imeiNumber}</span></div>
                            ${if (!saleItem?.notes.isNullOrBlank()) """<div class="info-row"><span class="label">Notes:</span> <span class="value">${saleItem?.notes}</span></div>""" else ""}
                        """ else """
                            <div class="info-row"><span class="label">Mobile Model:</span> <span class="value">${repairItem?.mobileModel}</span></div>
                            ${if (!repairItem?.imeiNumber.isNullOrBlank()) """<div class="info-row"><span class="label">IMEI:</span> <span class="value" style="font-family: monospace;">${repairItem?.imeiNumber}</span></div>""" else ""}
                            <div class="info-row"><span class="label">Problem:</span> <span class="value">${repairItem?.problemDetails}</span></div>
                            ${if (!repairItem?.partsUsed.isNullOrBlank()) """<div class="info-row"><span class="label">Parts Used:</span> <span class="value">${repairItem?.partsUsed}</span></div>""" else ""}
                            <div class="info-row"><span class="label">Status:</span> <span class="value" style="color: #0284c7;">${repairItem?.status}</span></div>
                        """}
                    </div>

                    <table class="amount-table">
                        <tr class="total-row">
                            <td>Total Amount / کل رقم</td>
                            <td style="text-align: right;">Rs. ${String.format("%.0f", totalAmt)}</td>
                        </tr>
                        <tr class="paid-row">
                            <td>Paid Amount / ادا شدہ</td>
                            <td style="text-align: right;">Rs. ${String.format("%.0f", paidAmt)}</td>
                        </tr>
                        ${if (remainingAmt > 0) """
                        <tr class="due-row">
                            <td>Remaining Balance / بقایا</td>
                            <td style="text-align: right;">Rs. ${String.format("%.0f", remainingAmt)}</td>
                        </tr>
                        """ else ""}
                    </table>

                    <div class="footer">
                        • Receipts required for any service query. Thank you for your business!<br>
                        <strong>KASHIF MOBILE AND REPAIR</strong> - Dedicated Service & Quality Products
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun printInvoice(
        context: Context,
        shopProfile: ShopProfileEntity?,
        saleItem: MobileSaleEntity? = null,
        repairItem: MobileRepairEntity? = null
    ) {
        val webView = WebView(context)
        val htmlContent = generateInvoiceHtml(shopProfile, saleItem, repairItem)
        
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                val printAdapter: PrintDocumentAdapter = webView.createPrintDocumentAdapter("Invoice_${saleItem?.invoiceNumber ?: repairItem?.invoiceNumber ?: "0001"}")
                val jobName = "Invoice_${shopProfile?.shopName ?: "KashifMobile"}"
                
                printManager?.print(jobName, printAdapter, PrintAttributes.Builder().build())
            }
        }
        
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    fun shareTextInvoice(
        context: Context,
        shopProfile: ShopProfileEntity?,
        saleItem: MobileSaleEntity? = null,
        repairItem: MobileRepairEntity? = null
    ) {
        val shopName = shopProfile?.shopName?.ifBlank { "KASHIF MOBILE AND REPAIR" } ?: "KASHIF MOBILE AND REPAIR"
        val owner = shopProfile?.ownerName?.ifBlank { "Muhammad Kashif" } ?: "Muhammad Kashif"
        val phone = shopProfile?.phone?.ifBlank { "0300-1234567" } ?: "0300-1234567"
        val address = shopProfile?.address?.ifBlank { "Main Mobile Market" } ?: "Main Mobile Market"
        val city = shopProfile?.city?.ifBlank { "Lahore" } ?: "Lahore"

        val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(
            Date(saleItem?.saleDate ?: repairItem?.receivedDate ?: System.currentTimeMillis())
        )

        val invoiceNo = saleItem?.invoiceNumber ?: repairItem?.invoiceNumber ?: "INV-0001"
        val customerName = saleItem?.customerName ?: repairItem?.customerName ?: "Valued Customer"
        val customerPhone = saleItem?.customerPhone ?: repairItem?.customerPhone ?: "N/A"

        val totalAmt = saleItem?.salePrice ?: repairItem?.repairCharges ?: 0.0
        val paidAmt = saleItem?.salePrice ?: repairItem?.advancePayment ?: 0.0
        val remainingAmt = if (saleItem != null) 0.0 else (repairItem?.remainingPayment ?: 0.0)

        val text = buildString {
            appendLine("===============================")
            appendLine("  🏢 $shopName")
            appendLine("  👑 Owner: $owner | 📞 $phone")
            appendLine("  📍 Address: $address, $city")
            appendLine("===============================")
            appendLine("📄 INVOICE NO: $invoiceNo")
            appendLine("📅 DATE: $dateFormatted")
            appendLine("-------------------------------")
            appendLine("👤 CUSTOMER DETAILS:")
            appendLine("• Name: $customerName")
            appendLine("• Phone: $customerPhone")
            appendLine("-------------------------------")
            if (saleItem != null) {
                appendLine("📱 MOBILE SALE:")
                appendLine("• Brand: ${saleItem.brandName}")
                appendLine("• Model: ${saleItem.modelName}")
                appendLine("• IMEI: ${saleItem.imeiNumber}")
                if (saleItem.notes.isNotBlank()) appendLine("• Notes: ${saleItem.notes}")
            } else if (repairItem != null) {
                appendLine("🔧 REPAIR JOB:")
                appendLine("• Model: ${repairItem.mobileModel}")
                if (repairItem.imeiNumber.isNotBlank()) appendLine("• IMEI: ${repairItem.imeiNumber}")
                appendLine("• Issue: ${repairItem.problemDetails}")
                if (repairItem.partsUsed.isNotBlank()) appendLine("• Parts Used: ${repairItem.partsUsed}")
                appendLine("• Status: ${repairItem.status}")
            }
            appendLine("-------------------------------")
            appendLine("💰 Total Amount (کل رقم): Rs. ${String.format("%.0f", totalAmt)}")
            appendLine("✅ Paid Amount (ادا شدہ): Rs. ${String.format("%.0f", paidAmt)}")
            if (remainingAmt > 0) {
                appendLine("⚠️ Remaining (بقایا): Rs. ${String.format("%.0f", remainingAmt)}")
            }
            appendLine("===============================")
            appendLine("Thank you for choosing $shopName!")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Invoice via")
        context.startActivity(shareIntent)
    }
}
