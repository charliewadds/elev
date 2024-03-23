import java.io.IOException;
import java.net.*;
import java.util.*;

public class Scheduler implements Runnable{

    static final double MOVE_MAX_TIME = 10000;//10 secon ds
    static List<Map<String, Object>> elevators = new ArrayList<Map<String, Object>>();
    static List<Map<String, Object>> floors = new ArrayList<Map<String, Object>>();

    static int floor;
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

        Scanner scanner = new Scanner(System.in);
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

//        try {
//            socket = new DatagramSocket(8000);
//        } catch (SocketException e) {
//            System.out.println(e);
//            throw new RuntimeException(e);
//        }



        System.out.println("SETUP: \n\n");

        System.out.println("Enter the number of elevators: ");
        int numElevators = scanner.nextInt();
        String ElevatorIp[] = new String[numElevators];
        int ElevatorPort[] = new int[numElevators];

        System.out.println("Enter the number of floors: \n\n");
        int numFloors = scanner.nextInt();
        String FloorIp[] = new String[numFloors];
        int FloorPort[] = new int[numFloors];


        for(int i = 0; i < numElevators; i++){
            System.out.println("is the elevator remote or local? (r/l)");
            String remote = scanner.next();
            if(remote.equals("r")) {
                System.out.println("Enter the IP address of elevator " + i + ": ");
                ElevatorIp[i] = scanner.next();
//                System.out.println("Enter the port number of elevator " + i + ": ");
//                ElevatorPort[i] = scanner.next();

            }else{
                //System.out.println("Enter the port number of elevator " + i + ": (this has to be the same as the one the elevator will ask for)");
                //ElevatorPort[i] = scanner.next();
                ElevatorIp[i] = "127.0.0.1";

                Elevator newElevator = new Elevator();
                Thread elevatorThread = new Thread(newElevator);
                elevatorThread.setDaemon(true);
                elevatorThread.start();

            }

            System.out.println("Waiting for response from elevator " + (i + 1) + "...");
            socket.receive(elevPacket);
            System.out.println("Response received from elevator " + (i + 1) + "...");
            ElevatorPort[i] = (int) elevPacket.getData()[0];

            if(remote.equals("l")){
                floorPacket.setData(new byte[]{(byte) (i+1)});
                floorPacket.setAddress(InetAddress.getByName(ElevatorIp[i]));
                floorPacket.setPort(ElevatorPort[i]);
                socket.send(floorPacket);

            }
            if(elevPacket.getAddress().getHostAddress().equals(ElevatorIp[i])){
                System.out.println("Elevator " + (i+1) + " is ready, the port is: " + ElevatorPort[i]);
            }else{
                System.out.println("Elevator " + (i +1)+ " is not ready");
            }

        }

        /////////////////////////////////
        for(int i = 0; i < numFloors; i++){
            System.out.println("is the floor remote or local? (r/l)");
            String remote = scanner.next();
            if(remote.equals("r")) {
                System.out.println("Enter the IP address of floor " + i + ": ");
                FloorIp[i] = scanner.next();
//                System.out.println("Enter the port number of elevator " + i + ": ");
//                ElevatorPort[i] = scanner.next();

            }else{
                //System.out.println("Enter the port number of elevator " + i + ": (this has to be the same as the one the elevator will ask for)");
                //ElevatorPort[i] = scanner.next();
                FloorIp[i] = "127.0.0.1";

                Floor newFloor = new Floor();
                Thread floorThread = new Thread(newFloor);
                floorThread.start();

            }

            System.out.println("Waiting for response from floor " + i + "...");
            socket.receive(elevPacket);
            System.out.println("Response received from floor " + i + "...");
            FloorPort[i] = (int) elevPacket.getData()[0];

            if(remote.equals("l")){
                floorPacket.setData(new byte[]{(byte) (i+1)});
                floorPacket.setAddress(InetAddress.getByName(FloorIp[i]));
                floorPacket.setPort(FloorPort[i]);
                socket.send(floorPacket);

            }
            if(elevPacket.getAddress().getHostAddress().equals(FloorIp[i])){
                System.out.println("Floor " + (i+1) + " is ready, the port is: " + FloorPort[i]);
            }else{
                System.out.println("Floor " + (i +1)+ " is not ready");
            }

        }

