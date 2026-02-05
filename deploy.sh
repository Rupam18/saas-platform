#!/bin/bash

echo "🚀 Deploying SaaS Platform..."

# Build and start containers
docker-compose up --build -d

echo "✅ Deployment started!"
echo "   App running at: http://localhost:8080"
echo "   Database running on port 3307 (Internal 3306)"
echo "   Logs: docker-compose logs -f"
