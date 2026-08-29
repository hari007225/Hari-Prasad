package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.IndigoPrimaryDark
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

@Composable
fun EmailAuthBuilder(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    isSignUp: Boolean,
    onToggleSignUp: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmailInput by remember { mutableStateOf("") }
    var resetSentMessage by remember { mutableStateOf<String?>(null) }

    // Password strength calculation
    val passwordScore = remember(uiState.password) {
        val p = uiState.password
        var score = 0
        if (p.length >= 6) score++
        if (p.length >= 10) score++
        if (p.any { it.isUpperCase() } && p.any { it.isLowerCase() }) score++
        if (p.any { it.isDigit() } || p.any { !it.isLetterOrDigit() }) score++
        score
    }

    val (strengthLabel, strengthColor) = when (passwordScore) {
        0, 1 -> "Weak" to ErrorRose
        2 -> "Moderate" to WarningAmber
        3 -> "Strong" to IndigoPrimaryLight
        else -> "Very Strong" to SuccessEmerald
    }

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
            // Mode Switcher Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MidnightDarkBg)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Sign In Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isSignUp) IndigoPrimary else Color.Transparent)
                        .clickable { onToggleSignUp(false) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (!isSignUp) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (!isSignUp) TextPrimary else TextSecondary
                    )
                }

                // Sign Up Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSignUp) IndigoPrimary else Color.Transparent)
                        .clickable { onToggleSignUp(true) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSignUp) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSignUp) TextPrimary else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Demo Account Quick Fill Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = IndigoPrimary.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimaryLight.copy(alpha = 0.3f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (isSignUp) {
                            viewModel.onFullNameChange("Hari Prasad")
                            viewModel.onEmailChange("hari.talkloop@mesh.io")
                            viewModel.onPasswordChange("TalkLoop#2026")
                            viewModel.onConfirmPasswordChange("TalkLoop#2026")
                        } else {
                            viewModel.onEmailChange("hari.talkloop@mesh.io")
                            viewModel.onPasswordChange("TalkLoop#2026")
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSignUp) "Auto-Fill Demo Sign-Up" else "Auto-Fill Demo Credentials",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ElectricCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Full Name (Sign Up only)
            AnimatedVisibility(
                visible = isSignUp,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    OutlinedTextField(
                        value = uiState.fullName,
                        onValueChange = { viewModel.onFullNameChange(it) },
                        label = { Text("Full Name", color = TextSecondary) },
                        placeholder = { Text("e.g. Hari Prasad", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = IndigoPrimaryLight)
                        },
                        trailingIcon = {
                            if (uiState.fullName.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onFullNameChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = customTextFieldColors(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Email Address
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email Address", color = TextSecondary) },
                placeholder = { Text("name@example.com", color = TextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = IndigoPrimaryLight)
                },
                trailingIcon = {
                    if (uiState.email.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onEmailChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Password", color = TextSecondary) },
                placeholder = { Text("Enter 6+ characters", color = TextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = IndigoPrimaryLight)
                },
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility",
                            tint = if (uiState.isPasswordVisible) ElectricCyan else TextMuted
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                colors = customTextFieldColors(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (isSignUp) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (!isSignUp) viewModel.submitEmailLogin()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Password Strength Indicator (Sign Up only)
            AnimatedVisibility(
                visible = isSignUp && uiState.password.isNotEmpty(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Security Strength:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Text(
                            text = strengthLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = strengthColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 1..4) {
                            val active = i <= passwordScore
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (active) strengthColor else MidnightDarkBg)
                            )
                        }
                    }
                }
            }

            // Confirm Password (Sign Up only)
            AnimatedVisibility(
                visible = isSignUp,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.confirmPassword,
                        onValueChange = { viewModel.onConfirmPasswordChange(it) },
                        label = { Text("Confirm Password", color = TextSecondary) },
                        leadingIcon = {
                            Icon(Icons.Default.Key, contentDescription = null, tint = IndigoPrimaryLight)
                        },
                        trailingIcon = {
                            if (uiState.confirmPassword.isNotEmpty() && uiState.confirmPassword == uiState.password) {
                                Icon(Icons.Default.Check, contentDescription = "Matches", tint = SuccessEmerald)
                            } else {
                                IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                                    Icon(
                                        imageVector = if (uiState.isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = TextMuted
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = customTextFieldColors(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.submitEmailSignUp()
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Remember Me & Forgot Password Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.toggleRememberMe() }
                ) {
                    Checkbox(
                        checked = uiState.isRememberMe,
                        onCheckedChange = { viewModel.toggleRememberMe() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = IndigoPrimary,
                            uncheckedColor = TextMuted,
                            checkmarkColor = TextPrimary
                        )
                    )
                    Text(
                        text = "Remember Me",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                if (!isSignUp) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ElectricCyan,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                resetEmailInput = uiState.email
                                resetSentMessage = null
                                showForgotPasswordDialog = true
                            }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (isSignUp) viewModel.submitEmailSignUp() else viewModel.submitEmailLogin()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = androidx.compose.foundation.layout.PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(IndigoPrimary, ElectricCyan)
                            )
                        )
                        .border(1.dp, IndigoPrimaryLight.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSignUp) "Create Account & Store Packet" else "Sign In to TalkLoop",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = TextPrimary
                    )
                }
            }
        }
    }

    // Forgot Password Modal Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            containerColor = MidnightCardBg,
            title = {
                Text(
                    text = "Reset Password Packet",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your registered TalkLoop email address to receive a secure recovery packet token.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = resetEmailInput,
                        onValueChange = { resetEmailInput = it },
                        label = { Text("Email", color = TextSecondary) },
                        singleLine = true,
                        colors = customTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (resetSentMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = resetSentMessage!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessEmerald
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetEmailInput.isNotBlank()) {
                            resetSentMessage = "Recovery packet token sent to $resetEmailInput!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text("Send Recovery Packet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ElectricCyan,
    unfocusedBorderColor = MidnightCardBorder,
    focusedLabelColor = ElectricCyan,
    unfocusedLabelColor = TextSecondary,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = ElectricCyan,
    focusedContainerColor = MidnightDarkBg.copy(alpha = 0.6f),
    unfocusedContainerColor = MidnightDarkBg.copy(alpha = 0.4f)
)
