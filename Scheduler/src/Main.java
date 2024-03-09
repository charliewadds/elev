import java.io.IOException;
import java.net.*;
import java.util.Dictionary;
import java.util.Scanner;

enum buttonState{
    UP, DOWN, NONE
}

enum elevatorState{
    ARRIVED, NOT_ARRIVED
}



public class Main {

    static buttonState buttonState;
    static elevatorState elevatorState;
    static int floor;
    static DatagramSocket socket;

    static Dictionary<Integer, Integer> floorPorts;


    public static void main(String[] args) throws IOException {
        elevatorState = elevatorState.NOT_ARRIVED;
        buttonState = buttonState.NONE;

        floorPorts.put(1, 21);//todo make this entered by the user

        Scanner scanner = new Scanner(System.in);

        System.out.println("Creating new Floor");
        System.out.println("Enter the floor number: ");
        floor = Integer.parseInt(scanner.nextLine());

        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

        System.out.println("Enter the recive port (21): ");
        int recvPort = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter the Floor destination port (21): ");
        int floorPort = Integer.parseInt(scanner.nextLine());//todo update this to use multiple floors

        System.out.println("Enter the Elevator destination port (21): ");
        int elevPort = Integer.parseInt(scanner.nextLine());//todo update this to use multiple floors


        int len = 32;
        DatagramPacket floorPacket = new DatagramPacket(new byte[len], len,serverAddress, floorPort);
        DatagramPacket elevPacket = new DatagramPacket(new byte[len], len,serverAddress, elevPort);



        while(true) {
            try {
                socket = new DatagramSocket(recvPort);
            } catch (SocketException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
            socket.receive(floorPacket);

            byte[] data = floorPacket.getData();

            switch (data[0]){
                case 0b00000000://floor button pressed
                   int upOrDown = data[1];
                   int floor = data[2];
                   int elevNum = data[3];

                   byte[] command = new byte[2];
                   command[0] = 0b00000000;//go to floor
                   command[1] = (byte) floor;
                   elevPacket.setData(command);

                   socket.send(elevPacket);
                   break;

                case 0b00000001://elevator reached floor
                       int newFloor = data[1];




                    break;

            }


        }

    }


}