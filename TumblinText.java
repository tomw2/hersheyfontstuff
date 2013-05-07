/*****************************************************************************/
//
//  Copyright (c) Jim Buzbee 1996
//
//  jbuzbee@nyx.net
//  House Blend Software
//
// Permission to use, copy, modify, and distribute this software
// for any is hereby granted provided
// this notice is kept intact within the source file
//
/***************************************************************************/

import java.awt.*;
import java.awt.Graphics;
import java.applet.Applet;
import java.awt.Image;
import java.awt.Event;
import java.io.*;
import java.net.URL;
import java.net.*;
import java.util.*;
import houseblend.*;

/***************************************************************************/

public class TumblinText extends java.applet.Applet implements Runnable 
{
   int count = 0;
   Image offscreen;
   Graphics g1;
   String string;
   String url;
   String fileName;
   Vector textLines = new Vector();
   float charSize;
   int xOffset;
   int yOffset;
   int xOffsetPending;
   int yOffsetPending;
   int lineWidth;
   int wordDelay = 100;
   int tumbleDelay = 10;
   int currentString = 0;
   boolean threadSuspended = false;
   boolean xRotationPending;
   boolean yRotationPending;
   boolean zRotationPending;
   boolean xRotation;
   boolean yRotation;
   boolean zRotation;

   Color foreground;
   Color background;
   int   loopCount;
   int   origLoopCount;

   HersheyFont tumbleFont;
   Thread myThread = null;

   float values[] = {   1.0f, 
                        0.9f, 
                        0.8f, 
                        0.7f, 
                        0.6f, 
                        0.5f, 
                        0.4f, 
                        0.3f, 
                        0.2f, 
                        0.1f, 
                        0.05f,
                        0.1f,
                        0.2f,
                        0.3f, 
                        0.4f,
                        0.5f, 
                        0.6f,
                        0.7f,
                        0.8f,
                        0.9f,
                        1.0f,
                       };

   /*************************************************************************/
   public void start() 
   {
      // if we don't already have a running thread 
      if ( myThread == null ) 
      {
         // create one
         myThread = new Thread( this );

         // start it
         myThread.start();
      }

      return;
   }
   /*************************************************************************/
   public void stop() 
   {
      // it's all over now...
      myThread.stop();

      // null out the thread for garbage collection
      myThread = null;

      return;
   }
   /*************************************************************************/
   public void run() 
   {
      // while we have a thread
      while (myThread != null) 
      { 
         try 
         {
            // pause at the upright word
            Thread.sleep(wordDelay);
         } 
         catch ( InterruptedException e )
         {
            System.out.println("" + e );
         }

         // do our main animation 
         paintit( this.getGraphics() );
      }

      myThread = null;

      return;
   }
   /*************************************************************************/

