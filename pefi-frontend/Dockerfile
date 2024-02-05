# This dockerfile is intended for development use only

FROM node:21 as nodebuilder

WORKDIR /app

COPY . .

RUN yarn install --prefer-offline --frozen-lockfile --non-interactive

CMD ["yarn", "dev", "--host"]