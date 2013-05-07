/*****************************************************************************/
//
//  Copyright (c) James P. Buzbee 1996
//  House Blend Software
//
//  jbuzbee@nyx.net
//
// Permission to use, copy, modify, and distribute this software
// for any use is hereby granted provided
// this notice is kept intact within the source file
//
/***************************************************************************/

import java.awt.*;
import java.awt.Graphics;
import java.applet.Applet;
import java.awt.Image;
import java.awt.Event;
import java.io.*;
import java.util.*;

/***************************************************************************/

public class fontMap extends Canvas
{
   int sizex=0;
   int sizey=0;
   int cellswide;
   int cellshigh;
   int base;

   protected HersheyFontEditor mappedFont;

   float display_size = 0.9f;

   int current_character = -1;

   /****************************************************************************/
   fontMap( HersheyFontEditor mF )
   {
      mappedFont = mF;

      sizey = ( int ) ( ( mappedFont.getYmax() - mappedFont.getYmin() ) /
                        ( 1.0f/ display_size )
                      );

      for ( int i = 0 ; i < mappedFont.numCharacters(); i ++ )
      {   
         int x;

         char c = ( char ) (i+32);

         x = ( mappedFont.getXmax(c) - mappedFont.getXmin(c) );

         if ( x > sizex )
	 {
            sizex = ( int ) ( x / ( 1.0f/ display_size ) );
	 }  
      }


   }
   /******************************************************************************/
   public void paint( Graphics g )
   {
      base = (int ) (this.size().width * 0.05f)/2;

      int x = base;
      int y = base;
      int cellx = 0;
      int celly = 0;

      cellswide = ( int ) (this.size().width * .95f ) / sizex;
      cellshigh = mappedFont.numCharacters() / cellswide + 1;
      g.setColor( Color.white );
      g.fillRect( 0,0,this.size().width, this.size().height );

      g.setColor( Color.black );
      
      for ( celly = 0; celly < cellshigh+1; celly++ )
      {
         g.drawLine( cellx+base, 
                     celly*sizey+base, 
                     cellswide*sizex+base, 
                     celly*sizey+base
                   );
      }

      celly=0;

      for ( cellx = 0; cellx < cellswide+1; cellx++ )
      {
         g.drawLine( cellx*sizex+base, 
                     celly+base, 
                     cellx*sizex+base, 
                     cellshigh*sizey+base 
                   );
      }

      g.setColor( Color.red );

      mappedFont.setVerticalAlignment( HersheyFont.VERTICAL_HALF );
    
      mappedFont.setHorizontalAlignment( HersheyFont.HORIZONTAL_CENTER );
       
      mappedFont.setHeight( display_size );
      mappedFont.setWidth( display_size );

      cellx = 0;

      for ( int i = 0 ; i < mappedFont.numCharacters(); i ++ )
      {   
         char c = ( char ) (i+32);

         mappedFont.drawString( "" + c, x+sizex/2, y+sizey/2 ,g  );

         x = x + sizex;

         cellx = cellx+1;

         if ( cellx >= cellswide )
	 {
            x = base;
            cellx = 0;
            y = y + sizey;
         }
      }
    }
   /******************************************************************************/
   public void paintCharacter( char character )
   {
      base = (int ) (this.size().width * 0.05f)/2;
      Graphics g = this.getGraphics();
      int x = base;
      int y = base;
      int cellx = 0;
      int celly = 0;

      cellswide = ( int ) (this.size().width * .95f ) / sizex;
      cellshigh = mappedFont.numCharacters() / cellswide + 1;
      g.setColor( Color.red );

      mappedFont.setVerticalAlignment( HersheyFont.VERTICAL_HALF );
    
      mappedFont.setHorizontalAlignment( HersheyFont.HORIZONTAL_CENTER );
       
      mappedFont.setHeight( display_size );
      mappedFont.setWidth( display_size );

      cellx = 0;

      for ( int i = 0 ; i < mappedFont.numCharacters(); i ++ )
      {   
         char c = ( char ) (i+32);

         if ( c == character )
	 {
           mappedFont.drawString( "" + c, x+sizex/2, y+sizey/2 ,g  );
         }

         x = x + sizex;

         cellx = cellx+1;

         if ( cellx >= cellswide )
	 {
            x = base;
            cellx = 0;
            y = y + sizey;
         }
      }
    }
  /*********************************************************************/

  public boolean mouseDown(Event evt, int x, int y) 
  {
    int c;

    x = x - base;
    x = x / sizex;

    if ( ( x < 0 ) ||  ( x >= cellswide ) )
    {
       return false;
    }

    y = y - base;
    y = y / sizey;

    if ( ( y < 0 ) ||  ( y >= cellshigh ) )
    {
       return false;
    }

    c = y * ( cellswide ) + x;

    if ( c >= mappedFont.numCharacters() )
    {
       return false;
    }

    c+=32;

    current_character = c;

    pickedCharacter( ( char ) c );

    return true;
  }

  /*********************************************************************/
  protected void pickedCharacter( char c )
  {

  }

}