   public void init( )
   {
      String param;
      String font;

      // get the font name MUST BE A HERSHEY FONT
      param = getParameter("Font");
      font = ( param == null ) ? "rowmans.jhf" : param;

      // build our font
      tumbleFont = new HersheyFont( getDocumentBase(), font );

      // get the URL associated with the applet
      param = getParameter("URL");
      url = ( param == null ) ? null : param;

      // double buffer
      offscreen = this.createImage( size().width, size().height );

      g1 = offscreen.getGraphics();

      // get the display string file
      fileName = getParameter("File");

      // if we did not get a file
      if ( fileName == null )
      {
         // make default display
         textLines = new Vector();
         textLines.addElement( "Tumblin` Text!" );
      }
      else
      {
          // load up from the file
          LoadTextStrings( fileName );
      }

      // Get the center position
      param  = getParameter("X");
      xOffset = ( param != null ) ? Integer.parseInt( param ) : size().width/2;
     
      // get the center position
      param  = getParameter("Y");
      yOffset= ( param != null ) ? Integer.parseInt( param ) : size().height/2;
     
      // word delay
      param  = getParameter("WD");
      wordDelay = ( param != null ) ? Integer.parseInt( param ) : 10000;
 
      // tumble delay
      param  = getParameter("TD");
      tumbleDelay = ( param != null ) ? Integer.parseInt( param ) : 1;

      // loop count before re-read
      param  = getParameter("lc");
      origLoopCount = ( param != null ) ? Integer.parseInt( param ) : 1;
      loopCount = ++origLoopCount;

      // x rotation flag
      param  = getParameter("xr");
      xRotation = ( param != null ) 
                 ? Boolean.valueOf( param ).booleanValue() 
                 : false;

      // y rotation flag
      param  = getParameter("yr");
      yRotation = ( param != null )
                 ? Boolean.valueOf( param ).booleanValue()
                 : false;

      // z rotation flag
      param  = getParameter("zr");
      zRotation = ( param != null )
                 ? Boolean.valueOf( param ).booleanValue()
                 : false;

      // get the size of the character
      param = getParameter("CS");
      charSize = ( param != null ) ?  Float.valueOf(param).floatValue()
                                     : 1.0f;

      // get the line width for the character
      param = getParameter("CW");
      lineWidth = ( param != null ) ? Integer.parseInt( param ) : 1;

      // get the foreground color
      foreground = getColorParameter( getParameter( "fg" ));

      // if we did not get it
      if ( foreground == null )
      {
         // default to red
         foreground = Color.red ;
      }

      // get the background color 
      background = getColorParameter(getParameter( "bg"));

      // if we did not get it
      if ( background == null )
      {
         // default to wite
         background = Color.white ;
      }

      // adjust based on char size
      for( int i = 0; i < values.length; i++ )
      {
         values[i] *= charSize;
      }

      // initialize
      tumbleFont.setHeight( charSize );
      tumbleFont.setLineWidth( lineWidth );

      tumbleFont.setHorizontalAlignment( HersheyFont.HORIZONTAL_CENTER ); 
      tumbleFont.setVerticalAlignment( HersheyFont.VERTICAL_HALF );

   }
   /**************************************************************************/
   protected Color getColorParameter( String name )
   {
      int    hexValue;

      // parse the number 
      try { hexValue = Integer.parseInt( name, 16 ); }
      catch ( NumberFormatException e ) { return( null ); }
      
      // return it
      return( new Color( hexValue ));
   }
   /**************************************************************************/
   public void paint( Graphics g )
   {
      g.drawImage( offscreen, 0,0, this );

      return;
   }
   /**************************************************************************/
   public void paintit( Graphics g )
   {
      double increment = 360.0/values.length;
      double angle = increment;
      int c = currentString;

       // if we don't have a file and a string and we are starting over
       if ( ( fileName != null ) && 
            ( currentString == 0 ) && 
            (--loopCount == 0 )
          )
       {  
          // reload the file
          LoadTextStrings( fileName );

          // reset the count
          loopCount = origLoopCount;
       }

      // setup
      xOffsetPending = xOffset;
      yOffsetPending = yOffset;
      xRotationPending = xRotation;
      yRotationPending = yRotation;
      zRotationPending = zRotation;

      // do the background
      g1.setColor( background );
      g1.fillRect( 0,0,this.size().width, this.size().height );

      // re-parse the parameters
      string = parseString ((String)textLines.elementAt(currentString));

      // set the foreground
      g1.setColor( foreground );
 
      // draw the string into the offscreen buffer
      tumbleFont.drawString( string , xOffset, yOffset, g1 );

      // update on screen
      g.drawImage( offscreen, 0,0, this );

      // for all of our values
      for( int i = 0; i < values.length; i++ )
      {
         // set the background color
         g1.setColor( background );

         // clear the background
         g1.fillRect( 0,0,this.size().width, this.size().height );

         // set the foreground color
         g1.setColor( foreground );

         // if we are doing a y rotation
         if ( yRotation )
         {
            // adjust the character width
            tumbleFont.setWidth( values[i] );
         }

         // if we are doing an x rotation
         if ( xRotation )
         {
            // adjust the height
            tumbleFont.setHeight( values[i] );
         }

         // if we are doing a z rotation
         if ( zRotation )
         {
            // adjust the rotation
            tumbleFont.setRotation( angle );

            // move on to the next angle
            angle+=increment;
          }

         // if this is the time to switch
         if ( values[i] == 0.05f * charSize )
         {
            // move to the next string index
            currentString++;

            // wrap around
            currentString %= textLines.size();

            // pick the string out, and reset the animation parameters
            string = parseString ((String)textLines.elementAt(currentString));
         }

         // draw the string
         tumbleFont.drawString( string, xOffset, yOffset, g1 );

         // update the on-screen image with the offscreen image
         g.drawImage( offscreen, 0,0,this );

         // slow down the pace...
         try 
         {
            // slow it down
            Thread.sleep(tumbleDelay);
         } 
         catch ( InterruptedException e )
         {
            // ooops
            System.out.println("" + e );
         }
      }
    
      // the rotation parameters will only be applied from the starting pos.
      xOffset = xOffsetPending;
      yOffset = yOffsetPending;
      xRotation = xRotationPending;
      yRotation = yRotationPending;
      zRotation = zRotationPending;
      return;
   }
   /************************************************************************/
   public boolean mouseDown(java.awt.Event evt, int x, int y) 
   {
      // if there is a URL associated with this
      if ( url != null )
      {
         URL theURL = null;

         try 
         { 
            // build a URL object associated with the url string
            theURL = new URL( url ); 

            // reset the browser
            getAppletContext().showDocument( theURL );
         }
         catch ( java.net.MalformedURLException e ) 
         { 
            System.out.println("BAD URL " + theURL);
         }
      }
      else  // treat the click as a pause/resume command
      {
         // if we are already suspended        
         if (threadSuspended) 
         {
            // start 'er up
            myThread.resume();
         }
         else 
         {
            // shut 'er down
            myThread.suspend();
         }  

         // flip the state flag
         threadSuspended = ! threadSuspended;
      }
      return true;
   }
   /************************************************************************/
   private String parseString( String string )
   {
      String textString = string;
      String param;

      try 
      {
         int sp = string.indexOf('{');
         int ep = string.indexOf('}');

         // if we have a paramter section
         if ( ( sp >=0 ) && ( ep >= 0 ) )
         {
            // pick it out
            String paramList = string.substring( sp+1, ep-1 );

            // build a tokenizer to get the values
            StringTokenizer st = new StringTokenizer( paramList, " 	=");

            // get the string portion
            textString = string.substring( ep+1 );

            // for all of the tokens 
            for (;;)
            {
               try
               {
                  // start getting tokens
                  String token = st.nextToken();

                  // x rotation flag
                  if ( token.equals("xr") )
                  {
                     xRotationPending = 
                          Boolean.valueOf( st.nextToken() ).booleanValue();
                  }
                  // y rotation flag
                  else if ( token.equals("yr") )
                  {
                     yRotationPending = 
                           Boolean.valueOf( st.nextToken() ).booleanValue();
                  }
                  // z rotation
                  else if ( token.equals("zr") )
                  {
                     zRotationPending = 
                           Boolean.valueOf( st.nextToken() ).booleanValue();
                  }
                  // x position
                  else if ( token.equals("x") )
                  {
                     xOffsetPending = Integer.parseInt( st.nextToken() );
                  }
                  // y position
                  else if ( token.equals("y") )
                  {
                      yOffsetPending = Integer.parseInt( st.nextToken() ); 
                  }
                  // url string
                  else if ( token.equals("url") )
                  {
                      url = st.nextToken(); 
                  }
                  // command file
                  else if ( token.equals("file") )
                  {
                      fileName = st.nextToken(); 
                  }
                  // work delay
                  else if ( token.equals("wd") )
                  {
                      wordDelay = Integer.parseInt( st.nextToken() ); 
                  }
                  // tumble delay
                  else if ( token.equals("td") )
                  {
                      tumbleDelay = Integer.parseInt( st.nextToken() ); 
                  }
                  // foreground color
                  else if ( token.equals("fg") )
                  {    
                     foreground = getColorParameter( st.nextToken() );

                     if ( foreground == null )
                     {
                        foreground = Color.red ;
                     }
                  }
                  // background color
                  else if ( token.equals("bg") )
                  {    
                     background = getColorParameter( st.nextToken() );

                     if ( foreground == null )
                     {
                        foreground = Color.white;
                     }
                  }
               }
               // no more tokens
               catch ( NoSuchElementException e )
               {
                  // break it down
                  break;
               }
            }
         }
      }
      catch( StringIndexOutOfBoundsException e )
      {
         // ooops 
         System.out.println( e );
      }

      // if we have a dynamic string
      if ( textString.equals("<DATE>") )
      {
         // translate it
         return( "" + new Date() );
      }
      else if ( textString.equals("<HOST>") )
      { 
        String hostname;       

        try
        {
            // who are we ?
            hostname = InetAddress.getLocalHost().toString();

        } 
        catch(UnknownHostException u)
        {
           hostname = "unknown";    // oop's, can't figure it out
        }
 
         return( hostname );
      }
      else
      {
         return( textString );
      }      
   }

   /************************************************************************/
   private void LoadTextStrings( String fileName )
   {
      int i = 0;
      String line;
      Vector temp = new Vector( );

      currentString = 0;

      try 
      { 
          InputStream textStream = 
                 new URL( fileName ).openStream();

          DataInput d = new DataInputStream( textStream );

          // while there is data left to read
          while ( (line = d.readLine()) != null )
          {
             // add the line to our list
             temp.addElement( line );
          }
          textLines = temp;
      }
      catch ( java.net.MalformedURLException e ) 
      {
         System.out.println("Malformed URL exception " + e );

         // if we don't have any strings yet
         if ( textLines == null )
         {
           textLines = new Vector();
           textLines.addElement("Bad URL");
         }
      }
      catch (  java.io.IOException e )
      {
         System.out.println(" IO exception " + e );

         // if we don't have any strings yet
         if ( textLines == null )
         {
           textLines = new Vector();
           textLines.addElement("IO Exception");
         }
      }
    }
   /************************************************************************/
}





