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

public class testedit extends java.applet.Applet
{
   HersheyFontEditor scriptFont;
   editorFontMap p0;
   Panel p1;
   characterCanvas p2;
   Panel p3;
   Canvas p4;
   Panel p5;
   Stack cStack;

   public void init( )
   {
      String font;
      String param;

      // get the font name MUST BE A HERSHEY FONT
      param = getParameter("fontname");
      font = ( param == null ) ? "romans.jhf" : param;

      scriptFont = new HersheyFontEditor(getDocumentBase(), font );

      cStack = new Stack();

      p1 = new Panel();                         // Lower half of window
      p2 = new characterCanvas();               // Edit window
      p2.set_font( scriptFont );
      p2.setStack( cStack );
      p3 = new Panel();                         // edit menu
      p4 = new Canvas();                        // unused lower left

      p0 = new editorFontMap( scriptFont,p2 );  // upper half - character display
      p2.setFontMap( p0 );

      p1.setLayout( new GridLayout(1,3,10,10) );
      p3.setLayout( new GridLayout(5,2,2,2) );

      this.setLayout( new GridLayout(2,1,10,10) );
      this.add( p0 );

      p1.add( p2 );

      p3.add( new Button("In") );
      p3.add( new Button("Out") );
      p3.add( new Button("Up") );
      p3.add( new Button("Down") );
      p3.add( new Button("Right") );
      p3.add( new Button("Left") );
      p3.add( new Button("Delete") );
      p3.add( new Button("Clear") );
      p3.add( new Button("Undo") );
      p3.add( new Button("Line") );
      p3.add( new Button("Write") );

      p1.add( p3 );
      p1.add( p4 );

      this.add( p1 );

      p2.setDisplayGraphics( p4.getGraphics(), 500, 500 );

   }
  /*********************************************************************/

  public boolean action(Event evt, Object arg )
  {
    float factor = 1.5f;

    if ( evt.target instanceof Button )
    {
       String s = ( String ) arg;

       if ( s.equals("In" ) )
       {
          p2.setMag( factor );
       }
       else if ( s.equals("Out" ) )
       {
         p2.setMag( ( float ) (1.0/factor));
       }
       else if ( s.equals("Up" ) )
       {
          p2.setScroll( 0, -10 );
       }
       else if ( s.equals("Down" ) )
       {
          p2.setScroll( 0, 10 );
       }
       else if ( s.equals("Right" ) )
       {
          p2.setScroll( 10, 0 );
       }
       else if ( s.equals("Left" ) )
       {
          p2.setScroll( -10, 0 );
       }
       else if ( s.equals("Delete" ) )  
       {
          p2.deleteVector();
       }
       else if ( s.equals("Clear" ) )  
       {
          p2.deleteAllVectors();
       }
       else if ( s.equals("Undo" ) )  
       {
          p2.undoChange();
       }
       else if ( s.equals("Line" ) )  
       {
          if ( ! p2.LineInProgress() )
	  {
            p2.LineInProgress(true);
          }
          else
	  {
            p2.LineInProgress( false );
          }

       }
       else if ( s.equals("Write" ) )  
       {
System.out.println("______________________ Cut Here ________________________________");
          scriptFont.writeHersheyFont();
System.out.println("______________________ Cut Here ________________________________");
       }
       else
       {
          return false;
       }
    }

    return true;
  }

}

/***********************************************************************/
class characterCanvas extends Canvas
{
   private char current_character = 'a';
   private HersheyFontEditor hf;
   float mag = 5.0f;
   int xoffset = 0;
   int yoffset = 0;
   int xscroll = 0;
   int yscroll = 0;
   int which = -1;
   Graphics g,g1;
   int g1width, g1height;
   int cXMin, cYMin;
   Vector currentVector;
   Vector nvec;
   Stack cStack;
   float downx;
   float downy;
   boolean dragging = false;
   boolean LineInProgress = false;
   float xdraglast = 0;
   float ydraglast = 0;
   int xmdraglast;
   int ymdraglast;

   boolean firstPoint;
   boolean firstDrag;
   Vector lineVec;
   Vector workingVec;
   editorFontMap eFM;

