# MasterMind

## About The Project

This is a MasterMind game created for the LinkedIn Reach Apprenticeship Program (2025).

This is a full-stack web application where players can start a game, submit guesses, receive feedback on the correct digits and positions, and use a limited number of hints to reveal parts of the secret code.

---

## Features

* **Create a new game with customizable rules:**
  * Code Length
  * Maximum digit that will be in the code
  * Maximum number of guesses
  * Maximum number of hints available

* **Load a pre-existing game with game ID**
 
* **Submit guesses and get feedback on:**
  * Number of correct digits
  * Number of correct digits in the correct location

* **Hints system:**
  * Reveals digits and their position
  * Prevents duplicate position hints
  * Limits usage to be less than the code length
 
* **Robust validation and error handling**
  * Clear error messages returned to client via `@AllControllerAdvice`
  * Custom exceptions for invalid guesses, completed games, hint overuse, etc.
 
## Tech Stack
### Backend
- Java
- Spring Boot
- Lombok
- MapStruct
- WebClient
- Maven
- JUit
- Mockito

### Frontend
- Javascript
- React
- TailwindCSS
- Axios

---

## Getting Started
To get a local copy up and running follow the steps below.

### Prerequisites
Before you begin make sure you have the following technologies installed:

1. **Java**  
   - Download: [https://jdk.java.net/](https://jdk.java.net/)  
   - Verify installation:  
     ```bash
     java -version
     ```

2. **Apache Maven+**  
   - Download: [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)  
   - Verify installation:  
     ```bash
     mvn -version
     ```

3. **Node.js**
   - Download: [https://nodejs.org/en/download](https://nodejs.org/en/download]
   - Verify installation:
     ```bash
     node -v
     ```
     

### Installation
- Clone the repository
   ```bash
   git clone https://github.com/Abi-Liu/MasterMind.git
   ```
#### **Backend**
1. Navigate to the backend directory: `cd backend`
2. Build the project by running `mvn clean install`
3. You can now start the server via `mvn spring-boot:run`
4. That's it! You should now be able to access the api at `http://localhost:8080`

#### **Frontend**
1. Navigate to the frontend directory: `cd frontend`
2. Install the dependecies by running `npm i`
3. Now you can start the development server by using `npm run dev`
4. You should now be able to visit the frontned at `http://localhost:5173`

---

## API Endpoints

| Method | Endpoint                  | Description                       |
|--------|---------------------------|-----------------------------------|
| POST   | `/game`                   | Create a new game                 |
| POST   | `/game/{id}/guess`        | Submit a guess                    |
| GET    | `/game/{id}/hint`         | Request a hint                    |
| GET    | `/game/{id}`              | Retrieve previous game            |

---
  
