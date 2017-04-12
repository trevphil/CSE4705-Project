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

    private final static String _user = "21";  // alternate ID = 22
    private final static String _password = "726887";  // alternate password = 791643
    private final static String _opponent = "0";
    private final String _machine  = "icarus.engr.uconn.edu"; 
    private int _port = 3499;
    private Socket _socket = null;
    private PrintWriter _out = null;
    private BufferedReader _in = null;

    private String _gameID;
    private String _myColor;
  
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

    public static void main(String[] args) {
    	String readMessage;
    	RmCheckersClient myClient = new RmCheckersClient();
    	
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
		    
		    GameEngine engine = new GameEngine(myClient.getColor());
		    
		    if (myClient.getColor().equals("White")) {
		    	for (int i = 0; i < 100; i++) {
			    	String opponentMove = myClient.readAndEcho(); // equals a black move
			    	engine.updateGameAfterOpponentMove(opponentMove);
			    	readMessage = myClient.readAndEcho(); // move query
			    	String myMove = engine.getMove();
			    	myClient.writeMessageAndEcho(myMove);
			    	engine.updateGameAfterMyMove();
			    	readMessage = myClient.readAndEcho(); // equals (my) white move
		    	}
		    } else {
		    	for (int i = 0; i < 100; i++) {
		    		readMessage = myClient.readAndEcho(); // move query
			    	String myMove = engine.getMove();
			    	myClient.writeMessageAndEcho(myMove);
			    	engine.updateGameAfterMyMove();
			    	readMessage = myClient.readAndEcho(); // equals (my) black move
			    	String opponentMove = myClient.readAndEcho(); // equals a white move
			    	engine.updateGameAfterOpponentMove(opponentMove);
		    	}
		    }
		   
		    myClient.getSocket().close();
		} catch (IOException e) {
		    System.out.println("Failed in read/close");
		    System.exit(1);
		}
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
    
}

