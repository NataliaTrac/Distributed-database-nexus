@echo off
echo Launching DatabaseNode
start java DatabaseNode 8080 1 100

echo Waiting for the server to start...
timeout /t 5 /nobreak > NUL

echo Launching DatabaseClient for get-value operation
start java DatabaseClient -gateway 127.0.0.1:8080 -operation "get-value 1"

echo Launching DatabaseClient for set-value operation
start java DatabaseClient -gateway 127.0.0.1:8080 -operation "set-value 1 200"

echo Launching DatabaseClient for new-record operation
start java DatabaseClient -gateway 127.0.0.1:8080 -operation "new-record 2 300"

echo Launching DatabaseClient for get-min operation
start java DatabaseClient -gateway 127.0.0.1:8080 -operation "get-min"

echo Launching DatabaseClient for get-max operation
start java DatabaseClient -gateway 127.0.0.1:8080 -operation "get-max"

echo Launching DatabaseClient for delete-value operation
start java DatabaseClient -gateway 127.0.0.1:8080 -operation "delete-value 1"

echo Launching DatabaseClient for find-key operation
start java DatabaseClient -gateway 127.0.0.1:8080 -operation "find-key 1"

pause
