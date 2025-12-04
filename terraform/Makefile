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