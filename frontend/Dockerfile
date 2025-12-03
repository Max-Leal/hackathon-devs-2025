FROM node:20-alpine

# Define a raiz do container
WORKDIR /app

RUN npm install -g @angular/cli

COPY scorebank/package*.json ./

RUN npm install

# Copia o resto do código fonte também para a raiz
COPY ./scorebank/ .

EXPOSE 4200

CMD ["ng", "serve", "--host", "0.0.0.0"]