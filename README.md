# RepJournal

RepJournal is an Android workout logging application built in Java as part of my **CI660 Advanced Mobile Application Development** module during my BSc Computer Science degree at the University of Brighton.

The app was designed to help users plan workout phases, organise weekly training days, log exercises, record sets/reps/weights, view workout history and track weekly progress through simple visual summaries. It uses **Firebase Authentication** for account access and **Cloud Firestore** to store user-specific workout data.

This project achieved **85/100 (A+)** and represents one of my strongest examples of individual mobile application development, cloud-backed data storage, authentication, UI/UX design and self-directed technical learning.

## Project Context

RepJournal was originally developed as an individual university project with the goal of creating a practical fitness tracking app that went beyond basic form input. The main focus was to design a smooth workout logging experience where users could move naturally from planning a training phase to recording actual exercise performance.

The project gave me hands-on experience with Android development in Java, Firebase Authentication, Firestore data modelling, fragment-based navigation, custom dialogs, RecyclerView-based data display, profile management and simple progress visualisation.

In 2026, I revisited the repository as part of my software engineering portfolio preparation. The recent work focuses on improving the GitHub presentation of the original project by adding clearer documentation, screenshots, technical explanations and a more professional README structure. The core application remains the original university project.

## Key Features

### Secure User Authentication

RepJournal uses **Firebase Authentication** to support account creation, login and email verification. Users must be authenticated before accessing the main workout tracking features, which gives the app a more realistic user-account structure than a simple local-only fitness tracker.

### Cloud-Based Workout Data Storage

Workout data is stored in **Cloud Firestore**, with each user having their own nested data structure for phases, weekdays, muscle groups, exercises and recorded sets. This allowed the app to persist user data across sessions and separate each user’s workout records securely.

### Workout Phase Planning

Users can create workout phases to organise their training over time. Each phase contains a weekly structure from Monday to Sunday, allowing users to plan different muscle groups and exercises for different training days.

### Weekly Training Calendar

The app provides a weekday-based flow where users can select a specific day and manage the workout assigned to that day. This makes the app feel more like a structured training planner rather than a basic exercise note-taking tool.

### Exercise and Muscle Group Tracking

Users can add muscle groups, create exercises under each muscle group, and build a workout structure that reflects their own routine. The app supports common workout planning needs such as selecting chest, back, shoulders, arms, legs, abdomen or rest days.

### Set, Rep and Weight Logging

RepJournal includes a custom workout logging flow where users can record sets, reps and weight for each exercise. This was designed to make workout entry quick and convenient during a training session.

### Workout History

The app stores previous exercise records so users can review their training history. This helps users track previous performance and revisit earlier workout sessions.

### Weekly Progress Visualisation

RepJournal includes simple visual summaries using charts to show weekly training distribution. This adds a data-driven element to the app and helps users understand how their workouts are spread across muscle groups.

### Profile Management

Users can view their profile details and upload a profile image. Firebase Storage is used to handle profile image storage and retrieval.

### Mobile-First UI/UX

The app was designed with a strong focus on user experience, including fragment-based navigation, a bottom navigation bar, custom dialogs, responsive screens and a clean workout flow from planning to logging.

## Tech Stack

| Area | Technologies / Tools |
|---|---|
| **Programming Language** | Java |
| **Platform** | Android |
| **IDE** | Android Studio |
| **Authentication** | Firebase Authentication |
| **Database** | Cloud Firestore |
| **Storage** | Firebase Storage |
| **UI Structure** | Activities, Fragments, Bottom Navigation, RecyclerView, Custom Dialogs |
| **Data Visualisation** | MPAndroidChart |
| **Image Loading** | Picasso |
| **Build Tool** | Gradle |
| **Version Control** | Git and GitHub |

### Technical Implementation Summary

RepJournal was built as a native Android application using **Java**. The app uses a combination of activities and fragments to separate authentication screens, the main dashboard, workout phases, weekday selection, workout logging and profile management.

Firebase was used as the backend service. **Firebase Authentication** handles user registration, login and email verification, while **Cloud Firestore** stores each user’s workout phases, calendar days, muscle groups, exercises and logged set records. **Firebase Storage** is used for profile image upload and retrieval.

The user interface was designed around a mobile-first workout flow. The app uses **RecyclerView** to display dynamic Firestore data, **custom dialogs** for adding phases, exercises and workout records, and **MPAndroidChart** to provide simple visual feedback on weekly workout distribution.

