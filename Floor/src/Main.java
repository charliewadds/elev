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
    static int floorNum;


    static DatagramSocket socket;

    public static void main(String[] args) throws IOException {
        elevatorState = elevatorState.NOT_ARRIVED;
        buttonState = buttonState.NONE;



        Scanner scanner = new Scanner(System.in);

        System.out.println("Creating new Floor");
        System.out.println("Enter the floor number: ");
        floorNum = Integer.parseInt(scanner.nextLine());

        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

        //System.out.println("Enter the recive port (20): ");
        int recvPort = 21 - floorNum;

        //System.out.println("Enter the destination port(21): ");
        int destPort = 21;


        int len = 32;
        DatagramPacket packet = new DatagramPacket(new byte[len], len, serverAddress, destPort);


        try {
            socket = new DatagramSocket(recvPort);
        } catch (SocketException e) {
            System.out.println("port number: " + recvPort + " is already in use");
            System.out.println(e);
            throw new RuntimeException(e);
        }
        while(true) {

            System.out.println("Waiting for data");
            socket.receive(packet);
            System.out.println("Received data");

            byte[] data = packet.getData();

            System.out.println("Data: " + data[0] + " " + data[1] + " " + data[2]);
            switch (data[0]){

                    case 0b00000000://elevator arrived at floor
                        System.out.println("Elevator arrived at floor");
                        elevatorState = elevatorState.NOT_ARRIVED;
                        if (data[1] == 0b00000000) {
                            elevatorState = elevatorState.NOT_ARRIVED;
                        } else if (data[1] == 0b00000001) {
                            System.out.println("Elevator arrived at floor " + floorNum);
                            elevatorState = elevatorState.ARRIVED;
                        } else {
                            System.out.println("Invalid elevator state");
                            elevatorState = elevatorState.NOT_ARRIVED;
                        }

                        break;

                    case 0b00000001://floor button pressed
                        System.out.println("up/down button pressed");
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
                        command[2] = (byte) floorNum;
                        packet.setData(command);


                        try {
                            packet.setPort(21);
                            socket.send(packet);
                            System.out.println("Sent data");

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

            }


        }

    }


}