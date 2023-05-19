FROM node:alpine

RUN mkdir /src
RUN mkdir /src/dist
WORKDIR /src

COPY package.json /src/package.json
COPY src/ /src/src/
COPY gulpfile.js /src/gulpfile.js
COPY webpack.config.js /src/webpack.config.js

RUN \
  apk update && \
  apk add $BUILD_APKS && \
  npm install sass && \
  apk del $BUILD_APKS

RUN npm rebuild node-sass

RUN npm install