# TvHelper

TvHelper is an Android library designed to simplify the debugging and control of Android TV applications. It creates a local web server on the TV device, allowing you to control the app, input text, and trigger actions from a web browser on your phone or computer.

## Features

*   **Remote Control**: Trigger custom actions in your app from a web interface.
*   **Text Input**: Send text to your TV app (great for avoiding tedious remote typing).
*   **QR Code Connection**: Easy connection via scanning a QR code displayed on the TV.
*   **Toggle Key**: Bind a specific remote key (e.g., Menu) to show/hide the connection dialog.
*   **Auto Port Selection**: Automatically finds an available port if the default one is occupied.

## Installation

### Step 1: Add JitPack repository

Add JitPack to your project's `settings.gradle` (or root `build.gradle` for older projects):

**For Gradle 7.0+ (settings.gradle or settings.gradle.kts):**

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }  // Add this line
    }
}
```

**For older Gradle versions (root build.gradle):**

```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }  // Add this line
    }
}
```

### Step 2: Add the dependency

Add TvHelper to your app's `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.devxiny:tv-helper:1.0.1'
}
```

## Usage

### 1. Initialize and Start Server

Start the server in your `Application.onCreate` or main `Activity.onCreate`.

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Configure actions and inputs
        val config = TvControlConfig()
        
        // Add a button to the web interface
        config.addAction("Test Toast")
        
        // Add a text input field
        config.addInput("Search Query")

        // Start the server
        TvHelper.startServer(this, 8989, config) { action, data ->
            // Handle callbacks on the main thread
            when (action) {
                "Test Toast" -> Toast.makeText(this, "Hello from Web!", Toast.LENGTH_SHORT).show()
                "Search Query" -> {
                    val query = data?.get("value")
                    // Handle search...
                }
            }
        }
    }
}
```

### 2. Show Connection Dialog

You can show the QR code dialog programmatically:

```kotlin
TvHelper.showDialog(this) // 'this' is your FragmentActivity
```

### 3. Bind a Toggle Key (Optional)

You can bind a key (e.g., the Menu key) to toggle the dialog visibility.

**Step 1: Set the key**

```kotlin
TvHelper.setToggleKey(KeyEvent.KEYCODE_MENU)
```

**Step 2: Intercept Key Events in Activity**

In your `BaseActivity` or target `Activity`, override `dispatchKeyEvent`:

```kotlin
override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    // Let TvHelper handle the toggle key
    if (TvHelper.handleKeyEvent(this, event)) {
        return true
    }
    return super.dispatchKeyEvent(event)
}
```

Now, pressing the Menu key will show the dialog, and pressing it again (or Back) will hide it.

### 4. Stop Server

Stop the server when your app exits or is destroyed.

```kotlin
TvHelper.stopServer()
```

## License

MIT License
