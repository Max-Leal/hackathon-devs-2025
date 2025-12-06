default: docker-run

docker-build:
	docker-compose build

docker-run:
	docker-compose up -d

docker-stop:
	docker-compose down

docker-clear:
	docker-compose down --volumes --remove-orphans

build-backend:
	docker-compose build backend

run-backend: build-backend
	docker-compose run backend -d