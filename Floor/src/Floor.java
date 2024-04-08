import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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

        System.out.println("Floor is running (default to port 8001 until updated)");



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
        System.out.println("Floor is waiting for new port");
        socket.receive(setupPacket);
        recvPort = setupPacket.getData()[0];
        socket.close();
        try {
            socket = new DatagramSocket(recvPort);//reset port

        } catch (SocketException e) {
            System.out.println("port number: " + recvPort + " is already in use");
            System.out.println(e);
        }

        System.out.println("Floor got new port: " + recvPort);





        //get elevator number
        System.out.println("Floor is waiting for number");
        socket.receive(setupPacket);
        floorNum = setupPacket.getData()[0];
        System.out.println("Floor got number: " + floorNum);

        byte[] data = new byte[5];
        while(true) {

            System.out.println("(floor " + floorNum + ") Waiting for data\n\n\n\n");
            socket.receive(packet);
            System.out.println("(floor  " + floorNum + ") Received data");

            data = packet.getData().clone();
            System.out.println("Data: " + data[0]);
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
                    command = new byte[3];
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