  /*********************************************************************/

   characterCanvas()
   {
      super();
      g = this.getGraphics();
   }

  /*********************************************************************/
  public void setMag( float m )
  {
     mag *= m;

     if ( mag < 0.3f )
     {
       mag = 0.3f;
     }
     else if ( mag > 30 )
     {
       mag = 30;
     }


     drawCharacter( current_character );
  }
  /*********************************************************************/
  public void setStack( Stack s )
  {
     cStack = s;
  }
  /*********************************************************************/
  public void setFontMap( editorFontMap e )
  {
     eFM = e;;
  }
  /*********************************************************************/
  public void LineInProgress( boolean state )
  {
     LineInProgress = state;  
  
     if ( LineInProgress )
     {
       // initialize
       downx = xdraglast = -1;

       // get a vector to collect points into
       lineVec = new Vector();

       // get the whole vector
       workingVec = hf.getCharacterVector( current_character );

       // save state
       pushChange();

       // add this new line ( empty at this point )
       workingVec.addElement( lineVec );

       g.setColor( Color.blue );
     }
     else
     {
        // if we have drawn anything before 
        if ( downx > 0  )
	{
          g.setXORMode(getBackground());

          // erase it
	  g.drawLine( (int)downx, (int)downy, (int)xdraglast, (int)ydraglast ); 

        }
     }

    firstPoint = !state;

  }
  /*********************************************************************/
  public boolean LineInProgress( )
  {
     return( LineInProgress );
  }
  /*********************************************************************/
  public void setScroll( int xs, int ys )
  {

     xscroll += xs;
     yscroll += ys;

     drawCharacter( current_character );
  }
  /*********************************************************************/

  //public void set_current( char c )
  //{
  //  current_character = c;
  //} 
  /*********************************************************************/
   public void set_font( HersheyFontEditor f )
   {
     hf = f;
   }
  /*********************************************************************/
   public void setDisplayGraphics( Graphics g, int w, int h )
   {
     g1 = g;
     g1width = w;
     g1height = h;
   }
  /*********************************************************************/
   private final void pushChange( )
  {
    Vector vectorVectors = new Vector();
    Vector nvec = hf.getCharacterVector( current_character );

    for ( int j = 0; j <  nvec.size() ; j++ )
    {            
	Vector v = ( Vector ) nvec.elementAt( j );
        Vector nv = new Vector();

        for ( int k = 0; k <  v.size() ; k++ )
        {            
	   Point p = ( Point ) v.elementAt( k );
           Point np = new Point( p.x, p.y );
           nv.addElement( np );
        }           

	vectorVectors.addElement(nv );
    }

    // push a copy 
    cStack.push( vectorVectors );
  }
  /*********************************************************************/
   // debugging only
  private final void printvector( Vector v0 )
  {
    for ( int j = 0; j <  v0.size() ; j++ )
    {            
	Vector v1 = ( Vector ) v0.elementAt( j );

        for ( int k = 0; k <  v1.size() ; k++ )
        {            
	   fPoint p = ( fPoint ) v1.elementAt( k );

           System.out.print("" + p.x + "," + p.y + " " );
       }
           System.out.println("");
    }

    System.out.println("");
 
  }
  /*********************************************************************/
   private final void popChange( )
   {
     if ( ! cStack.empty() )
     {
        Vector v = ( Vector ) cStack.pop();

        hf.v_replaceCharacter( current_character, v );
     }
   }
  /*********************************************************************/
   public void deleteVector( )
   {
     if ( which >= 0 )
     {
        pushChange();

        nvec.removeElementAt( which );
        which = -1;
        hf.v_replaceCharacter( current_character, nvec );
        drawCharacter( current_character );
     }
   }
  /*********************************************************************/
   public void deleteAllVectors( )
   {
     pushChange();
     which = -1;
     Vector nvec = hf.getCharacterVector( current_character );


     for ( int i = 0; i < nvec.size(); i++ )
     {       
        nvec.removeElementAt( i );
     }
     nvec= new Vector();
     hf.v_replaceCharacter( current_character, nvec );
     drawCharacter( current_character );
   }
  /*********************************************************************/
   public void undoChange( )
   {
     if ( ! cStack.empty() )
     {
        popChange();

        drawCharacter( current_character );
     }
   }
  /*********************************************************************/
   public void clearStack( )
   {
     while ( ! cStack.empty() )
     {
        cStack.pop();
     }
   }
  /*********************************************************************/
 
