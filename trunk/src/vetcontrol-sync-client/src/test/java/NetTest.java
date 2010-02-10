import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.02.2010 21:28:49
 */
public class NetTest {

    public static void main(String args[]) throws SocketException, UnknownHostException {
        System.out.println("getLocalHost:  "+ InetAddress.getLocalHost().getHostAddress());

        displayInterfaceInformation(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));

//
//        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
//        for (NetworkInterface netint : Collections.list(nets))
//            displayInterfaceInformation(netint);
    }

    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        PrintStream out = System.out;

        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();

        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
        }

        out.printf("Up? %s\n", netint.isUp());
        out.printf("Loopback? %s\n", netint.isLoopback());
        out.printf("PointToPoint? %s\n", netint.isPointToPoint());
        out.printf("Supports multicast? %s\n", netint.supportsMulticast());
        out.printf("Virtual? %s\n", netint.isVirtual());
//        out.printf("Hardware address: %s\n",  Arrays.toString(netint.getHardwareAddress()));

        String mac = "";
        if (netint.getHardwareAddress() != null){
            for (byte m  : netint.getHardwareAddress()){
                mac += String.format("%02x-", m);
            }
        }
        if (!mac.isEmpty()) mac = mac.substring(0, mac.length()-1).toUpperCase();

        out.printf("Hardware address: %s\n", mac);
        out.printf("MTU: %s\n", netint.getMTU());

        out.printf("\n");

     }
}
