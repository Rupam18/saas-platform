#!/bin/bash

BASE_URL="http://localhost:8080/auth"
API_URL="http://localhost:8080"
EMAIL="test_$(date +%s)@example.com"
PASSWORD="password123"

echo "----------------------------------------"
echo "🔍 Starting Automated API Verification"
echo "----------------------------------------"

# 1. Register
echo "Step 1: Registering user $EMAIL..."
REGISTER_RESPONSE=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$EMAIL\", \"password\": \"$PASSWORD\", \"companyName\": \"TestComp\", \"role\": \"ADMIN\"}")

HTTP_CODE=${REGISTER_RESPONSE: -3}
BODY=${REGISTER_RESPONSE:0:${#REGISTER_RESPONSE}-3}

if [ "$HTTP_CODE" -eq 200 ] || [ "$HTTP_CODE" -eq 201 ]; then
  echo "✅ Registration Successful"
else
  echo "❌ Registration Failed (HTTP $HTTP_CODE): $BODY"
  exit 1
fi

# 2. Login
echo "Step 2: Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$EMAIL\", \"password\": \"$PASSWORD\"}")

TOKEN=$LOGIN_RESPONSE
if [[ "$TOKEN" == ey* ]]; then
  echo "✅ Login Successful (Token received)"
else
  echo "❌ Login Failed: $TOKEN"
  exit 1
fi

# 3. Create Task
echo "Step 3: Creating a task..."
TASK_RESPONSE=$(curl -s -w "%{http_code}" -X POST "$API_URL/tasks" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "Automated Task", "description": "Created by script", "status": "PENDING"}')

HTTP_CODE=${TASK_RESPONSE: -3}
BODY=${TASK_RESPONSE:0:${#TASK_RESPONSE}-3}

if [ "$HTTP_CODE" -eq 200 ] || [ "$HTTP_CODE" -eq 201 ]; then
  echo "✅ Task Created Successfully"
else
  echo "❌ Create Task Failed (HTTP $HTTP_CODE): $BODY"
  exit 1
fi

# 4. Get Tasks
echo "Step 4: Fetching tasks..."
GET_RESPONSE=$(curl -s -w "%{http_code}" -X GET "$API_URL/tasks" \
  -H "Authorization: Bearer $TOKEN")

HTTP_CODE=${GET_RESPONSE: -3}
BODY=${GET_RESPONSE:0:${#GET_RESPONSE}-3}

if [ "$HTTP_CODE" -eq 200 ]; then
  echo "✅ Tasks Fetched Successfully"
  echo "Response Snippet: ${BODY:0:100}..."
else
  echo "❌ Fetch Tasks Failed (HTTP $HTTP_CODE): $BODY"
  exit 1
fi

echo "----------------------------------------"
echo "🎉 ALL TESTS PASSED SUCCESSFULLY"
echo "----------------------------------------"
