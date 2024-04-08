
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Controller {
    static DatagramSocket socket;
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("text or gui (1/0): ");
        int type = scanner.nextInt();
        scanner.nextLine();

        if(type == 0) {
            ElevGUI elevatorGUI = new ElevGUI();
            //gui
            //TODO
        }else {


            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
            int len = 32;
            DatagramPacket floorPacket = new DatagramPacket(new byte[len], len, serverAddress, 12);
            DatagramPacket elevPacket = new DatagramPacket(new byte[len], len, serverAddress, 11);


            try {
                socket = new DatagramSocket(8000);
            } catch (SocketException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }

//        Scheduler scheduler = new Scheduler();
//        Thread schedulerThread = new Thread(scheduler);
//        schedulerThread.start();

            while (true) {
                System.out.println("Enter the type of packet you want to send: ");
                System.out.println("1. Floor Packet");
                System.out.println("2. Elevator Packet");
                System.out.println("3. Exit");
                int choice = scanner.nextInt();
                int port;
                //scanner.nextLine();
                switch (choice) {
                    case 1:
                        System.out.println("Enter the floor number: ");
                        int floor = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Enter the direction: ");
                        String direction = scanner.nextLine();

                        System.out.println("Enter the port: ");
                        port = scanner.nextInt();
                        scanner.nextLine();

                        pushFloorButton(floor, direction, serverAddress, port);
                        break;
                    case 2:

                        System.out.println("Enter the elevator number: ");
                        int elevator = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Enter the floor number: ");
                        int floorNum = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Enter the port: ");
                        port = scanner.nextInt();
                        scanner.nextLine();

                        sendElevatorButton(elevator, floorNum, elevPacket, port);
                        break;
                    case 3:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice");
                        break;
                }
            }
        }
    }


    private static void pushFloorButton(int floor, String direction, InetAddress serverAddress, int port) {
        try {
            byte[] command = new byte[4];
            command[0] = 0b00000001;

            if (direction.equals("UP")) {
                command[1] = 0b00000000;
            } else {
                command[1] = 0b00000001;
            }

            DatagramPacket packet = new DatagramPacket(command, command.length, serverAddress, port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace(); // Handle or log the exception appropriately
        }
    }
    private static void sendElevatorButton(int elevator, int floor, DatagramPacket packet, int port){
        //send a message to the elevator
        byte[] command = new byte[4];
        command[0] = 0b00000010;
        command[1] = (byte) floor;
        command[2] = 0b00000000;
        command[3] = 0b00000000;
        packet.setPort(port);
        packet.setData(command);
        try {
            socket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
