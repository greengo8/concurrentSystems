import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServerRandomMultiThreaded {

    private Socket socket;

    private void setSocket(Socket socket) {
        this.socket = socket;
    }


    private void run() {
        try {
            // read the msg from client
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter os = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())))) {
                // enter lower limit and upper limits from the client
                int lowerLimit = Integer.parseInt(reader.readLine());
                int upperLimit = Integer.parseInt(reader.readLine());
                // generate a random number
                System.out.println("Generating Random Number");
                // variable for correct guess
                boolean isCorrectGuess = false;
                // You can set the upper and lower limits above
                int randomNum = getRandom(lowerLimit, upperLimit);
                // user feedback to show rand number has been generated
                System.out.println("I have a Random Number");
                // client is connected
                System.out.println("Client Accepted " + randomNum);
                // get the clients guess
                String clients_message = reader.readLine();
                while (!isCorrectGuess) {
                    // parse the input to make it an int
                    int casted_result = parseInput(clients_message);
                    // compare the input
                    if (casted_result == randomNum) {
                        // send the msg to the server
                        sendMessageToClient(os, "Correct");
                        isCorrectGuess = true;
                    } else {
                        sendMessageToClient(os, "Incorrect");
                        // if they get the msg wrong they enter their next guess
                        clients_message = reader.readLine();
                    }
                }
                os.flush();
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Exception " + e.getMessage());
        }
    }

    private int getRandom(int lowerLimit, int upperLimit) {
        Random r = new Random();
        return r.nextInt(upperLimit - lowerLimit) + lowerLimit;
    }

    // the predefined protocol for the math operation is operator:first_value:second_value
    private int parseInput(String guess) {
        return Integer.parseInt(guess);
    }

    private static void sendMessageToClient(PrintWriter os, String msg) {
        try {
            os.println(msg);
            os.flush();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 4444;
        if (args.length == 1) try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ignored) {
        }
        System.out.println(" Server is running ...");
        // run a random server that talks to the client
        ServerSocket serverSocket = new ServerSocket(port);
        do{
            ServerRandomMultiThreaded randomServer;
            try (Socket clientSocket = serverSocket.accept()) {
                randomServer = new ServerRandomMultiThreaded();
                randomServer.setSocket(clientSocket);
            }
            randomServer.run();
        } while (true);
    }



}



