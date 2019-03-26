import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServerRandomNumber {

    private Socket socket;

    private void setSocket(Socket socket) {
        this.socket = socket;
    }

    private void execute() {
        try {
            // set-up communication
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter os = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())))) {
                // enter lower limit and upper limits from the client
                int lowerLimit = Integer.parseInt(reader.readLine());
                int upperLimit = Integer.parseInt(reader.readLine());
                // generate a random no.
                System.out.println("Generating Random Number");
                // variable for correct guess
                boolean isGuessCorrect = false;
                // You can set the upper and lower limits above
                int randomNum = getRandom(lowerLimit, upperLimit);
                // user feedback random no generated
                System.out.println("I have a Random Number");
                // client is connected
                System.out.println("Client Accepted " + randomNum);
                //  clients guess
                String clients_message = reader.readLine();
                while (!isGuessCorrect) {
                    //  ParseInput to make an int
                    int casted_result = parseInput(clients_message);
                    // compare the input
                    if (casted_result == randomNum) {
                        // send the msg to the server
                        sendMessageToClient(os, "Correct");
                        isGuessCorrect = true;
                    } else {
                        sendMessageToClient(os, "Incorrect");
                        // if  msg wrong they enter their next guess
                        clients_message = reader.readLine();
                    }

                }
                os.flush();
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Exception " + e.getMessage());
        }
    }

    // get a random number in the range
    private int getRandom(int lowerLimit, int upperLimit) {
        Random r = new Random();
        return r.nextInt(upperLimit - lowerLimit) + lowerLimit;
    }

    // turn the string typed response into an int for comparison
    private int parseInput(String guess) {
        return Integer.parseInt(guess);
    }

    // send the msg to the client with the current outstream
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
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        System.out.println("Random Number Server is running ...");
        System.out.println("Lets play the Simple Number Guessing Game");
        // random server that talks to the client
        ServerRandomNumber randomServer = new ServerRandomNumber();
        ServerSocket serverSocket = new ServerSocket(port);
        do{
            try (Socket clientSocket = serverSocket.accept()) {
                randomServer.setSocket(clientSocket);
                randomServer.execute();
            }
        } while (true);
    }// end main

}
