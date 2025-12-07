APP_NAME = loomi-challenge
DOCKER_COMPOSE = docker-compose

.PHONY: help setup up down build test clean logs db-shell

help:
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

setup: build up
	@echo "Ambiente configurado e rodando!"

up:
	$(DOCKER_COMPOSE) up -d

down:
	$(DOCKER_COMPOSE) down

build:
	./mvnw clean package -DskipTests
	$(DOCKER_COMPOSE) build

test:
	./mvnw test

clean:
	$(DOCKER_COMPOSE) down -v
	./mvnw clean

logs:
	$(DOCKER_COMPOSE) logs -f

db-shell:
	docker exec -it loomi-db psql -U user -d orderdb