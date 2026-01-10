# SynapAi 🧠
SynapAi is a comprehensive, modern Android application designed to revolutionize the way you study. Going beyond simple flashcards, it transforms your study materials—whether raw text, PDFs, or DOCX files—into interactive learning tools using the power of **Generative AI**.
With a clean, intuitive interface built with Jetpack Compose and a robust offline-first architecture, SynapAi is your perfect partner for mastering any subject through active recall, summarization, and self-testing.

<br>

<table>
  <tr>
    <td align="center"><b>Login Screen</b></td>
    <td align="center"><b>Home Screen</b></td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/cff0172d-389f-4108-8c2c-792ddfdffb3f" width="500" />
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/ac4ab688-a0dd-471f-ad36-b24613674a89" width="500" />
    </td>
  </tr>

  <tr>
    <td align="center"><b>Manual Flashcard</b></td>
    <td align="center"><b>AI Card Generation</b></td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/4ca2e9bf-6da9-4d8f-b803-c40b2ad24aba" width="500" />
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/57ef1f0c-28ad-4a2c-974c-63f7f8ff5d98" width="500" />
    </td>
  </tr>
  <tr>
    <td align="center"><b>Test Generation</b></td>
    <td align="center"><b>Test Screen</b></td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/d065da35-5137-4565-b5d4-e7b177fb0ef9" width="500" />
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/8d3a89d9-226d-42f1-ad4a-cddf584fa3dc" width="500" />
    </td>
  </tr>
</table>


## ✨ Key Features

### 🤖 AI-Powered Content Generation
* **Multi-Format Support:** Upload **PDFs**, **DOCX files**, or paste raw text directly. SynapAi processes "dirty" text (headers, footers) to extract only the relevant concepts.
* **Smart Summaries:** Instantly generate structured, Markdown-formatted summaries of long documents to grasp key concepts quickly.
* **Auto-Generated Flashcards:** The AI automatically extracts Q&A pairs from your materials for active recall practice.
* **AI Quizzes & Tests:** Generate multiple-choice questions (JSON-parsed) to test your knowledge and track your score.

### 📚 Study & Organization
* **Interactive Study Sessions:**
    * *Flashcards:* Smooth flip animations and intuitive navigation.
    * *Quizzes:* Take tests with immediate feedback and score tracking.
* **Hierarchical Organization:** Keep materials organized by **Categories** (e.g., "Biology") and **Lessons** (e.g., "Cell Structure").
* **Manual Creation:** Full control to manually create, edit, and manage custom flashcards alongside AI-generated ones.

### ☁️ Sync & Persistence
* **Robust Offline-First Support:** Study anytime, anywhere. All data (cards, summaries, quiz results) is saved locally via Room Database.
* **Cross-Device Synchronization:** Never lose progress. A powerful background service using **WorkManager** automatically syncs your local changes with **Cloud Firestore** when an internet connection is available. Start studying on your phone and continue on another device seamlessly.
* **Google Sign-In:** Secure and quick authentication using Firebase Auth.

### 🎨 User Experience
* **Modern UI:** 100% Jetpack Compose interface with Material Design 3.
* **Dark Mode:** Fully supported dark theme for comfortable late-night study sessions

## 🛠️ Tech Stack & Architecture
This project is built with industry-standard Android development practices, focusing on scalability, testability, and performance.

### Architecture
* **Clean Architecture:** Strict separation of concerns into Data, Domain, and Presentation layers.
* **MVVM (Model-View-ViewModel):** Decouples UI logic from business rules and state management.
* **SOLID Principles:** Adherence to modular design patterns.

### Tech Stack
* **Language:** Kotlin (100%).
* **UI:** Jetpack Compose (Declarative UI), Material 3.
* **AI Integration:** **Google Gemini API** (Generative AI for text processing, JSON parsing, and summarization).
* **Asynchronous Programming:** Kotlin Coroutines & Flow (StateFlow/SharedFlow) for reactive data streams.
* **Dependency Injection:** Hilt (Dagger).
* **Local Persistence:** Room Database (SQLite) for offline capability.
* **Remote Backend:** Firebase Cloud Firestore (NoSQL).
* **Background Processing:** **WorkManager** (For reliable, deferrable data synchronization).
* **Authentication:** Firebase Authentication (Google Sign-In).
* **Monitoring:** Firebase Crashlytics & Analytics.

## 🚀 Future Roadmap
* **Spaced Repetition System (SRS):** Implement an algorithm (like SM-2) to schedule flashcards based on user performance.
* **Text-to-Speech (TTS):** Audio playback for flashcards and summaries for auditory learning.
* **Community Market:** Share and download card decks from other users.
