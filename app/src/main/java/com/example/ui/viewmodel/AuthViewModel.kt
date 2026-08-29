package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AuthPacketEntity
import com.example.data.local.TalkLoopDatabase
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode {
    EMAIL_LOGIN,
    EMAIL_SIGNUP,
    PHONE_AUTH
}

enum class PhoneStep {
    ENTER_PHONE,
    VERIFY_OTP,
    SET_PASSWORD
}

data class CountryInfo(
    val flagEmoji: String,
    val name: String,
    val dialCode: String,
    val maskLength: Int = 10
)

val POPULAR_COUNTRIES = listOf(
    CountryInfo("🇮🇳", "India", "+91", 10),
    CountryInfo("🇺🇸", "United States", "+1", 10),
    CountryInfo("🇬🇧", "United Kingdom", "+44", 10),
    CountryInfo("🇦🇪", "UAE", "+971", 9),
    CountryInfo("🇦🇺", "Australia", "+61", 9),
    CountryInfo("🇨🇦", "Canada", "+1", 10),
    CountryInfo("🇸🇬", "Singapore", "+65", 8),
    CountryInfo("🇩🇪", "Germany", "+49", 11),
    CountryInfo("🇯🇵", "Japan", "+81", 10)
)

data class AuthUiState(
    val currentMode: AuthMode = AuthMode.EMAIL_LOGIN,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isRememberMe: Boolean = true,
    
    // Phone Auth
    val selectedCountry: CountryInfo = POPULAR_COUNTRIES[0],
    val phoneNumber: String = "",
    val phoneStep: PhoneStep = PhoneStep.ENTER_PHONE,
    val otpCode: String = "",
    val otpCountdown: Int = 0,
    val newPhonePassword: String = "",
    val newPhonePasswordConfirm: String = "",
    val isPhonePasswordVisible: Boolean = false,
    
    // System & Animation state
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val errorMessage: String? = null,
    val snackbarMessage: String? = null,
    val loggedInUser: AuthPacketEntity? = null,
    
    // Packet Inspector Modal
    val isPacketInspectorOpen: Boolean = false,
    val inspectingPacket: AuthPacketEntity? = null,
    val copiedPacketJson: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AuthRepository
    val storedPackets: StateFlow<List<AuthPacketEntity>>

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var otpTimerJob: Job? = null

    init {
        val database = TalkLoopDatabase.getDatabase(application)
        repository = AuthRepository(database.authPacketDao())
        storedPackets = repository.allPackets.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun setAuthMode(mode: AuthMode) {
        _uiState.update {
            it.copy(
                currentMode = mode,
                errorMessage = null,
                isLoading = false
            )
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onConfirmPasswordChange(confirmPass: String) {
        _uiState.update { it.copy(confirmPassword = confirmPass, errorMessage = null) }
    }

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun toggleRememberMe() {
        _uiState.update { it.copy(isRememberMe = !it.isRememberMe) }
    }

    // Email / Password Login
    fun submitEmailLogin() {
        val state = _uiState.value
        if (state.email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email address") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email format (e.g. user@domain.com)") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Packaging TLS Auth Packet for ${state.email.trim()}...",
                    errorMessage = null
                )
            }
            delay(900) // Smooth packet assembly simulation

            val userEntity = repository.saveAuthPacket(
                provider = "EMAIL",
                identifier = state.email.trim(),
                displayName = state.fullName.ifBlank { state.email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() } },
                rawPassword = state.password,
                avatarColor = "#6366F1"
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    loggedInUser = userEntity,
                    snackbarMessage = "Packet ${userEntity.packetId} stored in Database! Welcome to TalkLoop."
                )
            }
        }
    }

    // Email / Password Sign Up
    fun submitEmailSignUp() {
        val state = _uiState.value
        if (state.fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your full name") }
            return
        }
        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Creating TalkLoop Account & Encrypting Packet...",
                    errorMessage = null
                )
            }
            delay(1000)

            val userEntity = repository.saveAuthPacket(
                provider = "EMAIL",
                identifier = state.email.trim(),
                displayName = state.fullName.trim(),
                rawPassword = state.password,
                avatarColor = "#4F46E5"
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    loggedInUser = userEntity,
                    snackbarMessage = "Account created & Packet ${userEntity.packetId} persisted in Database!"
                )
            }
        }
    }

    // Phone Auth
    fun selectCountry(country: CountryInfo) {
        _uiState.update { it.copy(selectedCountry = country) }
    }

    fun onPhoneNumberChange(phone: String) {
        val digitsOnly = phone.filter { it.isDigit() }
        _uiState.update { it.copy(phoneNumber = digitsOnly, errorMessage = null) }
    }

    fun onOtpChange(otp: String) {
        val digitsOnly = otp.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(otpCode = digitsOnly, errorMessage = null) }
    }

    fun onNewPhonePasswordChange(pass: String) {
        _uiState.update { it.copy(newPhonePassword = pass, errorMessage = null) }
    }

    fun onNewPhonePasswordConfirmChange(pass: String) {
        _uiState.update { it.copy(newPhonePasswordConfirm = pass, errorMessage = null) }
    }

    fun togglePhonePasswordVisibility() {
        _uiState.update { it.copy(isPhonePasswordVisible = !it.isPhonePasswordVisible) }
    }

    fun sendPhoneOtp() {
        val state = _uiState.value
        if (state.phoneNumber.length < 7) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid phone number") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Transmitting OTP packet to ${state.selectedCountry.dialCode} ${state.phoneNumber}...",
                    errorMessage = null
                )
            }
            delay(1100)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    phoneStep = PhoneStep.VERIFY_OTP,
                    otpCode = "729401", // Preset demo OTP for instant ease-of-use
                    otpCountdown = 30,
                    snackbarMessage = "Demo OTP (729401) dispatched via TalkLoop Gateway!"
                )
            }
            startOtpTimer()
        }
    }

    private fun startOtpTimer() {
        otpTimerJob?.cancel()
        otpTimerJob = viewModelScope.launch {
            while (_uiState.value.otpCountdown > 0) {
                delay(1000)
                _uiState.update { it.copy(otpCountdown = (it.otpCountdown - 1).coerceAtLeast(0)) }
            }
        }
    }

    fun verifyPhoneOtp() {
        val state = _uiState.value
        if (state.otpCode.length != 6) {
            _uiState.update { it.copy(errorMessage = "Please enter the 6-digit OTP code") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Verifying cryptographic OTP signature...",
                    errorMessage = null
                )
            }
            delay(900)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    phoneStep = PhoneStep.SET_PASSWORD,
                    snackbarMessage = "Phone verified! Set a secure password for your TalkLoop account."
                )
            }
        }
    }

    fun completePhoneRegistration() {
        val state = _uiState.value
        if (state.newPhonePassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters") }
            return
        }
        if (state.newPhonePassword != state.newPhonePasswordConfirm) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }

        val fullPhone = "${state.selectedCountry.dialCode} ${state.phoneNumber}"
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Storing Phone Auth Packet & Establishing Session...",
                    errorMessage = null
                )
            }
            delay(1000)

            val userEntity = repository.saveAuthPacket(
                provider = "PHONE",
                identifier = fullPhone,
                displayName = "TalkLoop User (${state.selectedCountry.dialCode})",
                rawPassword = state.newPhonePassword,
                phoneNumber = fullPhone,
                avatarColor = "#38BDF8"
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    loggedInUser = userEntity,
                    snackbarMessage = "Phone Account registered! Packet ${userEntity.packetId} stored in Database."
                )
            }
        }
    }

    fun resetPhoneFlow() {
        _uiState.update {
            it.copy(
                phoneStep = PhoneStep.ENTER_PHONE,
                otpCode = "",
                newPhonePassword = "",
                newPhonePasswordConfirm = "",
                errorMessage = null
            )
        }
    }

    // Switch account / test with existing DB packet
    fun loginWithExistingPacket(packet: AuthPacketEntity) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    loadingMessage = "Resuming Session for ${packet.displayName} (${packet.packetId})..."
                )
            }
            delay(600)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    loggedInUser = packet,
                    isPacketInspectorOpen = false,
                    snackbarMessage = "Switched to account: ${packet.displayName}"
                )
            }
        }
    }

    fun deletePacket(id: Int) {
        viewModelScope.launch {
            repository.deletePacket(id)
            _uiState.update { it.copy(snackbarMessage = "Database packet deleted.") }
        }
    }

    fun clearAllPackets() {
        viewModelScope.launch {
            repository.clearDatabase()
            _uiState.update { it.copy(snackbarMessage = "Database cleared.") }
        }
    }

    fun openPacketInspector(packet: AuthPacketEntity? = null) {
        _uiState.update {
            it.copy(
                isPacketInspectorOpen = true,
                inspectingPacket = packet
            )
        }
    }

    fun closePacketInspector() {
        _uiState.update {
            it.copy(
                isPacketInspectorOpen = false,
                inspectingPacket = null
            )
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                loggedInUser = null,
                password = "",
                confirmPassword = "",
                otpCode = "",
                snackbarMessage = "Logged out from TalkLoop session."
            )
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
