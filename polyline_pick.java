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

public class polyline_pick
{

private final static float CLOSE_TO_ZERO =  0.00000000001f;


public static float PolylineDistance( Vector points, /* polygon (polyline) points */
                        Point  point,  /* reference point */
                        int n_points,  /* reference point */
                        float aperture /* pick aperture */
                      )
{
    float          x,
                   y,
                   x0,
                   y0,
                   x1,
                   y1,
                   dx,
                   dy,
                   denom,
                   distance,
                   min_distance = 9999f;
   float           t;
   int             k = 0;

   x = point.x;
   y = point.y;

   // if we have no points
   if ( points.size() == 0  )
   {
      return(  min_distance );
   }

   Point p1 =  (Point ) points.elementAt( k++ );

   x0 = p1.x;
   y0 = p1.y;

   for (distance = 2.0f * aperture; --n_points > 0 && distance > aperture;)
   {

      Point p2 =  (Point ) points.elementAt( k++ );

      x1 = p2.x;
      y1 = p2.y;

      dx = x1 - x0;
      dy = y1 - y0;

      denom = dx * dx + dy * dy;

      if ( denom < CLOSE_TO_ZERO )
      {
         double ndx =  ( x - x ) *  ( x - y );
         double ndy =  ( x0 - y0 ) *  ( x0 - y0 );

            /* a very short line seg(x1) - (x0)ment */
         distance = (float)Math.sqrt( ndx + ndy );
      }
      else
      {

            /*
             * t parameterizes the line equation as
             * 
             * P(t) = P0 + t * ( P1 - P0 ),
             * 
             * where P(i) = ( x(i), y(i) )
             * 
             * if 0 < t < 1, the point of intersection between the line segment
             * and the perpendicular from the point (x, y) is on the line
             * segment, and the distance to the line (segment) is
             * 
             * | dx * ( y - y0 ) - dy * ( x - x0 ) |
             * ------------------------------------- denom
             * 
             * else the distance to the line segment is the distance to the
             * nearest end point of the segment
             */

         t = ((x - x0) * dx + (y - y0) * dy) / denom;

         if (t < 0)
	 {
            double ndx =  ( x  - y  ) *  ( x - y   );
            double ndy =  ( x0 - y0 ) *  ( x0 - y0 );

	    /*
            distance = PtToPt(x, y, x0, y0);
            */

           distance = (float)Math.sqrt( ndx + ndy );

         }
         else if (t > 1)
	 {
           double ndx =  ( x  - y  ) *  ( x - y   );
           double ndy =  ( x1 - y1 ) *  ( x1 - y1 );

           distance = (float)Math.sqrt( ndx + ndy );
         }
         else
	 {
            distance = (float)( Math.abs(dx * (y - y0) - dy * (x - x0)) /
                                Math.sqrt(denom)
                              );
         }
      }
      x0 = x1;
      y0 = y1;

      if ( distance < min_distance )
      {
         min_distance = distance;
      }

   }
   return (min_distance);
}

}
