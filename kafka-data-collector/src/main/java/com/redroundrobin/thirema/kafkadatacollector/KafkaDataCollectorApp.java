package com.redroundrobin.thirema.kafkadatacollector;

import com.redroundrobin.thirema.kafkadatacollector.utils.Consumer;
import com.redroundrobin.thirema.kafkadatacollector.utils.Database;
import com.redroundrobin.thirema.kafkadatacollector.utils.Producer;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class KafkaDataCollectorApp {

  public static void main(String[] args) {
    Logger logger = Logger.getLogger(KafkaDataCollectorApp.class.getName());

    // used to not show the logs of kafka
    ch.qos.logback.classic.Logger kafkaLogger =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    kafkaLogger.setLevel(ch.qos.logback.classic.Level.OFF);

    String kafkaBootstrapServers = "core.host.redroundrobin.site:29092";

    Database timescale = new Database("jdbc:postgresql://core.host.redroundrobin.site:3456/timescale", "user", "user");
    Database postgre = new Database("jdbc:postgresql://core.host.redroundrobin.site:6543/postgre", "user", "user");

    try {
      Consumer consumerInserter = new Consumer(Pattern.compile("^gw_.*"), kafkaBootstrapServers);
      Consumer consumerFilter = new Consumer(Pattern.compile("^gw_.*"), kafkaBootstrapServers);
      Producer producer = new Producer("alerts", kafkaBootstrapServers);

      // Creazione thread
      DataInserter inserter = new DataInserter(timescale, consumerInserter);
      DataFilter filter = new DataFilter(postgre, consumerFilter, producer);

      // Creazione executor
      Executor executor = Executors.newCachedThreadPool();

      // Scheduling ed esecuzione dei threads
      logger.info("Scheduling runnables");

      executor.execute(inserter);
      executor.execute(filter);

      logger.info("Done scheduling");
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Exception occur!", e);
    }
  }
}
