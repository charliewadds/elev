import java.io.IOException;
import java.net.*;
import java.util.Scanner;

enum doorState{
    OPEN, CLOSED
}
enum direction{
    UP, DOWN, NONE
}


public class Main {
    static doorState doorState;
    static direction direction;
    static int floor;
    static int elevNum;
    static DatagramSocket socket;

    static int port = 1;

    public static void main(String[] args) throws IOException {
        doorState = doorState.CLOSED;
        direction = direction.NONE;
        floor = 1;

        System.out.println("Creating new Elevator");
        System.out.println("Enter the elevator number: ");
        Scanner scanner = new Scanner(System.in);
        elevNum = scanner.nextInt();


        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

        System.out.println("Enter the recive port: ");
        int recvPort = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter the destination port: ");
        int destPort = Integer.parseInt(scanner.nextLine());


        int len = 32;
        DatagramPacket packet = new DatagramPacket(new byte[len], len,serverAddress, destPort);

        while(true){
            try {
                socket = new DatagramSocket(recvPort);
            } catch (SocketException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
            socket.receive(packet);
            byte[] data = packet.getData();

            switch (data[0]){
                case 0b00000000://door open/close
                    if(data[1] == 0b00000000) {//go to floor
                        System.out.println("Received: " + new String(data, 0, packet.getLength()));
                        System.out.println("go to floor");
                        floor = (int) data[2];
                        moveElevator(floor);
                        doorState = doorState.OPEN;
                    }
                    break;

            }
        }

    }



    private static void moveElevator(int destFloor){
        //todo make this add time?
        if(floor < destFloor){
            direction = direction.UP;
        }else if(floor > destFloor){
            direction = direction.DOWN;
        }else{
            direction = direction.NONE;
        }
        floor = destFloor;

    }
}