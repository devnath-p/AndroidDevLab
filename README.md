# Experiment 5 – Android Notifications

## Overview
This project demonstrates how to create and display notifications in an Android application using **Notification Channels**, **NotificationCompat.Builder**, and **NotificationManager**.

## Concept & Technology

### Android Notifications
A **Notification** is a message displayed by Android outside the application's normal UI. It allows an application to provide information or updates to the user.

- **Notification Channel:** Used to categorize and manage notifications on Android 8.0 (API level 26) and above.
- **Notification Builder:** Used to configure the notification title, message, icon, and other properties.
- **Notification Manager:** Responsible for delivering the notification to the Android system.

### Components Used:
- **Kotlin:** Programming language for application logic.
- **XML Layouts:** Defining the UI structure.
- **Android Notification API:** Creating and displaying notifications.
- **Button:** Used to trigger the notification.

## Scenario
The application implements a simple **Notification Trigger**:
1. **Main Screen (`MainActivity`):** The user is presented with a button to send a notification.
2. **Notification Channel:** A notification channel is created with a channel ID and name for managing the application's notifications.
3. **Notification Creation:** When the button is clicked, `NotificationCompat.Builder` is used to create a notification with a title, message, and icon.
4. **Notification Display:** `NotificationManager` sends the notification, which appears in the device's notification panel.

## Folder and File Structure
```text
Exp5/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/exp5/
│   │   │   │   └── MainActivity.kt          # Main activity and notification logic
│   │   │   ├── res/
│   │   │   │   └── layout/
│   │   │   │       └── activity_main.xml    # UI containing notification button
```

## Screenshots

![Screenshot 1](./screenshots/Screenshot2.png)

![Screenshot 2](./screenshots/Screenshot1.png)

## Result
The application successfully creates a notification channel and displays a notification with a title, message, and icon when the button on the main screen is clicked, confirming that the Android Notification API works as expected.