# ==========================================
# Smart Home Functions Worker
# ==========================================

FROM node:24-slim

# ------------------------------------------
# Working directory
# ------------------------------------------

WORKDIR /app

# ------------------------------------------
# Copy package files first
# This allows Docker to cache npm install
# ------------------------------------------

COPY package*.json ./

# ------------------------------------------
# Install dependencies
# ------------------------------------------

RUN npm ci

# ------------------------------------------
# Copy TypeScript configuration and source
# ------------------------------------------

COPY tsconfig.json ./
COPY src ./src

# ------------------------------------------
# Build TypeScript
# ------------------------------------------

RUN npm run build

# ------------------------------------------
# Start the worker
# ------------------------------------------

CMD ["npm", "start"]