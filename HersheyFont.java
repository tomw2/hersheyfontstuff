/*****************************************************************************/
//
//  Copyright (c) James P. Buzbee 1996
//  House Blend Software
//
//  jbuzbee@nyx.net
//  Version 1.1 Dec 11 1996
//
// Permission to use, copy, modify, and distribute this software
// for any use is hereby granted provided
// this notice is kept intact within the source file
//
// Very loosly based on code with authors listed as : 
// Alan Richardson, Pete Holzmann, James Hurt
/*****************************************************************************/

import java.applet.Applet;
import java.awt.*;
import java.io.*;
import java.net.URL;

/******************************************************************************/

public class HersheyFont
{
   public final static int HORIZONTAL_CENTER = 0;
   public final static int HORIZONTAL_LEFT = 1;
   public final static int HORIZONTAL_RIGHT = 2;
   public final static int HORIZONTAL_NORMAL = 1;

   public final static int VERTICAL_TOP = 0;
   public final static int VERTICAL_HALF = 1;
   public final static int VERTICAL_CAP = 2;
   public final static int VERTICAL_BOTTOM = 3;
   public final static int VERTICAL_NORMAL = 3;

   private final static int MAX_CHARACTERS = 256;
   private final static int MAX_POINTS = 400;
   protected final static int X = 0;
   protected final static int Y = 1;

   private float    hersheyWidth = 1;
   private float    hersheyHeight = 1;
   private int      hersheyLineWidth = 1;
   private int      hersheyHorizontalAlignment = HORIZONTAL_NORMAL; 
   private int      herhseyVerticalAlignment = VERTICAL_NORMAL;
   private double   hersheyTheta = 0;
   private boolean  hersheyItalics = false;
   private float    hersheyItalicSlant = 0.75f;
   private String   copyright = "Copyright (c) James P. Buzbee Mar 30, 1996";  

