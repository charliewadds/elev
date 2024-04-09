




## Elevator Controller 

### Running the code
- Run the main file in Scheduler module.
- Run the main file in the Controller module.

## Modules and what they do

### Scheduler
This module is responsible for starting and setting up all the threads for the elevators and floors.
It also gets inputs from the threads and connects the elevators and floors.
Finally it sends data to the GUI.

### Controller
This starts the GUI and sends packets to the Scheduler based on user inputs.

### Elevator
This module is responsible for the elevator logic. It receives inputs from the Scheduler and sends data back to the Scheduler.

### Floor
This module is responsible for the floor logic. It receives inputs from the Scheduler and sends data back to the Scheduler.
