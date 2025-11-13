#!/usr/bin/env bash
# Simple start script for development purposes.
# Runs the Spring Boot backend with live reload.

set -e

cd "$(dirname "$0")/backend"
# el comando dirname "$0" me da la ruta donde esta el script start.sh
# con cd me muevo a la carpeta backend donde esta el backend

echo "Starting backend on http://localhost:8080"
#echo es un print pero en bash

mvn clean
# aqui le estoy diciendo que limpie todo lo que haya compilado antes para que no haya conflictos

mvn spring-boot:run
# es un script de bash, donde mi gestor de paqueterias que ocupo es maven, entonces uso mvn para correr el backend