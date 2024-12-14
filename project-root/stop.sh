#!/bin/bash

# Stop the frontend screen
screen -S frontend -X quit

# Stop the backend screen
screen -S backend -X quit

# Stop MariaDB server and its screen
screen -S mariadb -X quit
sudo systemctl stop mariadb

echo "Frontend, Backend, and MariaDB screens have been stopped."