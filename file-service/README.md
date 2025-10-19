# File Service

This module is responsible for handling all file-related operations in the application. It provides a RESTful interface for interacting with the file system and is consumed by the `broker-gateway`.

## Overview

The file service is built with Spring Boot and consists of the following key components:

-   `RestFsController`: The main controller that exposes the file system operations as REST endpoints.
-   `RestFsService`: A service that contains the business logic for handling file operations.
-   `RestFsClient` and `ReactiveRestFsClient`: Clients for interacting with the file system.

## API

The file service exposes the following endpoints:

-   `POST /`: The main endpoint for all file operations. The operation to be performed is specified in the request body.

The following operations are supported:

-   `ls`: Lists the files in a directory.
-   `cd`: Changes the current directory.
-   `mkdir`: Creates a new directory.
-   `rmdir`: Removes a directory.
-   `newfile`: Creates a new file.
-   `deletefile`: Deletes a file.
-   `rename`: Renames a file or directory.
-   `copy`: Copies a file or directory.
-   `move`: Moves a file or directory.