   public void drawCharacter( char c )
   {
    char points[][]=new char[2][1000];
    int num;
    int xmid;
    int ymid;
    int editx = this.size().width/2 ;
    int edity = this.size().height/2;
    int ly;
    int fYmin = 

    current_character = c;

    cXMin =  hf.getXmin( current_character );
    cYMin = hf.getYmin();

    g = this.getGraphics();

    g.setColor( Color.white );
    g.fillRect( 0, 0, this.size().width, this.size().height );

    xmid =  (int) ( ( hf.getXmax( c ) - 
                      cXMin
                    ) * mag  
                  ) / 2;

    ymid =  (int) ( ( hf.getYmax() - 
                      cYMin 
                    ) * mag 
                  ) / 2;

   xoffset = editx+xscroll-xmid;
   yoffset = edity+yscroll-ymid;

   g.setColor( Color.green);

   // draw the font min Y
   ly = hf.transformY( yoffset, cYMin, cYMin, mag );
   g.drawLine( 0, ly, this.size().width, ly );

   // draw the font max Y
   ly = hf.transformY( yoffset, hf.getYmax(), cYMin, mag );
   g.drawLine( 0, ly,  this.size().width, ly );

   g.setColor( Color.blue );

   num = hf.getCharacter( current_character, points );

   hf.drawCharacter(  xoffset,
                      yoffset,
                      0,               // rotpx
                      0,               // rotpy
                      mag,             // width
                      mag,             // height
                      false,
                      0f,
                      0f,
		      true,
		      points,
		      num,
		      cXMin,
		      cYMin,
		      false, 
		      0,
		      g
		  );

   g1.setColor( Color.white );
   g1.fillRect( 0, 0, g1width, g1height );
   g1.setColor( Color.black );

   hf.drawCharacter(   100,
                      100,
                      0,               // rotpx
                      0,               // rotpy
                      1.0f,             // width
                      1.0f,             // height
                      false,
                      0f,
                      0f,
		      true,
		      points,
		      num,
		      cXMin,
		      cYMin,
		      false, 
		      0,
		      g1
		  );

   hf.drawCharacter(   150,
                      50,
                      0,               // rotpx
                      0,               // rotpy
                      0.5f,             // width
                      0.5f,             // height
                      false,
                      0f,
                      0f,
		      true,
		      points,
		      num,
		      cXMin,
		      cYMin,
		      false, 
		      0,
		      g1
		  );

  hf.drawCharacter(   50,
                      50,
                      0,               // rotpx
                      0,               // rotpy
                      1.5f,             // width
                      1.5f,             // height
                      false,
                      0f,
                      0f,
		      true,
		      points,
		      num,
		      cXMin,
		      cYMin,
		      false, 
		      0,
		      g1
		  );
  hf.drawCharacter(   50,
                      150,
                      0,               // rotpx
                      0,               // rotpy
                      2f,             // width
                      2f,             // height
                      false,
                      0f,
                      0f,
		      true,
		      points,
		      num,
		      cXMin,
		      cYMin,
		      false, 
		      0,
		      g1
		  );   
  }
  /*********************************************************************/
  public boolean mouseMove(Event evt, int x, int y) 
  {
     fPoint p = snapPosition( (float)x,(float)y );

     // if we are drawing a line and we have a point
     if ( LineInProgress && lineVec.size() > 0 )
     {
        // if we have drawn anything before 
        if ( ! firstPoint  )
	{
          g.setXORMode(getBackground());

          // erase it
	  g.drawLine( (int)(downx+0.5), (int)(downy+0.5), (int)(xdraglast+0.5), (int)(ydraglast+0.5) ); 
        }

        // if we have some points 
        if ( lineVec.size() > 0 )
	{
          firstPoint = false;
	  //System.out.println("" + p.x + "," + p.y + " " );

          g.drawLine( (int)(downx+0.5), (int)(downy+0.5), (int)(p.x+0.5), (int)(p.y+0.5) );
        }
     }

     ydraglast = p.y;
     xdraglast = p.x;

     return( false );
  }
  /*********************************************************************/

