* consume messages - kafkacat -b localhost:9092 -t telegram-topic -p0 -o end
  https://gist.github.com/mjuric/a1cdd53fa02d67d4da38681d52635aec

* kafkacat -P -b localhost:19092 -t telegram-topic

* kafkacat -b localhost -L | grep topic

* sudo sysctl -w vm.max_map_count=262144