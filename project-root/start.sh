#!/bin/bash

# Navigate to frontend directory and start in a screen
cd frontend
screen -dmS frontend bash -c "npm install && npm run serve"

# Navigate to backend directory and start in a screen
cd ../backend
screen -dmS backend bash -c "mvn spring-boot:run"

# Start MariaDB server in a screen
screen -dmS mariadb bash -c "sudo systemctl start mariadb && mysql -u root -proot"

echo "Frontend, Backend, and MariaDB have been started in separate screens."