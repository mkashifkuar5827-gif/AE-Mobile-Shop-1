package com.example.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.ShopProfileEntity
import com.example.ui.components.DualLanguageText
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun ShopProfileScreen(
    viewModel: ShopViewModel,
    onNavigateToBackup: () -> Unit
) {
    val shopProfile by viewModel.shopProfile.collectAsState()
    val isUrduEnabled by viewModel.isUrduEnabled.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    var isEditing by remember { mutableStateOf(false) }

    var shopName by remember(shopProfile) { mutableStateOf(shopProfile?.shopName ?: "KASHIF MOBILE AND REPAIR") }
    var ownerName by remember(shopProfile) { mutableStateOf(shopProfile?.ownerName ?: "Muhammad Kashif") }
    var phone by remember(shopProfile) { mutableStateOf(shopProfile?.phone ?: "0300-1234567") }
    var whatsapp by remember(shopProfile) { mutableStateOf(shopProfile?.whatsapp ?: "0300-1234567") }
    var address by remember(shopProfile) { mutableStateOf(shopProfile?.address ?: "Shop #15, Mobile Market") }
    var city by remember(shopProfile) { mutableStateOf(shopProfile?.city ?: "Lahore") }
    var tagline by remember(shopProfile) { mutableStateOf(shopProfile?.tagline ?: "Mobile Sales, Repairing & Accessories") }

    var saveSuccessMsg by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        DualLanguageText(
            english = "Shop Profile & Preferences",
            urdu = "دکان پروفائل اور ترتیبات",
            isUrduEnabled = isUrduEnabled,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Shop Header Logo Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0284C7).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shop_app_icon_1786601992623),
                        contentDescription = "Shop Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = shopName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = tagline,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = { isEditing = !isEditing },
                        modifier = Modifier.testTag("toggle_edit_profile_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isEditing) "Cancel Edit" else "Edit Details")
                    }

                    OutlinedButton(
                        onClick = onNavigateToBackup,
                        modifier = Modifier.testTag("backup_restore_nav_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Backup Data")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Fields Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Shop Details / دکان کی تفصیلات",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Shop Name") },
                    enabled = isEditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_shop_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Owner Details") },
                    enabled = isEditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_owner_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        enabled = isEditing,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_phone_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp Number") },
                        enabled = isEditing,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_whatsapp_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address / Market") },
                        enabled = isEditing,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_address_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        enabled = isEditing,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_city_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text("Shop Tagline / Services") },
                    enabled = isEditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_tagline_input"),
                    singleLine = true
                )

                if (isEditing) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val updated = ShopProfileEntity(
                                id = 1,
                                shopName = shopName.trim(),
                                ownerName = ownerName.trim(),
                                phone = phone.trim(),
                                whatsapp = whatsapp.trim(),
                                address = address.trim(),
                                city = city.trim(),
                                tagline = tagline.trim()
                            )
                            viewModel.saveProfile(updated)
                            isEditing = false
                            saveSuccessMsg = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_profile_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Save Shop Details", fontWeight = FontWeight.Bold)
                    }
                }

                if (saveSuccessMsg) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✓ Shop details saved successfully!",
                        color = Color(0xFF059669),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Preferences Card (Language Switcher & Dark Mode)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DualLanguageText(
                    english = "App Settings & Language",
                    urdu = "ایپ کی ترتیبات اور زبان",
                    appLanguage = appLanguage,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Language Switcher Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.language_switcher_title),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Language Chips (English, Urdu, Arabic, Bilingual)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = appLanguage == lang
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setAppLanguage(lang) },
                            label = {
                                Text(
                                    text = when (lang) {
                                        AppLanguage.ENGLISH -> stringResource(id = R.string.lang_english)
                                        AppLanguage.URDU -> stringResource(id = R.string.lang_urdu)
                                        AppLanguage.ARABIC -> stringResource(id = R.string.lang_arabic)
                                        AppLanguage.BILINGUAL -> stringResource(id = R.string.lang_bilingual)
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("lang_chip_${lang.name.lowercase()}"),
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                // Urdu Support Toggle Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Dual Language Mode", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Show English & Urdu together on screen", fontSize = 10.sp, color = Color.Gray)
                    }

                    Switch(
                        checked = isUrduEnabled,
                        onCheckedChange = { viewModel.toggleUrduSupport(it) },
                        modifier = Modifier.testTag("urdu_toggle_switch")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dark Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkMode == true) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = stringResource(id = R.string.dark_mode), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Sleek eye-safe dark theme", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Switch(
                        checked = isDarkMode == true,
                        onCheckedChange = { viewModel.toggleTheme(it) },
                        modifier = Modifier.testTag("dark_mode_switch")
                    )
                }
            }
        }
    }
}
