import java.util.Scanner;

public class NetworkSegment {
    public static void main(String[] args) {
        String ip = args.length >= 1 ? args[0] : prompt("Enter IP address (e.g. 192.168.1.100): ");
        String mask = args.length >= 2 ? args[1] : prompt("Enter subnet mask (e.g. 255.255.255.224): ");

        int ipInt = ipv4(ip);
        int maskInt = ipv4(mask);
        int netInt = ipInt & maskInt;
        int bcastInt = netInt | ~maskInt;

        System.out.println();
        System.out.println("IP Address:   " + bin(ipInt));
        System.out.println("Subnet Mask:  " + bin(maskInt));
        System.out.println("-------------------------------------------------");
        System.out.println("Network Addr: " + bin(netInt));
        System.out.println();
        System.out.println("Network address (decimal): " + dec(netInt));

        Integer prefix = cidrPrefix(maskInt);
        if (prefix != null) {
            System.out.println("CIDR prefix: /" + prefix);
            System.out.println("Addresses per subnet: " + (1 << (32 - prefix)));
        }

        System.out.println("Range: " + dec(netInt) + " to " + dec(bcastInt));
    }

    @SuppressWarnings("resource")
    private static String prompt(String msg) {
        System.out.print(msg);
        return new Scanner(System.in).nextLine().trim();
    }

    private static int ipv4(String s) {
        String[] p = s.split("\\.");
        if (p.length != 4) throw new IllegalArgumentException("Invalid IPv4: " + s);
        int v = 0;
        for (String part : p) {
            int o = Integer.parseInt(part);
            if (o < 0 || o > 255) throw new IllegalArgumentException("Invalid IPv4: " + s);
            v = (v << 8) | o;
        }
        return v;
    }

    private static String dec(int v) {
        return ((v >>> 24) & 255) + "." + ((v >>> 16) & 255) + "." + ((v >>> 8) & 255) + "." + (v & 255);
    }

    private static String bin(int v) {
        return b8((v >>> 24) & 255) + "." + b8((v >>> 16) & 255) + "." + b8((v >>> 8) & 255) + "." + b8(v & 255);
    }

    private static String b8(int o) {
        return String.format("%8s", Integer.toBinaryString(o)).replace(' ', '0');
    }

    // /prefix only when mask is contiguous 1s then 0s.
    private static Integer cidrPrefix(int mask) {
        boolean zero = false;
        int p = 0;
        for (int bit = 31; bit >= 0; bit--) {
            boolean one = ((mask >>> bit) & 1) == 1;
            if (!zero) {
                if (one) p++; else zero = true;
            } else if (one) {
                return null;
            }
        }
        return p;
    }
}
