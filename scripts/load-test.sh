#!/bin/bash
echo "Running load test using k6 (via Docker)..."
docker run --rm -i grafana/k6 run - < load-test.js
