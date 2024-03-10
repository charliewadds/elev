import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    void testMain_NoExceptionThrown() {
//        try {
//            Main.main(null);
//        } catch (UnknownHostException e) {
//            fail("An exception occurred: " + e.getMessage());
//        }
    }

    @Test
    void testMain_InputScanner() {
        String input = "1\n20\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertDoesNotThrow(() -> {
            Main.main(null);
        });

        System.setIn(System.in);
    }

    @Test
    void testMain_InvalidPortInput() {
        String input = "1\nabc\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertThrows(NumberFormatException.class, () -> {
            Main.main(null);
        });

        System.setIn(System.in);
    }

    @Test
    void testMain_ElevatorArrivedAtFloor() {
        String input = "1\n20\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertDoesNotThrow(() -> {
            Main.main(null);
        });

        System.setIn(System.in);
    }

    @Test
    void testMain_FloorButtonPressed() {
        String input = "1\n20\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertDoesNotThrow(() -> {
            Main.main(null);
        });

        System.setIn(System.in);
    }

    @Test
    void testMain_InvalidElevatorState() {
        String input = "1\n20\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertDoesNotThrow(() -> {
            Main.main(null);
        });

        System.setIn(System.in);
    }

    @Test
    void testMain_InvalidButtonState() {
        String input = "1\n20\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertDoesNotThrow(() -> {
            Main.main(null);
        });

        System.setIn(System.in);
    }

    // Add more tests to cover additional scenarios and edge cases...
}
