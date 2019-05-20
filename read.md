# Trending Meetup locations 

## Implementation

List Topics: 
- `bin/kafka-topics --zookeeper zookeeper:2181 --list`

Consume Topic: 
- `bin/kafka-console-consumer --bootstrap-server kafka:9092 --topic meetup`
- `bin/kafka-console-consumer --bootstrap-server kafka:9092 --topic meetupoutput`
- `bin/kafka-console-consumer --bootstrap-server kafka:9092 --topic meetuptrendingoutput`

Produce to Topic:
- `bin/kafka-console-producer --broker-list kafka:9092 --topic meetup`

# Execute

Kafka and Zookeeper

- `chmod +x run.sh`
- `./run.sh`


Endpoints: 

- `http://localhost:8080/api/v01/topLocations/snapshot`
- `http://localhost:8080/api/v01/topTrends/us`

## Architecture

Due to the high volume I decided to build distinct applications for each part of the process. More specifically the list of the apps is:

- Kafka
- Zookeeper 
- Collector App (Node)
- Processor (Apache Flink)
- Storage (Postgres)
- Api (Akka-Http)
 
## Improvements

- Logging 
- Testing
- Error handling
- Resolve docker issue
- Kafka Message type (JSON, Avro, etc)
