package com.wingspan.aimediahub.ui.theme.nestedcompose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wingspan.aimediahub.ui.theme.GradientButton

@Composable
fun TelegramConnectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var chatInput by remember { mutableStateOf("") }
    var isAdminConfirmed by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8FAFF)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                /* ---------- Header ---------- */
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFF229ED9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✈️",
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Connect Telegram",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Channel or Group integration",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                /* ---------- Input ---------- */
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    label = { Text("Channel / Group Username") },
                    placeholder = { Text("@yourchannel") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF229ED9),
                        cursorColor = Color(0xFF229ED9)
                    )
                )

                Text(
                    text = "Example: @mytelegramchannel",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                /* ---------- Admin Confirmation ---------- */
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isAdminConfirmed,
                        onCheckedChange = { isAdminConfirmed = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF229ED9)
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = buildAnnotatedString {
                            append("I’ve added ")
                            withStyle(
                                style = SpanStyle(color = Color(0xFF229ED9), fontWeight = FontWeight.Medium)
                            ) {
                                append("@wgsindia_bot")
                            }
                            append(" as admin")
                        },
                        fontSize = 14.sp
                    )

                }

                /* ---------- Help Link ---------- */
                Text(
                    text = "Need help? View step-by-step guide",
                    fontSize = 13.sp,
                    color = Color(0xFF229ED9),
                    modifier = Modifier.clickable {
                        // open tutorial
                    }
                )

                Divider()

                /* ---------- Actions ---------- */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    GradientButton(
                        text = "Connect",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = {
                            if (chatInput.isNotBlank() && isAdminConfirmed) {
                                onConfirm(chatInput.trim())
                            }
                        }
                    )

                }
            }
        }
    }
}
