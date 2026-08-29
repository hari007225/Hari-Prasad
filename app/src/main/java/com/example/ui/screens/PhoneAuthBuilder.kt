package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.IndigoPrimaryLight
import com.example.ui.theme.MidnightCardBg
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightDarkBg
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.CountryInfo
import com.example.ui.viewmodel.POPULAR_COUNTRIES
import com.example.ui.viewmodel.PhoneStep

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PhoneAuthBuilder(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var showCountryDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightCardBg.copy(alpha = 0.95f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step Progress Stepper
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepIndicatorItem(
                    stepNum = 1,
                    title = "Phone",
                    isActive = uiState.phoneStep == PhoneStep.ENTER_PHONE,
                    isDone = uiState.phoneStep != PhoneStep.ENTER_PHONE
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (uiState.phoneStep != PhoneStep.ENTER_PHONE) ElectricCyan else MidnightCardBorder)
                )
                StepIndicatorItem(
                    stepNum = 2,
                    title = "OTP",
                    isActive = uiState.phoneStep == PhoneStep.VERIFY_OTP,
                    isDone = uiState.phoneStep == PhoneStep.SET_PASSWORD
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (uiState.phoneStep == PhoneStep.SET_PASSWORD) ElectricCyan else MidnightCardBorder)
                )
                StepIndicatorItem(
                    stepNum = 3,
                    title = "Password",
                    isActive = uiState.phoneStep == PhoneStep.SET_PASSWORD,
                    isDone = false
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated step container
            AnimatedContent(
                targetState = uiState.phoneStep,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(180)) },
                label = "PhoneStepTransition"
            ) { step ->
                when (step) {
                    PhoneStep.ENTER_PHONE -> {
                        EnterPhoneStepContent(
                            uiState = uiState,
                            onOpenCountryPicker = { showCountryDialog = true },
                            onPhoneChange = { viewModel.onPhoneNumberChange(it) },
                            onSendOtp = {
                                focusManager.clearFocus()
                                viewModel.sendPhoneOtp()
                            },
                            onAutoFillDemo = {
                                viewModel.onPhoneNumberChange("9876543210")
                            }
                        )
                    }

                    PhoneStep.VERIFY_OTP -> {
                        VerifyOtpStepContent(
                            uiState = uiState,
                            onOtpChange = { viewModel.onOtpChange(it) },
                            onVerifyOtp = {
                                focusManager.clearFocus()
                                viewModel.verifyPhoneOtp()
                            },
                            onResendOtp = { viewModel.sendPhoneOtp() },
                            onChangePhone = { viewModel.resetPhoneFlow() }
                        )
                    }

                    PhoneStep.SET_PASSWORD -> {
                        SetPasswordStepContent(
                            uiState = uiState,
                            onPasswordChange = { viewModel.onNewPhonePasswordChange(it) },
                            onConfirmChange = { viewModel.onNewPhonePasswordConfirmChange(it) },
                            onToggleVisibility = { viewModel.togglePhonePasswordVisibility() },
                            onComplete = {
                                focusManager.clearFocus()
                                viewModel.completePhoneRegistration()
                            }
                        )
                    }
                }
            }
        }
    }

    // Country Picker Dialog
    if (showCountryDialog) {
        AlertDialog(
            onDismissRequest = { showCountryDialog = false },
            containerColor = MidnightCardBg,
            title = {
                Text(
                    text = "Select Country Code",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(POPULAR_COUNTRIES) { country ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (country.dialCode == uiState.selectedCountry.dialCode) {
                                IndigoPrimary.copy(alpha = 0.25f)
                            } else {
                                MidnightDarkBg
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (country.dialCode == uiState.selectedCountry.dialCode) IndigoPrimary else MidnightCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectCountry(country)
                                    showCountryDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = country.flagEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = country.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = country.dialCode,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ElectricCyan
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCountryDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun StepIndicatorItem(
    stepNum: Int,
    title: String,
    isActive: Boolean,
    isDone: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone -> SuccessEmerald
                        isActive -> IndigoPrimary
                        else -> MidnightDarkBg
                    }
                )
                .border(
                    1.dp,
                    if (isActive || isDone) ElectricCyan else MidnightCardBorder,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = "$stepNum",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isActive) TextPrimary else TextMuted
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isActive) ElectricCyan else TextMuted
        )
    }
}

@Composable
private fun EnterPhoneStepContent(
    uiState: AuthUiState,
    onOpenCountryPicker: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onAutoFillDemo: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Phone Number Authentication",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = "Receive a 6-digit OTP cryptographic token to register and set up your password.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Demo Chip
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = IndigoPrimary.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimaryLight.copy(alpha = 0.3f)),
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { onAutoFillDemo() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Demo Phone: 9876543210",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = ElectricCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Phone Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Country Code selector
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MidnightDarkBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenCountryPicker() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = uiState.selectedCountry.flagEmoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.selectedCountry.dialCode,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Phone input
            OutlinedTextField(
                value = uiState.phoneNumber,
                onValueChange = onPhoneChange,
                label = { Text("Phone Number", color = TextSecondary) },
                placeholder = { Text("Enter mobile", color = TextMuted) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onSendOtp,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
        ) {
            Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Send OTP Packet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun VerifyOtpStepContent(
    uiState: AuthUiState,
    onOtpChange: (String) -> Unit,
    onVerifyOtp: () -> Unit,
    onResendOtp: () -> Unit,
    onChangePhone: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Verify Cryptographic OTP",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            Text(
                text = "Sent to ${uiState.selectedCountry.dialCode} ${uiState.phoneNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Edit",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = ElectricCyan,
                modifier = Modifier
                    .clickable { onChangePhone() }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // OTP Display Boxes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0 until 6) {
                val char = uiState.otpCode.getOrNull(i)?.toString() ?: ""
                val isCurrent = i == uiState.otpCode.length
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MidnightDarkBg)
                        .border(
                            1.5.dp,
                            when {
                                char.isNotEmpty() -> ElectricCyan
                                isCurrent -> IndigoPrimaryLight
                                else -> MidnightCardBorder
                            },
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hidden input for typing OTP
        OutlinedTextField(
            value = uiState.otpCode,
            onValueChange = onOtpChange,
            label = { Text("Enter 6-digit OTP", color = TextSecondary) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = customTextFieldColors(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Resend Timer Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.otpCountdown > 0) {
                Text(
                    text = "Resend OTP in ${uiState.otpCountdown}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            } else {
                TextButton(onClick = onResendOtp) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = ElectricCyan
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Resend OTP", color = ElectricCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onVerifyOtp,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Verify OTP & Continue",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun SetPasswordStepContent(
    uiState: AuthUiState,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    onComplete: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Set Account Password",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = "Create a password for your phone-linked TalkLoop account packet.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        OutlinedTextField(
            value = uiState.newPhonePassword,
            onValueChange = onPasswordChange,
            label = { Text("New Password", color = TextSecondary) },
            placeholder = { Text("Min 6 characters", color = TextMuted) },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = IndigoPrimaryLight)
            },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (uiState.isPhonePasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = if (uiState.isPhonePasswordVisible) ElectricCyan else TextMuted
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (uiState.isPhonePasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = customTextFieldColors(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.newPhonePasswordConfirm,
            onValueChange = onConfirmChange,
            label = { Text("Confirm New Password", color = TextSecondary) },
            leadingIcon = {
                Icon(Icons.Default.Key, contentDescription = null, tint = IndigoPrimaryLight)
            },
            trailingIcon = {
                if (uiState.newPhonePasswordConfirm.isNotEmpty() && uiState.newPhonePasswordConfirm == uiState.newPhonePassword) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = SuccessEmerald)
                }
            },
            singleLine = true,
            visualTransformation = if (uiState.isPhonePasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = customTextFieldColors(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
        ) {
            Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Store Packet & Enter TalkLoop",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        }
    }
}
