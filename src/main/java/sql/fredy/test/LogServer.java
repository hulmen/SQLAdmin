package sql.fredy.test;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;           // For Set and Iterator

public class LogServer {
    public static void main(String[] args) throws IOException {
	
	// Get the character encoders and decoders we'll need
	Charset charset = Charset.forName("ISO-8859-1");
	CharsetEncoder encoder = charset.newEncoder();
	CharsetDecoder decoder = charset.newDecoder();

	// Allocate a buffer for communicating with clients
	ByteBuffer buffer = ByteBuffer.allocate(512);

	// All of the channels we use in this code will be in non-blocking 
	// mode. So we create a Selector object that will block while 
	// monitoring all of the channels and will only stop blocking when
	// one or more of the channels is ready for I/O of some sort.
	Selector selector = Selector.open();

	// Create a new ServerSocketChannel, and bind it to port 8000.  
	// Note that we have to do this using the underlying ServerSocket.
	ServerSocketChannel server = ServerSocketChannel.open();
	server.socket().bind(new java.net.InetSocketAddress(8000));
	// Put the ServerSocketChannel into non-blocking mode
	server.configureBlocking(false);
	// Now register it with the Selector (note we call register() on the
	// channel, not the selector object, however).  The SelectionKey
	// represents the registration of this channel with this Selector
	SelectionKey serverkey = server.register(selector,
						 SelectionKey.OP_ACCEPT);

	for(;;) {  // The main server loop.  The server runs forever.
	    // This call blocks until there is activity on one of the 
	    // registered channels. This is the key method in non-blocking I/O.
	    selector.select();

	    // Get a java.util.Set containing the SelectionKey objects for
	    // all channels that are ready for I/O.
	    Set keys = selector.selectedKeys();

	    // Use a java.util.Iterator to loop through the selected keys
	    for(Iterator i = keys.iterator(); i.hasNext(); ) {
		// Get the next SelectionKey in the set, and then remove it
		// from the set.  It must be removed explicitly, or it will
		// be returned again by the next call to select().
		SelectionKey key = (SelectionKey) i.next();
		i.remove();

		// Check whether this key is the SelectionKey we got when
		// we registered the ServerSocketChannel.
		if (key == serverkey) {
		    // Activity on the ServerSocketChannel means a client
		    // is trying to connect to the server.
		    if (key.isAcceptable()) {
			// Accept the client connection, and obtain a 
			// SocketChannel to communicate with the client.
			SocketChannel client = server.accept();
			// Put the client channel in non-blocking mode.
			client.configureBlocking(false);
			// Now register it with the Selector object, telling it
			// that we'd like to know when there is data to be
			// read from this channel.
			SelectionKey clientkey =
			    client.register(selector, SelectionKey.OP_READ);
			// Attach some client state to the key.  We'll use
			// this state below when we talk to the client.
			clientkey.attach(0);
		    }
		}
		else {
		    // If the key we got from the Set of keys is not the
		    // ServerSocketChannel key, then it must be a key 
		    // representing one of the client connections.  
		    // Get the channel from the key.
		    SocketChannel client = (SocketChannel) key.channel();

		    // If we got here, it should mean that there is data to
		    // be read from the channel, but we double-check here.
		    if (!key.isReadable()) continue;

		    // Now read bytes from the client.  We assume that
		    // we get all the client's bytes in one read operation
		    int bytesread = client.read(buffer);

		    // If read() returns -1, it indicates end-of-stream, which
		    // means the client has disconnected, so de-register the
		    // selection key and close the channel.
		    if (bytesread == -1) {  
			key.cancel();
			client.close();
			continue;
		    }

		    // Otherwise, decode the bytes to a request string
		    buffer.flip();
		    String request = decoder.decode(buffer).toString();
		    buffer.clear();
		    // Now reply to the client based on the request string
		    if (request.trim().equals("quit")) {
			// If the request was "quit", then send a final message
			// close the channel and de-register the SelectionKey
			client.write(encoder.encode(CharBuffer.wrap("Bye.")));
			key.cancel();
			client.close();
		    }
		    else {
			// Otherwise, send a response string comprised of the
			// sequence number of this request plus an uppercase
			// version of the request string. Note that we keep
			// track of the sequence number by "attaching" an
			// Integer object to the SelectionKey and incrementing
			// it each time.

			// Get sequence number from SelectionKey
			int num =(int) key.attachment();
			// For reponse string
			String response = num + ": " + request.toUpperCase();
			// Wrap, encode, and write the response string
			client.write(encoder.encode(CharBuffer.wrap(response)));
			// Attach an incremented sequence nubmer to the key
			key.attach(num+1);
		    }
		}
	    }
	}
    }
}
