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
/*****************************************************************************/

import java.applet.Applet;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;

/******************************************************************************/

public class HersheyFontEditor extends HersheyFont
{
/******************************************************************************/

HersheyFontEditor( String fontName )
{
   super( fontName );
}
/******************************************************************************/
HersheyFontEditor( URL base, String fontName )
{
   super( base, fontName );
}
/******************************************************************************/
public int numCharacters()
{
   return( charactersInSet );
}
/******************************************************************************/
public int getYmin()
{
   return( characterSetMinY );
}
/******************************************************************************/

public int getYmax()
{
   return( characterSetMaxY );
}
/******************************************************************************/
public int getXmin( char c )
{
   return( characterMinX[ ( int ) c - 32 ] );
}
/******************************************************************************/
public int getXmax( char c )
{
   return( characterMaxX[ ( int ) c - 32 ] );
}
/******************************************************************************/
public void replaceCharacter( char c, int num, char new_char[][] )
{
   int i = ( int ) c - 32;

   for ( int j = 0; j < num ; j++ )
   {
      characterVectors[i][X][j+1] = new_char[X][j];
      characterVectors[i][Y][j+1] = new_char[Y][j];
   } 

   // save the new number of points
   numberOfPoints[i] = num+1;

   // recalculate the size 
   calculateCharacterSize( i, fontAdjustment( this.name ) );

}
/******************************************************************************/
public void v_replaceCharacter( char c, Vector vectors )
{
    int this_c = ( (int) c ) -32 ;
    int k = 0;

    characterVectors[this_c][X][k   ] =  '!';
    characterVectors[this_c][Y][k++ ] =  '!';

    for ( int i = 0; i < vectors.size(); i ++ )
    {
       Vector pvec =  (Vector) vectors.elementAt(i);

       for ( int j = 0; j < pvec.size(); j ++ )
       {
          Point p1 = ( Point ) pvec.elementAt(j);

          characterVectors[this_c][X][k   ] =  (char) (p1.x);
          characterVectors[this_c][Y][k++ ] =  (char) (p1.y);
       }

       // if we are not at the end
       if ( i <  vectors.size()-1 )
       {
 	  // put in the skip
          characterVectors[this_c][X][k   ] =  ' ';
          characterVectors[this_c][Y][k++ ] =  '?';
       }
    }

   // save the new number of points
   numberOfPoints[ this_c ] = k;

   // recalculate the size 
   //   calculateCharacterSize( this_c, fontAdjustment( this.name ) );

}
/******************************************************************************/
public Vector getCharacterVector( char nc )
{
   int c = ( int ) nc - 32;
   Vector vectorPoints = new Vector();
   Vector vectorVectors = new Vector();

   for ( int j = 1; j <  numberOfPoints[c] ; j++ )
   {
       // if this is not the skip
       if (  characterVectors[c][X][j] != ' ' )
       {
          vectorPoints.addElement( new Point( (int) characterVectors[c][X][j] ,
                                              (int) characterVectors[c][Y][j]
                                            )
                                 );
       }
       else
       {
          vectorVectors.addElement( vectorPoints );
          vectorPoints = new Vector();
       } 
   } 
   vectorVectors.addElement( vectorPoints );

   // return the vector
   return( vectorVectors );

}
/******************************************************************************/
public int getCharacter( char nc, char new_char[][] )
{
   int c = ( int ) nc - 32;

   for ( int j = 0; j <  numberOfPoints[c] ; j++ )
   {
       new_char[X][j] = characterVectors[c][X][j];
       new_char[Y][j] = characterVectors[c][Y][j];
   } 

   // return the new number of points
   return( numberOfPoints[c] );

}
/******************************************************************************/

public void writeHersheyFont( )
{
  int             character, n;
  int             c;

  // loop through the characters in the file ... 
  character = 0;
   
  // while we have not processed all of the characters 
  while ( character < charactersInSet )
  {
      // get the number of vertices in this character 
      n = numberOfPoints[character];
  
      System.out.print("1234 " + ( (n>9) ? ( (n>99) ? "" : " " ) : "  " ) +  n );
 	 
      // read in the vertice coordinates ... 
      for (int i = 0; i < n; i++)
      {
	  // if we are at the end of the line 
	  if ((i == 32) || (i == 68) || (i == 104) || (i == 140))
	  {
	      // print the carriage return 
	      System.out.println("");
	  }
	  // print the x coordinate 
	  System.out.print(characterVectors[character][X][i]); 
   	    
	  // print the y coordinate 
	  System.out.print(characterVectors[character][Y][i]);

      }
   	 
      // print the carriage return
      System.out.println("");
   
      // increment the character counter 
      character++;
  }

  return;
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
   super.drawFontLine( x1, y1, x2, y2, width, g );

   //System.out.println("" + x1 + "," + y1 + " " + x2 + "," + y2 );
}
/******************************************************************************/


}
/******************************************************************************/


