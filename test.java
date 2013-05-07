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
import houseblend.*;
/***************************************************************************/

public class test extends java.applet.Applet
{
   HersheyFont scriptFont;
   HersheyFont gothicFont;
   HersheyFont rowmanFont;

   public void init( )
   {
      scriptFont = new HersheyFont(getDocumentBase(), "scripts.jhf");
      gothicFont = new HersheyFont(getDocumentBase(), "gothgbt.jhf");
      rowmanFont = new HersheyFont( getDocumentBase(), "rowmans.jhf");
   }
   public void paint( Graphics g )
   {
      g.setColor( Color.white );
      g.fillRect( 0,0,this.size().width, this.size().height );

      g.setColor( Color.black );
      scriptFont.setHeight( 1.0f );
      scriptFont.setWidth( 1.0f );
      scriptFont.setItalics( true );
      scriptFont.setItalicsSlant( 0.8f );
      scriptFont.setVerticalAlignment( HersheyFont.VERTICAL_TOP );
      scriptFont.setHorizontalAlignment( HersheyFont.HORIZONTAL_CENTER );
      scriptFont.drawString("This is a Centered Script Font", 250, 0, g );

      scriptFont.setHeight( -1.0f );
      scriptFont.setWidth( -1.0f );
      scriptFont.drawString("This is a Centered Script Font", 250, 500, g );

      g.setColor( Color.magenta );
      gothicFont.setRotation( 45 );
      gothicFont.setHeight( 2.0f );
      gothicFont.setWidth( 2.0f );
      gothicFont.setHorizontalAlignment( HersheyFont.HORIZONTAL_CENTER );
      gothicFont.drawString("Gothic!", 220, 170, g );

      g.setColor( Color.green );
      rowmanFont.setLineWidth( 4 );
      rowmanFont.setRotation( 270.0 );
      rowmanFont.setHeight( 1.0f );
      rowmanFont.setWidth( 1.0f );
      rowmanFont.setItalics( false );
      rowmanFont.drawString("This is a Roman Font", 0, 50, g );

      g.setColor( Color.red );
      rowmanFont.setWidth( -1.0f );
      rowmanFont.setHeight( -1.0f );
      rowmanFont.setHorizontalAlignment( HersheyFont.HORIZONTAL_RIGHT );
      rowmanFont.setVerticalAlignment( HersheyFont.VERTICAL_BOTTOM );
      rowmanFont.drawString("This is a Roman Font", 500, 50, g );

      g.setColor( Color.blue );
      rowmanFont.setRotation( -15.0 );
      rowmanFont.setHeight( -1.5f );
      rowmanFont.setWidth( 1.0f );
      rowmanFont.setHorizontalAlignment( HersheyFont.HORIZONTAL_LEFT );
      rowmanFont.drawString("Mirror Image !", 180, 360, g );
      rowmanFont.setHeight( 1.5f );
      rowmanFont.drawString("Mirror Image !", 180, 360, g );

      g.setColor( Color.orange );
      rowmanFont.setRotation( 0.0 );
      rowmanFont.setHeight( 1.0f );
      rowmanFont.setWidth(  1.0f );
      rowmanFont.setItalics( true );
      rowmanFont.setItalicsSlant( -0.5f );
      rowmanFont.setHorizontalAlignment( HersheyFont.HORIZONTAL_LEFT );
      rowmanFont.drawString("Reverse Italics ?", 175, 270, g );
      rowmanFont.setItalicsSlant( 0.5f );
      rowmanFont.drawString("Normal  Italics !", 190, 300, g );
    }
}




