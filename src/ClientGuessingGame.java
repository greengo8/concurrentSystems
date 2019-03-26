import java.io.*;
import java.net.Socket;

public class ClientGuessingGame {

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 4444;
        if (args.length != 2) {
            System.out.println("Use the default setting...");
        } else {
            hostname = args[0];
            port = Integer.parseInt(args[1]);
        }
        try {
            // create a socket
            Socket socket = new Socket(hostname, port);
            // set up communication
            try (PrintWriter os = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                 BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                // create user input stream for entering guesses
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                // vars for limits
                boolean isValidLimits = false;
                String lowerLimitC = "";
                String upperLimitC = "";
                // loop until the limits correct
                while (!isValidLimits) {
//                    System.out.println("Lets play the Simple Number Guessing Game");
                    // enter lower and upper limits to the server to gen rand number
                    System.out.print("First please enter the lower number limit of the range ? ");
                    lowerLimitC = userInput.readLine();
                    System.out.print("Now , please enter the upper number limit of the range? ");
                    upperLimitC = userInput.readLine();

                    System.out.println("The range to guess between is: " + "\n" + "Lower Limit: " + (lowerLimitC + "\n" + "Upper Limit: " + upperLimitC));
                    // validate
                    isValidLimits = validateLimits(lowerLimitC, upperLimitC);
                    if (!isValidLimits) {
                        System.out.println("Numbers must be positive integers \n" +
                                "lower limit number & upper limit number cannot be the same \n" +
                                "upper number must be larger than lower number and vice-versa \n" +
                                "PLEASE TRY AGAIN");
                    }
                }
                // not sending invalid limits to the server so only sending them now
                sendToServer(os, lowerLimitC);
                sendToServer(os, upperLimitC);
                // boolean value for correct guess
                boolean isThisGuessCorrect = false;
                boolean isThisGuessValid = false;
                // String to store current guess
                String guess = "";
                // while the current guess is false
                while (!isThisGuessCorrect) {
                    // loop while the guess isn't valid
                    while (!isThisGuessValid) {
                        // prompt for user input
                        System.out.print("Please guess the number ? ");
                        // accept user input
                        guess = userInput.readLine();
                        // validate guess
                        isThisGuessValid = validateGuess(lowerLimitC, upperLimitC, guess);
                        if (!isThisGuessValid) {
                            System.out.println("Guesses must be positive integers \n" +
                                    "The range to guess between is: " + "\n" + "Lower Limit: " + (lowerLimitC + "\n" + "Upper Limit: " + upperLimitC));
                        }
                    }
                    // reset isThisGuessValid
                    isThisGuessValid = false;
                    // send the input to the server
                    sendToServer(os, guess);
                    // accept the response from the server
                    String response = is.readLine();
                    // handle the response - aka print the msg
                    handleResponse(response);
                    // if the response is correct the set the condition to false so we don't go through the loop again
                    if (response.equalsIgnoreCase("Correct")) {
                        isThisGuessCorrect = true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    } // end main

    private static void sendToServer(PrintWriter os, String msg) {
        os.println(msg);
        os.flush();
    }

    private static boolean validateLimits(String lLimit, String uLimit) {
        // regex matching Integer vals
        if (!lLimit.matches("^\\d+$") && !uLimit.matches("^\\d+$")) {
            return false;
        }
        // convert values for comparision
        int casted_lLimit = Integer.parseInt(lLimit);
        int casted_uLimit = Integer.parseInt(uLimit);
        // checking limits
        return casted_lLimit > 0 && casted_uLimit > 0 && casted_lLimit < casted_uLimit;
    }

    private static boolean validateGuess(String lLimit, String uLimit, String guess) {
        // regex for checking Integer value.
        if (!guess.matches("^\\d+$")) {
            return false;
        }
        // casting for comparison
        int casted_guess = Integer.parseInt(guess);
        int casted_lLimit = Integer.parseInt(lLimit);
        int casted_uLimit = Integer.parseInt(uLimit);
        // comparing for positive val and between range
        return casted_guess > 0 && casted_guess <= casted_uLimit && casted_guess >= casted_lLimit;
    }

    private static void handleResponse(String result) {
        // check the response from the server and print appropriate msg
        if (result.equalsIgnoreCase("Correct")) {
            System.out.println("Congratulations you guessed correct number and won the game wohooooo ");
        } else {
            System.out.println("Your guess is incorrect , Please Try Again\n");
        }
    }

}