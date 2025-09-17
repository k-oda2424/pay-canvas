.PHONY: dev dev-front dev-back db-up db-down backend-build backend-run

DEV_FRONT?=npm run dev
BACKEND_DIR=src/backend
GRADLE?=./gradlew

dev-front:
	$(DEV_FRONT)

dev-back:
	cd $(BACKEND_DIR) && $(GRADLE) bootRun

db-up:
	docker compose up -d postgres

db-down:
	docker compose down

backend-build:
	cd $(BACKEND_DIR) && $(GRADLE) clean build

backend-run:
	cd $(BACKEND_DIR) && $(GRADLE) bootRun

dev: db-up dev-back
