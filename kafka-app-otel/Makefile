# Variables
DOCKER_HUB_USERNAME=shivanshu1333
VERSION=latest

# Image names
CONSUMER_AMD64=$(DOCKER_HUB_USERNAME)/kafka-consumer-amd64:$(VERSION)
CONSUMER_ARM64=$(DOCKER_HUB_USERNAME)/kafka-consumer-arm64:$(VERSION)
PRODUCER_AMD64=$(DOCKER_HUB_USERNAME)/kafka-producer-amd64:$(VERSION)
PRODUCER_ARM64=$(DOCKER_HUB_USERNAME)/kafka-producer-arm64:$(VERSION)

# Build targets
.PHONY: all build-all push-all clean

all: build-all push-all

# Build all images
build-all: build-consumer-amd64 build-consumer-arm64 build-producer-amd64 build-producer-arm64

# Consumer builds
build-consumer-amd64:
	docker build --platform linux/amd64 -t $(CONSUMER_AMD64) -f kafka-consumer/Dockerfile kafka-consumer

build-consumer-arm64:
	docker build --platform linux/arm64 -t $(CONSUMER_ARM64) -f kafka-consumer/Dockerfile kafka-consumer

# Producer builds
build-producer-amd64:
	docker build --platform linux/amd64 -t $(PRODUCER_AMD64) -f kafka-producer/Dockerfile kafka-producer

build-producer-arm64:
	docker build --platform linux/arm64 -t $(PRODUCER_ARM64) -f kafka-producer/Dockerfile kafka-producer

# Push all images
push-all: push-consumer-amd64 push-consumer-arm64 push-producer-amd64 push-producer-arm64

push-consumer-amd64:
	docker push $(CONSUMER_AMD64)

push-consumer-arm64:
	docker push $(CONSUMER_ARM64)

push-producer-amd64:
	docker push $(PRODUCER_AMD64)

push-producer-arm64:
	docker push $(PRODUCER_ARM64)

# Clean up
clean:
	docker rmi $(CONSUMER_AMD64) $(CONSUMER_ARM64) $(PRODUCER_AMD64) $(PRODUCER_ARM64) 