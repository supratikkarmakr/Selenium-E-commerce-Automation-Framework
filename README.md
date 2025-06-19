# 🛒 Selenium E-commerce Automation Framework

A comprehensive, enterprise-ready Selenium automation framework for e-commerce testing with advanced logging, error handling, and retry mechanisms. This framework automates the complete user journey from login to logout on Flipkart.

## 🚀 Features

### Core Functionality
- **🔐 Automated Login**: Phone number-based OTP authentication
- **🔍 Product Search**: Intelligent product discovery and selection
- **🛒 Add to Cart**: Robust cart management with multiple fallback strategies
- **🚪 Logout**: Complete session termination with account dropdown navigation

### Advanced Capabilities
- **📊 Professional Logging**: Timestamped logs with multiple severity levels (INFO, SUCCESS, WARNING, ERROR)
- **🔄 Retry Mechanisms**: Automatic retry with exponential backoff for critical operations
- **🛡️ Error Handling**: Comprehensive exception handling for TimeoutException, NoSuchElementException, WebDriverException
- **⚡ Performance Monitoring**: Execution time tracking and optimization
- **🎯 Multiple Selector Strategies**: Fallback XPath selectors for UI resilience
- **📱 Window Management**: Automatic handling of new tabs/windows
- **🔍 JavaScript Execution**: Enhanced element interaction capabilities

## 🛠️ Technology Stack

- **Java 17+**
- **Selenium WebDriver 4.15.0**
- **Maven** (Build Management)
- **Chrome WebDriver**
- **TestNG/JUnit** (Testing Framework)

## 📋 Prerequisites

1. **Java Development Kit (JDK) 17 or higher**
2. **Apache Maven 3.6+**
3. **Google Chrome Browser** (latest version)
4. **ChromeDriver** (automatically managed by Selenium)

## ⚙️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd selenium-ecommerce-automation
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Configure Phone Number
⚠️ **IMPORTANT**: Replace the dummy phone number before running tests.

In `src/test/java/com/test/SeleniumTest.java`, locate and replace the dummy phone number `7602596399` with your actual phone number:

```java
// Line ~88: Update login message
logger.logInfo("Starting login workflow with phone number: YOUR_PHONE_NUMBER");

// Line ~130: Update phone number input
phoneField.sendKeys("YOUR_PHONE_NUMBER");

// Line ~133: Update success message
logger.logSuccess("Phone number entered successfully: YOUR_PHONE_NUMBER");

// Line ~215: Update OTP request message
logger.logInfo("OTP request sent to phone number: YOUR_PHONE_NUMBER");
```

## 🚀 Usage

### Running the Test

#### Option 1: Using Maven
```bash
mvn test -Dtest=SeleniumTest
```

#### Option 2: Using Java directly
```bash
javac -cp ".;target/dependency/*" src/test/java/com/test/*.java -d target/classes
java -cp "target/classes;target/dependency/*" com.test.SeleniumTest
```

#### Option 3: Using IDE
- Open the project in your preferred IDE (IntelliJ IDEA, Eclipse, VS Code)
- Navigate to `src/test/java/com/test/SeleniumTest.java`
- Right-click and select "Run SeleniumTest"

### Test Execution Flow

1. **🌐 Browser Initialization**: Chrome browser opens with optimized settings
2. **📱 Login Process**: 
   - Navigate to Flipkart
   - Click login button
   - Enter phone number
   - Request OTP
   - **⏳ Manual Step**: Enter OTP when prompted
   - Automatic homepage redirect detection
3. **🔍 Product Search**: Search for "Google Pixel 9 Pro (Hazel, 256 GB)"
4. **🎯 Product Selection**: Select first matching product
5. **🛒 Add to Cart**: Add selected product to shopping cart
6. **🚪 Logout**: Complete logout process
7. **🧹 Cleanup**: Browser session termination

## 📊 Sample Output

```
================================================================================
>>> TEST STARTED: E-commerce Automation Test
>>> Start Time: 2025-06-19 21:01:08
================================================================================

[INFO] 2025-06-19 21:01:08 - Attempting to initialize browser (Attempt 1)
[SUCCESS] 2025-06-19 21:01:12 - Browser initialized successfully
[INFO] 2025-06-19 21:01:16 - Starting login workflow with phone number: 7602596399
[SUCCESS] 2025-06-19 21:01:21 - Login button clicked successfully!
[SUCCESS] 2025-06-19 21:01:24 - Phone number entered successfully: 7602596399
[SUCCESS] 2025-06-19 21:01:25 - Request OTP button clicked successfully
[WARNING] 2025-06-19 21:01:28 - WAITING FOR USER TO ENTER OTP...
[SUCCESS] 2025-06-19 21:02:21 - SUCCESS! Detected return to Flipkart homepage
[SUCCESS] 2025-06-19 21:02:32 - Product added to cart successfully
[SUCCESS] 2025-06-19 21:02:49 - User logged out successfully!
[SUCCESS] 2025-06-19 21:02:49 - Test completed successfully in 101230ms

================================================================================
<<< TEST COMPLETED: E-commerce Automation Test
<<< End Time: 2025-06-19 21:02:50
================================================================================
```

