#!/bin/bash
set -e

echo "🔹 1. Running Backend Unit Tests (Java)..."
./gradlew test

echo "🔹 2. Preventing build failure by ensuring Keycloak is ready..."
# Simple check for Keycloak port
if ! nc -z localhost 8080; then
    echo "⚠️ Keycloak is not accessible at localhost:8080."
    echo "   Please run 'docker-compose up -d keycloak' before running E2E tests."
    # We could auto-start, but that might interfere with dev state. Better to fail or warn.
    # For CI, we would auto-start.
    echo "   Skipping Theme Tests..."
    exit 0
else
    echo "   Keycloak detected."
fi

echo "🔹 3. Running Theme E2E Tests (Playwright)..."
cd theme-tests
# Ensure deps are installed (fast if already done)
npm install
npx playwright test

echo "🔹 4. Checking Backend E2E Tests (RestAssured)..."
if nc -z localhost 8088; then
    echo "   Backend detected on port 8088. Running E2E tests..."
    ../gradlew e2eTest
else
    echo "⚠️ Backend not running on localhost:8088. Skipping Java E2E tests."
fi

echo "✅ All Tests Passed (Backend Unit/Int + Theme + Backend E2E if avail)"
