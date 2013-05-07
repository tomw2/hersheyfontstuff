/*****************************************************************************/
//
//  Copyright (c) James P. Buzbee 1996
//
//  jbuzbee@nyx.net
//
// Permission to use, copy, modify, and distribute this software
// for any use is hereby granted provided
// this notice is kept intact within the source file
//
/***************************************************************************/

import java.awt.*;
import java.applet.Applet;
import java.io.*;
import houseblend.*;

/***************************************************************************/

public class spiral extends java.applet.Applet
{
   HersheyFont romanFont;
   String string;
   float numSpirals;
   int radius;   
   int xOffset;
   int yOffset;
   float startAngle;
   Color foreground;
   Color background;
   float charSize = 1.0f;

   public void init( )
   {
      String param;
      String font;

      // get the font name MUST BE A HERSHEY FONT
      param = getParameter("Font");
      font = ( param == null ) ? "romans" : param;

      // get the display string
      param = getParameter("String");
      string = ( param == null ) 
               ? "Help!  I've fallen, and I can't get up ! " 
               : param;

      // get the number of times to spiral
      param = getParameter("Spirals");
      numSpirals = ( param != null ) ?   Float.valueOf(param).floatValue() : 1;

      // get the radius of the circle
      param  = getParameter("Radius");
      radius = ( param != null ) ? Integer.parseInt( param ) : size().width/2;
     
      // Get the center position
      param  = getParameter("X");
      xOffset = ( param != null ) ? Integer.parseInt( param ) : size().width/2;
     
      // get the center position
      param  = getParameter("Y");
      yOffset= ( param != null ) ? Integer.parseInt( param ) : size().height/2;
     
      // get the starting angle
      param = getParameter("StartAngle");
      startAngle = ( param != null ) ?  Float.valueOf(param).floatValue()
                                     : ( float ) 0.0f;

      // get the size of the character
      param = getParameter("charSize");
      charSize = ( param != null ) ?  Float.valueOf(param).floatValue()
                                     : ( float ) 1.0f;

      // get the foreground color
      foreground = getColorParameter("foreground");
      if ( foreground == null )
      {
         foreground = Color.red ;
      }

      // get the background color 
      background = getColorParameter("background");
      if ( background == null )
      {
         background = Color.white ;
      }

      // load the hershey font
      romanFont = new HersheyFont( getDocumentBase(), font );
   }
   /**************************************************************************/
   protected Color getColorParameter( String name )
   {
      String value = this.getParameter( name );
      int    hexValue;
 
      // parse the number 
      try { hexValue = Integer.parseInt( value, 16 ); }
      catch ( NumberFormatException e ) { return( null ); }
      
      // return it
      return( new Color( hexValue ));
   }
   /**************************************************************************/
   public void paint( Graphics g )
   {
      double x,y;

      // step size around the circle ( don't ya just love "magic numbers" ? )
      int step = ( int ) Math.abs(( ( 5.0 * 250.0/radius ) * charSize ) );
      float adjustedCharSize;
      float endAngle = 360 * numSpirals + startAngle;
      int j = 0;

      // setup
      g.setColor( background );
      g.fillRect( 0,0,this.size().width, this.size().height );

      g.setColor( foreground );

      // move around the specified circle
      for ( float i = startAngle; i < endAngle ; i+=step )
      {
         // if we are doing a spiral, caculate the radius adjustement
         float spiral =  numSpirals > 1 ? ( i / 10.0f ) * charSize : 0.0f ;

         // calculate the radius
         float newRadius = radius - spiral ;

         // degrees to radians
         double theta = Math.PI / 180.0 * i;

         // adjust the char size if we are doing a spiral
         adjustedCharSize = charSize 
                      - ( ( numSpirals > 1 ) 
                           ? ( i / 2000.0f ) 
                           : 0.0f 
                        );

         // recalculate the step based on the new radius and char size
         step = ( int ) Math.abs(( ( 5.0 * 250.0/newRadius ) * 
                                     adjustedCharSize ) 
                                );

         // if we have gone as far as we can
         if ( step <= 0 )
         {
            // stop it
            break;
         }

         // get the character position
         x = Math.sin( theta ) * newRadius + xOffset ;
         y = Math.cos( theta ) * newRadius + yOffset ;

         // specifiy the character size
         romanFont.setHeight( adjustedCharSize );
         romanFont.setWidth( adjustedCharSize );

         // specify the character rotation
         romanFont.setRotation( i );

         // draw a single character
         romanFont.drawString("" + string.charAt( j % string.length() ) , 
                              ( int ) x, 
                              ( int ) y, 
                              g 
                             );

         // increment the character counter
         j++;
      }
    }
}