## 🏗️ Architecture

### Project Structure
```
selenium-ecommerce-automation/
├── src/
│   └── test/
│       └── java/
│           └── com/
│               └── test/
│                   └── SeleniumTest.java      # Main test class
├── target/
│   ├── classes/                               # Compiled classes
│   └── dependency/                            # Maven dependencies
├── pom.xml                                    # Maven configuration
└── README.md                                  # This file
```

### Key Components

#### 1. CustomLogger Class
- **Timestamped Logging**: All logs include precise timestamps
- **Severity Levels**: INFO, SUCCESS, WARNING, ERROR with visual indicators
- **Test Boundaries**: Clear test start/end demarcation
- **Exception Details**: Comprehensive error reporting

#### 2. Retry Mechanisms
- **Max Attempts**: 3 retry attempts for critical operations
- **Delay Strategy**: 2-second delays between retries
- **Smart Recovery**: Graceful degradation on failures

#### 3. Selector Strategies
- **Primary Selectors**: User-provided exact XPaths
- **Fallback Selectors**: Multiple alternative selectors for resilience
- **JavaScript Execution**: Enhanced element interaction capabilities

## 🔧 Configuration

### Customizable Elements

#### 1. Target Product
Modify the product search in the `performProductSearch` method:
```java
performProductSearch(driver, wait, "YOUR_PRODUCT_NAME");
```

#### 2. Timeout Settings
Adjust wait times in WebDriverWait initialization:
```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(YOUR_TIMEOUT));
```

#### 3. Retry Configuration
Modify retry settings:
```java
private static final int MAX_RETRY_ATTEMPTS = 3;
private static final long RETRY_DELAY_MS = 2000;
```

## 🛡️ Error Handling

### Exception Types Handled
- **TimeoutException**: Element not found within specified time
- **NoSuchElementException**: Element not present in DOM
- **WebDriverException**: Browser/driver related issues
- **InterruptedException**: Thread interruption handling

### Recovery Strategies
- **Automatic Retries**: Critical operations retry up to 3 times
- **Graceful Degradation**: Test continues even if non-critical steps fail
- **Detailed Logging**: Comprehensive error information for debugging

## 📈 Performance Optimizations

- **Efficient Element Location**: Smart selector ordering (most reliable first)
- **Minimal Wait Times**: Optimized explicit waits
- **Resource Management**: Proper browser cleanup
- **Parallel Execution Ready**: Thread-safe implementation

## 🔒 Security Considerations

- **No Hardcoded Credentials**: Uses dummy phone number (must be replaced)
- **Session Management**: Proper logout and session termination
- **Data Privacy**: No sensitive information logged

## 🐛 Troubleshooting

### Common Issues

#### 1. ChromeDriver Version Mismatch
```
WARNING: Unable to find CDP implementation matching 137
```
**Solution**: Update Selenium dependencies or Chrome browser

#### 2. Element Not Found
```
TimeoutException: No such element
```
**Solution**: Check XPath selectors or increase timeout values

#### 3. OTP Timeout
```
Timeout after 2 minutes - proceeding anyway
```
**Solution**: Enter OTP within 2 minutes or increase timeout in code

### Debug Mode
Enable verbose logging by modifying the CustomLogger class to include DEBUG level logs.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 🙏 Acknowledgments

- **Selenium WebDriver**: For providing the automation framework
- **Flipkart**: For the e-commerce platform used in testing
- **Maven**: For dependency management
- **Java Community**: For continuous language improvements

## 📞 Support

For issues, questions, or contributions, please reach out through any of my following channels:

### 📧 Contact Information
- **Email**: [karmakarsupratik10@gmail.com](mailto:karmakarsupratik10@gmail.com)
- **Phone**: [+91 7602596399](tel:+917602596399)

---

**⚠️ Important Reminder**: Always replace the dummy phone number `7602596399` with your actual phone number before running the tests. The framework requires a valid phone number to receive OTP for authentication.

**🎯 Ready to automate?**
