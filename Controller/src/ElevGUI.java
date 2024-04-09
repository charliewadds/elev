import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;

public class ElevGUI implements ActionListener{
    static DatagramSocket socket;
    private JButton changeButton;//this just triggers change
    private int[] elevFloor = new int[4];
    private JTextField currentFloorInput;
    private JTextField desiredFloorInput;
    private JButton confirmButton;
    private JTextArea elevOne;
    private JTextArea elevTwo;
    private JTextArea elevThree;
    private JTextArea elevFour;

    public ElevGUI(){


        JFrame frame = new JFrame("Elevator Project");
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new FlowLayout());

        JLabel labelOne = new JLabel("Elevator 1");
        JLabel labelTwo = new JLabel("Elevator 2");
        JLabel labelThree = new JLabel("Elevator 3");
        JLabel labelFour = new JLabel("Elevator 4");
        JLabel currentFloorLabel = new JLabel ("Current Floor:");
        JLabel desiredFloorLabel = new JLabel("Desired Floor:");
        elevOne = new JTextArea(20,15);
        elevOne.setEditable(false);
        elevTwo = new JTextArea(20,15);
        elevTwo.setEditable(false);
        elevThree = new JTextArea(20,15);
        elevThree.setEditable(false);
        elevFour = new JTextArea(20,15);
        elevFour.setEditable(false);
        currentFloorInput = new JTextField(10);
        desiredFloorInput = new JTextField(10);
        confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(this);

        // Elevator 1 Output
        JPanel elevOnePane = new JPanel(new GridLayout(1, 1));
        elevOnePane.add(labelOne);
        elevOnePane.add(elevOne);

        // Elevator 2 Output
        JPanel elevTwoPane = new JPanel(new GridLayout(1, 1));
        elevOnePane.add(labelTwo);
        elevOnePane.add(elevTwo);

        // Elevator 3 Output
        JPanel elevThreePane = new JPanel(new GridLayout(1, 1));
        elevOnePane.add(labelThree);
        elevOnePane.add(elevThree);

        // Elevator 4 Output
        JPanel elevFourPane = new JPanel(new GridLayout(1, 1));
        elevOnePane.add(labelFour);
        elevOnePane.add(elevFour);

        // User Input
        JPanel userInputPane = new JPanel(new GridLayout(1, 1));
        userInputPane.add(currentFloorLabel);
        userInputPane.add(currentFloorInput);
        userInputPane.add(desiredFloorLabel);
        userInputPane.add(desiredFloorInput);
        userInputPane.add(confirmButton);

        contentPane.add(elevOnePane);
        contentPane.add(elevTwoPane);
        contentPane.add(elevThreePane);
        contentPane.add(elevFourPane);
        contentPane.add(userInputPane, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }
    private void setupSocket() throws UnknownHostException {
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        int len = 32;
        DatagramPacket floorPacket = new DatagramPacket(new byte[len], len, serverAddress, 12);
        DatagramPacket elevPacket = new DatagramPacket(new byte[len], len, serverAddress, 11);


        try {
            socket = new DatagramSocket(8000);
        } catch (SocketException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }
//    public void updateFloors(int[] floors){
//        elevFloor = floors;
//        for(int i = 0; i< floors.length; i++){
//            elevators[i].setText("Elevator " + i + " is on floor " + floors[i]);
//        }
//    }
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == confirmButton) {
            String currentFloor = currentFloorInput.getText();
            String desiredFloor = desiredFloorInput.getText();
            // TODO Process the input, update elevator status, and display it in JTextAreas



        }
    }


}
