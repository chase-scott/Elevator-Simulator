This file is a documentation of the project code with the 12 classes and 3 enums.

* A main file containing a Java Class called Main; 
- Class Main
The main class create the floorSystem thread as well as both schedulerSystem and elevetorSystem
threads. Those threads communicate via the communicationPipe and schedule the events from the 
eventFile to make the elevator functionnable. 

* A State file containing Enum Direction, DoorState and Motor State;
These Enum are used to represent a catalog of signals or names for the direction of the elevator 
(up or down) as well as the state of the door ( open or closed) and the state of the motor of the
elevator.

* A elevator file containing Classes Door,Elevator,ElevatorButton and Motor;
- Class Door
This class keeps track of the state of the elevator door ( Open or Closed). The door is closed 
when the elevator is in use and openned when someone enters or goes out.
- Class ElevatorButton
This class keeps track of the destinations of the elevator when in use.
- Class Motor
This class keeps track of whether the elevator's motor is in use or idle.
- Class Elevator 
This class uses all of the above class to implement the elevator system with its motor, the door
and all the buttons. The button lamp turns on when pressed. 

* A floor file containing Classes Floor,FloorButton and FloorEvent;
- Class FloorButton
This class keeps track of whether the person who wants to enter the elevator pressed the Up Button
or the DOWN button.
- Class FloorEvent
This class keeps track of the time at which the person on the floor pressed the elevator button and
in which direction he is going.
- Class Floor
This class represent the floor system and keeps tracks of where the elevator is when the floor 
button is pressed.

* An Event file containing both EventFile and FloorEvent classes
- Class EventFile
This class is used to write or read events from the files. 
- Class FloorEvent
This class is an extension of FloorEvent class in file Floor which would be able to read an event from
a file.

* A System file containing ElevatorSystem,FloorSystem,Pipe and Scheduler classes
- Class ElevatorSystem
This class is to check if scheduler has job for elevator.
- Class FloorSystem
This class is to monitors event file for new floor events.
- Class Scheduler
This class schedule the event and notify the system via pipe if the evnt is ready or the scheduling done. 