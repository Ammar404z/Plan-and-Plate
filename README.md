# Recipe Search App

This project is a full-stack recipe search application that allows users to search for recipes by name. It consists of a **Spring Boot** backend and a **Vue.js** frontend, using **TheMealDB API** to fetch recipe data.

## Table of Contents
- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Backend (Spring Boot)](#running-the-backend-spring-boot)
- [Running the Frontend (Vue.js)](#running-the-frontend-vuejs)
- [Usage](#usage)

## Getting Started

Follow these instructions to set up and run the project locally.

### Prerequisites

- **Java 17** or higher
- **Maven** (for backend dependencies)
- **Node.js and npm** (for frontend dependencies)
- **Axios** (for HTTP requests in the frontend)
- **Git** (optional, for cloning the project)

## Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.tik.uni-stuttgart.de/iste-sopra-2024-aeb/team-13.git
   cd team-13

2. **Install Java 17 or higher**

3. **Install Node.js and npm**

4. **Install Maven**

5. **Install Axios**
   ```bash
   cd frontend
   npm install axios

   
   
## Running the Backend (Spring Boot)

1. **Navigate to the backend directory**:
   ```bash
   cd backend
   
2. **Build and Start the Application**:
   ```bash
   mvn spring-boot:run
   
   The backend server should start at http://localhost:8080.
   
 ## Running the frontend (Vue.js)
 
 1. **Navigate to the frontend directory**:
    ```bash
    cd ../frontend
    
 2. **Install Dependencies (if not done previously)**:
    ```bash
    npm install
    
 3. **Start the Vue Development Server**:
    ```bash
    npm run serve
    
    The frontend should start on http://localhost:8081.
 
 ## Usage
 
 1. Access The Application:
 
    	•	Open a browser and go to http://localhost:8081.
      •	The frontend sends a request to the backend, which fetches data from TheMealDB API and returns it to be displayed.
      
 2.	Search for Recipes:

	•	Type a recipe name (e.g., “pizza” or “pasta”) in the search bar.
	•	The frontend sends a request to the backend, which fetches data from TheMealDB API and returns it to be displayed.