# Recipe Search App

http://193.196.52.222:8081/

This is a full-stack recipe search application that enables users to search for, save, and manage recipes, as well as generate weekly meal plans and shopping lists. It consists of a **Spring Boot** backend and a **Vue.js** frontend, using **TheMealDB API** to fetch recipe data.

## Features

### Recipe Search and Save

- **Search for Recipes**:

  - Enter a recipe name in the search bar (e.g., "pizza" or "pasta").
  - The application retrieves and displays matching recipes using **TheMealDB API**.

- **Save Recipes**:
  - Users can save recipes to their personal list for later use.

### Weekly Meal Plans

- **Create a Weekly Meal Plan**:

  - Use saved recipes to create and organize meal plans for the week.
  - Plans can be customized to include different meals for each day.

- **View Weekly Plans**:
  - Users can view all their created weekly plans and delete them if no longer needed.

### Shopping List Generation

- **Generate Shopping Lists**:
  - Automatically generate a shopping list based on a selected weekly meal plan.
  - Duplicate ingredients are consolidated, and the list scales according to the selected number of servings.

### Cooking Statistics

- **Most Saved Recipes**:
  - View the top 5 recipes saved by users.
- **Saved Recipes Counter**:
  - Track the amount of times a user saved recipes.

---

## Accessing the Application

### User Interface

- Navigate to the app through the provided URL.
- The user interface includes the following key sections:
  - **Search Recipes**: Enter a recipe name to search for meals.
  - **Saved Meals**: View and manage your saved recipes.
  - **Weekly Plans**: Create and view weekly meal plans.
  - **Shopping List**: Generate and view shopping lists for your weekly plans.
  - **Statistics**: Gain insights into saved and cooked recipes.

### Navigation

- The header contains links to all major features, making it easy to navigate between them.

---

## Technology Stack

- **Frontend**:
  - **Vue.js**: Used for building the user interface.
  - **Axios**: For making API calls to the backend.
- **Backend**:

  - **Spring Boot**: Provides REST APIs and handles business logic.
  - **MariaDB**: Database for storing user data, recipes, and statistics.

- **Third-Party API**:
  - **TheMealDB API**: Used for fetching recipe data.

---

For further assistance contact: st188386@stud.uni-stuttgart.de.
