import java.io.IOException;
import java.net.*;
import java.util.*;





public class Main {
    static List<Map<String, Object>> elevators = new ArrayList<Map<String, Object>>();
    static List<Map<String, Object>> floors = new ArrayList<Map<String, Object>>();

    static int floor;
    static DatagramSocket socket;




    public static void main(String[] args) throws IOException, InterruptedException {


        Scanner scanner = new Scanner(System.in);

        //-----------------
        System.out.println("Enter the number of elevators: ");
        int numElevs = scanner.nextInt();

        System.out.println("Enter the number of floors: ");
        int numFloors = scanner.nextInt();

        for(int i = 0; i < numElevs; i++){
            Map<String, Object> elevator = new HashMap<String, Object>();
            elevator.put("elevNum", i);
            elevator.put("doorState", "CLOSED");
            elevator.put("direction", "NONE");
            elevator.put("floor", 1);
            elevators.add(elevator);
        }

        for(int i = 0; i < numFloors; i++){
            Map<String, Object> floor = new HashMap<String, Object>();
            floor.put("floorNum", i);
            floor.put("buttonState", "NONE");
            floor.put("elevatorState", "NOT_ARRIVED");
            floors.add(floor);
        }


        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        int len = 32;
        DatagramPacket floorPacket = new DatagramPacket(new byte[len], len,serverAddress, 20);
        DatagramPacket elevPacket = new DatagramPacket(new byte[len], len,serverAddress, 22);

        try {
            socket = new DatagramSocket(21);
        } catch (SocketException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }

        while(true) {

            System.out.println("Waiting for data");
            socket.receive(floorPacket);
            System.out.println("Received data");

            byte[] data = floorPacket.getData();
            System.out.println("Data: " + data[0] + " " + data[1] + " " + data[2] + " " + data[3]);
            byte[] command = new byte[5];
            switch (data[0]){
                case 0b00000000://floor button pressed
                    System.out.println("Floor button pressed");
                   int upOrDown = data[1];
                   int floor = data[2];
                   int elevNum = 1;//todo get closest elevator

                    if((int) elevators.get(elevNum-1).get("floor") != floor){//if the elevator is not already on the floor
                        System.out.println("Elevator is not on the floor");
                        command[0] = 0b00000001;//send arrived at floor to floor
                        floorPacket.setPort(21-floor);
                        floorPacket.setData(command);
                        socket.send(floorPacket);
                        break;
                    }

                    command[0] = 0b00000001;//open doors
                    command[1] = 0b00000000;
                    elevPacket.setPort(21+elevNum);
                    elevators.get(elevNum-1).put("doorState", "OPEN");//open the door
                    elevPacket.setData(command);
                    socket.send(elevPacket);

                    break;

                case 0b00000001://elevator reached floor
                    System.out.println("Elevator reached floor");
                    int newFloor = data[1];
                    elevators.get(data[2]-1).put("floor", newFloor);//update the floor of the elevator
                    elevators.get(data[2]-1).put("doorState", "OPEN");//open the door
                    Thread.sleep(1000);//wait for the door

                    command[0] = 0b00000001;//send open door
                    command[1] = 0b00000000;
                    elevPacket.setPort(21+data[2]);
                    System.out.println("sendCloseDoor");
                    elevators.get(data[2]-1).put("doorState", "CLOSED");//open the door
                    elevPacket.setData(command);
                    socket.send(elevPacket);


                    command[0] = 0b00000000;//send arrived at floor to floor
                    floorPacket.setPort(21-newFloor);
                    floorPacket.setData(command);
                    socket.send(floorPacket);
                    break;

                case 0b00000010://elevator button pressed
                    System.out.println("Elevator button pressed");
                    int floorNum = data[1];
                    int elev = data[2];
                    command[0] = 0b00000000;//go to floor
                    command[1] = (byte) floorNum;
                    elevPacket.setPort(21+elev);
                    elevPacket.setData(command);
                    socket.send(elevPacket);

                    break;

            }


        }

    }


}