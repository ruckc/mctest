package io.ruck.mctest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author ruckc
 */
public final class MCConsumer {

    private MCConsumer() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java -cp mctest.jar io.ruck.mctest.MCProducer <multicast group>");
            System.exit(1);
        }
        InetAddress group = InetAddress.getByName(args[0]);

        MulticastSocket socket = new MulticastSocket(5000);
        socket.joinGroup(group);
        byte[] buffer = new byte[18];
        DatagramPacket packet = new DatagramPacket(buffer, 18);
        while (true) {
            socket.receive(packet);
            String string = new String(packet.getData(), StandardCharsets.UTF_8);
            System.out.println(string);
        }
    }
}
