# Server-Client-Operations

## Algorithm Functionality

Below is described how to run and interpret the IO of the algorithm:

- By running the `Server.java` class, the server will start on the `localhost` with the port `8000`. This is for the evenly distributed tasks per thread algorithm. For the uneven version start `ServerV2.java` class.

- After running the server, start the `Client.java` class from the terminal with the parameters `localhost` and `8000`.

- Once the connection is established, the server sends the client the operations it can use. Eg. `POW2`, `RLOG`, etc.

- On the terminal of the client, type the operation you want to perform as the first argument, and the list of inputs as the other parameters. Eg. `2POW 1 5 3 10 9`, [ENTER]

- Then, the server will process the request of the client and allocate the data inputs as tasks to be performed distrubuted amongst the threads. 
- The number of threads can be changed on line 124 for both `Server.java` and `ServerV2.java`. The default number of threads is `5`.

- You will then be asked by the server to wait for your feedback, and shortly after receive in on the client terminal.

- The server will also provide the client the number of milliseconds it took to process the input data.
