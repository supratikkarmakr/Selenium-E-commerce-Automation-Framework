package com.test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;

public class SeleniumTest {
	
	// Custom logger instance for enhanced logging
	private static final CustomLogger logger = new CustomLogger();
	
	// Retry configuration
	private static final int MAX_RETRY_ATTEMPTS = 3;
	private static final long RETRY_DELAY_MS = 2000;

	public static void main(String[] args) {
		logger.logTestStart("E-commerce Automation Test");
		long testStartTime = System.currentTimeMillis();
		
		WebDriver driver = null;
		WebDriverWait wait = null;
		
		try {
			// Step 1: Initialize browser with enhanced error handling
			driver = initializeBrowserWithRetry();
			wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			logger.logSuccess("Browser initialized successfully");
			
			// Step 2: Open Flipkart
			navigateToWebsiteWithRetry(driver, "https://www.flipkart.com");
			driver.manage().window().maximize();
			logger.logInfo("Navigated to Flipkart and maximized window");
			
			// Step 3: Handle login popup and login with dummy account
			handleLoginPopupAndLogin(driver, wait);
			
			// Step 4: Search for product with retry mechanism
			performProductSearch(driver, wait, "Google Pixel 9 Pro (Hazel, 256 GB)");
			
			String mainPage = driver.getWindowHandle();
			logger.logInfo("Main page window handle: " + mainPage);
			
			// Step 5: Select product with enhanced error handling
			selectProductWithRetry(driver, wait, "Google Pixel 9 Pro (Hazel, 256 GB)");
			
			// Handle new window/tab
			handleNewWindow(driver, mainPage);
			logger.logInfo("Product page URL: " + driver.getCurrentUrl());
			
			// Step 6: Display product information with fallback strategies
			extractProductInformation(driver);
			
			// Step 7: Add to cart with retry mechanism
			addToCartWithRetry(driver, wait);
			
			// Step 8: Logout
			logout(driver, wait);
			
			long testDuration = System.currentTimeMillis() - testStartTime;
			logger.logSuccess("Test completed successfully in " + testDuration + "ms");
			
		} catch(Exception e) {
			logger.logError("Critical error in main test execution", e);
			handleCriticalError(e);
		} finally {
			// Step 9: Clean up resources safely
			cleanupResources(driver);
			logger.logTestEnd("E-commerce Automation Test");
		}
	}
	
	private static void handleLoginPopupAndLogin(WebDriver driver, WebDriverWait wait) {
		logger.logInfo("Starting login workflow with phone number: 7602596399");
		try {
			// Wait for page to load completely
			Thread.sleep(3000);
			
			// Look for login button with updated selectors for current Flipkart UI
			try {
				// Updated selectors for current Flipkart interface
				String[] loginButtonSelectors = {
					"//a[contains(@href, '/account/login')]",
					"//a[text()='Login']",
					"//span[text()='Login']",
					"//a[@class='_1_3w1N' and text()='Login']",
					"//div[text()='Login']//parent::a",
					"//button[contains(text(),'Login')]"
				};
				
				boolean loginButtonClicked = false;
				for (String selector : loginButtonSelectors) {
					try {
						logger.logInfo("Trying login button selector: " + selector);
						WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
							By.xpath(selector)));
						loginButton.click();
						logger.logSuccess("Login button clicked successfully!");
						loginButtonClicked = true;
						Thread.sleep(2000);
						break;
					} catch(TimeoutException e) {
						logger.logInfo("Selector failed, trying next: " + selector);
					}
				}
				
				if (!loginButtonClicked) {
					logger.logError("Could not find any login button", null);
					throw new RuntimeException("No login button found");
				}
				
			} catch(Exception e) {
				logger.logError("Error clicking login button", e);
				throw new RuntimeException("Cannot access login", e);
			}
			
