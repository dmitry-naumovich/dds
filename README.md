# Ad hoc network model for Distributed Data Storage Algorithm Testing

The developed model is a multithreaded Java application where the lifecycle of each network device is described by a 
separate thread. All nodes are independent entities who delegate all main functionality (file distribution, 
route search, node status checks) to the special managers. 

* _The Chunk Manager_ distributes files into chunks, creates their copies, manages their location tables, 
defines which node the regular chunk will be sent to.
* _The Routing Manager_ has access to the host's Routing Table, generates RREQ, RREP and RERR requests, periodically verifies 
the neighbor nodes' status. The main logic of the AODV protocol is implemented here, this manager processes all incoming 
RREQs and reports about route errors.
* The main responsibility of _the Message Manager_ is to process the messages and requests of all types (RREQ, RREP, RERR, 
ChunkMessage, BackupMessage, Gratuitous RREQ) received by the node. Also, this manager verifies the incoming messages 
and either transmits them further or delegates the processing to the Routing Manager. Furthermore, this manager is 
responsible for backups when the master node discovers any other node is not accessible.

* Each node is the model also has the RREQ Buffer which stores all recently transmitted Route Requests. _RREQ Buffer Manager_
allows to add new requests to the Buffer and periodically clear it. Moreover, each node has a message container 
(implemented as the FIFO queue) where all messages get into and where they are taken out by the Message Manager later
for the further processing.

The network model represents a graph located in the bounded rectangular area where each node has the coordinates and 
the radius which means they are circles and the edges exist between neighbor nodes. The edges don't have a graphical 
representation. All nodes are constantly moving (because it's an ad hoc network), they are repelled from the borders 
of the area. At the application start all nodes appear in a random place and has a random move direction.
 
We use the The Expanding Ring search algorithm with the HL (Hop Limit) parameter so as to solve the parasitic traffic 
problem. This algorithm determines how many times the request can be redirected between the nodes before it wil be 
automatically destroyed.
 
We implemented several network scenarios in the model and their imitation modelling allows us to measure the 
equability and the performance of the DDS algorithm and the nodes' load. The modelling results are the output data
and it's possible to make conclusions, draw charts and graphs of the parameter interdependence.

The investigated scenarios are listed below:
* File distribution, i.e. splitting files into chunks and matching them with the nodes where they will be stored;
* Route search using AODV protocol (sending and receiving RREQs, RREPs, RERRS, sequence number (SN) processing and 
connection set up between nodes)
* Detecting inaccessible nodes by other, i.e. checking status for those nodes who the current one has actual routes to;
* Backuping data to provide reliability of data chunk storing mechanism

## Distributed Data Storage Algorithm

The Distributed Data Storage Algorithm provides reliable, safe and flexible data storing in wireless peer computer networks.
It defines the rule by which the data is distributed into encrypted chunks, how this chunk corresponds to the node 
where it will be stored, how the chunks are coalescing together back into the solid file, how the backups are held and so on.
The DDS algorithm does not need to know about the network topology - it only needs to know about the nodes themselves 
and the chunks stored there.

## Dynamic Routing

Implemented with **Ad hoc On-demand Distance Vector** routing protocol.
Its description is available here: https://tools.ietf.org/html/draft-ietf-manet-aodv-09

## About author and project objective

This project was created by Dzmitry Naumovich as the practical part of the diploma work.

The objective of the project was to develop a model of the distributed ad hoc network with the integrated dynamic 
routing protocol and distributed data storage algorithm; to analyze its functioning in order to measure the efficiency 
and performance of the distributed data storage algorithm.