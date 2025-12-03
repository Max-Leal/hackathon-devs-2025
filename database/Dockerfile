FROM postgres:15-alpine

ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres
ENV POSTGRES_DB=hackathondb

COPY init.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 5432

HEALTHCHECK --interval=15s --timeout=5s --start-period=5s --retries=5 \
  CMD pg_isready -U "${POSTGRES_USER}" || exit 1