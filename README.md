# Zero Hunger Food Bank

This is a lightweight Android app designed to help organizations and communities share surplus food.

## Features
- **Admin Portal:** Manage inventory, view near-expiry food, distribute food to people waiting in a queue (FIFO), and undo actions.
- **Community Portal:** Request food or donate surplus food easily.
- **Offline First:** Built using Room Database for local storage.

## Architecture
- Kotlin
- Jetpack Compose (Modern UI)
- Room Database
- MVVM Architecture

## Setup Instructions
1. Open this directory (`d:\zero hunger`) in Android Studio.
2. Android Studio will automatically sync the Gradle files and download the necessary dependencies.
3. Once synced, you can run the app on an Android Emulator or a physical device.
4. Default sample data is loaded automatically on the first run for testing purposes.

## Testing
Run unit tests inside `app/src/test/java/com/zerohunger/app/AppRepositoryTest.kt` to verify the core business logic (sorting, distribution, undo).