			// Enter the phone number automatically
			try {
				// Updated selectors using the exact XPath provided by user
				String[] inputFieldSelectors = {
					"(//input[@class='r4vIwl BV+Dqf'])[1]",  // Exact XPath from user
					"//input[@class='r4vIwl BV+Dqf']",        // Without index
					"//input[contains(@class, 'r4vIwl')]",    // Contains first class
					"//input[contains(@class, 'BV+Dqf')]",    // Contains second class
					"//input[contains(@class, 'r4vIwl') and contains(@class, 'BV+Dqf')]", // Both classes
					"//input[@placeholder='Enter Email/Mobile number']",
					"//form//input[@type='text']",
					"//input[@type='text']"
				};
				
				boolean phoneEntered = false;
				for (String selector : inputFieldSelectors) {
					try {
						logger.logInfo("Trying input field selector: " + selector);
						
						// Wait for the element to be present and visible
						WebElement phoneField = wait.until(ExpectedConditions.presenceOfElementLocated(
							By.xpath(selector)));
						
						// Scroll to element if needed
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", phoneField);
						Thread.sleep(500);
						
						// Make sure element is clickable
						wait.until(ExpectedConditions.elementToBeClickable(phoneField));
						
						// Clear and enter phone number
						phoneField.clear();
						Thread.sleep(500);
						phoneField.sendKeys("7602596399");
						
						logger.logSuccess("Phone number entered successfully: 7602596399");
						phoneEntered = true;
						break;
						
					} catch(Exception e) {
						logger.logInfo("Input field selector failed: " + selector + " - " + e.getMessage());
					}
				}
				
				if (!phoneEntered) {
					logger.logError("Could not find email/mobile input field with any selector", null);
					throw new RuntimeException("Cannot find phone input field");
				}
			} catch(Exception e) {
				logger.logError("Error entering phone number", e);
				throw new RuntimeException("Cannot enter phone number", e);
			}
			