  public boolean mouseDrag(Event evt, int x, int y) 
  {
     // if we are really drawing a line, or if nothing is selected 
     if ( LineInProgress || which < 0 )
     {
       return ( false );
     }

     dragging = true;

     fPoint p0 = dctowc( downx, downy );
     fPoint p1 = dctowc( (float)x,(float)y );
     int xdist = (int)(p1.x - p0.x);
     int ydist = (int)(p1.y - p0.y);

     if ( xmdraglast > -999 )
     {
       g.setXORMode(getBackground());

       // erase the previous
       drawCharacterVector( which, xmdraglast, ymdraglast, g );
     }

     // draw the new
     drawCharacterVector( which, xdist, ydist, g );

     ymdraglast = ydist;
     xmdraglast = xdist;

     return false;
  }
  /*********************************************************************/
  private void drawCharacterVector( int v, int x, int y, Graphics g )
  {
    Vector nvec = hf.getCharacterVector( current_character );

    // get the selected vector
    currentVector =  (Vector) nvec.elementAt( v );
    
    // for all of the points in the vector
    for ( int k = 0; k < currentVector.size()-1; k++)
    {
	int x1,y1,x2,y2;
	Point p;
        fPoint np;

	p =  ( Point ) currentVector.elementAt( k );
        np = wctodc( (p.x+x), (p.y+y) );
        x1 = ( int ) ( np.x + 0.5 );
        y1 = ( int ) ( np.y + 0.5 );

	p =  ( Point ) currentVector.elementAt( k+1 );
        np = wctodc( (p.x+x), (p.y+y) );
        x2 = ( int ) ( np.x + 0.5 );
        y2 = ( int ) ( np.y + 0.5 );

	// draw a line 
	g.drawLine( x1,y1, x2,y2 );
    }

    return;
  }
  /*********************************************************************/
  private fPoint wctodc( float x, float y )
  {
    //System.out.println("wctodc " + xoffset + ", " + x + ", " + cXMin + ", " + mag );

    float x1 =  ( xoffset + ( x - cXMin ) * mag ) ;
    float y1 =  ( yoffset + ( y - cYMin ) * mag ) ;

    return( new fPoint( x1,y1) );
  }
  /*********************************************************************/
  private fPoint dctowc( float x, float y )
  {
    float x1 = ((( x - xoffset ) / mag ) + cXMin );
    float y1 = ((( y - yoffset ) / mag ) + cYMin );

    return( new fPoint( x1,y1) );
  }
  /*********************************************************************/
  public fPoint snapPosition( float x, float y )
  {
    // snap the point to the final resolution
    fPoint np = dctowc( x, y );
    return( wctodc( np.x,np.y ) );
  }
  /*********************************************************************/
  //  private void drawGrid( Graphics g)
  //{
  //  for ( int x = 0; x < 100; x++ )
  //  {
  //     Point p0 = wctodc( x,0 );
  //     Point p1 = wctodc( x,100 );

  //        g.drawLine(p0.x,p0.y,p1.x,p1.y);
  //  }

