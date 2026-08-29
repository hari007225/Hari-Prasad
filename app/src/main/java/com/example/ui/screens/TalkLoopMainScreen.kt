package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.PacketInspectorSheet
import com.example.ui.components.TalkLoopLogoHeader
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.IndigoPrimaryLight
import com.example.ui.theme.MidnightCardBg
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightDarkBg
import com.example.ui.theme.MidnightDarkSurface
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AuthMode
import com.example.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TalkLoopMainScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val storedPackets by viewModel.storedPackets.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle snackbar triggers
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Authenticated Screen Switcher
    Crossfade(
        targetState = uiState.loggedInUser,
        animationSpec = tween(350),
        label = "AuthCrossfade"
    ) { user ->
        if (user != null) {
            TalkLoopDashboardScreen(
                user = user,
                viewModel = viewModel,
                modifier = modifier
            )
        } else {
            // Auth Builder Screens
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = MidnightDarkBg,
                modifier = modifier
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Ambient Deep Indigo / Midnight Blue Particle Background
                    MidnightAmbientBackground()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .imePadding()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // Logo Header
                        TalkLoopLogoHeader(
                            packetCount = storedPackets.size,
                            onOpenDbInspector = { viewModel.openPacketInspector() }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Navigation Mode Selector Tabs (Email, Phone, Facebook/Social)
                        AuthNavigationSelector(
                            currentMode = uiState.currentMode,
                            onSelectMode = { viewModel.setAuthMode(it) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Error Banner
                        AnimatedVisibility(
                            visible = uiState.errorMessage != null,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ErrorRose.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRose.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = null,
                                        tint = ErrorRose,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = uiState.errorMessage ?: "",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = ErrorRose
                                    )
                                }
                            }
                        }

                        // Auth Builder Body
                        AnimatedContent(
                            targetState = uiState.currentMode,
                            transitionSpec = {
                                fadeIn(tween(250)) togetherWith fadeOut(tween(200))
                            },
                            label = "AuthModeContent"
                        ) { mode ->
                            when (mode) {
                                AuthMode.EMAIL_LOGIN -> {
                                    EmailAuthBuilder(
                                        viewModel = viewModel,
                                        uiState = uiState,
                                        isSignUp = false,
                                        onToggleSignUp = { isSignUp ->
                                            viewModel.setAuthMode(if (isSignUp) AuthMode.EMAIL_SIGNUP else AuthMode.EMAIL_LOGIN)
                                        }
                                    )
                                }

                                AuthMode.EMAIL_SIGNUP -> {
                                    EmailAuthBuilder(
                                        viewModel = viewModel,
                                        uiState = uiState,
                                        isSignUp = true,
                                        onToggleSignUp = { isSignUp ->
                                            viewModel.setAuthMode(if (isSignUp) AuthMode.EMAIL_SIGNUP else AuthMode.EMAIL_LOGIN)
                                        }
                                    )
                                }

                                AuthMode.PHONE_AUTH -> {
                                    PhoneAuthBuilder(
                                        viewModel = viewModel,
                                        uiState = uiState
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Bottom Quick DB Inspector Trigger Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MidnightDarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.openPacketInspector() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Storage,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Room SQLite Packet Inspector",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "View real-time database packets, raw JSON & switch accounts",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                Text(
                                    text = "OPEN",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = ElectricCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Loading Modal Overlay
                    if (uiState.isLoading) {
                        Dialog(onDismissRequest = {}) {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MidnightCardBg),
                                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimaryLight.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = ElectricCyan,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = uiState.loadingMessage.ifBlank { "Packaging TalkLoop Packet..." },
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Packet Inspector Bottom Sheet
                    PacketInspectorSheet(
                        isOpen = uiState.isPacketInspectorOpen,
                        packets = storedPackets,
                        onDismiss = { viewModel.closePacketInspector() },
                        onSelectPacketForLogin = { packet ->
                            viewModel.loginWithExistingPacket(packet)
                        },
                        onDeletePacket = { id -> viewModel.deletePacket(id) },
                        onClearAll = { viewModel.clearAllPackets() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthNavigationSelector(
    currentMode: AuthMode,
    onSelectMode: (AuthMode) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MidnightDarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Email Tab
            val isEmailActive = currentMode == AuthMode.EMAIL_LOGIN || currentMode == AuthMode.EMAIL_SIGNUP
            NavSelectorPill(
                icon = Icons.Default.Email,
                title = "Email Auth",
                isActive = isEmailActive,
                activeColor = IndigoPrimary,
                modifier = Modifier.weight(1f),
                onClick = { onSelectMode(AuthMode.EMAIL_LOGIN) }
            )

            // Phone Tab
            val isPhoneActive = currentMode == AuthMode.PHONE_AUTH
            NavSelectorPill(
                icon = Icons.Default.Phone,
                title = "Phone OTP",
                isActive = isPhoneActive,
                activeColor = ElectricCyan,
                modifier = Modifier.weight(1f),
                onClick = { onSelectMode(AuthMode.PHONE_AUTH) }
            )
        }
    }
}

@Composable
private fun NavSelectorPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) activeColor.copy(alpha = 0.25f) else Color.Transparent)
            .border(
                1.dp,
                if (isActive) activeColor else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) activeColor else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isActive) TextPrimary else TextSecondary
            )
        }
    }
}

@Composable
private fun MidnightAmbientBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_drift")
    val shiftX by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shiftX"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Deep Midnight canvas background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    MidnightDarkBg,
                    Color(0xFF0D1224),
                    MidnightDarkBg
                )
            )
        )

        // Top-left glowing Indigo aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    IndigoPrimary.copy(alpha = 0.22f),
                    Color.Transparent
                ),
                center = Offset(width * 0.2f + shiftX, height * 0.15f),
                radius = width * 0.7f
            )
        )

        // Bottom-right glowing Cyan / Purple aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ElectricCyan.copy(alpha = 0.14f),
                    NeonPurple.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(width * 0.85f - shiftX, height * 0.8f),
                radius = width * 0.65f
            )
        )
    }
}
