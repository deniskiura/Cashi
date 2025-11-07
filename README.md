# Cashi - Cross-Platform Payment App

A modern FinTech mobile application built with Kotlin Multiplatform (KMP) that enables users to send payments and view transaction history. This project demonstrates clean architecture, Firebase integration, comprehensive testing, and cross-platform capabilities.

## 📱 Features

### ✅ Implemented Features

- **Send Payment**: Users can send payments by entering:
  - Recipient's email address
  - Amount (in cents, e.g., $100.00 = 10000)
  - Currency (USD or EUR)

- **Transaction History**:
  - View all past payment transactions
  - Real-time updates via Firestore integration
  - Offline-first architecture with local caching
  - Ordered by timestamp (newest first)

- **Input Validation**:
  - Email format validation
  - Amount must be greater than zero
  - Currency must be USD or EUR
  - All validation in shared KMP module

- **Firebase Integration**:
  - Firestore for transaction storage and retrieval
  - Real-time synchronization across devices
  - Offline support with local Room database

## 🏗️ Architecture

### Kotlin Multiplatform Structure

```
Cashi/
├── composeApp/          # Android-specific code
│   ├── src/
│   │   ├── androidMain/
│   │   │   ├── kotlin/
│   │   │   │   └── ke/kiura/cashi/
│   │   │   │       ├── CashiApplication.kt
│   │   │   │       ├── MainActivity.kt
│   │   │   │       └── presentation/  # UI Layer (Jetpack Compose)
│   │   │   └── AndroidManifest.xml
│   │   └── commonMain/      # Shared UI code
│   └── build.gradle.kts
│
└── shared/              # Shared KMP Module
    ├── src/
    │   ├── commonMain/      # Platform-agnostic code
    │   │   └── kotlin/
    │   │       └── ke/kiura/cashi/
    │   │           ├── data/          # Data layer
    │   │           │   ├── mapper/    # DTO ↔ Domain mappers
    │   │           │   └── repository/ # Repository implementations
    │   │           ├── db/            # Room database (expect/actual)
    │   │           │   ├── dao/
    │   │           │   └── entity/
    │   │           ├── domain/        # Business logic
    │   │           │   ├── model/     # Domain models
    │   │           │   ├── repository/ # Repository interfaces
    │   │           │   └── usecase/   # Use cases
    │   │           ├── remote/        # Network layer
    │   │           │   └── dto/       # Data Transfer Objects
    │   │           └── di/            # Dependency injection
    │   │
    │   ├── androidMain/     # Android-specific implementations
    │   │   └── kotlin/
    │   │       └── ke/kiura/cashi/
    │   │           ├── db/            # Room actual implementation
    │   │           ├── di/            # Android DI module
    │   │           └── remote/        # Firebase implementation
    │   │
    │   ├── commonTest/      # Shared tests
    │   │   └── kotlin/
    │   │       └── ke/kiura/cashi/
    │   │           ├── data/          # Data layer tests
    │   │           └── domain/        # Domain layer tests
    │   │
    │   └── androidUnitTest/ # Android-specific tests
    │       ├── kotlin/
    │       │   └── ke/kiura/cashi/
    │       │       └── bdd/           # Cucumber BDD tests
    │       └── resources/
    │           └── features/          # BDD feature files
    │
    └── build.gradle.kts
```

### Clean Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                     │
│          (Jetpack Compose, ViewModels, UI State)        │
│                    [androidMain]                         │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                     Domain Layer                         │
│        (Use Cases, Models, Repository Interfaces)       │
│                    [commonMain]                          │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                      Data Layer                          │
│         (Repositories, Mappers, Data Sources)           │
│                    [commonMain]                          │
└────────────┬──────────────────────────┬─────────────────┘
             │                          │
