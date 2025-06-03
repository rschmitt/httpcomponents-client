#!/usr/bin/env bash

while true
do
  echo "$(date): $(lsof -c java | grep TCP | grep -i -e httpbin -e amazon -e google -e cloudfront -e akamai | awk '{print $10}' | sort | uniq -c | sort -nr | paste -sd, -)"; sleep 1
done
