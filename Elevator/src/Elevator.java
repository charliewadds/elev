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
        System.out.println("Elevator is running (default to port 8001 until updated)");
        doorState = doorState.CLOSED;
        direction = direction.NONE;
        floor = 1;
        int elevNum;
        int recvPort = 8001;
        int destPort = 21;
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        int len = 32;
        DatagramPacket packet = new DatagramPacket(new byte[len], len,serverAddress, destPort);
        DatagramPacket setupPacket = new DatagramPacket(new byte[len], len,serverAddress, destPort);
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



        //get new port
        System.out.println("Elevator is waiting for new port");
        socket.receive(setupPacket);
        recvPort = setupPacket.getData()[0];
        socket.close();
        try {
            socket = new DatagramSocket(recvPort);//reset port

        } catch (SocketException e) {
            System.out.println("port number: " + recvPort + " is already in use");
            System.out.println(e);
        }
        System.out.println("Elevator got new port: " + recvPort);





        //get elevator number
        System.out.println("Elevator is waiting for number");
        socket.receive(setupPacket);
        elevNum = setupPacket.getData()[0];
        System.out.println("Elevator got number: " + elevNum);




        while(true){

            //System.out.println("(Elevator " + elevNum + ")  waiting for a command.");

            socket.receive(packet);
            byte[] data = packet.getData();

            //System.out.println("Data: " + data[0] + " " + data[1] + " " + data[2] );
            switch (data[0]){
                case 0b00000000://go to floor
                    //System.out.println("go to floor");
                    int newFloor = data[1];
                    //System.out.println("Received: " + new String(data, 0, packet.getLength()));
                    System.out.println("(Elevator " + elevNum + ") go to floor");
                    if(isDoorClosed()){
                        System.out.println("(Elevator " + elevNum + ") Door closed");
                        moveElevator(newFloor);
                        doorState = doorState.OPEN;//todo make this in scheduler
                        command = new byte[3];
                        command[0] = 0b00000001;//send arrived at floor
                        command[1] = (byte) floor;
                        command[2] = (byte) elevNum;
                        packet.setData(command);
                        packet.setAddress(serverAddress);
                        packet.setPort(destPort);
                        Thread.sleep(100);
                        socket.send(packet);
                        floor = newFloor;
                    }

                    break;

                case 0b00000001://open door
                    System.out.println("(Elevator " + elevNum + ") open/close door");
                    if(data[1] == 0b00000000) {//open door
                        //System.out.println("(Elevator " + elevNum + ") Received: " + new String(data, 0, packet.getLength()));
                        System.out.println("(Elevator " + elevNum + ")opening door");
                        Thread.sleep(5000);
                        System.out.println("(Elevator " + elevNum + ") door opened");
                        packet.setData(new byte[]{0b00000001, (byte) elevNum});
                        socket.send(packet);
                        doorState = doorState.OPEN;
                    }else {//close door
                        //System.out.println("Received: " + new String(data, 0, packet.getLength()));
                        System.out.println("(Elevator " + elevNum + ") closing door");
                        Thread.sleep(5000);
                        packet.setData(new byte[]{0b00000100, (byte) elevNum});
                        socket.send(packet);
                        System.out.println("(Elevator " + elevNum + ") door closed\n\n\n");
                        doorState = doorState.CLOSED;
                    }


                    break;

                case 0b00000010://elevator button pressed

                    //System.out.println("Received: " + new String(data, 0, packet.getLength()));
                    System.out.println("(Elevator " + elevNum + ") elevator button pressed\n\n\n");
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