import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    void testMain_NoExceptionThrown() {
        String input = "1\n21\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertDoesNotThrow(() -> {
            Main.main(null);
        });

        System.setIn(System.in);
    }

    @Test
    void testMain_InputScanner() {
        String input = "1\n21\n21\n";
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
    void testMain_DoorOpenClose() {
        String input = "1\n21\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertDoesNotThrow(() -> {
            Main.main(null);
        });

        System.setIn(System.in);
    }

    @Test
    void testMain_SocketException() {
        String input = "1\n21\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertThrows(RuntimeException.class, () -> {
            Main.main(null);
        });

        System.setIn(System.in);
    }

    @Test
    void testMain_MoveElevator() {
        assertEquals(Main.direction.NONE, Main.direction);
        Main.moveElevator(2);
        assertEquals(Main.direction.UP, Main.direction);
        assertEquals(2, Main.floor);
    }

    @Test
    void testMain_MoveElevatorSameFloor() {
        Main.floor = 3;
        Main.direction = Main.direction.UP;
        Main.moveElevator(3);
        assertEquals(Main.direction.NONE, Main.direction);
        assertEquals(3, Main.floor);
    }

    @Test
    void testMain_ElevatorNumberInput() {
        String input = "2\n21\n21\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        assertDoesNotThrow(() -> {
            Main.main(null);
        });

        System.setIn(System.in);
    }

    @Test
    void testMain_SameFloorNoMovement() {
        Main.floor = 4;
        Main.moveElevator(4);
        assertEquals(Main.direction.NONE, Main.direction);
        assertEquals(4, Main.floor);
    }

    // Add more tests to cover additional scenarios and edge cases...
}
