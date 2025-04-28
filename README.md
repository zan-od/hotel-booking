## Hotel booking

A sample console application to demonstrate working with booking data.
This is an interactive application that loads initial data from files and then waits for commands in console.  
It can process 2 commands:
1. Getting availability (including zero and negative values) by a hotel and a room type
```
Availability(H1, 20250101-20250131, SGL)
```
2. Search available rooms from now
```
Search(H1, 365, SGL)
```
If an user inputs and empty string, the application finishes

### How to build
#### Requirements: jdk16+

Download the project to a local machine. Open console, go to the project directory, run `gradle build` command:
(Windows)
```shell
./gradlew.bat build
```
Gradle will save the output jar file as `<project_root>/build/libs/hotel-booking-1.0-SNAPSHOT.jar`

### How to run

Open console, go to the project directory. Run the command
```shell
java -jar ./build/libs/hotel-booking-1.0-SNAPSHOT.jar --hotels ./src/test/resources/hotels.json --bookings ./src/test/resources/bookings.json
```
(you can specify path to your data files)