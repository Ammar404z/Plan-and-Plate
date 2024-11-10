#!/bin/bash

screen -S frontend -X quit

screen -S backend -X quit

echo "Frontend- und Backend-Screens wurden beendet."