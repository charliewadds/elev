import java.io.IOException;
import java.net.*;
import java.util.Scanner;

enum doorState{
    OPEN, CLOSED
}
enum direction{
    UP, DOWN, NONE
}


public class Elevator implements Runnable{
    static doorState doorState;
    static direction direction;
    static int floor;
    static int elevNum;
    static DatagramSocket socket;

    static int port = 20;
    @Override
    public void run() {

        try {
            main(null);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Elevator is running");
        doorState = doorState.CLOSED;
        direction = direction.NONE;
        floor = 1;
        int recvPort;

//        System.out.println("Creating new Elevator");
//        System.out.println("Enter the elevator number: ");
//        Scanner scanner = new Scanner(System.in);
//        elevNum = scanner.nextInt();
        Thread.sleep(1000);
        System.out.println("(unknown Elevator) enter port number");
        Scanner scanner = new Scanner(System.in);
        recvPort = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Elevator port: " + elevNum);


        System.out.println("enter the ip of the scheduler (if local enter 127.0.0.1)");
        String ip = scanner.nextLine();


        InetAddress serverAddress = InetAddress.getByName(ip);




        System.out.println("enter the port of the scheduler (default 21)");
        int destPort = scanner.nextInt();
        scanner.nextLine();


        int len = 32;
        DatagramPacket packet = new DatagramPacket(new byte[len], len,serverAddress, destPort);
        byte[] command = new byte[3];
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
        //
        packet.setData(new byte[]{(byte) recvPort});
        packet.setAddress(serverAddress);
        packet.setPort(destPort);
        socket.send(packet);

        socket.receive(packet);
        System.out.println("Received: " +(int) packet.getData()[0]);
        elevNum = packet.getData()[0];

        while(true){

            System.out.println("(Elevator " + elevNum + ")  waiting for a command.");
            socket.receive(packet);
            byte[] data = packet.getData();

            System.out.println("Data: " + data[0] + " " + data[1] + " " + data[2] );
            switch (data[0]){
                case 0b00000000://go to floor
                    //System.out.println("go to floor");
                    int newFloor = data[1];
                    //System.out.println("Received: " + new String(data, 0, packet.getLength()));
                    System.out.println("go to floor");
                    if(isDoorClosed()){
                        System.out.println("Door closed");
                        moveElevator(newFloor);
                        doorState = doorState.OPEN;//todo make this in scheduler
                        command = new byte[3];
                        command[0] = 0b00000001;//send arrived at floor
                        command[1] = (byte) floor;
                        command[2] = (byte) elevNum;
                        packet.setData(command);
                        packet.setAddress(serverAddress);
                        packet.setPort(destPort);

                        socket.send(packet);
                        floor = newFloor;
                    }
                    System.out.println("Door Fault");
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
                    packet.setAddress(serverAddress);
                    packet.setPort(destPort);
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

    public static boolean isDoorClosed(){
        return doorState == doorState.CLOSED;
    }

    public static doorState getDoorState(){
        return doorState;
    }
}