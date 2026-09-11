package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CardData

@Composable
fun CreditCardPreview(
    cardData: CardData,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1E1B4B),
            Color(0xFF312E81),
            Color(0xFF0F172A)
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .border(1.dp, Color(0xFF6366F1).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: Chip and Contactless + Brand
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Golden Chip
                        Box(
                            modifier = Modifier
                                .size(34.dp, 26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFD97706))
                                .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(6.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp, 12.dp)
                                    .align(Alignment.Center)
                                    .border(0.5.dp, Color(0xFF78350F), RoundedCornerShape(2.dp))
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Icon(
                            imageVector = Icons.Default.Nfc,
                            contentDescription = "Contactless",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Card Brand indicator
                    Text(
                        text = cardData.cardBrand.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = Color.White,
                        letterSpacing = 1.5.sp
                    )
                }

                // Middle: Card Number
                val displayNumber = if (cardData.number.isNotBlank()) {
                    formatCardNumber(cardData.number)
                } else {
                    "••••  ••••  ••••  4242"
                }

                Text(
                    text = displayNumber,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )

                // Bottom row: Holder name & Expiry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "CARDHOLDER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (cardData.holderName.isNotBlank()) cardData.holderName.uppercase() else "ALEX RIVERA",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "EXPIRES",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (cardData.expiry.isNotBlank()) cardData.expiry else "12/28",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun formatCardNumber(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    val sb = StringBuilder()
    for (i in digits.indices) {
        if (i > 0 && i % 4 == 0) sb.append("  ")
        sb.append(digits[i])
    }
    return sb.toString()
}
