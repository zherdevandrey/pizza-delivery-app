#!/bin/bash
# check-config-server-started.sh

apt-get update -y

yes | apt-get install curl

curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://elastic-1:9200)

echo "result status code:" "$curlResult"

while [[ ! $curlResult == "200" ]]; do
  >&2 echo "Elastic server is not up yet!"
  sleep 20
  curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://elastic-1:9200)
done



