#!/bin/bash

echo "Starting Ollama server..."
ollama serve &  # Start Ollama in the background

echo "Waiting for Ollama server to be active..."
while [ -z "$(ollama list | grep 'NAME')" ]; do
  sleep 1
done
echo "Ollama is ready"

echo "Pulling the models..."
ollama pull llama3.1:8b

echo "Starting the models..."
ollama run llama3.1:8b

# Wait indefinitely so the container stays alive
wait