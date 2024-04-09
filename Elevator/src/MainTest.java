//import org.testng.annotations.Test;
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class MainTest {
//
//    @Test
//    void testMain_NoExceptionThrown() {
//        String input = "1\n21\n21\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        assertDoesNotThrow(() -> {
//            Elevator.main(null);
//        });
//
//        System.setIn(System.in);
//    }
//
//    @Test
//    void testMain_InputScanner() {
//        String input = "1\n21\n21\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        assertDoesNotThrow(() -> {
//            Elevator.main(null);
//        });
//
//        System.setIn(System.in);
//    }
//
//    @Test
//    void testMain_InvalidPortInput() {
//        String input = "1\nabc\n21\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        assertThrows(NumberFormatException.class, () -> {
//            Elevator.main(null);
//        });
//
//        System.setIn(System.in);
//    }
//
//    @Test
//    void testMain_DoorOpenClose() {
//        String input = "1\n21\n21\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        assertDoesNotThrow(() -> {
//            Elevator.main(null);
//        });
//
//        System.setIn(System.in);
//    }
//
//    @Test
//    void testMain_SocketException() {
//        String input = "1\n21\n21\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        assertThrows(RuntimeException.class, () -> {
//            Elevator.main(null);
//        });
//
//        System.setIn(System.in);
//    }
//
//    @Test
//    void testMain_MoveElevator() {
//        assertEquals(Elevator.direction.NONE, Elevator.direction);
//        Elevator.moveElevator(2);
//        assertEquals(Elevator.direction.UP, Elevator.direction);
//        assertEquals(2, Elevator.floor);
//    }
//
//    @Test
//    void testMain_MoveElevatorSameFloor() {
//        Elevator.floor = 3;
//        Elevator.direction = Elevator.direction.UP;
//        Elevator.moveElevator(3);
//        assertEquals(Elevator.direction.NONE, Elevator.direction);
//        assertEquals(3, Elevator.floor);
//    }
//
//    @Test
//    void testMain_ElevatorNumberInput() {
//        String input = "2\n21\n21\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        assertDoesNotThrow(() -> {
//            Elevator.main(null);
//        });
//
//        System.setIn(System.in);
//    }
//
//    @Test
//    void testMain_SameFloorNoMovement() {
//        Elevator.floor = 4;
//        Elevator.moveElevator(4);
//        assertEquals(Elevator.direction.NONE, Elevator.direction);
//        assertEquals(4, Elevator.floor);
//    }
//
//    // Add more tests to cover additional scenarios and edge cases...
//}
