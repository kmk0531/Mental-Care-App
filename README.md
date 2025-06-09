# Mental-Care-App

# In:Mind

An Android application that allows users to log their emotions, view emotional trends, and receive supportive prompts via GPT-based chatbot.

## Features

- 📌 **User Authentication**  
  - Email/Password login using MySQL + Express.js  

- ✍️ **Emotion Diary**  
  - Select emotion via color-coded interface  
  - Write journal entries tagged with emotion  
  - View emotion trends (monthly top 3 emotions and frequent words)

- 🧠 **GPT Chatbot**  
  - GPT 4o mini based chatbot that provides supportive responses  
  - Prompts engineered based on professional psychological consultation

- 👤 **My Page**  
  - View user profile  
  - Change password or logout  
  - Access Self-Diagnosis test or emergency contacts

## Tech Stack

| Layer      | Technology        |
|------------|-------------------|
| Frontend   | Android (Java)    |
| Backend    | Express.js (Node.js) |
| Database   | MySQL             |
| AI Service | OpenAI GPT API    |

## System Architecture

Android App
|
Volley (HTTP Request)
|
Express.js (REST API)
|
MySQL (Data Storage)
|
OpenAI (for chatbot prompt response)
