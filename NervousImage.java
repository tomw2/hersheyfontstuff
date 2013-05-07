/* 

   Jim Buzbee NervousImage based on original NervousText by :

    Daniel Wyszynski
    kwalrath: Changed string; added thread suspension. 5-9-95
*/
import java.awt.*;

public class NervousImage extends java.applet.Applet implements Runnable 
{
	String s = null;
        String param;
	Thread killme = null;
	int i;
	int x_coord = 0, y_coord = 0;
	String num;
	int speed = 35;
	int counter = 0;
        int number;
	boolean threadSuspended = false; //added by kwalrath
        Image myImage;
        int imageWidth = -1;
        int imageHeight = -1;
        float jitter;
        float spacing;
        int delay;

public void init()
{      
	/* My error checking is VERY poor here... :-( */
	s = getParameter("image");
        myImage = getImage( getCodeBase(), s );

        param = getParameter("Delay");
        delay = ( param != null ) ? Integer.parseInt( param ) : 200;

        param = getParameter("Spacing");
        spacing = ( param != null ) ?  Float.valueOf(param).floatValue()
                                    : ( float ) 1.0;

        param = getParameter("Jitter");
        jitter = ( param != null ) ?  Float.valueOf(param).floatValue()
                                    : ( float ) 0.5;

        param = getParameter("Number");
        number = ( param != null ) ? Integer.parseInt( param ) : 1; 
}

public void start() 
{
	if ( killme == null ) 
	{
        	killme = new Thread( this );
        	killme.start();
	}
}

public void stop() 
{
	killme = null;
}

public void run() 
{
	while (killme != null) 
        { 
        	try 
        	{
         	  Thread.sleep(delay);
       		} 
        	catch ( InterruptedException e )
	     	{
		  ;
        	}

	  	repaint();
	}

	killme = null;
 }

public void paint(Graphics g) 
{
	/* if we don't have the width yet */
        if ( imageWidth == -1 )
        {
           /* get it */
           imageWidth = myImage.getWidth( this );
        }

	/* if we don't have the height yet */
        if ( imageHeight == -1 )
        {
           /* get it */
           imageHeight = myImage.getHeight( this );
        }

        /* for all the images specified */
	for( i=0; i< number ; i++)
	{
          /* get the x position of the image */
	  x_coord = (int) ( ( Math.random() * imageWidth * jitter )
                            + i * ( imageWidth * spacing ) 
                          );

          /* get the y position of the image */
	  y_coord = (int) (Math.random() * imageHeight * jitter );

          /* draw it */
          g.drawImage( myImage, x_coord, y_coord, null );

     }
 }
 
/* Added by kwalrath. */
public boolean mouseDown(java.awt.Event evt, int x, int y) 
{
        if (threadSuspended) 
        {
            killme.resume();
        }
        else 
        {
            killme.suspend();
        }

        threadSuspended = ! threadSuspended;

        return true;
    }
}
