@g#!/bin/sh
cd kafka-data-collector

mvn clean package

mv ./target/kafka-data-collector*.jar ../../kafka-data-collector.jar
cd ../..
rm -rf tmp 

java -jar /usr/src/data-collector/kafka-data-collector.jar

echo "Kafka data collector started..."