┌────────────▼────────────┐  ┌─────────▼──────────────────┐
│   Local Data Source     │  │   Remote Data Source       │
│   (Room Database)       │  │   (Firebase Firestore)     │
│    [androidMain]        │  │    [androidMain]           │
└─────────────────────────┘  └────────────────────────────┘
```

### Key Architectural Patterns

1. **Clean Architecture**: Clear separation of concerns with Domain, Data, and Presentation layers
2. **Repository Pattern**: Abstracts data sources from business logic
3. **Use Case Pattern**: Single-responsibility business logic operations
4. **MVVM**: ViewModel manages UI state and business logic calls
5. **Offline-First**: Local database as primary data source with Firebase sync
6. **Dependency Injection**: Koin for managing dependencies

## 🚀 Technology Stack

### Core Technologies
- **Kotlin Multiplatform (KMP)**: Share business logic across platforms
- **Jetpack Compose**: Modern declarative UI framework
- **Firebase Firestore**: Cloud database for real-time data sync
- **Room Database**: Local persistence with offline support
- **Koin**: Dependency injection framework
- **Coroutines & Flow**: Asynchronous programming and reactive streams

### Testing Stack
- **JUnit**: Unit testing framework
- **Kotest**: Kotlin-first assertions and matchers
- **MockK**: Mocking library for Kotlin
- **Cucumber (BDD)**: Behavior-driven development tests
- **Turbine**: Flow testing library

### Additional Libraries
- **Kotlinx Serialization**: JSON serialization
- **Kotlinx DateTime**: Cross-platform date/time handling
- **Ktor Client**: HTTP client (configured but using Firebase for backend)

## 📋 Prerequisites

- **JDK 11** or higher
- **Android Studio** Ladybug | 2024.2.1 or later
- **Android SDK** with API level 24+
- **Gradle 8.13** (included via wrapper)
- **Firebase Project** with Firestore enabled

## 🔧 Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Cashi
```

### 2. Firebase Configuration

#### Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project (or use existing)
3. Add an Android app with package name: `ke.kiura.cashi`

#### Download Configuration File

1. Download `google-services.json` from Firebase Console
2. Place it in: `composeApp/google-services.json`

#### Enable Firestore

1. In Firebase Console → Build → Firestore Database
2. Click "Create database"
3. Start in **test mode** (for development)

**Test Mode Rules** (expires 30 days):
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2025, 3, 1);
    }
  }
}
```

For detailed Firebase setup instructions, see [FIREBASE_SETUP.md](./FIREBASE_SETUP.md)

### 3. Build the Project

```bash
# Clean and build
./gradlew clean build

# Or build specific modules
./gradlew :shared:build
./gradlew :composeApp:build
```

### 4. Run the App

#### From Android Studio
1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Select `composeApp` configuration
4. Click Run ▶️

#### From Command Line
```bash
# Install on connected device/emulator
./gradlew :composeApp:installDebug

# Install and launch
./gradlew :composeApp:installDebug && adb shell am start -n ke.kiura.cashi/.MainActivity
```

## 🧪 Testing

The project includes comprehensive testing at multiple levels:

### Unit Tests

Test business logic, data layer, and domain models:

```bash
# Run all unit tests
./gradlew test

# Run shared module tests
./gradlew :shared:testDebugUnitTest

# Run with coverage
./gradlew :shared:testDebugUnitTestCoverage
```

**Test Coverage Includes:**
- ✅ Payment validation (email format, amount > 0, currency support)
- ✅ Transaction mapping (DTO ↔ Domain ↔ Entity)
- ✅ Repository operations (save, fetch, update)
- ✅ Use case logic (SendPayment, GetTransactions)
- ✅ Error handling and edge cases

### BDD Tests (Cucumber)

Behavior-driven tests for user scenarios:

```bash
# Run Cucumber tests
./gradlew :shared:testDebugUnitTest --tests "*CucumberTestRunner*"
```

**Feature File**: `shared/src/androidUnitTest/resources/features/send_payment.feature`

**Scenarios Covered:**
1. ✅ Successfully send a payment with valid details
2. ✅ Fail to send payment with invalid email
3. ✅ Fail to send payment with zero amount
4. ✅ Fail to send payment with negative amount
5. ✅ Successfully send payment with different currencies
6. ✅ Handle API failure gracefully
7. ✅ Handle validation error from API

**Example Scenario:**
```gherkin
Scenario: Successfully send a payment with valid details
  Given a user wants to send a payment
  When they enter recipient email "recipient@example.com"
  And they enter amount "100" in currency "USD"
  And they submit the payment
  Then the payment should be processed successfully
  And the transaction should be saved with status "COMPLETED"
