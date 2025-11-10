#!/usr/bin/env bash
# Simple start script for development purposes.
# Runs the Spring Boot backend with live reload.

set -e

cd "$(dirname "$0")/backend"

echo "Starting backend on http://localhost:8080"
mvn spring-boot:run