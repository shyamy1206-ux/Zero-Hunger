# Zero Hunger Food Bank
# 🍲 Zero Hunger App

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)

## 📖 Overview
**Zero Hunger** is a tech-driven logistics solution engineered to bridge the gap between surplus food generation and starvation. Built natively for Android, this application acts as a real-time digital supply chain, instantly connecting restaurants, event caterers, and individuals with nearby NGOs and beneficiaries before surplus food spoils.

This project was developed under the **Engineering and Technology** theme, emphasizing that hunger is not just a production problem, but a complex logistics and data-routing challenge.

## Key Features
*    **Real-Time Geo-Routing:** Integrates with Google Maps API to match food donors with the nearest active volunteers, minimizing transit time.
*    **Inclusive Accessibility (TTS):** Features an integrated Text-to-Speech (TTS) engine and dynamic UI translation in **Marathi, Hindi, and English**, ensuring illiterate or non-tech-savvy users can easily request food via voice guides.
*   **Cloud Database:** Powered by a highly scalable Supabase (PostgreSQL) backend for millisecond-fast food matching.
*  **Admin Logistics Dashboard:** A comprehensive control room for NGOs to manage beneficiary queues, monitor food expiry times, and generate automated PDF audit reports.
*    **Modern UI/UX:** Built entirely with Jetpack Compose using a clean, light-mode civic-tech aesthetic.

##  Technology Stack
*   **Frontend:** Kotlin, Jetpack Compose, Material Design 3
*   **Backend & Auth:** Supabase 
*   **Location Services:** Google Maps API, Android Location Services
*   **Accessibility:** Native Android Speech & Locale APIs
*   **Architecture:** MVVM (Model-View-ViewModel)

##  How to Run Locally
1. Clone this repository:
   ```bash
   git clone https://github.com/YourUsername/Zero-Hunger-App.git

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
Home screen
<img width="720" height="1600" alt="4" src="https://github.com/user-attachments/assets/d800a2ae-665a-4c88-8ce7-3e44968b47d7" />
Donate food
<img width="720" height="1600" alt="3" src="https://github.com/user-attachments/assets/e0d32c28-23aa-4e50-bdae-ee4088f2d180" />
<img width="720" height="1600" alt="1" src="https://github.com/user-attachments/assets/4b9af0c7-2976-49fc-afb3-b9cd88ea0bc9" />
<img width="720" height="1600" alt="2" src="https://github.com/user-attachments/assets/dbfec6ea-42c6-4337-b478-186182582e3e" />
<img width="720" height="1600" alt="WhatsApp Image 2026-09-27 at 4 52 26 PM" src="https://github.com/user-attachments/assets/e9113c6c-ae3a-4dbe-a6f0-e7a3c0bc6485" />
<img width="720" height="1600" alt="WhatsApp Image 2026-09-27 at 4 52 25 PM" src="https://github.com/user-attachments/assets/63ed1947-1ce8-42e3-b397-59f67452bc8d" />