```

### Test Reports

After running tests, view reports at:
- **Unit Tests**: `shared/build/reports/tests/testDebugUnitTest/index.html`
- **Coverage**: `shared/build/reports/coverage/testDebugUnitTest/html/index.html`

## 📊 Project Structure Details

### Shared Module (`shared/`)

Contains all platform-agnostic business logic:

#### Domain Layer
```kotlin
// Domain Models
data class Payment(
    val recipientEmail: String,
    val amount: Int,
    val currency: Currency
)

data class Transaction(
    val id: String,
    val recipient: String,
    val amount: Int,
    val currency: Currency,
    val timestamp: String,
    val status: TransactionStatus
)

// Use Cases
class SendPaymentUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(payment: Payment): DomainState<Transaction>
}

class GetTransactionsUseCase(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<DomainState<List<Transaction>>>
}
```

#### Data Layer
```kotlin
// Repository Implementation
class PaymentRepositoryImpl(
    private val remoteApi: RemoteApi,
    private val transactionDao: TransactionDao
) : PaymentRepository {
    // Saves to local DB, syncs with Firebase, updates status
}

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val remoteApi: RemoteApi
) : TransactionRepository {
    // Offline-first: local data + background Firebase sync
}
```

#### Remote Layer
```kotlin
// Firebase Implementation (androidMain)
class FirebaseRemoteApi(
    private val firestore: FirebaseFirestore
) : RemoteApi {
    override suspend fun saveTransaction(transaction: TransactionDto): Remote<Unit>
    override suspend fun getTransactions(): Remote<List<TransactionDto>>
}
```

### Android Module (`composeApp/`)

Android-specific UI and platform implementations:

#### Presentation Layer
```kotlin
// ViewModel
class SendPaymentViewModel(
    private val sendPaymentUseCase: SendPaymentUseCase
) : ViewModel() {
    val uiState: StateFlow<SendPaymentUiState>
    fun sendPayment(payment: Payment)
}

// Composable UI
@Composable
fun SendPaymentScreen(viewModel: SendPaymentViewModel) {
    // Email input, amount input, currency dropdown, send button
}

@Composable
fun TransactionHistoryScreen(viewModel: TransactionHistoryViewModel) {
    // List of transactions with pull-to-refresh
}
```

## 🔐 Data Flow

### Sending a Payment

```
User Input
    ↓
SendPaymentScreen (Compose)
    ↓
SendPaymentViewModel
    ↓
SendPaymentUseCase (validates payment)
    ↓
PaymentRepository
    ↓
┌───────────────────┴──────────────────┐
│                                      │
▼                                      ▼
Local DB (Room)                   Firebase Firestore
Save PENDING                      Save transaction
    ↓                                  ↓
Update to COMPLETED/FAILED ◄──────────┘
    ↓
UI updates with result
```

### Viewing Transaction History

```
User Opens History
    ↓
TransactionHistoryScreen
    ↓
TransactionHistoryViewModel
    ↓
GetTransactionsUseCase
    ↓
TransactionRepository (offline-first)
    ↓
┌─────────────────┬──────────────────┐
│                 │                  │
▼                 ▼                  ▼
Show local data   Sync Firebase     Update UI
(instant)         (background)      (reactive)
```

## 🎯 Key Features Implementation

### 1. Input Validation

All validation is in the shared KMP module:

```kotlin
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}

