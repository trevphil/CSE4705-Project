// RmCheckersClient.java is a client that interacts with Sam, a checkers 
// server. It is designed to illustrate how to communicate with the server
// in a minimal way.  It is not meant to be beautiful java code.
// Given the correct machine name and port for the server, a user id, and a 
// password (_machine, _port, _user, and _password in the code), running 
// this program will initiate connection and start a game with the default 
// player. (the _machine and _port values used should be correct, but check
// the protocol document.)
// 
// the program has been tested and used under Java 5.0, and 6.0, but probably 
// would work under older or newer versions. (also works under Java 8).
//
// Copyright (C) 2008 Robert McCartney
 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
// USA

import java.io.*;
import java.net.*;

public class RmCheckersClient {

    private final static String _user = "21";  // original ID = 21, alternate ID = 22
    private final static String _password = "726887";  // original password = 726887, alternate password = 791643
    private final static String _opponent = "0";
    private final String _machine  = "icarus.engr.uconn.edu"; 
    private int _port = 3499;
    private Socket _socket = null;
    private PrintWriter _out = null;
    private BufferedReader _in = null;

    private String _gameID;
    private String _myColor;
    
    private static RmCheckersClient myClient = null;
    private static GameEngine engine = null;
  
    // constructor
    public RmCheckersClient() { _socket = openSocket(); }

    // getters and setters
    public Socket getSocket() { return _socket; }
    public PrintWriter getOut() { return _out; }
    public BufferedReader getIn() { return _in; }
    public void setGameID(String id) { _gameID = id; }
    public String getGameID() { return _gameID; }
    public void setColor(String color) { _myColor = color; }
    public String getColor() { return _myColor; }

    private static final int NUM_GAMES_TO_PLAY = 20;
    private static boolean myPlayerWonLastGame;
    
    public static void main(String[] args) {
    	int wonGames = 0;
    	for (int i = 0; i < NUM_GAMES_TO_PLAY; i++) {
    		System.out.println("Starting game # " + (i+1));
    		playGame();
    		if (myPlayerWonLastGame) wonGames++;
    		System.out.println("Finished game # " + (i+1) + " (" + (myPlayerWonLastGame ? "win" : "loss") + ")");
    	}
    	System.out.printf("won %d/%d games (%.02f%%)\n", wonGames, NUM_GAMES_TO_PLAY, (double)wonGames/NUM_GAMES_TO_PLAY);
    }
    
    private static void playGame() {
    	String readMessage;
    	myClient = new RmCheckersClient();
    	
    	try {
		    myClient.readAndEcho(); // start message
		    myClient.readAndEcho(); // ID query
		    myClient.writeMessageAndEcho(_user); // user ID
		    
		    myClient.readAndEcho(); // password query 
		    myClient.writeMessage(_password);  // password
	
		    myClient.readAndEcho(); // opponent query
		    myClient.writeMessageAndEcho(_opponent);  // opponent
	
		    String gameIDtag = myClient.readAndEcho();
		    myClient.setGameID(gameIDtag.substring(5, gameIDtag.length())); // game 
		    myClient.setColor(myClient.readAndEcho().substring(6, 11));  // color
		    System.out.println("I am playing as " + myClient.getColor() + " in game number " + myClient.getGameID());
		    
		    engine = new GameEngine(myClient.getColor());
		    
		    boolean gameOver = false;
		    if (myClient.getColor().equals("White")) {
		    	while (!gameOver) {
			    	String opponentMove = myClient.read(); // equals a black move
			    	gameOver = isGameOver(opponentMove); // test for Game Over
			    	if (gameOver) break;
			    	engine.updateGameAfterOpponentMove(opponentMove);
			    	engine.game.printState();
			    	readMessage = myClient.read(); // move query
			    	gameOver = isGameOver(readMessage); // test for Game Over
			    	if (gameOver) break;
			    	String myMove = engine.getMove();
			    	myClient.writeMessage(myMove);
			    	engine.updateGameAfterMyMove();
			    	engine.game.printState();
			    	readMessage = myClient.read(); // equals (my) white move
		    	}
		    } else {
		    	while (!gameOver) {
		    		readMessage = myClient.read(); // move query
		    		gameOver = isGameOver(readMessage); // test for Game Over
			    	if (gameOver) break;
			    	String myMove = engine.getMove();
			    	myClient.writeMessage(myMove);
			    	engine.updateGameAfterMyMove();
			    	engine.game.printState();
			    	readMessage = myClient.read(); // equals (my) black move
			    	String opponentMove = myClient.read(); // equals a white move
			    	gameOver = isGameOver(opponentMove); // test for Game Over
			    	if (gameOver) break;
			    	engine.updateGameAfterOpponentMove(opponentMove);
			    	engine.game.printState();
		    	}
		    }
		   
		    myClient.getSocket().close();
		} catch (IOException e) {
		    System.out.println("Failed in read/close");
		    System.exit(1);
		}
    }
    
    public String read() throws IOException {
    	String readMessage = _in.readLine();
    	return readMessage;
    }

    public String readAndEcho() throws IOException {
		String readMessage = _in.readLine();
		System.out.println("read: " + readMessage);
		return readMessage;
    }

    public void writeMessage(String message) throws IOException {
		_out.print(message + "\r\n");  
		_out.flush();
    }
 
    public void writeMessageAndEcho(String message) throws IOException {
		_out.print(message + "\r\n");  
		_out.flush();
		System.out.println("sent: " + message);
    }
			       
    public  Socket openSocket() {
    	// Create socket connection, adapted from Sun example
    	try {
	       _socket = new Socket(_machine, _port);
	       _out = new PrintWriter(_socket.getOutputStream(), true);
	       _in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
    	} catch (UnknownHostException e) {
	       System.out.println("Unknown host: " + _machine);
	       System.exit(1);
    	} catch (IOException e) {
	       System.out.println("No I/O");
	       System.exit(1);
    	}
    	return _socket;
    }
    
    private static boolean isGameOver(String s) {
    	boolean gameOver = s.contains("Result");
    	int indexOfT = s.indexOf('t');
    	String winningPlayer = s.substring(indexOfT + 2, s.length());
    	if (gameOver) {
    		if (myClient.getColor().equals(winningPlayer)) {
    			engine.saveMutatedWeights();
    			myPlayerWonLastGame = true;
    		} else {
    			engine.saveOriginalWeights();
    			myPlayerWonLastGame = false;
    		}
    	}
    	return gameOver;
    }
    
}

