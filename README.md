# Experiment 11 – Google Maps Integration Using Device Location

## Overview

This project demonstrates how to retrieve the geographical location of an Android device and display it on Google Maps within an Android application. The application uses the **Fused Location Provider** to obtain the device's last known location and displays the corresponding map using an Android `WebView`.

The application is developed using Kotlin and demonstrates location permissions, location retrieval, and the integration of web-based maps into a native Android application.

## Concept & Technology

### Fused Location Provider

The Fused Location Provider, provided by Google Play Services, is used to retrieve the device's last known geographical location. It provides the latitude and longitude required to display the location on a map.

### Runtime Location Permissions

Android requires applications to request location permissions before accessing the device's location. The application uses fine and coarse location permissions to obtain precise or approximate location access, depending on the permission granted by the user.

### Google Maps Integration

Google Maps is embedded into the application using an HTML iframe. The latitude and longitude obtained from the location provider are passed to the map URL to display the corresponding geographical area.

### WebView

The Android `WebView` component displays the HTML content containing the embedded Google Maps interface. This allows the map to be viewed directly within the application without opening a separate browser.

### Components Used

- **Kotlin:** Programming language used for application logic.
- **XML Layouts:** Define the application's user interface.
- **FusedLocationProviderClient:** Retrieves the device's last known location.
- **LocationServices:** Provides access to the Fused Location Provider.
- **Runtime Permissions:** Handles location access permissions.
- **WebView:** Displays the embedded Google Maps interface.
- **HTML iframe:** Embeds Google Maps using the supplied coordinates.
- **Google Play Services Location:** Provides location-related functionality.

## Scenario

The application demonstrates the integration of device location services with Google Maps:

1. The application initializes the Fused Location Provider client.
2. The application checks whether the required location permission has been granted.
3. If permission has not been granted, the user is prompted to allow location access.
4. After permission is granted, the application requests the device's last known location.
5. The latitude and longitude are retrieved from the returned location object.
6. The coordinates are used to construct an HTML page containing an embedded Google Maps iframe.
7. The HTML content is loaded into the WebView to display the corresponding map.
8. If the location is unavailable or a handled permission exception occurs, the application uses a predefined fallback location.

## Folder and File Structure

```text
TestApp/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── <application_package>/
│           │       └── MainActivity.kt
│           ├── res/
│           │   └── layout/
│           │       └── activity_main.xml
│           └── AndroidManifest.xml
├── build.gradle.kts
└── settings.gradle.kts
```

*Note: This is an illustrative folder structure. The actual package name and file organization may differ in the project.*

## Permissions

The application requires the following permissions in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<uses-permission android:name="android.permission.INTERNET" />
```

The location permissions allow the application to request access to the device's geographical location. Internet permission allows the embedded Google Maps interface to load online map content.

Location access is also subject to Android's runtime permission requirements.

## UI Design

The application uses a simple, map-focused interface.

### Main Map Screen

- Displays Google Maps inside the application using a WebView.
- Uses the retrieved latitude and longitude to identify the geographical area.
- Allows the user to view roads and surrounding locations.
- Provides a straightforward interface focused on displaying the map.

### Location Permission Screen

- Requests location access when required.
- Allows the user to grant precise or approximate location permission.
- Ensures that location access follows Android's permission system.

## Output

The application displays an embedded Google Maps interface using the coordinates retrieved from the device.

The output demonstrates:

- Location permission handling.
- Retrieval of latitude and longitude.
- Display of Google Maps within the application.
- Fallback handling when location data is unavailable.


![Application Output](screenshots/Screenshot1.png)
![Application Output](screenshots/Screenshot2.png)

## Result

The Android application demonstrates the retrieval of the device's last known geographical coordinates using the Fused Location Provider and the display of the corresponding Google Maps interface through a WebView.

Thus, the integration of Android location services and Google Maps in a mobile application was demonstrated.