fun Payment.validate(): ValidationResult {
    // Email validation
    if (!recipientEmail.contains("@")) {
        return Invalid("Invalid email format")
    }

    // Amount validation
    if (amount <= 0) {
        return Invalid("Amount must be greater than zero")
    }

    return Valid
}
```

### 2. Offline-First Architecture

The app works seamlessly offline:

- **Local Database**: Room stores all transactions
- **Background Sync**: Firebase syncs when online
- **Reactive Updates**: Flow automatically updates UI
- **Conflict Resolution**: Firebase is source of truth

### 3. Error Handling

Comprehensive error handling with sealed classes:

```kotlin
sealed class DomainState<out T> {
    data object Loading : DomainState<Nothing>()
    data class Success<T>(val data: T) : DomainState<T>()
    data class Error(val message: String) : DomainState<Nothing>()
}

sealed class Remote<out T> {
    data class Success<T>(val data: T) : Remote<T>()
    data class Failure(val error: String) : Remote<Nothing>()
    data class ValidationError(val errors: Map<String, List<String>>) : Remote<Nothing>()
    data object UnAuthenticated : Remote<Nothing>()
}
```

### 4. Dependency Injection (Koin)

```kotlin
val sharedModule = module {
    // Repositories
    single<PaymentRepository> { PaymentRepositoryImpl(get(), get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get()) }

    // Use Cases
    factory { SendPaymentUseCase(get()) }
    factory { GetTransactionsUseCase(get()) }
}

val platformModule = module {
    // Room Database
    single { Room.databaseBuilder(...).build() }
    single { get<AppDatabase>().transactionDao() }

    // Firebase
    single { FirebaseFirestore.getInstance() }
    single<RemoteApi> { FirebaseRemoteApi(get()) }
}
```

## 📱 Screenshots & Demo

### App Screens

1. **Send Payment Screen**
   - Email input field
   - Amount input field (numeric)
   - Currency dropdown (USD, EUR)
   - Send Payment button
   - Success/Error messages

2. **Transaction History Screen**
   - Scrollable list of transactions
   - Each showing: recipient, amount, currency, timestamp, status
   - Real-time updates
   - Empty state when no transactions

### Running a Demo

```bash
# 1. Start emulator or connect device
adb devices

# 2. Install app
./gradlew :composeApp:installDebug

# 3. Send a test payment
# - Open app
# - Navigate to Send Payment
# - Enter email: test@example.com
# - Enter amount: 10000 (= $100.00)
# - Select currency: USD
# - Tap Send Payment

# 4. View transaction history
# - Navigate to History tab
# - See the transaction appear
# - Check Firebase Console to verify data
```

## 🔍 Code Quality

### Best Practices

- ✅ **SOLID Principles**: Single responsibility, dependency inversion
- ✅ **Clean Architecture**: Clear layer separation
- ✅ **Immutability**: Data classes with `val` properties
- ✅ **Type Safety**: Sealed classes for state and results
- ✅ **Reactive Programming**: Kotlin Flow for data streams
- ✅ **Coroutines**: Structured concurrency with proper scope management
- ✅ **Error Handling**: Explicit error states, no exceptions in business logic

### Code Style

- Follows [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Clear naming conventions
- Comprehensive documentation
- Meaningful variable and function names

## 🚧 Future Enhancements

### Backend API Integration

Currently using Firebase directly. Can be extended with:

```kotlin
// Ktor HTTP Client (already configured)
class KtorRemoteApi(private val client: HttpClient) : RemoteApi {
    override suspend fun saveTransaction(transaction: TransactionDto): Remote<Unit> {
        return try {
            client.post("/payments") {
                setBody(transaction)
            }
            Remote.Success(Unit)
        } catch (e: Exception) {
            Remote.Failure(e.message ?: "Network error")
        }
    }
}
```

### iOS Support

KMP architecture is ready for iOS:

1. Add `iosMain` source set
2. Implement platform-specific dependencies (Firebase iOS SDK)
3. Create SwiftUI views
4. Use shared business logic

### Performance Testing (JMeter)

Create test plan for API endpoints:

```xml
<!-- JMeter Test Plan -->
<TestPlan>
  <ThreadGroup threads="5" rampUp="1" loops="10">
    <HTTPSampler method="POST" path="/payments">
      <JSONPostProcessor>
        <responseTime>
          <assertion lessThan="500ms" />
        </responseTime>
      </JSONPostProcessor>
    </HTTPSampler>
  </ThreadGroup>
