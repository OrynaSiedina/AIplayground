FROM node:16-alpine AS base

WORKDIR /app

COPY package*.json ./

RUN npm ci

FROM base AS build

WORKDIR /app

COPY . .

RUN npm run build


FROM node:16-alpine AS production

WORKDIR /app

COPY --from=build /app/next.config.js ./
COPY --from=build /app/public ./public
COPY --from=build /app/.next ./.next
COPY --from=base /app/node_modules ./node_modules
COPY --from=build /app/package*.json ./

EXPOSE 3000

CMD ["npm", "start"]
