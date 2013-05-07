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

public class testmet extends java.applet.Applet
{
   HersheyFont metFont;

   public void init( )
   {
      //metFont = new HersheyFont( getParameter("font"));
      metFont = new HersheyFont( getDocumentBase(), getParameter("font"));
   }
   public void paint( Graphics g )
   {
      int MAXSYMB  = 236 - 32;
      g.setColor( Color.white );
      g.fillRect( 0,0,this.size().width, this.size().height );

      g.setColor(Color.black);
      int y = 50;

      metFont.setHeight(.8f);
      metFont.setWidth(.8f);
      metFont.setItalics(false);
      metFont.setHorizontalAlignment( HersheyFont.HORIZONTAL_LEFT );
      for (int n=0; n<220; n=n+20) {
        StringBuffer s = new StringBuffer();
        for (int i=n; i<n+20; i++) {
          if (i > MAXSYMB) continue;
          byte[] b = {(byte)(i+32)};
          s.append(new String(b,0));
        }
        g.drawString("#"+(n+32),10,y-10);
        metFont.drawString(s.toString(), 50, y, g );
        y = y + 50;
      }
      
    }
}




