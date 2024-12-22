# React + Vite

# Playlist UI Project (React + Vite)

This is the UI project for the **BIG-DATA-SLAYLIST** system, built using **React** and **Vite**. It provides a lightweight, fast, and modern setup to run a React frontend.

---

## Prerequisites

Before running the project, ensure you have the following installed:

1. **Node.js** (v16+ recommended)  
   [Download and install Node.js](https://nodejs.org/).

2. **npm** or **yarn**  
   npm is included with Node.js by default. Verify your installation with:  
   ```bash
   npm -v

---
## Installation Steps

Install dependencies:
Run the following command to install all project dependencies:
1. **bash**
```bash
   npm install
```
---
## Running the project
To start the development server:
1. **bash**
```bash
   npm run dev
```
The app will be available at: http://localhost:5173/

Hot Module Replacement (HMR): The app will reload automatically when you make changes to the code.

---
## Troubleshooting


1. **Port Conflicts**
If you encounter a port conflict on **5173**, you can update the port in the [vite.config.js](./vite.config.js) file:

```javascript
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000, // Change to desired port
  },
});
```

```bash
kubectl port-forward svc/catalougeapi 8080:8080
```