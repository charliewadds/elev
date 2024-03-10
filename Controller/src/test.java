import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;


public class test {

    static DatagramSocket sendSocket;

    static DatagramSocket elevSocket;
    static DatagramSocket floorSocket;
    @Test
    public void testElevator() throws IOException {
        System.out.println("Testing Elevator");
        System.out.println("make sure only one instance of the elevator is running and nothing else");
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

        DatagramPacket packet = new DatagramPacket(new byte[5], 5,serverAddress, 22);

        try {
            sendSocket = new DatagramSocket(21);
        } catch (SocketException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }

        byte[] command = new byte[3];
        //test Go to floor
        command[0] = 0b00000000;
        command[1] = 0b00000001;

        packet.setData(command);
        try {
            sendSocket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sendSocket.receive(packet);
        byte[] data = packet.getData();
        assertEquals(0b00000001, data[0]);
        assertEquals(0b00000001, data[1]);
        assertEquals(0b00000001, data[2]);

        //test open door

        //test elevator button pressed
        command[0] = 0b00000010;
        command[1] = 0b00000001;

        packet.setData(command);
        try {
            sendSocket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sendSocket.receive(packet);
        data = packet.getData();
        assertEquals(0b00000010, data[0]);
        assertEquals(0b00000001, data[1]);
        assertEquals(0b00000001, data[2]);

    }

    @Test
    public void testScheduler() throws IOException {
        System.out.println("Testing Scheduler");
        System.out.println("make sure only one instance of the scheduler is running and nothing else");
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

        DatagramPacket packet = new DatagramPacket(new byte[5], 5, serverAddress, 21);

        try {
            //sendSocket = new DatagramSocket(21);
            elevSocket = new DatagramSocket(22);
            floorSocket = new DatagramSocket(20);
        } catch (SocketException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }


        byte[] command = new byte[3];
        //test up/down button pressed
        command[0] = 0b00000000;
        command[1] = 0b00000001;
        command[2] = 0b00000010;

        packet.setData(command);
        try {
            elevSocket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        elevSocket.receive(packet);
        byte[] data = packet.getData();
        System.out.println("Data: " + data[0] + " " + data[1] + " " + data[2] );
        assertEquals(0b00000000, data[0]);
        assertEquals(0b00000001, data[1]);




        //test elevator arrived at floor
        command[0] = 0b00000001;
        command[1] = 0b00000001;

        packet.setData(command);
        try {
            floorSocket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        floorSocket.receive(packet);
        data = packet.getData();
        assertEquals(0b00000001, data[0]);
        assertEquals(0b00000000, data[1]);


    }

    @Test
    public void testFloor() throws IOException {

        try {
            sendSocket = new DatagramSocket(21);
        } catch (SocketException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }

        System.out.println("Testing Floor");
        System.out.println("make sure only one instance of the floor is running and nothing else");
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");

        DatagramPacket packet = new DatagramPacket(new byte[5], 5,serverAddress, 20);



        //test open door
        byte[] command = new byte[2];
        //test elevator button pressed
        command[0] = 0b00000001;
        command[1] = 0b00000001;

        packet.setData(command);
        try {
            sendSocket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sendSocket.receive(packet);
        byte[] data = packet.getData();
        assertEquals(0b00000000, data[0]);
        assertEquals(0b00000001, data[1]);


    }





}
