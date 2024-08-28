## start kafka
```bash
cd docker
docker compose -f kafka.yaml up > /dev/null 2>&1 &
```
## collector sending data to signoz
```bash
cd docker
docker compose -f collector.yaml up > /dev/null 2>&1
```

## metrics collector
```bash
cd collector
./otelcol-contrib --config collector-contrib-config.yaml > /dev/null 2>&1 
```

## start producer
```bash
cd docker
docker compose -f producer-consumer.yaml up > /dev/null 2>&1
```

## start consumer
```bash
cd scripts
./consumer1.sh > /dev/null 2>&1
./consumer2.sh > /dev/null 2>&1
./consumer3.sh > /dev/null 2>&1
./consumer4.sh > /dev/null 2>&1
./consumer5.sh > /dev/null 2>&1
./consumer6.sh > /dev/null 2>&1
```