package io.ruck.mctest;

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
    
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            System.err.println("Usage: java -cp mctest.jar io.ruck.mctest.MCProducer <multicast group>");
            System.exit(1);
        }
        InetAddress group = InetAddress.getByName(args[0]);
        
        MulticastSocket socket = new MulticastSocket();
        socket.setTimeToLive(5);
        
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String payload = String.format("RUCKDUCK %09d", i);
            byte[] bytes = payload.getBytes(UTF_8);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, 5000);
            socket.send(packet);
            System.out.println("Sent: " + payload);
            Thread.sleep(1000);
        }
    }
}