        for(int i = 0; i < numElevators; i++){
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




        while(true) {


            double moveTimers[] = new double[numElevators];//timers for each elevator
            for(int i = 0; i < numElevators; i++){
                moveTimers[i] = -1;
            }
            System.out.println("(scheduler) Waiting for data");
            socket.receive(floorPacket);
            System.out.println("(scheduler) Received data");

            byte[] data = floorPacket.getData();
            //System.out.println("Data: " + data[0] + " " + data[1] + " " + data[2] + " " + data[3]);
            byte[] command = new byte[5];
            switch (data[0]){


                case 0b00000000://floor button pressed, elevator should reach floor in 10 seconds max
                    System.out.println("(scheduler) Floor button pressed");
                    int upOrDown = data[1];
                    int floor = data[2];
                    int elevNum = findClosestElev(floor);

                    if((int) elevators.get(elevNum-1).get("floor") != floor){//if the elevator is not already on the floor
                        System.out.println("(scheduler) Elevator is not on the floor");
                        Thread.sleep(1000);
                        command[0] = 0b00000000;//send move to floor
                        command[1] = (byte) floor;
                        elevPacket.setAddress(InetAddress.getByName(ElevatorIp[elevNum]));
                        elevPacket.setPort(ElevatorPort[elevNum]);
                        elevPacket.setData(command);
                        socket.send(floorPacket);
                        moveTimers[elevNum] = System.currentTimeMillis();//set the move timer for the elevator
                        break;
                    }

                    command[0] = 0b00000001;//open doors
                    command[1] = 0b00000000;
                    elevPacket.setAddress(InetAddress.getByName(ElevatorIp[elevNum]));
                    elevPacket.setPort(ElevatorPort[elevNum]);
                    elevators.get(elevNum-1).put("doorState", "OPEN");//open the door
                    elevPacket.setData(command);
                    socket.send(elevPacket);

                    break;

                case 0b00000001://elevator reached floor
                    System.out.println("(scheduler) Elevator reached floor");
                    int newFloor = data[1];
                    elevators.get(data[2]-1).put("floor", newFloor);//update the floor of the elevator
                    elevators.get(data[2]-1).put("doorState", "OPEN");//open the door
                    //Thread.sleep(1000);//wait for the door

                    command[0] = 0b00000001;//send open door
                    command[1] = 0b00000000;
                    elevPacket.setData(command);
                    elevPacket.setAddress(InetAddress.getByName(ElevatorIp[data[2]]));
                    elevPacket.setPort(ElevatorPort[data[2]]);
                    System.out.println("(scheduler) sendOpenDoor");//todo implement door wait max time
                    socket.send(elevPacket);

                    socket.receive(elevPacket);
                    byte[] doorData = elevPacket.getData();
                    if(doorData[0] == 0b00000001 && doorData[1] == data[2]){
                        System.out.println("(scheduler) Door is open");
                        moveTimers[data[2]] = -1;//reset the move timer

                    }else{
                        System.out.println("(scheduler) DOOR ERROR");
                    }


                    elevPacket.setAddress(InetAddress.getByName(ElevatorIp[data[2]]));
                    elevPacket.setPort(ElevatorPort[data[2]]);
                    System.out.println("(scheduler) sendCloseDoor");
                    elevators.get(data[2]-1).put("doorState", "CLOSED");//open the door
                    elevPacket.setData(command);
                    socket.send(elevPacket);


                    command[0] = 0b00000000;//send arrived at floor to floor
                    floorPacket.setAddress(InetAddress.getByName(FloorIp[newFloor]));
                    floorPacket.setPort(FloorPort[newFloor]);
                    floorPacket.setData(command);
                    socket.send(floorPacket);
                    break;

                case 0b00000010://elevator button pressed
                    System.out.println("(scheduler) Elevator button pressed");
                    int floorNum = data[1];
                    int elev = data[2];
                    command[0] = 0b00000000;//go to floor
                    command[1] = (byte) floorNum;
                    elevPacket.setAddress(InetAddress.getByName(ElevatorIp[elev]));
                    elevPacket.setPort(ElevatorPort[elev]);
                    elevPacket.setData(command);
                    socket.send(elevPacket);

                    break;

            }

            for(int i = 0; i< moveTimers.length; i++){
                if(moveTimers[i] != -1){
                    if(System.currentTimeMillis() - moveTimers[i] > MOVE_MAX_TIME){
                        System.out.println("(scheduler) Elevator " + i + " failed to reach floor in time");
                        //todo send a message to the floor
                    }
                }
            }
        }

    }


    private static int findClosestElev(int floor){
        int closestElev = 1;
        int minDist = 100;
        for(int i = 1; i < elevators.size(); i++){
            int dist = Math.abs((int) elevators.get(i).get("floor") - floor);
            if(dist < minDist){
                minDist = dist;
                closestElev = i;
            }
        }
        return closestElev;
    }


}