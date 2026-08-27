# Experiment 6 – Registration Page Using Basic Android Views

## Overview

This project demonstrates the use of basic Android UI components to create an interactive **Registration Page**.

The application allows the user to enter personal information, select a gender, accept the Terms & Conditions, enable or disable notifications, and submit the registration form.

## Concept & Technology

### Android UI Views

Android provides various UI components, called **Views**, that allow users to interact with an application.

### Components Used:

- **TextView:** Used to display the title, labels and supporting text.
- **EditText:** Used to accept the user's name, email and password.
- **Button:** Used to submit the registration form.
- **ImageButton:** Used as a profile image button.
- **CheckBox:** Used for accepting the Terms & Conditions.
- **ToggleButton:** Used to enable or disable notifications.
- **RadioButton:** Used to select the user's gender.
- **RadioGroup:** Used to group the gender RadioButtons.
- **Kotlin:** Programming language used for application logic.
- **XML Layouts:** Used to define the structure and appearance of the UI.

## Scenario

The application implements a simple **User Registration Flow**:

1. **Registration Screen (`MainActivity`):** The user enters their name, email and password into the respective text fields.
2. **Gender Selection:** The user selects one gender option using the RadioButtons grouped inside a RadioGroup.
3. **Terms & Conditions:** The user must select the CheckBox to accept the Terms & Conditions.
4. **Notifications:** The user can use the ToggleButton to enable or disable notifications.
5. **Registration:** The user clicks the "CREATE ACCOUNT" Button to submit the form.
6. **Validation:** The application checks that all required fields are filled, a gender is selected and the Terms & Conditions are accepted.
7. **Result:** If all validations are successful, an AlertDialog displays a registration success message along with the user's name, email and notification status.

## Folder and File Structure

```text
Exp6/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/exp6/
│   │   │   │   └── MainActivity.kt       # Main activity and registration logic
│   │   │   ├── res/
│   │   │   │   └── layout/
│   │   │   │       └── activity_main.xml # Registration page UI
│   │   │   └── AndroidManifest.xml       # Application configuration
│   ├── build.gradle.kts                   # App-level build configuration
├── build.gradle.kts                       # Project-level build configuration
└── settings.gradle.kts                    # Project settings



```

## Output

![Registration Page](screenshots/Screenshot1.png)

## Result

- The Android registration application was successfully developed using basic UI components.
- The application accepts user details through EditText fields.
- Gender selection is implemented using RadioButton and RadioGroup.
- Terms & Conditions are handled using CheckBox.
- Notification preferences are controlled using ToggleButton.
- The Register button validates the entered information.
- A successful registration message is displayed using an AlertDialog.