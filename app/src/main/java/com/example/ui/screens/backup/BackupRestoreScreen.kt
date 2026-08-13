package com.example.ui.screens.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DualLanguageText
import com.example.ui.viewmodel.ShopViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupRestoreScreen(
    viewModel: ShopViewModel,
    onBack: (() -> Unit)? = null
) {
    val isUrduEnabled by viewModel.isUrduEnabled.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var generatedBackupJson by remember { mutableStateOf("") }
    var restoreInputJson by remember { mutableStateOf("") }

    var backupStatusMsg by remember { mutableStateOf("") }
    var restoreStatusMsg by remember { mutableStateOf("") }

    var localBackupFiles by remember { mutableStateOf(getLocalBackupFiles(context)) }

    // System File Chooser to Save JSON Backup to Device Storage
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(generatedBackupJson.toByteArray())
                }
                backupStatusMsg = "✓ Saved backup JSON file directly to device storage!"
                localBackupFiles = getLocalBackupFiles(context)
            } catch (e: Exception) {
                backupStatusMsg = "Error saving file: ${e.message}"
            }
        }
    }

    // System File Picker to Restore JSON Backup from Device Storage
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val jsonString = inputStream?.bufferedReader()?.use { reader -> reader.readText() }
                if (!jsonString.isNullOrBlank()) {
                    restoreInputJson = jsonString
                    viewModel.restoreBackup(jsonString) { success ->
                        if (success) {
                            restoreStatusMsg = "✓ Database restored successfully from selected file!"
                            restoreInputJson = ""
                        } else {
                            restoreStatusMsg = "Invalid backup JSON file format."
                        }
                    }
                } else {
                    restoreStatusMsg = "Selected file was empty."
                }
            } catch (e: Exception) {
                restoreStatusMsg = "Error reading backup file: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        DualLanguageText(
            english = "Local Database Backup & Restore",
            urdu = "لوکل ڈیٹا بیک اپ اور ریسٹور",
            isUrduEnabled = isUrduEnabled,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Security Info Banner
        Surface(
            color = Color(0xFFE0F2FE),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "100% Offline & Private Device Storage", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                    Text(text = "Exports SQLite database as JSON file. You can save, export, or restore files at any time.", fontSize = 11.sp, color = Color(0xFF0284C7))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Export Backup Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Backup, contentDescription = null, tint = Color(0xFF0284C7))
                    Spacer(modifier = Modifier.width(10.dp))
                    DualLanguageText(
                        english = "Export Local JSON Backup",
                        urdu = "ڈیٹا بیک اپ فائل بنائیں",
                        isUrduEnabled = isUrduEnabled,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.createBackup { json ->
                            if (json != null) {
                                generatedBackupJson = json
                                saveBackupToAppStorage(context, json)
                                localBackupFiles = getLocalBackupFiles(context)
                                backupStatusMsg = "✓ Backup file generated & saved to app storage!"
                            } else {
                                backupStatusMsg = "Failed to generate backup."
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_backup_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Generate & Save Backup File (.json)", fontWeight = FontWeight.Bold)
                }

                if (backupStatusMsg.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = backupStatusMsg, fontSize = 12.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                }

                if (generatedBackupJson.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = generatedBackupJson,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Backup JSON Content Preview") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                createDocumentLauncher.launch("kashif_mobile_backup_$timeStamp.json")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.InsertDriveFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Export Storage", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(generatedBackupJson))
                                backupStatusMsg = "✓ Copied JSON to clipboard!"
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Copy", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                shareTextFile(context, generatedBackupJson)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0369A1))
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Share", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Saved Local Backup Files List
        if (localBackupFiles.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FolderZip, contentDescription = null, tint = Color(0xFF059669))
                        Spacer(modifier = Modifier.width(10.dp))
                        DualLanguageText(
                            english = "Saved Backup Files on Device (${localBackupFiles.size})",
                            urdu = "محفوظ شدہ بیک اپ فائلیں",
                            isUrduEnabled = isUrduEnabled,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    localBackupFiles.take(5).forEachIndexed { index, file ->
                        if (index > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = file.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${formatFileSize(file.length())} • ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(file.lastModified()))}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }

                            Row {
                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val jsonString = file.readText()
                                            viewModel.restoreBackup(jsonString) { success ->
                                                if (success) {
                                                    restoreStatusMsg = "✓ Restored from file: ${file.name}"
                                                } else {
                                                    restoreStatusMsg = "Failed to restore file."
                                                }
                                            }
                                        } catch (e: Exception) {
                                            restoreStatusMsg = "Error reading file: ${e.message}"
                                        }
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Restore", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = {
                                        file.delete()
                                        localBackupFiles = getLocalBackupFiles(context)
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Restore Backup Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Restore, contentDescription = null, tint = Color(0xFFD97706))
                    Spacer(modifier = Modifier.width(10.dp))
                    DualLanguageText(
                        english = "Restore Data from Backup File",
                        urdu = "بیک اپ سے ڈیٹا بحال کریں",
                        isUrduEnabled = isUrduEnabled,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Button to pick file from device storage
                Button(
                    onClick = {
                        openFileLauncher.launch("*/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pick_backup_file_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Choose Backup JSON File from Device Storage", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Or paste JSON content manually:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = restoreInputJson,
                    onValueChange = { restoreInputJson = it },
                    placeholder = { Text("Paste Backup JSON data here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("restore_json_input"),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                )

                if (restoreStatusMsg.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = restoreStatusMsg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (restoreStatusMsg.contains("✓")) Color(0xFF059669) else Color(0xFFDC2626)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        if (restoreInputJson.isBlank()) {
                            restoreStatusMsg = "Please paste backup JSON content or choose a file."
                            return@OutlinedButton
                        }
                        viewModel.restoreBackup(restoreInputJson) { success ->
                            if (success) {
                                restoreStatusMsg = "✓ Data restored successfully!"
                                restoreInputJson = ""
                            } else {
                                restoreStatusMsg = "Invalid JSON data format."
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_restore_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Restore from Text Input", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun saveBackupToAppStorage(context: Context, json: String): File? {
    return try {
        val backupDir = File(context.getExternalFilesDir(null), "backups")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(backupDir, "kashif_mobile_backup_$timeStamp.json")
        file.writeText(json)
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getLocalBackupFiles(context: Context): List<File> {
    val backupDir = File(context.getExternalFilesDir(null), "backups")
    if (!backupDir.exists()) {
        backupDir.mkdirs()
    }
    return backupDir.listFiles()?.filter { it.extension == "json" }?.sortedByDescending { it.lastModified() } ?: emptyList()
}

private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val kb = size / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1) {
        String.format("%.2f MB", mb)
    } else {
        String.format("%.1f KB", kb)
    }
}

private fun shareTextFile(context: Context, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share Backup JSON"))
}