   protected String   name;
   protected char     characterVectors[][][] = 
                                  new char[ MAX_CHARACTERS ][2][MAX_POINTS ];
   protected int      numberOfPoints[] = new int [MAX_CHARACTERS ];
   protected int      characterMinX[];
   protected int      characterMaxX[];
   protected int      characterSetMinY;
   protected int      characterSetMaxY;
   protected int      charactersInSet;

/*****************************************************************************/

private int getInt( InputStream file, int n ) throws IOException
{
   char[]            buf;
   int               c;
   int               j = 0;

   buf = new char[n];

   // for the specified number of characters 
   for (int i = 0; i < n; i++)
   {
      c = file.read();

      // get character and discard spare newlines 
      while ( (  c == '\n'  ) || ( c == '\r' ) )
      { 
	 c = file.read(); 
      }
      
      // if we hit end of file 
      if (c == -1 )
      {
	 // return an error 
         return (c);
      }
 
      // if this is not a blank 
      if ( ( char ) c != ' ' )
      {
         // save the character 
         buf[j++] = ( char ) c;
      }
   }

   // return the decimal equivilent of the string 
   return (Integer.parseInt( String.copyValueOf( buf,0,j ) ) );

}
/******************************************************************************/
HersheyFont( String fontName )
{
   name = fontName;
   try
   {
      // open the font file 
      InputStream fontStream = new FileInputStream( fontName );

      // load the font file 
      LoadHersheyFont( fontName, fontStream );

      // close the font file 
      fontStream.close();

   }
   catch ( FileNotFoundException e )
   {
      System.out.println(e); 
   }
   catch ( IOException e )
   {
      System.out.println(e); 
   }

   return;
}
/******************************************************************************/
HersheyFont( URL base, String fontName )
{
   name = fontName;

   try
   {
      // open the font file 
      InputStream fontStream= new URL(base, fontName ).openStream();

      // load the font file 
      LoadHersheyFont( fontName, fontStream );

      // close the font file 
      fontStream.close();
   }
   catch ( FileNotFoundException e )
   {
       System.out.println(e);
   }
   catch ( IOException e )
   {
       System.out.println(e);
   }
   /*
   int k = ( int ) 'W' - ( int ) ' ';

   replaceCharacter( 'i',  numberOfPoints[k], characterVectors[k] );

   if ( fontName.indexOf("scri") >= 0 )
   {
      writeHersheyFont();
   }
   */
   return;
}
/******************************************************************************/

private void LoadHersheyFont(String fontname, InputStream fontStream )
{
   int             character,
                   n;
   int             c;
   int             xadjust = fontAdjustment( fontname );

   try
   {
      // loop through the characters in the file ... 
      character = 0;
   
      // while we have not processed all of the characters 
      while ( true )
      {
            // if we cannot read the next field 
         if ( getInt(fontStream, 5) < 1)
         {
   	    // we are done, set the font specification for num chars 
            charactersInSet = character;
   	 
   	    // break the read loop 
            break;
         }
         else
         {
   	    // get the number of vertices in this character 
   	    n = getInt(fontStream, 3);
   	 
     	    // save it 
            numberOfPoints[character] = n;
   	 
   	    // read in the vertice coordinates ... 
            for (int i = 0; i < n; i++)
            {
   	       // if we are at the end of the line 
               if ((i == 32) || (i == 68) || (i == 104) || (i == 140))
   	       {
   	          // skip the carriage return 
                  fontStream.read();
   	       }

               // get the next character
               c = fontStream.read();

               // if this is a return ( we have a DOS style file )
               if ( c == '\n' )
               { 
                  // throw it away and get another
                  c = fontStream.read();
               }
   	    
   	       // get the x coordinate 
               characterVectors[character][X][i] = ( char ) c; 
   	    
   	       // read the y coordinate 
               characterVectors[character][Y][i] = ( char ) fontStream.read(); 
            }
   	 
   	    // skip the carriage return
            fontStream.read();
   
   	    // increment the character counter 
            character++;
         }
      }
         // determine the size of each character ... 
   
      characterMinX = new int[ charactersInSet ];
      characterMaxX = new int[ charactersInSet ];
   
      // initialize ... 
      characterSetMinY = 1000;
      characterSetMaxY = -1000;
  
      // loop through each character ( except the space character )
      for (int j = 1; j < charactersInSet; j++)
      {
 	 // calculate the size
         calculateCharacterSize( j, xadjust );
      }

      // handle the space character - if the 'a' character is defined
      if ( ( ( int ) 'a' - ( int ) ' ' ) <= charactersInSet )
      {
	// make the space character the same size as the 'a'
         characterMinX[0] = characterMinX[ ( int ) 'a' - ( int ) ' ' ];  
         characterMaxX[0] = characterMaxX[ ( int ) 'a' - ( int ) ' ' ];
      }
      else
      {
	 // make the space char the same size as the last char
         characterMinX[0] = characterMinX[ charactersInSet -1 ];  
         characterMaxX[0] = characterMaxX[ charactersInSet -1 ];
      }
   }
   catch ( IOException e )
   {
      System.out.println(e); 
   }

   return;
}
/*************************************************************************************/
protected void calculateCharacterSize( int j, int xadj  )
{
  characterMinX[j] = 1000;
  characterMaxX[j] = -1000;

  // for all the vertices in the character 
  for (int i = 1; i < numberOfPoints[j]; i++)
  {
      // if this is not a "skip" 
      if (characterVectors[j][X][i] != ' ')
      {
	  // if this is less than our current minimum 
	  if (characterVectors[j][X][i] < characterMinX[j] )
	  {
	      // save it 
	      characterMinX[j] = characterVectors[j][X][i];
	  }
   	    
	  // if this is greater than our current maximum 
	  if ( characterVectors[j][X][i] > characterMaxX[j] )
	  {
	      // save it 
	      characterMaxX[j] =  characterVectors[j][X][i];
	  }
   	    
	  // if this is less than our current minimum 
	  if (characterVectors[j][Y][i] < characterSetMinY )
	  {
	      // save it 
	      characterSetMinY =  characterVectors[j][Y][i];
	  }
   	    
	  // if this is greater than our current maximum 
	  if (characterVectors[j][Y][i] > characterSetMaxY )
	  {
	      // save it 
	      characterSetMaxY =  characterVectors[j][Y][i];
	  }
      }
  }

  characterMinX[j] -= xadj;
  characterMaxX[j] += xadj;
}

/******************************************************************************/
   
public void drawString( String text, int x, int y, Graphics g )
{
   drawText( text, 
	     x,
	     y, 
	     hersheyWidth,
             hersheyHeight,
	     hersheyHorizontalAlignment, 
	     herhseyVerticalAlignment, 
	     hersheyTheta,
	     true,
             g
	    );
   return;
}
/******************************************************************************/

public void setRotation( double theta )
{
   hersheyTheta = theta;
   return;
}

/******************************************************************************/
public void setWidth( float width )
{
   hersheyWidth = width;
   return;
}
/******************************************************************************/
public void setHeight( float height )
{
   hersheyHeight = height;
   return;
}
/******************************************************************************/
public void setVerticalAlignment( int alignment )
{
   herhseyVerticalAlignment = alignment;
   return;
}

/******************************************************************************/
public void setHorizontalAlignment( int alignment )
{
   hersheyHorizontalAlignment = alignment;
   return;
}
/******************************************************************************/
public void setItalics ( boolean flag )
{
   hersheyItalics = flag;
   return;
}
/******************************************************************************/
public void setItalicsSlant ( float slant )
{
   hersheyItalicSlant = slant;
   return;
}

/******************************************************************************/
public void setLineWidth ( int width )
{
   hersheyLineWidth = width;
   return;
}

/******************************************************************************/
public String getName (  )
{
   return( name );
}
/******************************************************************************/

protected int
drawText(String text, 
	    int xc,
	    int yc, 
	    float width,
            float height,
	    int Horizontal_Alignment, 
	    int Vertical_Alignment, 
	    double theta,
	    boolean Draw,
            Graphics g
	    )
{

   int             i;
   int             character;
   int             len;
   int             rotpx = 0,
                   rotpy = 0;
   int             xp,
                   yp;
   boolean         rotate = false;
   float           scale;
   float	   cosTheta = 0,
		   sinTheta = 0;
   float           verticalOffsetFactor = 0;

   // set the flag to true if the angle is not 0.0 
   rotate = ( theta != 0.0 ) ? true : false ;
   
   // if we are to do a rotation 
   if ( rotate )
   {
      // set up the rotation variables       
      theta = -Math.PI / 180.0 * theta;
      cosTheta = ( float ) Math.cos(theta);
      sinTheta = ( float ) Math.sin(theta);

      // set the position to do all rotations about  
      rotpx = xc;
      rotpy = yc;     
   }
   
      // starting position 
   xp = xc;
   yp = yc;
   
   switch ( Vertical_Alignment )
   {
      case VERTICAL_TOP :
	 verticalOffsetFactor = 0;
	 break;
	 
      case VERTICAL_HALF :
	 verticalOffsetFactor = 0.5f;
	 break;
	 
      case VERTICAL_NORMAL : // also VERTICAL_BOTTOM 
	 verticalOffsetFactor = 1;
	 break;
	 
      case VERTICAL_CAP :
	 verticalOffsetFactor = 0.25f;
	 break;
	 
   }
   
   // move the y position based on the vertical alignment 
   yp = yp - (int) ( verticalOffsetFactor * 
	( height * (float) (characterSetMaxY -  characterSetMinY)));

   // if we have a non-standard horizontal alignment 
   if ( (Horizontal_Alignment != HORIZONTAL_LEFT ) &&
        (Horizontal_Alignment != HORIZONTAL_NORMAL ) 
      )
   {
         // find the length of the string in pixels ... 
      len = 0;

      for (int j = 0; j < text.length(); j++)
      {
            // the character's number in the array ... 
         character = ( int ) text.charAt( j ) - ( int ) ' ';

         len += (characterMaxX[character] - characterMinX[character]) * width;
      }

         // if we are center aligned  
      if (Horizontal_Alignment == HORIZONTAL_CENTER )
      {
	 // move the starting point half to the left 
         xp -= len / 2;
      }
      else
      {
	 // alignment is right, move the start all the way to the left 
         xp -= len;
      }
   }
   
   // if we are not going to actually draw the string 
   if ( ! Draw )
   {
      // set up to initialize the bounding rectangle 
   }
   
   // loop through each character in the string ... 
   for (int j = 0; j < text.length(); j++)
   {
      // the character's number in the array ... 
      character = ( int ) text.charAt( j ) - ( int ) ' ';

      // render this character 
      drawCharacter( 
                     xp,
                     yp,
                     rotpx,
                     rotpy,
                     width,
                     height,
                     rotate,
                     sinTheta,
                     cosTheta,
                     Draw,
                     characterVectors[character],
                     numberOfPoints[character],
                     characterMinX[character],
                     characterSetMinY ,
                     hersheyItalics,
                     hersheyItalicSlant,
                     g
                   );

         // advance the starting coordinate 
      xp += (int) ((float) (characterMaxX[character] - 
                            characterMinX[character] 
                           ) * width
                  );

   } // end for each character 

   return(0);
}


/******************************************************************************/

protected void drawFontLine( int x1, 
                           int y1, 
                           int x2, 
                           int y2, 
                           int width, 
                           Graphics g 
                         )
{
   // if the width is greater than one 
   if ( width > 1 )
   {
      Polygon filledPolygon = new Polygon();

      int offset = width/2;

      // this does not generate a true "wide line" but it seems to
      // look OK for font lines 
      
      filledPolygon.addPoint( x1 - offset, y1 + offset );
      filledPolygon.addPoint( x1 + offset, y1 - offset );
      filledPolygon.addPoint( x2 + offset, y2 - offset );
      filledPolygon.addPoint( x2 - offset, y2 + offset );

      // draw a polygon 
      g.fillPolygon(filledPolygon);
   }
   else
   {
      // draw a line 
      g.drawLine( x1,y1, x2,y2 );
   }

   return;
}
/******************************************************************************/
protected int fontAdjustment( String fontname )
{
   int             xadjust = 0;

   // if we do not have a script type font 
   if ( fontname.indexOf("scri") < 0 )
   {
     // if we have a gothic font
     if (  fontname.indexOf("goth") >= 0  )
     {
        xadjust = 2;
     }
     else
     {
        xadjust = 3;
     }  
   }

   return( xadjust );

}
/******************************************************************************/

protected void drawCharacter( 
                              int xp,
                              int yp,
                              int rotpx,
                              int rotpy,
                              float width,
                              float height,
                              boolean rotate,
                              float sinTheta,
                              float cosTheta,
                              boolean Draw,
                              char Vectors[][],
                              int  numberOfPoints,
                              int minX,
                              int characterSetMinY,
                              boolean Italics,
                              float slant,
                              Graphics g
                           )
{
    float           xd,
                   yd,
                   xd2,
                   yd2;
    int            oldx = 0,
                   oldy = 0,
                   x,
                   y,
                   i;
    boolean        skip = true;
    int            thisCharacterMinX;
    int            thisCharacterMaxX;
    float          finalSlant = height * ( -slant );
                                             
            
    //System.out.print("" +  Vectors[X][0] +  Vectors[Y][0] );

      // loop through each vertex in the character 
   for (i = 1; i < numberOfPoints; i++)
   {
     //System.out.print("" +  Vectors[X][i] +  Vectors[Y][i] );
       // if this is a "skip" 
      if ( Vectors[X][i] == ( int ) ' ')
      {
         // set the skip flag 
         skip = true ;
      }
      else
      {
         // calculate italics offset if necessary 
         x = ( int )	( ( Italics ) 
                       ? ( ( Vectors[Y][i] - 
                             characterSetMinY
                           ) * finalSlant 
                         ) 
                       : 0 
                      ) +
                // add italics offset to the "normal" point transformation 
	   //	        xp + (int) ((float) (Vectors[X][i] - 
	   //			     minX ) * width );

         transformX( xp, Vectors[X][i], minX, width );
	    
         // calculate the y coordinate 
         //y = yp + (int) ((float) (Vectors[Y][i] -
	 //				     characterSetMinY  ) * height);
         y = transformY( yp, Vectors[Y][i], characterSetMinY, height );

         // if we are doing a rotation 
	 if ( rotate )
         {
	    // apply the rotation matrix ... 
	       
            // transform the coordinate to the rotation center point 
	    xd = ( float ) (x - rotpx);
	    yd = ( float ) (y - rotpy);
	       
            // rotate 
	    xd2 = xd * cosTheta - yd * sinTheta;
	    yd2 = xd * sinTheta + yd * cosTheta;
	       
            // transform back 
	    x = (int) (xd2 + 0.5) + rotpx;
	    y = (int) (yd2 + 0.5) + rotpy;
	 }
	    
         if ( ! skip )
         {
	    // if we are to draw the string 
	    if ( Draw )
            {
               drawFontLine( oldx, oldy, x,y, hersheyLineWidth, g );
	    }
	    else
	    {
	       // we just want the bounding box of the string 
               // TBD 
            }
	 } // end if not skip 
	    
         skip = false;

         oldx = x;
         oldy = y;
	    
      } // end if skip 
	 
   } // end for each vertex in the character 

   //System.out.println("");

}
/******************************************************************************/

protected int transformX( int xoffset, int px, int minx, float mag )
{

   return( ( int ) ( xoffset + ( px - minx ) * mag ) );
}

/******************************************************************************/

protected int transformY( int yoffset, int py, int miny, float mag )
{

   return( ( int ) ( yoffset + ( py - miny ) * mag ) );
}


}
/******************************************************************************/


