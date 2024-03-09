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

    static int port = 20;

    public static void main(String[] args) throws IOException {
        doorState = doorState.CLOSED;
        direction = direction.NONE;
        floor = 1;

        System.out.println("Creating new Elevator");
        System.out.println("Enter the elevator number: ");
        Scanner scanner = new Scanner(System.in);
        elevNum = scanner.nextInt();


        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");


        int recvPort = 21+elevNum;

        //System.out.println("Enter the destination port: ");
        int destPort = 21;


        int len = 32;
        DatagramPacket packet = new DatagramPacket(new byte[len], len,serverAddress, destPort);
        byte[] command = new byte[3];
        try {
            socket = new DatagramSocket(recvPort);
        } catch (SocketException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }

        while(true){

            System.out.println("Elevator " + elevNum + " is waiting for a command.");
            socket.receive(packet);
            byte[] data = packet.getData();

            System.out.println("Data: " + data[0] + " " + data[1] + " " + data[2] );
            switch (data[0]){
                case 0b00000000://go to floor
                    System.out.println("go to floor");
                    int newFloor = data[1];
                    //System.out.println("Received: " + new String(data, 0, packet.getLength()));
                    System.out.println("go to floor");
                    moveElevator(newFloor);
                    doorState = doorState.OPEN;//todo make this in scheduler
                    command = new byte[3];
                    command[0] = 0b00000001;//send arrived at floor
                    command[1] = (byte) floor;
                    command[2] = (byte) elevNum;
                    packet.setData(command);
                    socket.send(packet);
                    floor = newFloor;

                    break;

            case 0b00000001://open door
                System.out.println("open door");
                if(data[1] == 0b00000000) {//open door
                    System.out.println("Received: " + new String(data, 0, packet.getLength()));
                    System.out.println("open door");
                    doorState = doorState.OPEN;
                }else {//close door
                    System.out.println("Received: " + new String(data, 0, packet.getLength()));
                    System.out.println("close door");
                    doorState = doorState.CLOSED;
                }


                break;

                case 0b00000010://elevator button pressed

                    //System.out.println("Received: " + new String(data, 0, packet.getLength()));
                    System.out.println("elevator button pressed");
                    command = new byte[3];
                    command[0] = 0b00000010;
                    command[1] = data[1];
                    command[2] = (byte) elevNum;

                    packet.setPort(21);
                    packet.setData(command);
                    socket.send(packet);
                    break;


            }
        }

    }



    static void moveElevator(int destFloor){
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