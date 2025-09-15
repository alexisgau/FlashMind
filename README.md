# FlashMind 🧠
FlashMind is a modern Android application designed to revolutionize the way you study. It transforms any text, from class notes to article summaries, into interactive flashcards using the power of AI. With a clean, intuitive interface and robust offline capabilities, FlashMind is your perfect partner for effective learning.

<br>

<table>
<tr>
<td align="center"><b>Login Screen</b></td>
<td align="center"><b>Home Screen</b></td>
</tr>
<tr>
<td><img src="https://github.com/user-attachments/assets/e8f47703-32f3-4a8f-9ee7-b5858956d486" width="500"/></td>
<td><img src="https://github.com/user-attachments/assets/c1603684-0086-4f82-9586-43cc3f68e0d7" width="500"/></td>
</tr>
<tr>
<td align="center"><b>Manual Flashcard</b></td>
<td align="center"><b>AI Card Generation</b></td>
</tr>
<tr>
<td><img src="https://github.com/user-attachments/assets/57ef1f0c-28ad-4a2c-974c-63f7f8ff5d98" width="500"/></td>
<td><img src="https://github.com/user-attachments/assets/4ca2e9bf-6da9-4d8f-b803-c40b2ad24aba" width="500"/></td>
</tr>
</table>

## ✨ Key Features
AI-Powered Flashcard Generation: Simply paste any text or summary, and FlashMind's integrated Gemini AI will automatically generate question-and-answer flashcards for you.

Manual Card Creation: Full control to create, edit, and manage your own custom flashcards.

Interactive Study Sessions: Engage in focused learning sessions where cards are presented one by one. A simple tap triggers a smooth flip animation to reveal the answer.

Hierarchical Organization: Keep your study materials neatly organized by creating Categories (e.g., "World War II") and then breaking them down into Lessons (e.g., "Unit 1: The European Theater").

Robust Offline-First Support: Study anytime, anywhere. All your data is saved locally to your device and seamlessly synchronized with the cloud via a WorkManager-powered background service when an internet connection is available.

Google Sign-In: Quick and secure authentication using your Google account.

Dark Mode: A beautifully implemented dark theme for comfortable study sessions at night.

## 🛠️ Tech Stack & Architecture
This project is built with modern Android development practices and a robust architecture.

Architecture:

Clean Architecture: The codebase is structured in layers (data, domain, presentation) for a clear separation of concerns, making it scalable and easy to maintain.

MVVM (Model-View-ViewModel): A modern architectural pattern to separate UI logic from business logic.

SOLID: Adherence to SOLID principles to ensure the code is modular, testable, and extensible.

Tech Stack:

UI: Jetpack Compose is used for building the entire user interface with a declarative and reactive approach.

Asynchronous Programming: Kotlin Coroutines & Flow are used extensively for managing background threads and handling streams of data, ensuring a responsive UI.

Dependency Injection: Hilt is used to manage dependencies throughout the application, simplifying object lifecycle and improving testability.

Local Database: Room provides a local SQLite database to persist all categories, lessons, and flashcards, enabling the offline-first experience.

Remote Database: Cloud Firestore is used as the cloud backend to store user data and sync flashcards across sessions.

Background Sync: WorkManager ensures that any data created or modified offline is reliably uploaded to Firestore once the device reconnects to the internet.

Authentication: Firebase Authentication handles user sign-up and sign-in, including support for Google Sign-In.

Crash & Analytics Reporting: Firebase Crashlytics and Analytics are integrated to monitor app stability and user engagement.

## 🚀 Future Features
While the current version is fully functional, here are some features planned for the future:

File Upload: Allow users to upload documents (.pdf, .txt) for AI to process directly.

Multiple Choice Quizzes: Automatically generate quizzes based on the flashcards in a lesson.

Spaced Repetition System (SRS): Implement an algorithm to schedule flashcard reviews at optimal intervals for long-term retention.
