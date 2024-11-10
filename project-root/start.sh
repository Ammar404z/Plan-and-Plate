#!/bin/bash

cd frontend
screen -dmS frontend bash -c "npm install && npm run serve"

cd ../backend
screen -dmS backend bash -c "mvn spring-boot:run"

echo "Frontend und Backend wurden in separaten Screens gestartet."