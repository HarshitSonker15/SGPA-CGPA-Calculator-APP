AKTU CGPA Calculator - Jetpack Compose
A simple, modern, and reactive CGPA/SGPA calculator for Android, built entirely with Kotlin and Jetpack Compose. This app is designed to help students of Dr. A.P.J. Abdul Kalam Technical University (AKTU) and other universities with a similar 10-point grading system to easily calculate their academic performance.

✨ Features
Dynamic Semesters & Subjects: Add or remove any number of semesters and subjects on the fly.

Real-time Calculation: SGPA and CGPA are updated instantly as you input your grades and credits.

Editable Grade Scale: The grade-to-point mapping (e.g., O=10, A+=9) is fully editable. You can add, remove, or modify grades to match your specific university ordinance.

Percentage Conversion: Optionally calculate the equivalent percentage from your final CGPA using the standard formula (CGPA - 0.75) * 10.

Clean & Modern UI: A single-screen, intuitive interface built with Material 3 components.

State-Aware & Performant: Built using modern Android development principles and optimized for performance with Jetpack Compose's state management.

📱 Screenshots
(A visual representation of the app's user interface)

+---------------------------------------------------+
|                                                   |
|  AKTU SGPA / CGPA Calculator                      |
|  -----------------------------                    |
|                                                   |
|  ▼ Grade Scale (Editable)                         |
|    [ Grade: O      ] [ Points: 10.0   ] [ Del ]   |
|    [ Grade: A+     ] [ Points: 9.0    ] [ Del ]   |
|    [ Add Grade Mapping ]                          |
|                                                   |
|  ▼ Semesters                                      |
|  +-----------------------------------------------+
|  | [ Semester 1                       ] [Remove] |
|  |                                               |
|  |   [ Grade: A+ ] [ Credits: 4 ]       [ Del ]  |
|  |   [ Grade: O  ] [ Credits: 3 ]       [ Del ]  |
|  |                                   [Add Subject]|
|  |                                               |
|  | SGPA: 9.43 | Credits: 7.0                     |
|  +-----------------------------------------------+
|                                                   |
|  [ Add Semester ]                                 |
|                                                   |
|  [✓] Show percentage (CGPA - 0.75) x 10           |
|                                                   |
|  Overall CGPA: 9.43                               |
|  Equivalent Percentage: 86.80%                    |
|                                                   |
+---------------------------------------------------+

🛠 Tech Stack
Language: Kotlin

UI Toolkit: Jetpack Compose

Architecture: State-hoisting, Unidirectional Data Flow (UDF) principles

UI Design: Material 3

🚀 How to Use
Launch the App: Open the application on your device.

Verify Grade Scale: Check if the default 10-point grade scale matches your university's system. If not, tap on the grade or point values to edit them. You can also add new grades or remove existing ones.

Add Semesters: Use the "Add Semester" button to create entries for each of your academic semesters.

Enter Subjects: For each semester, use the "Add Subject" button to create rows for your subjects.

Input Data: Enter the grade you received (e.g., "A+") and the credits for each subject.

View Results: The SGPA for each semester and your overall CGPA are calculated and displayed in real-time at the bottom of the screen.

📂 Code Overview
The project is contained within a single file, demonstrating the conciseness and power of Jetpack Compose for building small to medium-sized applications.

MainActivity.kt: The entry point of the app, containing all composable functions and business logic.

Data Classes:

SubjectEntry: Represents a single subject with its ID, name, grade, and credits.

Semester: Represents a semester, containing a list of SubjectEntry items.

Core Composables:

@Composable fun AktuCgpaApp(): The main composable function that holds the state and builds the entire UI.

@Composable fun SemesterCard(): A modular component for displaying and managing a single semester's data.

@Composable fun SubjectRow(): A reusable component for a single subject's input fields.

State Management:

The app uses remember { mutableStateOf(...) } to hold the state for the list of semesters and the grade map.

Calculations are wrapped in a remember(keys) { derivedStateOf { ... } } block to ensure they are only re-computed when the underlying data (semesters or grade map) changes, optimizing performance.

🔧 How to Build from Source
Clone the repository:

git clone [https://github.com/your-username/aktu-cgpa-calculator.git](https://github.com/your-username/aktu-cgpa-calculator.git)

Open in Android Studio: Open the cloned project in the latest version of Android Studio (Hedgehog or newer is recommended).

Sync Gradle: Let Android Studio download all the required dependencies.

Run: Build and run the app on an Android emulator or a physical device.

📜 License
This project is licensed under the MIT License. See the LICENSE file for details.

Feel free to contribute to this project by submitting issues or pull requests.
