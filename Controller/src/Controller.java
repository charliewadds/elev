
import java.net.*;
import java.util.Scanner;

public class Controller {
    static DatagramSocket socket;
    public static void main(String[] args) throws UnknownHostException {
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        int len = 32;
        DatagramPacket floorPacket = new DatagramPacket(new byte[len], len,serverAddress, 20);
        DatagramPacket elevPacket = new DatagramPacket(new byte[len], len,serverAddress, 22);
        Scanner scanner = new Scanner(System.in);

        try {
            socket = new DatagramSocket(8000);
        } catch (SocketException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }

        while(true){
            System.out.println("Enter the type of packet you want to send: ");
            System.out.println("1. Floor Packet");
            System.out.println("2. Elevator Packet");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            //scanner.nextLine();
            switch(choice){
                case 1:
                    System.out.println("Enter the floor number: ");
                    int floor = scanner.nextInt();
                    System.out.println("Enter the direction: ");
                    scanner.nextLine();
                    String direction = scanner.nextLine();
                    pushFloorButton(floor, direction, floorPacket);
                    break;
                case 2:

                    System.out.println("Enter the elevator number: ");
                    int elevator = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Enter the floor number: ");
                    int floorNum = scanner.nextInt();
                    scanner.nextLine();
                    sendElevatorButton(elevator, floorNum, elevPacket);
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


    private static void pushFloorButton(int floor, String direction, DatagramPacket packet){
        //send a message to the scheduler
        byte[] command = new byte[4];
        command[0] = 0b00000001;

        if(direction.equals("UP")){
            command[1] = 0b00000000;
        }else if(direction.equals("DOWN")){
            command[1] = 0b00000001;
        }
        packet.setPort(21- floor);
        packet.setData(command);
        try {
            socket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    private static void sendElevatorButton(int elevator, int floor, DatagramPacket packet){
        //send a message to the elevator
        byte[] command = new byte[4];
        command[0] = 0b00000010;
        command[1] = (byte) floor;
        command[2] = 0b00000000;
        command[3] = 0b00000000;
        packet.setPort(21+elevator);
        packet.setData(command);
        try {
            socket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
