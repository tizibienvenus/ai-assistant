#!/bin/bash

# Charger les variables d'environnement depuis .env si le fichier existe
if [ -f /app/.env ]; then
    echo "Loading environment variables from /app/.env"
    export $(grep -v '^#' /app/.env | xargs)
fi

# Lancer l'application
exec java -jar app.jar "$@"