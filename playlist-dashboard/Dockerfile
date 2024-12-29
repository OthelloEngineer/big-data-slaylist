# --- Stage 1: Build ---
FROM node:18-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

# Build command outputs to /app/dist by default
RUN npm run build

# --- Stage 2: Serve with Nginx ---
FROM nginx:alpine
# NOTE the folder is now 'dist' instead of 'build'
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
