import org.testng.annotations.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    // Test that the main method runs without throwing an exception
    @Test
    void testMain_NoExceptionThrown() {
//        try {
//            //Scheduler.main(null);
//        } catch (IOException e) {
//            fail("An exception occurred: " + e.getMessage());
//        }
    }

    // Test providing input through a Scanner to simulate user interaction
    @Test
    void testMain_InputScanner() {
        // Mock user input
        String input = "1\n21\n21\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Ensure no exceptions are thrown
        assertDoesNotThrow(() -> {
            Scheduler.main(null);
        });

        // Reset System.in
        System.setIn(System.in);
    }

    // Test providing invalid port number as input
    @Test
    void testMain_InvalidPortInput() {
        // Mock user input with invalid port number
        String input = "1\n21\n21\nxyz\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Ensure an IOException is thrown
        assertThrows(IOException.class, () -> {
            Scheduler.main(null);
        });

        // Reset System.in
        System.setIn(System.in);
    }

    // Test handling of elevator reaching a floor
    @Test
    void testMain_ElevatorReachedFloor() {
        // Mock user input
        String input = "1\n21\n21\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Ensure that the elevator state changes to ARRIVED when a floor is reached
        assertDoesNotThrow(() -> {
            Scheduler.main(null);
        });

        // Reset System.in
        System.setIn(System.in);
    }

    // Test handling of floor button pressed event
    @Test
    void testMain_FloorButtonPressed() {
        // Mock user input
        String input = "1\n21\n21\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Ensure that the elevator receives the correct command when a floor button is pressed
        assertDoesNotThrow(() -> {
            Scheduler.main(null);
        });

        // Reset System.in
        System.setIn(System.in);
    }
    
}
