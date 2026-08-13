package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.example.ui.viewmodel.AppLanguage

@Composable
fun DualLanguageText(
    english: String,
    urdu: String,
    arabic: String = "",
    modifier: Modifier = Modifier,
    isUrduEnabled: Boolean = true,
    appLanguage: AppLanguage? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    color: Color = Color.Unspecified
) {
    val effectiveLang = appLanguage ?: if (isUrduEnabled) AppLanguage.BILINGUAL else AppLanguage.ENGLISH

    when (effectiveLang) {
        AppLanguage.ENGLISH -> {
            Text(
                text = english,
                modifier = modifier,
                fontSize = fontSize,
                fontWeight = fontWeight ?: FontWeight.SemiBold,
                color = color
            )
        }
        AppLanguage.URDU -> {
            Text(
                text = urdu.ifBlank { english },
                modifier = modifier,
                fontSize = fontSize,
                fontWeight = fontWeight ?: FontWeight.SemiBold,
                color = color
            )
        }
        AppLanguage.ARABIC -> {
            Text(
                text = arabic.ifBlank { urdu.ifBlank { english } },
                modifier = modifier,
                fontSize = fontSize,
                fontWeight = fontWeight ?: FontWeight.SemiBold,
                color = color
            )
        }
        AppLanguage.BILINGUAL -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = english,
                    fontSize = fontSize,
                    fontWeight = fontWeight ?: FontWeight.SemiBold,
                    color = color
                )
                if (urdu.isNotBlank()) {
                    Text(
                        text = " • $urdu",
                        fontSize = fontSize,
                        fontWeight = FontWeight.Normal,
                        color = color.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}
