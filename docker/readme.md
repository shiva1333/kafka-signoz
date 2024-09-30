## start kafka
```bash
cd docker
nohup sudo docker compose -f kafka.yaml up > /dev/null 2>&1 &
```
## collector sending data to signoz
```bash
cd docker
nohup sudo docker compose -f collector.yaml up > /dev/null 2>&1 &
```

## metrics collector
```bash
cd collector
nohup ./otelcol-contrib --config collector-contrib-config.yaml > /dev/null 2>&1 & 
```

## start producer
```bash
cd docker
nohup sudo docker compose -f producer-consumer.yaml up > /dev/null 2>&1 &
```

## start consumer
```bash
cd scripts
nohup ./consumer1.sh > /dev/null 2>&1 &
nohup ./consumer2.sh > /dev/null 2>&1 &
nohup ./consumer3.sh > /dev/null 2>&1 &
nohup ./consumer4.sh > /dev/null 2>&1 &
nohup ./consumer5.sh > /dev/null 2>&1 &
nohup ./consumer6.sh > /dev/null 2>&1 &
```