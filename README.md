# MasterMind

An implementation of a MasterMind game created for the LinkedIn Reach Apprenticeship Program (2025).

This is a full-stack web application where players can start a game, submit guesses, receive feedback on the correct digits and positions, and use a limited number of hints to reveal parts of the secret code.

## Table of Contents
- [Development Approach](#development-approach)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [API Endpoints](#api-endpoints)
- [Acknowledgements](#acknowledgements)

## Development Approach

My goal for this project was to build a robust, testable, and extensible API that would first focus on meeting the project requirements, and then expand with additional features to make the game more engaging and flexible. From the start, I prioritized maintainability by structuring the backend using an MVC architecture, ensuring each layer had a clear responsibility: controllers handle client interaction, services contain the core game logic, and repositories manage data persistence. This separation of concerns makes the code easier to navigate, debug, and build upon.

Reliability was a major consideration, so I implemented unit tests to verify that my services behaved correctly and handled invalid input gracefully. These tests also give me confidence when adding new functionality. I included extensive validation checks to ensure both the user’s guesses are valid (correct length, valid digits) and that the game creation request has proper inputs (e.g., positive attempt limits, valid code length, maximum digit within bounds, hints cannot exceed code length). To standardize error handling, I created custom exception classes and a centralized controller advice to send consistent responses back to the client. For resilience, I added a fallback method to locally generate the secret code if the Random.org API became unavailable, ensuring the game remains playable under any circumstances.

Extensibility was another key focus. I designed contracts using interfaces so that components depend on abstractions rather than specific implementations. This allows the current in-memory storage to be easily swapped for a SQL database via JPA with no changes to the service layer. Beyond the baseline requirements, I gave users greater control over how they play by making all game settings customizable, including the secret code length, the maximum digit allowed, the number of attempts, and the number of hints.

I also implemented a hints system to enhance gameplay. Players can request a hint that reveals a digit of the secret code along with its position, but the number of hints is capped to prevent bypassing the challenge entirely. Additionally, I added a “resume game” feature, allowing users to retrieve a pre-existing game by its ID and continue playing from where they left off, in cases where they had to leave before finishing.


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


## Getting Started
To get a local copy up and running, follow the steps below.

### Prerequisites
Before you begin, make sure you have the following technologies installed:

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
2. Install the dependencies by running `npm i`
3. Now you can start the development server by using `npm run dev`
4. You should now be able to visit the frontend at `http://localhost:5173`


## API Endpoints

| Method | Endpoint                  | Description                       |
|--------|---------------------------|-----------------------------------|
| POST   | `/game`                   | Create a new game                 |
| POST   | `/game/{id}/guess`        | Submit a guess                    |
| GET    | `/game/{id}/hint`         | Request a hint                    |
| GET    | `/game/{id}`              | Retrieve previous game            |


## Acknowledgements

I’d like to thank the LinkedIn REACH program team and reviewers for taking the time to evaluate my project. I truly appreciate the opportunity to demonstrate my skills, creativity, and problem-solving approach. I’ve enjoyed the process of designing, building, and refining this application, and I’m grateful for the chance to share my work with you.

  
---
