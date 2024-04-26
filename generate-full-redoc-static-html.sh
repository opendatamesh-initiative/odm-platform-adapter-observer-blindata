#!/bin/bash

# Generate openapi.json and redoc-static.html for each module
mvn clean package spring-boot:repackage -DskipTests
mvn clean verify -DskipTests -Pgenerate-doc

# Create a directory for documentation
DOC_DIR="redocly-docs"

# Check if the doc directory already exists
if [ -d "$DOC_DIR" ]; then
    # If it exists, remove it and create a new one
    rm -rf "$DOC_DIR"
fi

# Create a new doc directory
mkdir "$DOC_DIR"

# Find all redoc-static.html files
REDOC_FILE="observer-blindata-server/target/redoc-static.html"

# Check if any redoc-static.html files were found
if [ -z "$REDOC_FILE" ]; then
    echo "No redoc-static.html file found in target directory."
    exit 1
fi

# Copy and rename all redoc-static.html files to the aggregated documentation directory
NEW_NAME="${DOC_DIR}/observer-blindata-server.html"
cp "$REDOC_FILE" "$NEW_NAME"
echo "Renamed and copied $REDOC_FILE to $NEW_NAME"

echo "Documentation generated successfully in $DOC_DIR"
