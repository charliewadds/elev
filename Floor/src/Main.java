import java.io.IOException;
import java.net.*;
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

    public static void main(String[] args) throws UnknownHostException {
        elevatorState = elevatorState.NOT_ARRIVED;
        buttonState = buttonState.NONE;



        Scanner scanner = new Scanner(System.in);

        System.out.println("Creating new Floor");
        System.out.println("Enter the floor number: ");
        floor = Integer.parseInt(scanner.nextLine());

        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

        System.out.println("Enter the recive port (20): ");
        int recvPort = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter the destination port(21): ");
        int destPort = Integer.parseInt(scanner.nextLine());


        int len = 32;
        DatagramPacket packet = new DatagramPacket(new byte[len], len,serverAddress, destPort);



        while(true) {
            try {
                socket = new DatagramSocket(recvPort);
            } catch (SocketException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }


            byte[] data = packet.getData();

            switch (data[0]){
                    case 0b00000000://elevator arrived at floor
                        if(data[1] == 0b00000000) {
                            elevatorState = elevatorState.NOT_ARRIVED;
                        }else if(data[1] == 0b00000001) {
                            elevatorState = elevatorState.ARRIVED;
                        }else{
                            System.out.println("Invalid elevator state");
                            elevatorState = elevatorState.NOT_ARRIVED;
                        }
                        break;

                    case 0b00000001://floor button pressed
                        if(data[1] == 0b00000000) {
                            buttonState = buttonState.UP;

                        }else if(data[1] == 0b00000001) {
                            buttonState = buttonState.DOWN;
                        }else{
                            System.out.println("Invalid button state");
                            buttonState = buttonState.NONE;
                        }

                        //send button state to scheduler
                        byte[] command = new byte[3];
                        command[0] = 0b00000000;
                        command[1] = data[1];
                        command[2] = (byte) floor;
                        packet.setData(command);
                        try {
                            socket.send(packet);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

            }


        }

    }


}