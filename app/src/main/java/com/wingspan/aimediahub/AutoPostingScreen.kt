package com.wingspan.aimediahub

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.wingspan.aimediahub.models.SelectableAccount
import com.wingspan.aimediahub.utils.Prefs
import com.wingspan.aimediahub.viewmodel.FacebookViewModel
import java.util.Calendar

@Composable
fun AutoPostingScreen(rootNavController: NavHostController, prefs: Prefs,viewModel: FacebookViewModel= hiltViewModel()) {

    val context = LocalContext.current

    var contentIdea by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }



    var currentfbPages by remember {
        mutableStateOf(
            prefs.getFacebookPages().map { page ->
                SelectableAccount(
                    id = page.id,
                    name = page.name,
                    imageUrl = page.imageUrl,
                    platform = page.platform ?: "facebook",
                    isSelected = false
                )
            }.toMutableList()
        )
    }
    var twitterProfile by remember {
        mutableStateOf(
            prefs.getTwitterAccount()?.let {
                SelectableAccount(
                    id = it.id,
                    name = it.name,
                    imageUrl = it.imageUrl,
                    platform = "twitter"
                )
            }
        )
    }

    var linkedInProfile by remember {
        mutableStateOf(
            prefs.getLinkedInAccount()?.let {
                SelectableAccount(
                    id = it.id,
                    name = it.name,
                    imageUrl = it.imageUrl,
                    platform = "linkedin"
                )
            }
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 36.dp)
    ) {

        // 🔙 Back Button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .clickable { rootNavController.navigateUp() }
                .padding(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_icon),
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        // 📦 Main Card
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(
                    containerColor = Color.White   // 👈 Card background color
                    ),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "AI Automation Posting",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Content Idea
                OutlinedTextField(
                    value = contentIdea,
                    onValueChange = { contentIdea = it },
                    placeholder = { Text("Enter your content idea...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Start Date
                Text("Start Date", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = startDate,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("mm/dd/yyyy") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Start Date",
                            modifier = Modifier.clickable {
                                showDatePicker(context) {
                                    startDate = it
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // End Date
                Text("End Date", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = endDate,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("mm/dd/yyyy") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "End Date",
                            modifier = Modifier.clickable {
                                showDatePicker(context) {
                                    endDate = it
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Time
                // Time
                Text("Time", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("--:-- --") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clock),
                            contentDescription = "Select Time",
                            modifier = Modifier.clickable {
                                showTimePicker(context) {
                                    time = it
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select Social Accounts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                ConnectedAccountsSection(
                    facebookPages = currentfbPages,
                    twitterAccount = twitterProfile,
                    linkedInAccount = linkedInProfile,
                    onAccountSelectionChange = { updatedAccount ->
                        // Update the corresponding account in your state
                        when (updatedAccount.platform) {
                            "facebook" -> {
                                currentfbPages = currentfbPages.map {
                                    if (it.id == updatedAccount.id) updatedAccount else it
                                }.toMutableList()
                            }
                            "twitter" -> twitterProfile = updatedAccount
                            "linkedin" -> linkedInProfile = updatedAccount
                        }
                    }
                )



                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        // TODO: Create Automation
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Create Automation")
                }
            }
        }
    }
}
fun showTimePicker(
    context: Context,
    onTimeSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()

    TimePickerDialog(
        context,
        { _, hour, minute ->
            val formattedTime = String.format(
                "%02d:%02d %s",
                if (hour > 12) hour - 12 else if (hour == 0) 12 else hour,
                minute,
                if (hour >= 12) "PM" else "AM"
            )
            onTimeSelected(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false   // 12-hour format
    ).show()
}

/**
 * 📅 Date Picker Helper
 */
fun showDatePicker(
    context: android.content.Context,
    onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, day ->
            val date = "${month + 1}/$day/$year"
            onDateSelected(date)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
@Composable
fun SocialCheckboxChip(
    icon: Int,
    iconTint: Color,
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked)
                iconTint.copy(alpha = 0.12f)
            else
                Color(0xFFF5F5F5)
        ),

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onCheckedChange(!isChecked) }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {

            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}


@Composable
fun ConnectedAccountsSection(
    facebookPages: List<SelectableAccount>,
    twitterAccount: SelectableAccount?,
    linkedInAccount: SelectableAccount?,
    onAccountSelectionChange: (SelectableAccount) -> Unit
) {

    val hasAccounts =
        facebookPages.isNotEmpty() || twitterAccount != null || linkedInAccount != null

    if (!hasAccounts) {
        NoAccountsConnected() // Replace this with your actual composable when no accounts are connected
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        //facebook
        items(facebookPages.size) { idx ->
            val facebookData = facebookPages[idx]
            SocialCheckboxChip(
                icon = R.drawable.ic_fb,
                iconTint = Color(0xFF1877F2),
                label = facebookData.name,
                isChecked = facebookData.isSelected,
                onCheckedChange = { isSelected ->
                    onAccountSelectionChange(facebookData.copy(isSelected = isSelected))
                }
            )
        }



        // TWITTER
        twitterAccount?.let { tp ->
            item {
                SocialCheckboxChip(
                    icon = R.drawable.ic_twitter,
                    iconTint = Color(0xFF1DA1F2),
                    label = tp.name,
                    isChecked = tp.isSelected,
                    onCheckedChange = { isSelected ->
                        onAccountSelectionChange(tp.copy(isSelected = isSelected))
                    }
                )
            }
        }

        // INSTAGRAM / LINKEDIN
        linkedInAccount?.let { ip ->
            item {
                SocialCheckboxChip(
                    icon = R.drawable.ic_linkedin,
                    iconTint = Color(0xFFDD2A7B),
                    label = ip.name,
                    isChecked = ip.isSelected,
                    onCheckedChange = { isSelected ->
                        onAccountSelectionChange(ip.copy(isSelected = isSelected))
                    }
                )
            }
        }
    }
}
@Composable
fun NoAccountsConnected() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No social accounts connected",
            color = Color.Gray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
