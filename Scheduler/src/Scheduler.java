import java.io.IOException;
import java.net.*;
import java.util.*;

public class Scheduler implements Runnable {
    static final int MAX_ELEVATORS = 100;
    private static int numElevators = 0;
    private static int numFloors = 0;
    static final double MOVE_MAX_TIME = 1;//10 secon ds
    static List<Map<String, Object>> elevators = new ArrayList<Map<String, Object>>();
    static List<Map<String, Object>> floors = new ArrayList<Map<String, Object>>();

    static String[] ElevatorIp = new String[MAX_ELEVATORS];
    static int[] ElevatorPort = new int[MAX_ELEVATORS];

    static String[] FloorIp = new String[MAX_ELEVATORS];
    static int[] FloorPort = new int[MAX_ELEVATORS];
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
        DatagramPacket floorPacket = new DatagramPacket(new byte[len], len, serverAddress, 20);
        DatagramPacket elevPacket = new DatagramPacket(new byte[len], len, serverAddress, 22);
        DatagramPacket setupPacket = new DatagramPacket(new byte[len], len, serverAddress, 22);

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


        while (true) {


            double[] moveTimers = new double[numElevators];//timers for each elevator
            for (int i = 0; i < numElevators; i++) {
                moveTimers[i] = -1;
            }
            //System.out.println("(scheduler) Waiting for data");
            socket.receive(floorPacket);
            //System.out.println("(scheduler) Received data");
            byte[] data = floorPacket.getData();
            //System.out.println("Data: " + data[0] + " " + data[1] + "\n\n");
            byte[] command = new byte[5];
            switch (data[0]) {

                case 0b00111111://guiInfo
                    System.out.println("(scheduler) GUI info\n\n");
                    command[0] = (byte) numElevators;
                    //System.out.println("(scheduler) numElevators: " + numElevators);
                    command[1] = (byte) numFloors;
                    //System.out.println("(scheduler) numFloors: " + numFloors);
                    setupPacket.setData(command);//todo might need to change size
                    setupPacket.setAddress(InetAddress.getByName("127.0.0.1"));
                    setupPacket.setPort(8000);

                    socket.send(setupPacket);


                    break;


                case 0b01111111://addElev
                    System.out.println("(scheduler) addElev");

                    //byte[] setupData = setupPacket.getData();
                    System.out.println("(scheduler) Elevator port: " + data[1] + "\n\n");
                    addElevator("127.0.0.1", data[1]);//todo this only works for localhost right now
                    break;

                case 0b01111110://addFloor
                    System.out.println("(scheduler) addFloor");

                    //byte[] setupData = setupPacket.getData();
                    System.out.println("(scheduler) Floor port: " + data[1] + "\n\n");
                    addFloor("127.0.0.1", data[1]);//todo this only works for localhost right now
                    break;

                case 0b00000000://floor button pressed, elevator should reach floor in 10 seconds max todo I think this is wrong, not sure why, bad vibes
                    System.out.println("(scheduler) Floor button pressed\n\n");
                    int upOrDown = data[1];
                    int floor = data[2];
                    int elevNum = findClosestElev(floor);

                    if ((int) elevators.get(elevNum - 1).get("floor") != floor) {//if the elevator is not already on the floor
                        System.out.println("(scheduler) Elevator is not on the floor\n\n");
                        Thread.sleep(1000);
                        command[0] = 0b00000000;//send move to floor
                        command[1] = (byte) floor;
                        command[2] = (byte) elevNum;
                        elevPacket.setAddress(InetAddress.getByName(ElevatorIp[elevNum - 1]));
                        elevPacket.setPort(ElevatorPort[elevNum - 1]);
                        elevPacket.setData(command);
                        socket.send(floorPacket);
                        moveTimers[elevNum - 1] = System.currentTimeMillis();//set the move timer for the elevator
                        break;
                    }

                    command[0] = 0b00000001;//open doors
                    command[1] = 0b00000000;
                    command[2] = (byte) elevNum;
                    elevPacket.setAddress(InetAddress.getByName(ElevatorIp[elevNum - 1]));
                    elevPacket.setPort(ElevatorPort[elevNum - 1]);
                    elevators.get(elevNum - 1).put("doorState", "OPEN");//open the door
                    elevPacket.setData(command);
                    socket.send(elevPacket);

                    break;

                case 0b00000001://elevator reached floor
                    System.out.println("(scheduler) Elevator reached floor\n\n");
                    int newFloor = data[1];
                    int currElevNum;
                    elevators.get(data[2] - 1).put("floor", newFloor);//update the floor of the elevator
                    elevators.get(data[2] - 1).put("doorState", "OPEN");//open the door
                    //Thread.sleep(1000);//wait for the door
                    setupPacket.setPort(8000);//todo magic number
                    setupPacket.setData(new byte[]{0b00000001, data[2], (byte) newFloor});
                    socket.send(setupPacket);//todo this only works if it is local
                    command[0] = 0b00000001;//send open door
                    command[1] = 0b00000000;
                    command[2] = data[2];
                    elevPacket.setData(command);
                    elevPacket.setAddress(InetAddress.getByName(ElevatorIp[data[2] - 1]));
                    elevPacket.setPort(ElevatorPort[data[2] - 1]);
                    //System.out.println("(scheduler) sendOpenDoor");//todo implement door wait max time
                    double doorTimer = System.currentTimeMillis();
                    socket.send(elevPacket);
                    socket.receive(elevPacket);
                    byte[] doorData = elevPacket.getData();
                    System.out.println("Door timer " + doorTimer);
                    System.out.println("CurrTime " + (double) System.currentTimeMillis());
                    if (doorData[0] == 0b00000001 && doorData[1] == data[2] && (doorTimer < System.currentTimeMillis() - 10000)) {
                        System.out.println("(scheduler) Door is open\n\n");
                        moveTimers[data[2] - 1] = -1;//reset the move timer

                    } else {
                        System.out.println("(scheduler) DOOR ERROR\n\n");
                        sendError(data[2]);
                    }


                    elevPacket.setAddress(InetAddress.getByName(ElevatorIp[data[2] - 1]));
                    elevPacket.setPort(ElevatorPort[data[2] - 1]);
                    currElevNum = data[2];
                    System.out.println("(scheduler) sendCloseDoor\n\n");
                    elevators.get(data[2] - 1).put("doorState", "CLOSED");//open the door
                    command[0] = 0b00000001;//send close door
                    command[1] = 0b00000001;

                    elevPacket.setData(command);
                    socket.send(elevPacket);


                    command[0] = 0b00000000;//send arrived at floor to floor
                    //command[1] = (byte) newFloor;
                    //command[2] = (byte) data[2];
                    command[1] = 0b00000001;

                    floorPacket.setAddress(InetAddress.getByName(FloorIp[newFloor]));
                    floorPacket.setPort(FloorPort[newFloor]);
                    floorPacket.setData(command);
                    socket.send(floorPacket);





                    break;

                case 0b00000010://elevator button pressed
                    System.out.println("(scheduler) Elevator button pressed");
                    int floorNum = data[1];
                    int elev = data[2];
                    command[0] = 0b0000000;//go to floor
                    command[1] = (byte) floorNum;
                    command[2] = 0b0000000;
                    System.out.println("(scheduler) ElevatorIP");
                    System.out.println(ElevatorIp[elev - 1]);
                    elevPacket.setAddress(InetAddress.getByName(ElevatorIp[elev - 1]));
                    System.out.println("(scheduler) ElevatorPort");
                    System.out.println(ElevatorPort[elev - 1]);
                    elevPacket.setPort(ElevatorPort[elev - 1]);
                    elevPacket.setData(command);
                    System.out.println("(scheduler) sendMoveToFloor");
                    Thread.sleep(100);
                    socket.send(elevPacket);

                    break;

                case 0b00000011://door Opened

                    System.out.println("(scheduler) Door opened");
                    break;

                case 0b00000100://door Opened

                    System.out.println("(scheduler) Door closed");
                    break;

            }

            for (int i = 0; i < moveTimers.length; i++) {
                if (moveTimers[i] != -1) {
                    if (System.currentTimeMillis() - moveTimers[i] > MOVE_MAX_TIME) {
                        System.out.println("(scheduler) Elevator " + i + " failed to reach floor in time");
                        //todo send a message to the floor
                    }
                }
            }
        }

    }


    private static int findClosestElev(int floor) {
        int closestElev = 1;
        int minDist = 100;
        for (int i = 1; i < elevators.size(); i++) {
            int dist = Math.abs((int) elevators.get(i).get("floor") - floor);
            if (dist < minDist) {
                minDist = dist;
                closestElev = i;
            }
        }
        return closestElev;
    }

    private static void sendError(int elevNum) throws IOException {
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        DatagramPacket ControllerPacket = new DatagramPacket(new byte[32], 32, serverAddress, 8000);
        byte[] command = new byte[3];
        command[0] = 0b00000010;
        command[1] = (byte) elevNum;
        ControllerPacket.setData(command);
        socket.send(ControllerPacket);
    }

    private static void sendResetError(int elevNum) throws IOException {
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        DatagramPacket ControllerPacket = new DatagramPacket(new byte[32], 32, serverAddress, 8000);
        byte[] command = new byte[3];
        command[0] = 0b00000011;
        command[1] = (byte) elevNum;
        ControllerPacket.setData(command);
        socket.send(ControllerPacket);
    }


    public static void addElevator(String ip, int port) throws IOException, InterruptedException {
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        DatagramPacket setupPacket = new DatagramPacket(new byte[32], 32, serverAddress, 22);
        Map<String, Object> elevator = new HashMap<String, Object>();
        elevator.put("elevNum", elevators.size());
        elevator.put("doorState", "CLOSED");
        elevator.put("direction", "NONE");
        elevator.put("floor", 1);
        elevators.add(elevator);

        //start a new thread for the elevator if the ip is local
        //the port will default to 80001
        if (ip.equals("127.0.0.1")) {
            Elevator newElevator = new Elevator();
            Thread elevatorThread = new Thread(newElevator);
            elevatorThread.start();
        }
        Thread.sleep(100);
        setupPacket.setPort(8001);
        setupPacket.setData(new byte[]{(byte) port});
        socket.send(setupPacket);
        setupPacket.setPort(port);
        Thread.sleep(100);
        setupPacket.setData(new byte[]{(byte) elevators.size()});
        socket.send(setupPacket);
        numElevators++;
        ElevatorIp[elevators.size() - 1] = ip;
        ElevatorPort[elevators.size() - 1] = port;

    }

    public static void addFloor(String ip, int port) throws IOException, InterruptedException {
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        DatagramPacket setupPacket = new DatagramPacket(new byte[32], 32, serverAddress, 22);
        Map<String, Object> floor = new HashMap<String, Object>();
        floor.put("floorNum", floors.size());
        floor.put("doorState", "CLOSED");
        floor.put("direction", "NONE");
        floor.put("floor", 1);
        floors.add(floor);

        //start a new thread for the floor if the ip is local
        //the port will default to 80001
        if (ip.equals("127.0.0.1")) {
            Floor newFloor = new Floor();
            Thread elevatorThread = new Thread(newFloor);
            elevatorThread.start();
        }
        Thread.sleep(100);
        setupPacket.setPort(8001);
        setupPacket.setData(new byte[]{(byte) port});
        socket.send(setupPacket);
        setupPacket.setPort(port);
        Thread.sleep(100);
        setupPacket.setData(new byte[]{(byte) floors.size()});
        socket.send(setupPacket);
        numFloors++;
        FloorIp[floors.size() - 1] = ip;
        FloorPort[floors.size() - 1] = port;

    }


}


/*
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
                elevatorThread.start();

            }

            System.out.println("Waiting for response from elevator " + (i + 1) + "...");
            socket.receive(setupPacket);
            System.out.println("Response received from elevator " + (i + 1) + "...");
            ElevatorPort[i] = (int) setupPacket.getData()[0];

            if(remote.equals("l")){
                setupPacket.setData(new byte[]{(byte) (i+1)});
                setupPacket.setAddress(InetAddress.getByName(ElevatorIp[i]));
                setupPacket.setPort(ElevatorPort[i]);
                socket.send(setupPacket);

            }
            if(setupPacket.getAddress().getHostAddress().equals(ElevatorIp[i])){
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
            socket.receive(setupPacket);
            System.out.println("Response received from floor " + i + "...");
            FloorPort[i] = (int) setupPacket.getData()[0];

            if(remote.equals("l")){
                setupPacket.setData(new byte[]{(byte) (i+1)});
                setupPacket.setAddress(InetAddress.getByName(FloorIp[i]));
                setupPacket.setPort(FloorPort[i]);
                socket.send(setupPacket);

            }
            if(setupPacket.getAddress().getHostAddress().equals(FloorIp[i])){
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






 */