</TestPlan>
```

### UI Testing (Appium)

Automated UI tests:

```kotlin
// Appium test example
@Test
fun testSendPayment() {
    // Find elements
    val emailInput = driver.findElement(By.id("email_input"))
    val amountInput = driver.findElement(By.id("amount_input"))
    val sendButton = driver.findElement(By.id("send_button"))

    // Perform actions
    emailInput.sendKeys("test@example.com")
    amountInput.sendKeys("10000")
    sendButton.click()

    // Verify
    val successMessage = driver.findElement(By.id("success_message"))
    assert(successMessage.isDisplayed)
}
```

## 📖 Documentation

- **[FIREBASE_SETUP.md](./FIREBASE_SETUP.md)**: Complete Firebase configuration guide
- **[FIREBASE_IMPLEMENTATION.md](./FIREBASE_IMPLEMENTATION.md)**: Technical implementation details
- **[TESTING.md](./TESTING.md)**: Comprehensive testing guide
- **[composeApp/README.md](./composeApp/README.md)**: Android app specific documentation

## 🤝 Contributing

This is a coding challenge project. For production use:

1. Fork the repository
2. Create a feature branch
3. Implement changes with tests
4. Ensure all tests pass
5. Submit a pull request

## 📄 License

This project is created for the Cashi Mobile App Challenge.

## 👥 Author

Developed as part of the Cashi Senior Android Developer Technical Challenge.

## 🎯 Challenge Completion Checklist

### ✅ Core Requirements
- [x] **Send Payment**: Email, amount, currency input with validation
- [x] **Transaction History**: Display past payments from Firestore
- [x] **Backend Integration**: Firebase Firestore for data storage
- [x] **KMP Architecture**: Shared business logic in `shared` module
- [x] **Jetpack Compose**: Modern Android UI
- [x] **Koin**: Dependency injection
- [x] **Clean Architecture**: Domain, Data, Presentation layers

### ✅ Testing
- [x] **BDD Tests**: Cucumber scenarios for payment flow
- [x] **Unit Tests**: Comprehensive coverage of business logic
- [x] **Integration Tests**: Repository and use case tests
- [x] **Kotest**: Kotlin-first assertions
- [x] **MockK**: Mocking for isolated testing

### ✅ Firebase
- [x] Firestore integration for transaction storage
- [x] Real-time data synchronization
- [x] Offline support with local caching
- [x] Proper error handling

### ✅ Code Quality
- [x] Clean code with meaningful names
- [x] SOLID principles
- [x] Comprehensive documentation
- [x] Type-safe error handling

### 📋 Optional Enhancements (Future Work)
- [ ] **Appium**: UI automation tests
- [ ] **JMeter**: API performance testing
- [ ] **iOS Support**: Extend KMP to iOS platform
- [ ] **Backend API**: REST API with POST /payments endpoint

## 🆘 Troubleshooting

### Common Issues

**Build Error: "google-services.json is missing"**
```bash
# Solution: Download from Firebase Console and place in composeApp/
cp ~/Downloads/google-services.json composeApp/
```

**Firebase Permission Denied**
```bash
# Solution: Update Firestore rules in Firebase Console
# Enable test mode or configure proper security rules
```

**Tests Failing**
```bash
# Clean and rebuild
./gradlew clean test

# Check test reports
open shared/build/reports/tests/testDebugUnitTest/index.html
```

**App Crashes on Launch**
```bash
# Check logcat
adb logcat | grep Cashi

# Verify Firebase configuration
ls -la composeApp/google-services.json
```

## 📞 Support

For questions or issues:
1. Check documentation files in the repository
2. Review Firebase Console for backend issues
3. Check test reports for test failures
4. Review logcat for runtime issues

---

**Built with ❤️ using Kotlin Multiplatform, Jetpack Compose, and Firebase**
