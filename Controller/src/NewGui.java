import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class NewGui extends JFrame {
    final int MAX_ELEV = 4;
    final int MAX_FLOOR = 100;

    private JTextField[] elevatorInputs;
    private int[] elevStatus;
    private JLabel[] elevatorStatusLights;
    private JButton[] elevatorSendButtons;

    int numElev;
    int numFloor;
    int newPort;

    int elevatorPorts[];
    String elevatorIPs[];

    static DatagramSocket socket;
    public NewGui(int numElev, int numFloors) throws UnknownHostException {
        super("Elevator Control System");
        this.numElev = numElev;
        this.numFloor = numFloors;
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        DatagramPacket setupPacket = new DatagramPacket(new byte[32], 32,serverAddress, 21);

        try {
            socket = new DatagramSocket(null);
        } catch (SocketException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }


        this.elevStatus = new int[MAX_ELEV];
        this.elevatorInputs = new JTextField[MAX_ELEV];
        this.elevatorStatusLights = new JLabel[MAX_ELEV];
        this.elevatorSendButtons = new JButton[MAX_ELEV];
        this.elevatorPorts = new int[MAX_ELEV];
        this.elevatorIPs = new String[MAX_ELEV];


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLayout(new GridLayout(floorStatus.size(), 2));
        setLayout(new GridLayout(numElev+1, 3));
        JPanel addPanel = new JPanel(new FlowLayout());
        JPanel blankPanel = new JPanel(new FlowLayout());

        JTextField newPortField = new JTextField(5);
        newPortField.setText("Enter port for new Floor");
        JTextField newPortFieldFloorStart = new JTextField(0);
        newPortFieldFloorStart.setText("Enter starting port for new Floors");
        JTextField newPortFieldFloorEnd = new JTextField(0);
        newPortFieldFloorEnd.setText("Enter ending port for new Floors");

        JButton addElev = new JButton("Add Elevator");
        JButton addFloor = new JButton("Add Floors");
        addFloor.addActionListener(e -> {
            for (int i = 0; i < Integer.parseInt(newPortFieldFloorEnd.getText()) - Integer.parseInt(newPortFieldFloorStart.getText()); i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    addFloor(Integer.parseInt(newPortFieldFloorStart.getText()) + i);
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("Floor " +  i + " added\n\n\n");
            }
        });
        try {
            addElev.addActionListener(e -> {
                try {
                    addElev(Integer.parseInt(newPortField.getText()));
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } finally{
            ;
        }
        addPanel.add(addElev);
        addPanel.add(newPortField);
        addPanel.add(addFloor);
        addPanel.add(newPortFieldFloorStart);
        addPanel.add(newPortFieldFloorEnd);


        add(addPanel);
        //add(blankPanel);
        //add(blankPanel1);

        for (int i = 0; i < numElev; i++) {
            JPanel floorPanel = new JPanel(new FlowLayout());
            JLabel floorLabel = new JLabel("elevator #" + (i + 1) + ": " + "floorStatus.get(i)");
            floorPanel.add(floorLabel);
            add(floorPanel);

            JPanel inputPanel = new JPanel(new FlowLayout());
            elevatorInputs[i] = new JTextField(5);
            inputPanel.add(elevatorInputs[i]);
            elevatorSendButtons[i] = new JButton("Send to elevator #" + (i + 1));
            int finalI = i;
            elevatorSendButtons[i].addActionListener(e -> {
                System.out.println("Sending " + (elevatorInputs[finalI].getText()));
            });
            inputPanel.add(elevatorSendButtons[i]);
            add(inputPanel);


            JPanel statusPanel = new JPanel(new FlowLayout());
            elevatorStatusLights[i] = new JLabel();
            elevatorStatusLights[i].setOpaque(true);
            elevatorStatusLights[i].setPreferredSize(new Dimension(20, 20));
            elevatorStatusLights[i].setBackground(Color.GREEN); // Green circle initially
            elevatorStatusLights[i].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Border for visibility
            statusPanel.add(elevatorStatusLights[i]);
            //elevatorStatusLights.add(statusLabel);
            add(statusPanel);
        }

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }



    private void addElev(int port) throws UnknownHostException {
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        DatagramPacket setupPacket = new DatagramPacket(new byte[32], 32, serverAddress, 21);

        setupPacket.setData(new byte[]{0b01111111, (byte) port });
        try {
            socket.send(setupPacket);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        numElev++;
        addElevGUI(numElev);
    }

    private void sendElevatorFloor(int elevator, int floor) {
        InetAddress serverAddress = InetAddress.getByName("

    private void addFloor(int port) throws UnknownHostException {
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        DatagramPacket setupPacket = new DatagramPacket(new byte[32], 32, serverAddress, 21);

        setupPacket.setData(new byte[]{0b01111110, (byte) port });
        try {
            socket.send(setupPacket);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        numFloor++;
    }

    private void addElevGUI(int i){
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));


        JPanel floorPanel = new JPanel(new FlowLayout());
        JLabel floorLabel = new JLabel("elevator #" + (i + 1) + ": " + "floorStatus.get(i)");
        floorPanel.add(floorLabel);
        add(floorPanel);

        JPanel inputPanel = new JPanel(new FlowLayout());
        elevatorInputs[i] = new JTextField(5);
        inputPanel.add(elevatorInputs[i]);
        elevatorSendButtons[i] = new JButton("Send to elevator #" + (i + 1));
        int finalI = i;
        elevatorSendButtons[i].addActionListener(e -> {
            System.out.println("Sending " + (elevatorInputs[finalI].getText()));
        });
        inputPanel.add(elevatorSendButtons[i]);
        add(inputPanel);


        JPanel statusPanel = new JPanel(new FlowLayout());
        elevatorStatusLights[i] = new JLabel();
        elevatorStatusLights[i].setOpaque(true);
        elevatorStatusLights[i].setPreferredSize(new Dimension(20, 20));
        elevatorStatusLights[i].setBackground(Color.GREEN); // Green circle initially
        elevatorStatusLights[i].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Border for visibility
        statusPanel.add(elevatorStatusLights[i]);
        //elevatorStatusLights.add(statusLabel);
        add(statusPanel);
        repaint();
        setVisible(true);
        pack();

    }
    public void updateElevs(List<Integer> floorStatus) {
        for (int i = 0; i < 4; i++) {
            JLabel floorLabel = new JLabel("elevator #" + (i + 1) + ": " + floorStatus.get(i));
            JPanel floorPanel = (JPanel) getContentPane().getComponent(i * 3);
            floorPanel.removeAll();
            floorPanel.add(floorLabel);
            floorPanel.revalidate();
            floorPanel.repaint();
        }
    }
}