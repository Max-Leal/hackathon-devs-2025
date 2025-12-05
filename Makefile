default: docker-run

docker-build:
	docker-compose build

docker-run:
	docker-compose up -d

docker-stop:
	docker-compose down

docker-clear:
	docker-compose down --volumes --remove-orphans

run-backend:
	docker-compose run backend