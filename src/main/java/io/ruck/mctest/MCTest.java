package io.ruck.mctest;

import java.io.IOException;

/**
 *
 * @author ruckc
 */
public final class MCTest {
    private MCTest() {}
    
    public static void main(String ... args) throws IOException, InterruptedException {
        MCOptions opts = new MCOptions();
        opts.parse(args);
        
        if(opts.isConsumer()) {
            MCConsumer.run(opts);
        }
        if(opts.isProducer()) {
            MCProducer.run(opts);
        }
    }
}
