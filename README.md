# Recipe Search App

This project is a full-stack recipe search application that allows users to search for recipes by name. It consists of a **Spring Boot** backend and a **Vue.js** frontend, using **TheMealDB API** to fetch recipe data.

## Table of Contents
- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Stopping the Application](#stopping-the-application)
- [Accessing the Application](#acessing-the-application)
- [Usage](#usage)

## Getting Started

Follow these instructions to set up and run the project on a VM.

### Prerequisites

- **Java 17** or higher
- **Maven** (for backend dependencies)
- **Node.js and npm** (for frontend dependencies)
- **Axios** (for HTTP requests in the frontend)
- **Git** (optional, for cloning the project)

(everything here is already installed on the vm, so you can skip the Installation steps)

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

   
   
## Running the Application

A script has been provided to start both the frontend and backend servers in separate screen session:

1. **Run the Start Script**:
   ```bash
   ./start.sh
   ```
   (this has already been done previously, no need to run the script again, do this only if the Application has not been started yet)

   this script will :

   - Navigate to the frontend directory, install dependencies, and start the frontend server in a screen session named "frontend."
   - Navigate to the backend directory, install dependencies, and start the Spring Boot application server in a screen session named "backend."

   *note that u have to be in the project-root directory to be able to run the script*


 ## Stopping the Application
 
 A stop script has also been provided to stop both the frontend and backend screen sessions.

 1. **Run the Stop Script**:
   ```bash
   ./stop.sh

   this script will :

   - Stop the frontend server in the "frontend" screen session.
   - Stop the backend server in the "backend" screen session.

   *note that you have to be in the project-root directory to be able to run the script*
 
 ## Usage
 
 1. Access The Application:
 
    	•	Open a browser and go to http://[2001:7c0:2320:1:f816:3eff:fe09:d4aa]:8081/
      •	The frontend sends a request to the backend, which fetches data from TheMealDB API and returns it to be displayed.
      
 2.	Search for Recipes:

	•	Type a recipe name (e.g., “pizza” or “pasta”) in the search bar.
	•	The frontend sends a request to the backend, which fetches data from TheMealDB API and returns it to be displayed.