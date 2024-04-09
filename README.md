




## Elevator Controller 

### Running the code
- Run the main file in Scheduler module.
- Run the main file in the Controller module.

## Modules and what they do

### Scheduler
This module is responsible for starting and setting up all the threads for the elevators and floors.
It also gets inputs from the threads and connects the elevators and floors.
Finally it sends data to the GUI.
#### Commands


##### case 0b00111111:
This Command retuns info to the GUI about the state of the system
##### case 0b01111111:
This Command Adds an elevator
##### case 0b01111110:
This Command Adds a floor
##### case 0b00000000:
This Command is sent by the floor when an up or down button is pressed
##### case 0b00000001:
This Command is received anytime an elevator reaches a floor
##### case 0b00000010:
This Command is received anytime a button in an elevator is pressed
##### case 0b00000011:
This Command is received anytime the door finishes opening
##### case 0b00000100:
This Command is received anytime the door finishes closing




### Controller
This starts the GUI and sends packets to the Scheduler based on user inputs.

### Elevator
This module is responsible for the elevator logic. It receives inputs from the Scheduler and sends data back to the Scheduler.
#### Commands
##### Case 0b0000000:
Scheduler sends this command to tell the elevator to go to a Floor
##### Case 0b00000001:
Scheduler sends this command to tell the elevator to open or close the door
##### Case 0b00000010:
Any process can send this to 'press' a floor button in the elevator.

### Floor
This module is responsible for the floor logic. It receives inputs from the Scheduler and sends data back to the Scheduler.
#### Commands
##### case 0b00000000:
This command is received when an elevator arrives at a floor
##### case 0b00000001:
This command is received when a button is pressed on a floor