## Screenshots

The screenshots below show the main user journey in RepJournal, from account creation through to workout planning, exercise logging and progress tracking.

### App Preview

RepJournal provides a mobile-first workout dashboard where users can view the current training day, planned muscle groups and a weekly exercise overview.

<p align="center">
  <img src="docs/screenshots/front-page.png" alt="RepJournal app preview showing the dashboard and weekly overview" width="400">
</p>

---

### Authentication Flow

Users can create an account, log in and verify their email before accessing the main application. Firebase Authentication is used to manage registration, sign-in and verified access.

<p align="center">
  <img src="docs/screenshots/signup-activity.png" alt="RepJournal authentication screens including login, signup and email verification" width="280">
</p>

---

### Home Dashboard

The home screen shows the user’s current workout day, selected muscle groups, a start training action and a visual weekly overview. The bottom navigation gives quick access to Home, Phases and Profile.

<p align="center">
  <img src="docs/screenshots/home-screen.png" alt="RepJournal home dashboard showing today's workout and weekly progress chart" width="300">
</p>

---

### Workout Phases, Weekly Planning and Profile

Users can organise training into workout phases, select weekdays from a weekly calendar view and manage their profile through the main navigation.

<p align="center">
  <img src="docs/screenshots/phase-weekdays-profile-fragments.png" alt="RepJournal workout phases, weekday planning and profile screens" width="720">
</p>

---

### Exercise Logging Workflow

The main workout flow guides the user from selecting a phase and weekday to choosing muscle groups, adding exercises and recording workout sets. This creates a structured route from planning to actual workout tracking.

<p align="center">
  <img src="docs/screenshots/how-to-log-exercise.png" alt="RepJournal workflow showing phase selection, weekday selection, muscle group selection and exercise logging" width="400">
</p>

---

### Set, Rep and Weight Entry

RepJournal includes a custom input flow for recording workout performance. Users can enter set number, reps and weight values quickly during a workout session.

<p align="center">
  <img src="docs/screenshots/how-to-add-weights.png" alt="RepJournal set logging screen for entering reps and weight" width="420">
</p>

---

### Dashboard Interaction and Progress View

The dashboard was designed to keep workout actions and progress information close together. MotionLayout was used to support a smoother visual transition between the workout summary and progress chart.

<p align="center">
  <img src="docs/screenshots/stats-motionlayout-demonstration.png" alt="RepJournal dashboard interaction and progress chart view" width="420">
</p>

## Firebase / Data Structure

RepJournal uses Firebase as the backend for authentication, user data and profile image storage. The app separates each user’s data using their Firebase user ID, which means workout phases, weekly plans, exercises and logged sets are stored under the authenticated user account.

Cloud Firestore was used because it allowed the app to store flexible nested workout data without needing a separate custom backend server. This was useful for an individual Android project because it let me focus on the mobile app logic, user flow and data structure while still working with a realistic cloud-backed database.

### Firestore Structure

The Firestore structure is organised around the user, then broken down into workout phases, calendar days, muscle groups, exercises and exercise records.

<p align="center">
  <img src="docs/screenshots/complete-workout-phase-structure.png" alt="RepJournal complete workout phase structure" width="420">
</p>

```text
users
└── {userId}
    ├── credentials
    │   └── {userId}
    │       ├── firstname
    │       ├── lastname
    │       ├── email
    │       └── dateAccountCreated
    │
    └── userdata
        └── {phaseId}
            ├── phaseTitle
            ├── datePhaseCreated
            │
            └── calendar
                └── {weekday}
                    ├── dayTitle
                    │
                    └── musclegroups
                        └── {muscleGroupTitle}
                            ├── muscleGroupTitle
                            ├── workoutDay
                            ├── phaseId
                            │
                            └── exercises
                                └── {exerciseTitle}
                                    ├── exerciseTitle
                                    ├── dateModified
                                    │
                                    └── exerciseData
                                        └── {date}
                                            ├── exerciseDataCreated
                                            ├── exerciseDayCreated
                                            ├── exerciseMonthCreated
                                            │
                                            └── exerciseRecords
                                                └── set{number}
                                                    ├── setCount
                                                    ├── repCount
                                                    └── weight
```

## Application Flow
## What I Built
## Recent Repository Cleanup
## How to Run Locally
## Future Improvements
## Author
