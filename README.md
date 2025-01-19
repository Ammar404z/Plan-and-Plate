# Recipe Search App

http://193.196.52.222:8081/

This is a full-stack recipe search application that enables users to search for, save, and manage recipes, as well as generate weekly meal plans and shopping lists. It consists of a **Spring Boot** backend and a **Vue.js** frontend, using **TheMealDB API** to fetch recipe data.

---

## Core Features

### Recipe Search and Save

- **Search for Recipes**:

  - Enter a recipe name in the search bar (e.g., "pizza" or "pasta").
  - The application retrieves and displays matching recipes using **TheMealDB API**.
  - Use filters above the search bar access recipe categories/areas faster.

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

---

## Additional Features

- **Random Meal Button**:

  - A button to generate and display a random meal suggestion.

- **YouTube Buttons for Meals**:

  - A dedicated button for each meal to view its YouTube tutorial video.

- **Mark Saved Meals as Favorite**:

  - Users can mark saved meals as favorites by clicking on a star icon.

- **Search Saved Meals**:

  - A search functionality to find meals within the user's saved recipes.

- **Sort Meals in Favorite Meals**:

  - Ability to sort favorite meals by name in ascending or descending order.

- **Filter Meals by Favorites**:

  - A filter option to display only favorite meals.

- **New Meal View Component**:

  - A detailed view for each meal showing:
    - Ingredients
    - Instructions
    - Category
    - Name
    - YouTube tutorial video
    - Buttons to save and share the meal

- **Thumbnails for Ingredients**:

  - Ingredients displayed with thumbnails in the meal view and shopping list.
  - Includes support for custom meal ingredients.

- **Shopping List as a Todo List**:

  - A shopping list feature where users can check off ingredients as they buy them.

- **Category Statistics**:

  - A new chart displaying the distribution of recipes by category.

- **Share Feature on Multiple Platforms**:

  - Share recipes on platforms like WhatsApp, Facebook, Twitter, or copy the link to the clipboard.

- **Enhanced User Experience with Icons**:
  - Interactive icons for features like saving, deleting, favoriting, and sharing meals.

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

## Starting the Project Locally

### Prerequisites

- **Java 17** or higher
- **Maven** (for building the backend)
- **Node.js** and **npm** (for building the frontend)
- **MariaDB** (for the database)

### Backend Setup

1. Navigate to the `backend` folder:

   ```bash
   cd backend
   ```

2. Install dependencies and build the project:

   ```bash
   mvn install
   ```

3. Start the backend:
   ```bash
   mvn spring-boot:run
   ```

### Frontend Setup

1. Navigate to the frontend folder:

   ```bash
   cd frontend
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Start the frontend:
   ```bash
   npm run serve
   ```

### Database Configuration

Ensure the MariaDB database is running locally. Update the database credentials in the `application.properties` file located in the `backend/src/main/resources` directory(you can also use the default credentials like in this project. Username:root, Password:root. If you do so, then there is no need to edit the application.properties file).

### Accessing the Application

- Open your browser and navigate to [http://localhost:8080](http://localhost:8080) to access the application locally.

---

For further assistance, contact: st188386@stud.uni-stuttgart.de.
