
# Experiment 10 – Data Sharing Using Contacts Content Provider

## Overview

This project demonstrates how to access and display contact information in an Android application using the **Contacts Content Provider**. The app requests permission from the user, retrieves contact names and phone numbers through Android's `ContentResolver`, and displays the results in a contact list.

## Concept & Technology

### Contacts Content Provider

The Contacts Content Provider makes contact information available to applications through a standard Android interface. Apps can query this data without directly accessing the underlying contacts database.

### ContentResolver

`ContentResolver` is used to communicate with the Contacts Content Provider and query contact records.

### Runtime Permission

The app requests the `READ_CONTACTS` permission at runtime before reading contacts. If permission is denied, the app does not load the contact data.

### Contact List UI

A `ListView` displays the retrieved contacts. Each row shows a contact's name, phone number, and a circular avatar containing the first letter of the name.

### Components Used

- **Kotlin:** Programming language for application logic.
- **XML Layouts:** Define the app's user interface.
- **ContentResolver:** Queries the Contacts Content Provider.
- **ContactsContract:** Provides the contacts content URI and column definitions.
- **Cursor:** Reads the results returned by the query.
- **Runtime Permissions:** Requests access to contacts using `READ_CONTACTS`.
- **ListView:** Displays the contact entries.
- **Custom ArrayAdapter:** Binds contact data to the list rows.
- **Drawable Resource:** Provides the circular contact avatar background.

## Scenario

The application demonstrates provider-based access to contact data:

1. The main screen displays the Contact Sharing heading, a **Load Contacts** button, a contact count, and the contact list area.
2. When the user taps **Load Contacts**, the app checks whether `READ_CONTACTS` permission has been granted.
3. If permission has not been granted, Android displays a permission request.
4. After permission is granted, the app queries `ContactsContract.CommonDataKinds.Phone.CONTENT_URI` using `ContentResolver`.
5. The returned contact names and phone numbers are read using a `Cursor`.
6. The app displays each contact in a custom list row and updates the contact count.
7. If no contacts with phone numbers are found, the app displays an empty-state message.

## Folder and File Structure

```text
Exp10/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/exp10/
│           │   └── MainActivity.kt
│           ├── res/
│           │   ├── drawable/
│           │   │   └── contact_avatar.xml
│           │   ├── layout/
│           │   │   ├── activity_main.xml
│           │   │   └── item_contact.xml
│           │   └── values/
│           │       └── themes.xml
│           └── AndroidManifest.xml
├── build.gradle.kts
└── settings.gradle.kts
```

## Permission

The application declares the following permission in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.READ_CONTACTS" />
```

The permission is also requested at runtime before the app reads contacts.

## Output

![Application Output](screenshots/Screenshot1.png)
![Application Output](screenshots/Screenshot2.png)

## Result

The Android application was implemented using Kotlin and XML. It demonstrates data access through the Contacts Content Provider by requesting runtime permission, retrieving contact names and phone numbers with `ContentResolver` and `ContactsContract`, and displaying the results in a `ListView`.

**Privacy note:** The app reads contacts locally on the device. It does not upload or send contact information to another app or server.
