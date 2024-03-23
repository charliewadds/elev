import java.io.IOException;
import java.net.*;
import java.util.Scanner;

enum buttonState{
    UP, DOWN, NONE
}

enum elevatorState{
    ARRIVED, NOT_ARRIVED
}

public class Floor implements Runnable{

    static buttonState buttonState;
    static elevatorState elevatorState;
    static int floorNum;


    static DatagramSocket socket;
    @Override
    public void run() {
        try {
            main(null);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws IOException, InterruptedException {
        elevatorState = elevatorState.NOT_ARRIVED;
        buttonState = buttonState.NONE;
        int recvPort;


        Thread.sleep(1000);
        System.out.println("(unknown Floor) enter port number");
        Scanner scanner = new Scanner(System.in);
        recvPort = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Floor port: " + floorNum);


        System.out.println("enter the ip of the scheduler (if local enter 127.0.0.1)");
        String ip = scanner.nextLine();
        InetAddress serverAddress = InetAddress.getByName(ip);

        System.out.println("enter the port of the scheduler (default 21)");
        int destPort = scanner.nextInt();
        scanner.nextLine();



        int len = 32;
        DatagramPacket packet = new DatagramPacket(new byte[len], len, serverAddress, destPort);

        while(true) {
            try {
                socket = new DatagramSocket(recvPort);
                break;
            } catch (SocketException e) {
                System.out.println("port number: " + recvPort + " is already in use");
                System.out.println(e);
                //throw new RuntimeException(e);
            }
            System.out.println("Try a different port number");
        }

        packet.setData(new byte[]{(byte) recvPort});
        packet.setAddress(serverAddress);
        packet.setPort(destPort);
        socket.send(packet);

        socket.receive(packet);
        System.out.println("Received: " +(int) packet.getData()[0]);
        floorNum = packet.getData()[0];

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
                        if(!Elevator.isDoorClosed()){
                            System.out.println("System failed. Elevator door is not closed");
                        }
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
                        packet.setPort(destPort);
                        socket.send(packet);
                        System.out.println("Sent data");

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

            }


        }

    }


}