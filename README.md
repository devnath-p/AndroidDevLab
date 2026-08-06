# Experiment 3 – Fragments in Android

## Objective

Develop an Android application using Fragments to create a flexible user interface. The application consists of two fragments where the first fragment displays a list of courses and the second fragment displays details of the selected course. The experiment also demonstrates Android Studio debugging using normal and conditional breakpoints.

---

## Concept

### Fragments

Fragments are reusable UI components that represent a portion of an Activity. They help create flexible interfaces that can adapt to different screen sizes.

### Fragment Communication

The application uses an interface (`OnItemSelectedListener`) to communicate between `ListFragment` and `MainActivity`. The selected course is passed to `DetailFragment` using a `Bundle`.

### Debugging

Android Studio Debugger was used to:

- Place a normal breakpoint in `DetailFragment`
- Place a conditional breakpoint in `ListFragment`
- Inspect local variables
- Observe the Fragment lifecycle
- Analyze the call stack

---

## Scenario

A simple **Course Explorer** application was developed.

The first screen displays a list of available courses.

- Android
- Java
- Python
- Flutter

When a user selects a course, its description is displayed in the second fragment.

---

## Technologies Used

- Kotlin
- Android Studio
- XML Layout
- Fragments
- ListView
- ArrayAdapter

---

## Project Structure

```
Exp3
│
├── app
│   ├── src
│   ├── res
│   │     ├── layout
│   │     │      activity_main.xml
│   │     │      fragment_list.xml
│   │     │      fragment_detail.xml
│   │     └── values
│   │
│   └── AndroidManifest.xml
│
├── screenshots
│
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## Output

### Course List

![Output](<screenshots/Screenshot 1.png>)

### Course Details

![Output](<screenshots/Screenshot 2.png>)

---

## Debugging

### Normal Breakpoint

Placed inside `DetailFragment`.

Observed:

- Fragment lifecycle
- Local variables
- Call stack

### Conditional Breakpoint

Condition:

```kotlin
selectedCourse == "Android"
```

Execution paused only when **Android** was selected.

---

## Result

The Android application using Fragments was successfully developed and tested. The application displayed course details based on user selection, and Android Studio debugging tools were successfully used to inspect the execution flow.