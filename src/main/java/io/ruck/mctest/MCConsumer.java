package io.ruck.mctest;

import com.google.common.net.InetAddresses;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 *
 * @author ruckc
 */
public final class MCConsumer {

    private MCConsumer() {
    }

    public static void run(MCOptions opts) throws IOException {
        int base = InetAddresses.coerceToInteger(opts.getGroup());
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        Iterator<NetworkInterface> nifs = new Iterator<NetworkInterface>() {
            @Override
            public boolean hasNext() {
                return networkInterfaces.hasMoreElements();
            }

            @Override
            public NetworkInterface next() {
                return networkInterfaces.nextElement();
            }
        };
        NetworkInterface nif = StreamSupport.stream(Spliterators.spliteratorUnknownSize(nifs, Spliterator.ORDERED), false).filter(ni -> {
            try {
                return ni.isUp() && ni.supportsMulticast() && !ni.isLoopback();
            } catch (Throwable t) {
                return false;
            }
        }).findFirst().get();

        DatagramChannel[] channels = new DatagramChannel[opts.getQuantity()];
        SelectableChannel[] schans = new SelectableChannel[opts.getQuantity()];
        MembershipKey[] keys = new MembershipKey[opts.getQuantity()];
        Map<DatagramChannel, MembershipKey> channelToKey = new HashMap<>();

        Selector selector = Selector.open();
        for (int i = 0; i < channels.length; i++) {
            channels[i] = DatagramChannel.open(StandardProtocolFamily.INET)
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                    .bind(new InetSocketAddress(5000))
                    .setOption(StandardSocketOptions.IP_MULTICAST_IF, nif);
            keys[i] = channels[i].join(InetAddresses.fromInteger(base + i), nif);
            channelToKey.put(channels[i], keys[i]);
            schans[i] = channels[i].configureBlocking(false);
            schans[i].register(selector, SelectionKey.OP_READ);
        }

        ByteBuffer packet = ByteBuffer.allocate(50);

        while (true) {
            if (selector.selectNow() >= 0) {
                selector.selectedKeys().stream().forEach(key -> {
                    try {
                        packet.clear();
                        SocketAddress addy = ((DatagramChannel) key.channel()).receive(packet);
                        if (addy != null) {
                            System.out.println(addy + " -> " + channelToKey.get((DatagramChannel) key.channel()).group() + " " + packet.asCharBuffer().toString());
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MCConsumer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        }
    }
}
