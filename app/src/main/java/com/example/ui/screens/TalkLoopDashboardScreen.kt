package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AuthPacketEntity
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.IndigoPrimaryDark
import com.example.ui.theme.IndigoPrimaryLight
import com.example.ui.theme.MidnightCardBg
import com.example.ui.theme.MidnightCardBorder
import com.example.ui.theme.MidnightDarkBg
import com.example.ui.theme.MidnightDarkSurface
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SupabaseGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AuthViewModel

data class ChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val time: String,
    val isMe: Boolean,
    val avatarColor: Color = IndigoPrimary
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalkLoopDashboardScreen(
    user: AuthPacketEntity,
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Loop Chats", "Voice Mesh", "Packet Telemetry")

    // Interactive message list
    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "TalkLoop Core", "Welcome to TalkLoop Communication Hub! Authenticated via Packet ${user.packetId}.", "Just now", false, ElectricCyan),
            ChatMessage("2", "Aria Loop", "Hey ${user.displayName}! The voice low-latency mesh is running smooth at 120fps.", "1m ago", false, NeonPurple),
            ChatMessage("3", "Dev Team", "All auth credentials and OAuth states are persisted in local Room SQLite database.", "2m ago", false, IndigoPrimaryLight)
        )
    }
    var inputMessage by remember { mutableStateOf("") }
    var isMicMuted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(IndigoPrimary, ElectricCyan))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TL",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TalkLoop Hub",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SuccessEmerald)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Online • ${user.packetId}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = ElectricCyan
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Database Inspector Action
                    IconButton(onClick = { viewModel.openPacketInspector(user) }) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "Database Packets",
                            tint = ElectricCyan
                        )
                    }

                    // Logout Action
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = ErrorRose.copy(alpha = 0.85f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MidnightDarkBg
                )
            )
        },
        containerColor = MidnightDarkBg
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // User Session Header Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(IndigoPrimary, ElectricCyan)
                                )
                            )
                            .border(1.5.dp, ElectricCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.displayName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.displayName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val badgeColor = when (user.authProvider.uppercase()) {
                                "SUPABASE" -> SupabaseGreen
                                "PHONE" -> ElectricCyan
                                "FACEBOOK" -> com.example.ui.theme.FacebookBlue
                                "GOOGLE" -> com.example.ui.theme.GoogleBrandColor
                                else -> IndigoPrimaryLight
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = badgeColor.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, badgeColor)
                            ) {
                                Text(
                                    text = user.authProvider,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = badgeColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Text(
                            text = user.identifier,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessEmerald.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessEmerald,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "DB Stored",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = SuccessEmerald
                            )
                        }
                    }
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MidnightDarkBg,
                contentColor = TextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ElectricCyan,
                        height = 3.dp
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selectedTab == index) ElectricCyan else TextSecondary
                            )
                        }
                    )
                }
            }

            // Tab Contents
            when (selectedTab) {
                0 -> {
                    // Chat Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Channels List Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ChannelChip("# general-loop", isActive = true)
                            ChannelChip("# voice-mesh", isActive = false)
                            ChannelChip("# announcements", isActive = false)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Chat Messages List
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(messages, key = { it.id }) { msg ->
                                ChatBubbleItem(message = msg)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Message Input Field
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = inputMessage,
                                onValueChange = { inputMessage = it },
                                placeholder = { Text("Type a loop message...", color = TextMuted) },
                                singleLine = true,
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = MidnightCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    cursorColor = ElectricCyan,
                                    focusedContainerColor = MidnightCardBg,
                                    unfocusedContainerColor = MidnightCardBg
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if (inputMessage.isNotBlank()) {
                                            messages.add(
                                                ChatMessage(
                                                    id = System.currentTimeMillis().toString(),
                                                    senderName = user.displayName,
                                                    text = inputMessage.trim(),
                                                    time = "Just now",
                                                    isMe = true,
                                                    avatarColor = IndigoPrimary
                                                )
                                            )
                                            inputMessage = ""
                                        }
                                    }
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    if (inputMessage.isNotBlank()) {
                                        messages.add(
                                            ChatMessage(
                                                id = System.currentTimeMillis().toString(),
                                                senderName = user.displayName,
                                                text = inputMessage.trim(),
                                                time = "Just now",
                                                isMe = true,
                                                avatarColor = IndigoPrimary
                                            )
                                        )
                                        inputMessage = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(IndigoPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // Voice Room Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(IndigoPrimary.copy(alpha = 0.5f), Color.Transparent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(if (isMicMuted) ErrorRose else ElectricCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = MidnightDarkBg,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isMicMuted) "Microphone Muted" else "Voice Loop Active",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )

                        Text(
                            text = "Connected to TalkLoop Ultra-Low-Latency Audio Channel (Opus 48kHz / 12ms)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = { isMicMuted = !isMicMuted },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMicMuted) SuccessEmerald else ErrorRose
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMicMuted) Icons.Default.Mic else Icons.Default.MicOff,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isMicMuted) "Unmute Mic" else "Mute Mic")
                            }

                            Button(
                                onClick = { viewModel.openPacketInspector(user) },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Storage, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Inspect DB Packet")
                            }
                        }
                    }
                }

                2 -> {
                    // Telemetry & Packet View Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MidnightCardBg),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Active Session Room Packet",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ElectricCyan
                                        )
                                        Text(
                                            text = user.packetId,
                                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                            color = SuccessEmerald
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF070B14),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = user.packetJson,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp
                                            ),
                                            color = SuccessEmerald,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelChip(name: String, isActive: Boolean) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) IndigoPrimary.copy(alpha = 0.25f) else MidnightCardBg,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) ElectricCyan else MidnightCardBorder
        )
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (isActive) ElectricCyan else TextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ChatBubbleItem(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isMe) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(message.avatarColor.copy(alpha = 0.25f))
                    .border(1.dp, message.avatarColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.senderName.take(1).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            if (!message.isMe) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = if (message.isMe) 14.dp else 2.dp,
                    bottomEnd = if (message.isMe) 2.dp else 14.dp
                ),
                color = if (message.isMe) IndigoPrimary else MidnightCardBg,
                border = if (!message.isMe) androidx.compose.foundation.BorderStroke(1.dp, MidnightCardBorder) else null
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Text(
                text = message.time,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}
