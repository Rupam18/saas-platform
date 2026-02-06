# 🚀 Deployment Guide: Vercel & Backend

To deploy your SaaS Platform live, you need to host the **Frontend** and **Backend** separately.

## 1. Backend Deployment (Required First)
Vercel is great for Frontends (React), but it **cannot** host a Java Spring Boot backend easily. You should use a dedicated backend host.

### Recommended Free/Cheap Options:
- **Render** (easiest): [render.com](https://render.com) - Supports Docker.
- **Railway**: [railway.app](https://railway.app) - Supports Docker.
- **Fly.io**: [fly.io](https://fly.io)

### Steps to Deploy Backend:
1.  **Push your code to GitHub**.
2.  **Create a Service** on Render/Railway.
3.  **Connect GitHub Repo**: Select your repo.
4.  **Config**: It should detect the `Dockerfile`.
5.  **Environment Variables**:
    *   Set `SPRING_PROFILES_ACTIVE=prod` (optional).
    *   **Database**: You will need a PostgreSQL/MySQL database provided by the host (Railway/Render provide this easily). Update `application.properties` with the DB URL.
6.  **Get Public URL**: Once deployed, you will get a URL like `https://my-saas-backend.onrender.com`.

### ⚠️ IMPORTANT: Update CORS
Your backend currently only allows `localhost`. You MUST update `WebConfig.java` to allow your Vercel URL (once you have it) or allow all temporarily:
```java
registry.addMapping("/**")
        .allowedOrigins("https://your-vercel-app.vercel.app", "http://localhost:5173") 
```

---

## 2. Frontend Deployment (Vercel)
Once your backend is live, deploying the frontend is easy.

1.  **Go to [Vercel.com](https://vercel.com)** and Login.
2.  **Add New Project** -> Import from GitHub.
3.  **Build Settings**:
    *   Framework: Vite
    *   Root Directory: `frontend` (Important! Select the frontend folder).
4.  **Environment Variables** (The Magic Step ✨):
    *   Add a variable named: `VITE_API_URL`
    *   Value: `https://my-saas-backend.onrender.com` (Your **Backend Public URL** from Step 1).
5.  **Click Deploy**.

## 3. Final Check
Open your Vercel URL. It should load the Landing Page.
*   **Test**: Try to Register.
*   **Troubleshoot**: If it fails, check the Browser Console (F12). If you see "CORS error", go back to Step 1 and update your Backend `WebConfig` to allow the Vercel domain.
