#!/bin/bash

# Configuration
API_URL="http://localhost:8080"
EMAIL="test_flow_$(date +%s)@example.com"
PASSWORD="password123"
COMPANY="FlowTest_$(date +%s)"

echo "--------------------------------------------------"
echo "🚀 Starting Full Flow Verification"
echo "Target: $API_URL"
echo "User: $EMAIL"
echo "Company: $COMPANY"
echo "--------------------------------------------------"

# 1. Register Owner
echo ""
echo "1️⃣  Registering Owner..."
REGISTER_RESPONSE=$(curl -s -X POST "$API_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\",
    \"companyName\": \"$COMPANY\",
    \"role\": \"ADMIN\"
  }")
echo "Response: $REGISTER_RESPONSE"

# 2. Login Owner
echo ""
echo "2️⃣  Logging in Owner..."
LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\"
  }")

TOKEN=$(echo $LOGIN_RESPONSE)
echo "Token received (length: ${#TOKEN})"

# 3. Access Dashboard (Protected)
echo ""
echo "3️⃣  Accessing Dashboard (Protected Route)..."
DASHBOARD_RESPONSE=$(curl -s -X GET "$API_URL/analytics/tasks" \
  -H "Authorization: Bearer $TOKEN")
echo "Response: $DASHBOARD_RESPONSE"

# 4. Send Invitation
INVITE_EMAIL="invited_$(date +%s)@example.com"
echo ""
echo "4️⃣  Sending Invitation to $INVITE_EMAIL..."
INVITE_RESPONSE=$(curl -s -X POST "$API_URL/invitations" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$INVITE_EMAIL\",
    \"role\": \"USER\"
  }")
echo "Response: $INVITE_RESPONSE"

echo ""
echo "--------------------------------------------------"
echo "✅ Flow Verification Complete"
echo "Check Docker logs for the Invitation Link!"
echo "--------------------------------------------------"