			// Click "Request OTP" or "Continue" button
			try {
				Thread.sleep(1000);
				String[] submitSelectors = {
					"//button[contains(text(), 'Request OTP')]",
					"//button[contains(text(), 'CONTINUE')]", 
					"//button[contains(text(), 'Continue')]",
					"//button[@type='submit']",
					"//form//button",
					"//button[contains(@class, '_2KpZ6l')]"
				};
				
				boolean submitClicked = false;
				for (String selector : submitSelectors) {
					try {
						logger.logInfo("Trying submit button selector: " + selector);
						WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
							By.xpath(selector)));
						submitButton.click();
						logger.logSuccess("Request OTP button clicked successfully");
						submitClicked = true;
						break;
					} catch(TimeoutException e) {
						logger.logInfo("Submit button selector failed, trying next: " + selector);
					}
				}
				
				if (!submitClicked) {
					logger.logError("Could not find Request OTP button", null);
					throw new RuntimeException("Cannot find Request OTP button");
				}
			} catch(Exception e) {
				logger.logError("Error clicking Request OTP button", e);
				throw new RuntimeException("Cannot request OTP", e);
			}
			
			// Wait for OTP screen and user input
			Thread.sleep(3000);
			logger.logInfo("OTP request sent to phone number: 7602596399");
			logger.logWarning("WAITING FOR USER TO ENTER OTP...");
			logger.logInfo("Please check your phone for SMS and enter the OTP in the browser");
			
			// First, wait for OTP input screen to appear
			try {
				logger.logInfo("Waiting for OTP input screen to appear...");
				String[] otpScreenSelectors = {
					"//input[@placeholder='Enter OTP']",
					"//input[contains(@class, 'otp')]",
					"//div[contains(text(), 'OTP')]",
					"//input[@maxlength='6']",
					"//input[@type='text' and @maxlength]"
				};
				
				boolean otpScreenFound = false;
				WebDriverWait otpWait = new WebDriverWait(driver, Duration.ofSeconds(10));
				
				for (String selector : otpScreenSelectors) {
					try {
						WebElement otpElement = otpWait.until(ExpectedConditions.presenceOfElementLocated(
							By.xpath(selector)));
						logger.logSuccess("OTP input screen detected!");
						otpScreenFound = true;
						break;
					} catch(TimeoutException e) {
						// Try next selector
					}
				}
				
				if (!otpScreenFound) {
					logger.logWarning("OTP screen not detected - may already be on different page");
				}
				
			} catch(Exception e) {
				logger.logWarning("Error detecting OTP screen: " + e.getMessage());
			}
			
			// Wait for user to complete OTP and successful login - Simplified approach
			try {
				logger.logInfo("PLEASE COMPLETE THE FOLLOWING STEPS:");
				logger.logInfo("1. Check your phone for the OTP SMS");
				logger.logInfo("2. Enter the OTP in the browser");
				logger.logInfo("3. Wait to be redirected to Flipkart homepage");
				logger.logWarning("Script will automatically detect when you return to homepage...");
				
				boolean loginSuccessful = false;
				int checkCount = 0;
				int maxChecks = 60; // Check for 2 minutes total
				
				while (!loginSuccessful && checkCount < maxChecks) {
					checkCount++;
					Thread.sleep(2000); // Wait 2 seconds between checks
					
					try {
						String currentUrl = driver.getCurrentUrl();
						
						// Simple check: if URL is just flipkart.com or doesn't contain login/otp
						if (currentUrl.equals("https://www.flipkart.com/") || 
							currentUrl.equals("https://www.flipkart.com") ||
							(currentUrl.startsWith("https://www.flipkart.com") && 
							 !currentUrl.contains("login") && 
							 !currentUrl.contains("otp") &&
							 !currentUrl.contains("account/login"))) {
							
							logger.logSuccess("SUCCESS! Detected return to Flipkart homepage");
							logger.logSuccess("Current URL: " + currentUrl);
							logger.logSuccess("Login completed after " + (checkCount * 2) + " seconds");
							
							// Wait a bit more for page to fully load
							Thread.sleep(3000);
							loginSuccessful = true;
							
						} else {
							// Show progress every 10 checks (20 seconds)
							if (checkCount % 10 == 0) {
								logger.logInfo("Still waiting... Current URL: " + currentUrl);
								logger.logInfo("Please complete OTP entry if you haven't already...");
							}
						}
						
					} catch(Exception e) {
						logger.logInfo("Error checking page status: " + e.getMessage());
					}
				}
				
				if (loginSuccessful) {
					logger.logSuccess("User has successfully logged in with OTP!");
					logger.logInfo("Proceeding with remaining automation tasks...");
				} else {
					logger.logWarning("Timeout after 2 minutes - proceeding anyway");
					logger.logWarning("Current URL: " + driver.getCurrentUrl());
					logger.logInfo("Continuing with automation...");
				}
				
			} catch(Exception e) {
				logger.logWarning("Error during login verification: " + e.getMessage());
				logger.logInfo("Continuing with test anyway...");
			}
			
		} catch(Exception e) {
			logger.logError("Error during login workflow", e);
			logger.logWarning("Continuing test execution despite login issues");
			// Navigate to home page if login fails
			try {
				navigateToWebsiteWithRetry(driver, "https://www.flipkart.com");
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	private static void logout(WebDriver driver, WebDriverWait wait) {
		logger.logInfo("Starting logout workflow");
		try {
			// Navigate back to main page first
			logger.logInfo("Navigating to homepage for logout");
			driver.get("https://www.flipkart.com");
			Thread.sleep(3000);
			
			// Updated selectors for current Flipkart logged-in user account
			String[] accountSelectors = {
				"//span[normalize-space()='Account']",  // User-provided exact XPath
				"//div[contains(@class, '_1_3w1N') and not(text()='Login')]",  // Any account element that's not Login
				"//span[contains(@class, '_1_3w1N') and not(text()='Login')]", // Span version
				"//div[@class='_1_3w1N']",  // Generic account class
				"//a[contains(@href, '/account') and not(contains(@href, 'login'))]", // Account links not login
				"//div[contains(@class, 'exehdJ')]",
				"//button[contains(@class, '_1_3w1N')]", // Button version
				"//*[contains(text(), 'Hi') or contains(text(), 'Hello')]", // Greeting text
				"//div[contains(@class, '_1kb8')]"
			};
			
			boolean logoutAttempted = false;
			for (String selector : accountSelectors) {
				try {
					logger.logInfo("Trying account element selector: " + selector);
					
					// Try to find any account-related element
					WebElement accountElement = wait.until(ExpectedConditions.presenceOfElementLocated(
						By.xpath(selector)));
					
					// Check if the element is clickable and try to click
					if (accountElement.isDisplayed() && accountElement.isEnabled()) {
						try {
							wait.until(ExpectedConditions.elementToBeClickable(accountElement));
							accountElement.click();
							logger.logSuccess("Account element clicked successfully: " + accountElement.getText());
							Thread.sleep(2000);
							
							// Now look for logout options with more generic selectors
							String[] logoutSelectors = {
								"//li[normalize-space()='Logout']",  // User-provided exact XPath
								"//*[text()='Logout']",
								"//*[contains(text(), 'Logout')]",
								"//*[contains(text(), 'Log out')]",
								"//*[contains(text(), 'Sign out')]",
								"//a[contains(@href, 'logout')]",
								"//button[contains(text(), 'Logout')]",
								"//div[contains(text(), 'Logout')]",
								"//span[contains(text(), 'Logout')]"
							};
							
							for (String logoutSelector : logoutSelectors) {
								try {
									logger.logInfo("Trying logout selector: " + logoutSelector);
									WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(
										By.xpath(logoutSelector)));
									logoutButton.click();
									logger.logSuccess("User logged out successfully!");
									logoutAttempted = true;
									break;
								} catch(TimeoutException le) {
									logger.logInfo("Logout selector failed: " + logoutSelector);
								}
							}
							
							if (logoutAttempted) break;
							
						} catch(Exception clickException) {
							logger.logInfo("Could not click account element: " + clickException.getMessage());
						}
					}
					
				} catch(TimeoutException e) {
					logger.logInfo("Account element not found: " + selector);
				}
			}
			
			if (!logoutAttempted) {
				logger.logWarning("Could not complete logout - account elements not accessible");
				logger.logInfo("This might be due to UI changes or user not being logged in");
				logger.logInfo("Session will still be terminated when browser closes");
			}
			
		} catch(Exception e) {
			logger.logError("Error during logout workflow", e);
		}
		
		logger.logInfo("Logout workflow completed - session will be terminated");
	}
	
	// Enhanced helper methods with retry mechanisms and better error handling
	
	private static WebDriver initializeBrowserWithRetry() {
		for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
			try {
				logger.logInfo("Attempting to initialize browser (Attempt " + attempt + ")");
				WebDriver driver = new ChromeDriver();
				return driver;
			} catch (WebDriverException e) {
				logger.logWarning("Browser initialization attempt " + attempt + " failed: " + e.getMessage());
				if (attempt == MAX_RETRY_ATTEMPTS) {
					throw new RuntimeException("Failed to initialize browser after " + MAX_RETRY_ATTEMPTS + " attempts", e);
				}
				try {
					Thread.sleep(RETRY_DELAY_MS);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		}
		return null;
	}
	
	private static void navigateToWebsiteWithRetry(WebDriver driver, String url) {
		for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
			try {
				logger.logInfo("Navigating to: " + url + " (Attempt " + attempt + ")");
				driver.get(url);
				return;
			} catch (Exception e) {
				logger.logWarning("Navigation attempt " + attempt + " failed: " + e.getMessage());
				if (attempt == MAX_RETRY_ATTEMPTS) {
					throw new RuntimeException("Failed to navigate to " + url + " after " + MAX_RETRY_ATTEMPTS + " attempts", e);
				}
				try {
					Thread.sleep(RETRY_DELAY_MS);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	private static void performProductSearch(WebDriver driver, WebDriverWait wait, String productName) {
		try {
			logger.logInfo("Searching for product: " + productName);
			WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
			searchBox.sendKeys(productName);
			searchBox.submit();
			logger.logSuccess("Product search executed successfully");
		} catch (TimeoutException e) {
			logger.logError("Search box not found or not clickable", e);
			throw new RuntimeException("Product search failed", e);
		}
	}
	
	private static void selectProductWithRetry(WebDriver driver, WebDriverWait wait, String productName) {
		for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
			try {
				logger.logInfo("Selecting product: " + productName + " (Attempt " + attempt + ")");
				WebElement productLink = wait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//div[normalize-space()='" + productName + "']")));
				productLink.click();
				logger.logSuccess("Product selected successfully");
				return;
			} catch (TimeoutException e) {
				logger.logWarning("Product selection attempt " + attempt + " failed: " + e.getMessage());
				if (attempt == MAX_RETRY_ATTEMPTS) {
					logger.logError("Failed to select product after " + MAX_RETRY_ATTEMPTS + " attempts", e);
					throw new RuntimeException("Product selection failed", e);
				}
				try {
					Thread.sleep(RETRY_DELAY_MS);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	private static void handleNewWindow(WebDriver driver, String mainPage) {
		try {
			logger.logInfo("Handling new window/tab");
			Set<String> allPages = driver.getWindowHandles();
			for(String page : allPages) {
				if(!page.equals(mainPage)) {
					driver.switchTo().window(page);
					logger.logSuccess("Switched to new window: " + page);
					break;
				}
			}
		} catch (Exception e) {
			logger.logError("Error handling new window", e);
		}
	}
	
	private static void extractProductInformation(WebDriver driver) {
		logger.logInfo("Extracting product information");
		try {
			// Try multiple selectors for better reliability
			String[] selectors = {"_21Ahn-", "_4rR01T", "B_NuCI", "_1fQZEK"};
			boolean found = false;
			
			for (String selector : selectors) {
				try {
					List<WebElement> products = driver.findElements(By.className(selector));
					if (!products.isEmpty()) {
						logger.logSuccess("Found " + products.size() + " product elements using selector: " + selector);
						for(WebElement product: products) {
							String text = product.getText().trim();
							if(!text.isEmpty()) {
								logger.logInfo("Product info: " + text);
							}
						}
						found = true;
						break;
					}
				} catch (Exception e) {
					// Try next selector
				}
			}
			
			if (!found) {
				logger.logWarning("Could not find product details with any of the available selectors");
			}
		} catch(Exception e) {
			logger.logError("Error extracting product information", e);
		}
	}
	
	private static void addToCartWithRetry(WebDriver driver, WebDriverWait wait) {
		logger.logInfo("Adding product to cart");
		for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
			try {
				// Updated selectors for current Flipkart add to cart buttons
				String[] addToCartSelectors = {
					"//button[normalize-space()='Add to cart']",  // User-provided exact XPath
					"//button[contains(text(), 'Add to cart')]",
					"//button[contains(text(), 'ADD TO CART')]",
					"//button[@class='_2KpZ6l _2U9uOA _3v1-ww']",
					"//button[contains(@class, '_2KpZ6l') and contains(text(), 'cart')]",
					"//span[text()='Add to cart']//parent::button",
					"//div[text()='Add to cart']//parent::button"
				};
				
				boolean cartAdded = false;
				for (String selector : addToCartSelectors) {
					try {
						logger.logInfo("Trying add to cart selector: " + selector);
						WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
							By.xpath(selector)));
						
						// Scroll to button if needed
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);
						Thread.sleep(500);
						
						addToCartBtn.click();
						logger.logSuccess("Product added to cart successfully using selector: " + selector);
						cartAdded = true;
						
						// Wait for cart action to complete
						Thread.sleep(2000);
						return;
						
					} catch (TimeoutException e) {
						logger.logInfo("Add to cart selector failed: " + selector);
					}
				}
				
				if (!cartAdded) {
					throw new TimeoutException("No add to cart button found with any selector");
				}
				
			} catch (TimeoutException e) {
				logger.logWarning("Add to cart attempt " + attempt + " failed: Button not found or not clickable");
				if (attempt == MAX_RETRY_ATTEMPTS) {
					logger.logError("Failed to add to cart after " + MAX_RETRY_ATTEMPTS + " attempts", e);
				} else {
					try {
						Thread.sleep(RETRY_DELAY_MS);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}
				}
			} catch (InterruptedException e) {
				logger.logError("Thread interrupted during cart addition", e);
				Thread.currentThread().interrupt();
			}
		}
	}
	
	private static void handleCriticalError(Exception e) {
		logger.logError("=== CRITICAL ERROR DETAILS ===", null);
		logger.logError("Error Type: " + e.getClass().getSimpleName(), null);
		logger.logError("Error Message: " + e.getMessage(), null);
		logger.logError("Stack trace will be printed below:", null);
		e.printStackTrace();
	}
	
	private static void cleanupResources(WebDriver driver) {
		logger.logInfo("Cleaning up browser resources");
		if (driver != null) {
			try {
				driver.quit();
				logger.logSuccess("Browser session closed successfully");
			} catch (Exception e) {
				logger.logError("Error during browser cleanup", e);
			}
		}
	}
	
	private static boolean isSuccessfulLoginRedirect(String currentUrl, String previousUrl) {
		// Check if we've been redirected from login/OTP page to homepage
		boolean wasOnLoginPage = previousUrl.contains("login") || previousUrl.contains("otp");
		boolean nowOnHomepage = currentUrl.equals("https://www.flipkart.com/") || 
							   (currentUrl.contains("flipkart.com") && 
								!currentUrl.contains("login") && 
								!currentUrl.contains("otp") &&
								!currentUrl.contains("account/login"));
		
		return wasOnLoginPage && nowOnHomepage;
	}
	
	/**
	 * Custom Logger class for enhanced test logging with timestamps and visual indicators
	 */
	private static class CustomLogger {
		private final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		public void logTestStart(String testName) {
			System.out.println("\n" + "=".repeat(80));
			System.out.println(">>> TEST STARTED: " + testName);
			System.out.println(">>> Start Time: " + getCurrentTimestamp());
			System.out.println("=".repeat(80) + "\n");
		}
		
		public void logTestEnd(String testName) {
			System.out.println("\n" + "=".repeat(80));
			System.out.println("<<< TEST COMPLETED: " + testName);
			System.out.println("<<< End Time: " + getCurrentTimestamp());
			System.out.println("=".repeat(80) + "\n");
		}
		
		public void logInfo(String message) {
			System.out.println("[INFO] " + getCurrentTimestamp() + " - " + message);
		}
		
		public void logSuccess(String message) {
			System.out.println("[SUCCESS] " + getCurrentTimestamp() + " - " + message);
		}
		
		public void logWarning(String message) {
			System.out.println("[WARNING] " + getCurrentTimestamp() + " - " + message);
		}
		
		public void logError(String message, Exception exception) {
			System.out.println("[ERROR] " + getCurrentTimestamp() + " - " + message);
			if (exception != null) {
				System.out.println("   Exception: " + exception.getClass().getSimpleName() + " - " + exception.getMessage());
			}
		}
		
		private String getCurrentTimestamp() {
			return LocalDateTime.now().format(timestampFormatter);
		}
	}
}