  //    for ( int y = 0; y < 100; y++ )
  //  {
  //    Point p0 = wctodc( 0,y );
  //    Point p1 = wctodc( 100,y );
  //
  //    g.drawLine(p0.x,p0.y,p1.x,p1.y);
  //  }
  // }
  /*********************************************************************/
  public boolean mouseUp(Event evt, int x, int y) 
  {
     // if we are dragging something and are not drawing a line	
     if ( dragging  && which >= 0 && !  LineInProgress )
     {
         fPoint p0 = dctowc( downx, downy );
         fPoint p1 = dctowc( (float)x,(float)y );
         int xdist = (int)(p1.x - p0.x);
         int ydist = (int)(p1.y - p0.y);

	 Vector nvec = hf.getCharacterVector( current_character );

	 Vector pvec =  (Vector) nvec.elementAt(which);

         // for all of the points in this vector
	 for ( int j = 0; j < pvec.size(); j ++ )
	 {
	     Point p2 = ( Point ) pvec.elementAt(j);

	     p2.translate( xdist, ydist );
	 }

         // nothing currently selected 
	 which = -1;
         xmdraglast = -999;

	 // save the current state 
	 pushChange();

	 // update with the new
	 hf.v_replaceCharacter( current_character, nvec );

	 // draw it
	 drawCharacter( current_character );
     }

     dragging = false;

     return( false );
  }
  /*********************************************************************/
  public boolean mouseDown(Event evt, int x, int y) 
  {
    float minDistance = 9999f;

    // if we are drawing a line and the right mouse button was pressed
    if ( LineInProgress && ( (evt.modifiers & Event.META_MASK ) != 0 ) )
    {
      // stop it
       LineInProgress( false);
       return ( false );
    }

    // snap the point to the final resolution
    fPoint np = snapPosition( (float)x,(float)y );

    downx = np.x;
    downy = np.y;

    // if we are drawing a line
    if ( LineInProgress )
    {       
      //      System.out.println("mouse down " + downx + "," + downy + " " );
      //    fPoint pp = dctowc( downx, downy );
      //   System.out.println("mouse todc " + pp.x + "," + pp.y );
      //   fPoint pp1 = wctodc( pp.x, pp.y );
      //    System.out.println("mouse trans " + pp1.x + "," + pp1.y + " xoffset " + xoffset + "cxmin" + cXMin + " mag " + mag );

         fPoint dpx = dctowc(  downx, downy );
          Point  lp = new Point( ( int ) ( dpx.x +0.5), ( int ) (  dpx.y +0.5) );

       lineVec.addElement( lp ) ;

       // save state
       pushChange();

       // update with this new point
       hf.v_replaceCharacter( current_character, workingVec );

      drawCharacter( current_character );

      return ( false );
    }

    xmdraglast = -999;

    fPoint pt =  dctowc( downx, downy );
    Point point = new Point( ( int) pt.x, ( int) pt.y);
    

    float d;
 
    nvec = hf.getCharacterVector( current_character );

    // for all of the vectors in the character
    for ( int i = 0; i < nvec.size()  ; i ++ )
    {
       Vector pvec =  (Vector)nvec.elementAt(i);

       // calculate the distance from the pick to the vector
       d = polyline_pick.PolylineDistance( pvec, point, pvec.size(), 1.0f );

       // if this is closer than the minimum so far
       if ( d < minDistance )
       {
          minDistance = d;
          which = i;
       }
    }

    // if we got a pick
    if ( minDistance < 2 )
    {
        // get the selected vector
	currentVector =  (Vector) nvec.elementAt( which );

        // redraw to clear any old highlight
        drawCharacter( current_character );

        g.setColor( Color.red );

        // highlight it
        drawCharacterVector( which, 0, 0, g );
    }
    else
    {
       drawCharacter( current_character );
       which = -1;
    }

    return false;
  }
  /*********************************************************************/
  public void paint( Graphics g )  
  {
    drawCharacter( current_character );
  }
  /*********************************************************************/
}
/*********************************************************************/
class fPoint 
{ 
   public float x; 
   public float y; 

   fPoint( float nx, float ny )
   {
      x = nx;
      y = ny;
   }

}
/*********************************************************************/
class editorFontMap extends fontMap
{

  private characterCanvas drawingCanvas;
  private char currentC = ( char ) 0;

  /*********************************************************************/
  editorFontMap( HersheyFontEditor eMF, characterCanvas p )
  {
     super( eMF);
     drawingCanvas = p;
  }
  /*********************************************************************/

  protected void pickedCharacter( char c )
  {
     drawingCanvas.clearStack();
     drawingCanvas.LineInProgress(false);
     drawingCanvas.drawCharacter( c );
  }
  /*********************************************************************/

}
/*********************************************************************/
