package io.ruck.mctest;

import com.google.common.net.InetAddresses;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 *
 * @author ruckc
 */
public final class MCProducer {

    private MCProducer() {
    }

    public static void run(MCOptions opts) throws IOException, InterruptedException {
        MulticastSocket[] sockets = new MulticastSocket[opts.getQuantity()];
        // setup
        for (int i = 0; i < sockets.length; i++) {
            sockets[i] = new MulticastSocket();
            sockets[i].setTimeToLive(5);
        }

        for (int j = 0; j < Integer.MAX_VALUE; j++) {
            String payload = String.format("RUCKDUCK %09d", j);
            byte[] bytes = payload.getBytes(UTF_8);

            int base = InetAddresses.coerceToInteger(opts.getGroup());
            for (int i = 0; i < sockets.length; i++) {
                InetAddress group = InetAddresses.fromInteger(base + i);
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, 5000);
                sockets[i].send(packet);
                System.out.println("Sent " + payload + " to " + group);
            }
            Thread.sleep(opts.getSleepInterval());
        }
    }
}
