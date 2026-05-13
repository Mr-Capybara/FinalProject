# Clarity UI Reference Files

This directory contains reference files from the original React/Vite prototype of **Clarity**, a mobile-first task, calendar, focus timer, and productivity dashboard app.

These files are **not Android source code**. They are design and interaction references for rebuilding the app as a native Android application, preferably with **Kotlin + Jetpack Compose**.

## How to Use These Files

Use the React and CSS files as the source of truth for:

- Visual style
- Color tokens
- Typography scale
- Spacing scale
- Component shapes
- Navigation structure
- Screen layout
- Basic interaction behavior
- Initial sample data model

Do not try to run these files inside Android Studio. Instead, read them and translate the design and behavior into native Android components.

## Recommended Reading Order

1. `index.css`

   Defines the design tokens:

   - colors
   - fonts
   - text sizes
   - spacing values
   - shadows
   - shared utility classes

   When implementing Android UI, convert these values into Compose theme files such as `Color.kt`, `Type.kt`, and `Theme.kt`.

2. `App.tsx`

   Defines the overall app shell:

   - app name: `Clarity`
   - mobile-first root layout
   - bottom navigation
   - task data model
   - in-memory task state
   - category drawer
   - screen switching

   Use this file to understand the main navigation structure and shared app state.

3. `views/CalendarView.tsx`

   Defines the calendar screen:

   - weekly collapsed calendar
   - monthly expanded calendar
   - selected date state
   - task dots on dates
   - scheduled tasks for selected day
   - swipe up/down expand behavior

4. `views/TaskListView.tsx`

   Defines the task list screen:

   - category filtering
   - task completion checkbox
   - completed task visual state
   - priority badge styling
   - empty category state

5. `views/EditTaskView.tsx`

   Defines the add/edit task screen:

   - task title input
   - category chips
   - priority selector
   - date and time inputs
   - notes field
   - save button

6. `views/TimerView.tsx`

   Defines the focus timer screen:

   - 25-minute timer
   - circular progress indicator
   - play/pause/reset controls
   - brown noise toggle
   - current focus task display

7. `views/DashboardView.tsx`

   Defines the statistics screen:

   - focus time card
   - completion rate ring
   - tasks completed count
   - current streak card
   - category distribution bars

   The values in this screen are static in the prototype. In the Android app, they should be calculated from real task and timer data.

8. `views/SettingsView.tsx`

   Defines the settings screen:

   - appearance section
   - theme color row
   - help/about section
   - instructions row

   The prototype only provides the UI shell. Native settings behavior should be implemented separately.

## Important Notes for Code Agents

- Preserve the overall visual direction: calm, mobile-first, light theme, soft green primary color, rounded surfaces, and low-contrast shadows.
- Treat the React state logic as prototype behavior only. A real Android app should use persistent storage, such as Room or DataStore.
- The current prototype stores tasks only in memory. Refreshing the web app loses user changes.
- The dashboard currently uses placeholder values. Replace them with computed statistics in the Android implementation.
- The settings screen is mostly static. Add real behavior only if required by the Android app requirements.
- The dependency `@google/genai` exists in the web project, but the current UI files do not use Gemini or any AI API.

## Expected Android Translation



The goal is not to copy the web implementation line by line. The goal is to recreate the same product experience as a native Android app using these files as the design and behavior reference.
