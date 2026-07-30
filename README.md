# Activity Lifecycle Demonstration (EXp2)

An Android application designed to help developers understand and visualize the **Activity Lifecycle** through real-time logging and UI feedback.

---

## 🚀 Features

- **Full Lifecycle Implementation**: Overrides all major lifecycle methods (`onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onRestart`, `onDestroy`).
- **Visual Feedback**: Real-time `Toast` messages for each state change.
- **Detailed Logging**: Debug logs in Logcat for precise tracking of the execution flow.
- **Multi-Activity Interaction**: Includes a second activity to demonstrate how backgrounding one activity affects the lifecycle of another.

---

## 📂 Project Structure

- `app/src/main/java/com/example/exp2/`
    - `MainActivity.kt`: The main entry point. Logs its own lifecycle and handles navigation to the second activity.
    - `SecondActivity.kt`: A secondary activity to demonstrate transition-based lifecycle changes.
- `app/src/main/res/layout/`
    - `activity_main.xml`: UI for the main activity with a navigation button.
    - `activity_second.xml`: UI for the second activity with a back button.
- `app/src/main/AndroidManifest.xml`: Activity registrations.

---

## 🛠️ How to Use

1. **Build and Run**: Deploy the app to an Android emulator or a physical device.
2. **Open Logcat**: 
    - In Android Studio, open the **Logcat** tool window.
    - Filter by `Lifecycle` to isolate the relevant messages.
3. **Observe Transitions**:
    - **App Launch**: Notice `onCreate` -> `onStart` -> `onResume`.
    - **Navigate to Second Activity**: Observe `MainActivity` moving to `onPause` -> `onStop` while `SecondActivity` starts.
    - **Press Back**: See `SecondActivity` being destroyed (`onDestroy`) while `MainActivity` restarts.
    - **Home Button**: Watch both activities move to `onStop`.

---

## 📝 Lifecycle Methods Explained

This app implements the following methods to demonstrate their specific roles:

| Method | When it's called |
| :--- | :--- |
| `onCreate()` | When the activity is first created. Initialization happens here. |
| `onStart()` | When the activity becomes visible to the user. |
| `onResume()` | When the activity starts interacting with the user (at the top of the stack). |
| `onPause()` | When the activity is partially obscured (e.g., by a dialog) or about to be stopped. |
| `onStop()` | When the activity is no longer visible to the user. |
| `onRestart()` | When the activity is returning from a stopped state. |
| `onDestroy()` | When the activity is being finished or reclaimed by the system. |

---

## 💻 Code Snippet (MainActivity.kt)

```kotlin
override fun onResume() {
    super.onResume()
    showLogAndToast("onResume")
}

private fun showLogAndToast(methodName: String) {
    Log.d("MainActivityLifecycle", "$methodName called")
    Toast.makeText(this, "MainActivity: $methodName", Toast.LENGTH_SHORT).show()
}
```

---

## ⚙️ Configuration 

- **Compile SDK**: 37
- **Min SDK**: 24
- **Language**: Kotlin
- **UI Toolkit**: XML Layouts / AppCompat

## Screenshots
![Screenshot 2026-07-29 225657.png](images/Screenshot%202026-07-29%20225657.png)
![Screenshot 2026-07-29 225715.png](images/Screenshot%202026-07-29%20225715.png)

##videos
[Screen Recording 2026-07-30 104017.mp4](videos/Screen%20Recording%202026-07-30%20104017.mp4)