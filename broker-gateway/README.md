# Broker Gateway

This module acts as a gateway to the various services in the application. It provides a single entry point for all client requests and routes them to the appropriate backend service.

## Overview

The broker gateway is built with Spring Boot and uses a `BrokerService` to dynamically route requests to the correct service based on the operation specified in the request. It consolidates the APIs from the following services:

-   `broker-service`
-   `user-service`
-   `file-service`
-   `export-service`
-   `upload-service`
-   `login-service`

## API

The broker gateway exposes a single endpoint:

-   `POST /`: All requests are sent to this endpoint, and the `operation` parameter in the request body determines which service will handle the request.
