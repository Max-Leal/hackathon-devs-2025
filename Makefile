default: docker-run

docker-build:
	docker-compose build

docker-run:
	docker-compose up -d

docker-stop:
	docker-compose down

docker-clear:
	docker-compose down --volumes --remove-orphans

tf-init:
	terraform init

tf-plan: tf-init
	terraform plan

tf-apply: tf-plan
	terraform apply -auto-approve

tf-destroy: tf-init
	terraform destroy -auto-approve

tf-fmt:
	terraform fmt -recursive