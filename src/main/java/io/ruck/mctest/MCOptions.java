package io.ruck.mctest;

import io.ruck.clop.Option;
import io.ruck.clop.Options;
import java.net.InetAddress;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ruckc
 */
public class MCOptions {

    private static final Logger LOG = LogManager.getLogger();

    private final Options opts = new Options();
    private final Option<Boolean> producer;
    private final Option<Boolean> consumer;
    private final Option<InetAddress> group;
    private final Option<Integer> quantity;
    private final Option<Integer> sleep;

    public MCOptions() {
        producer = opts.booleanOption("producer").shortName("p").longName("producer").validator(value -> value != isConsumer()).defaultValue(Boolean.FALSE).build();
        consumer = opts.booleanOption("consumer").shortName("c").longName("consumer").validator(value -> value != isProducer()).defaultValue(Boolean.FALSE).build();
        group = opts.inetOption("first multicast group").shortName("g").longName("group").validator(address -> address.isMulticastAddress()).build();
        quantity = opts.integerOption("quantity of groups").shortName("n").longName("quantity").defaultValue(1).build();
        sleep = opts.integerOption("sleep interval (ms)").shortName("s").longName("sleep").defaultValue(1000).build();
        LOG.debug("MCOptions configured with: " + Arrays.asList(producer, consumer, group, quantity, sleep));
    }

    public boolean isConsumer() {
        LOG.trace(consumer);
        return consumer.getValue();
    }

    public boolean isProducer() {
        LOG.trace(producer);
        return producer.getValue();
    }

    public void parse(String[] args) {
        try {
            if (Arrays.asList(args).contains("-h")) {
                showHelp();
                System.exit(0);
            } else {
                opts.parse(args);
            }
        } catch (Throwable t) {
            System.err.println("Error while parsing options");
            t.printStackTrace();
            System.err.println();
            showHelp();
            System.exit(1);
        }
    }

    public int getQuantity() {
        return quantity.getValue();
    }

    public int getSleepInterval() {
        return sleep.getValue();
    }

    public InetAddress getGroup() {
        return group.getValue();
    }

    public void showHelp() {
        System.out.println("Usage: java -jar mctest.jar (-c|-p) [-g mcgroup] [-n #] [-s #]\n");
        System.out.println(opts.generateHelp());
    }
}
