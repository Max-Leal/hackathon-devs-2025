FROM node:20-alpine

WORKDIR /app

RUN npm install -g @angular/cli

COPY ./app/package*.json ./  
RUN npm install

COPY ./app ./              

EXPOSE 4200

CMD ["ng", "serve", "--host", "0.0.0.0"]