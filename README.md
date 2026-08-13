# Experiment 4 – Linking Activities Using Intents

## Overview
This project demonstrates how to link multiple Activities in an Android application using **Intents** and how to pass data between them.

## Concept & Technology
### Intents
An **Intent** is a fundamental messaging object in Android used to request an action from another app component.
- **Explicit Intents:** Used to start a specific component (like an Activity) within your own app. In this experiment, `MainActivity` explicitly starts `WelcomeActivity`.
- **Data Passing:** Data is passed between activities using "Extras" (key-value pairs) attached to the Intent object.

### Components Used:
- **Kotlin:** Programming language for logic.
- **XML Layouts:** Defining the UI structure.
- **Android Material Design:** For UI components like `Button` and `TextInputEditText`.

## Scenario
The application implements a simple **Login Flow**:
1. **Login Screen (`MainActivity`):** The user enters their name into a text field and clicks "LOGIN".
2. **Logic:** The app checks if the name is not empty. If valid, it creates an explicit Intent, attaches the username as an extra using `putExtra()`, and starts `WelcomeActivity`.
3. **Welcome Screen (`WelcomeActivity`):** The second activity retrieves the username using `getStringExtra()` and displays a personalized welcome message.

## Folder and File Structure
```text
Exp4/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/exp4/
│   │   │   │   ├── MainActivity.kt      # Main entry point, handles login logic
│   │   │   │   └── WelcomActivity.kt    # Displays personalized welcome message
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml    # UI for Login screen
│   │   │   │   │   └── activity_welcome.xml # UI for Welcome screen
│   │   │   │   └── values/
│   │   │   │       └── strings.xml          # App string resources
│   │   │   └── AndroidManifest.xml      # Declares app components and entry point
│   ├── build.gradle.kts                 # App-level build configuration
├── build.gradle.kts                     # Project-level build configuration
└── settings.gradle.kts                  # Project settings
```

## Output

![Application Output](screenshots/Screenshot1.png)
![Application Output](screenshots/Screenshot2.png)