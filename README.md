# ChordDHTSimulator

_Simulates the Chord routing algorithm._   
   
Accepts as parameter the number of bit m of the identifier and the number n of nodes.   
A central coordinator initializes the system creating nodes and settings for each node its id, the predecessor, the successor and the finger table.   
At the end of this phase a csv file with all the edges will be exported.   
   
Then the program simulates a number q of query, selecting a random node and asking to solve the query using the Chord algorithm.   
   
For more information please check the [assignment file](https://github.com/daquinoaldo/ChordDHTSimulator/blob/master/Chord%20DHT%20assignment.pdf) and the [relationship](https://github.com/daquinoaldo/ChordDHTSimulator/blob/master/Chord%20DHT%20Relationship.pdf).   
   
The program is written in Kotlin, you can find a JAR in the [releases](https://github.com/daquinoaldo/ChordDHTSimulator/releases/).
