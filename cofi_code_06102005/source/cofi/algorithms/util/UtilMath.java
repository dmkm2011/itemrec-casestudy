/**
 *  (c) National Research Council of Canada, 2002-2003 by Daniel Lemire, Ph.D.
 *  Email lemire at ondelette dot com for support and details.
 */
 /**
 *  This program is free software; you can
 *  redistribute it and/or modify it under the terms of the GNU General Public
 *  License as published by the Free Software Foundation (version 2). This
 *  program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 *  details. You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software Foundation,
 *  Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package cofi.algorithms.util;

import gnu.trove.*;
import cofi.algorithms.*;

/**
 *  A convenience class for some common operations on arrays and other data
 *  structures. For research and non commercial purposes. $Id: UtilMath.java,v
 *  1.17 2003/08/22 13:38:23 howsen Exp $ $Date: 2003/12/07 23:21:23 $ $Author:
 *  howsen $ $Revision: 1.8 $ $Log: UtilMath.java,v $
 *  howsen $ $Revision: 1.8 $ Revision 1.8  2003/12/07 23:21:23  lemired
 *  howsen $ $Revision: 1.8 $ More fiddling
 *  howsen $ $Revision: 1.8 $
 *  howsen $ $Revision: 1.8 $ Revision 1.7  2003/12/05 21:47:08  lemired
 *  howsen $ $Revision: 1.8 $ I think I finally got rule of 3 right
 *  howsen $ $Revision: 1.8 $
 *  howsen $ $Revision: 1.8 $ Revision 1.6  2003/11/24 16:26:29  lemired
 *  howsen $ $Revision: 1.8 $ Removed data package again
 *  howsen $ $Revision: 1.8 $
 *  howsen $ $Revision: 1.8 $ Revision 1.5  2003/11/11 13:25:58  lemired
 *  howsen $ $Revision: 1.8 $ Added gpl headers
 *  howsen $ $Revision: 1.8 $
 *  howsen $ $Revision: 1.8 $ Revision 1.4  2003/11/03 23:41:57  lemired
 *  howsen $ $Revision: 1.8 $ Latest changes: should almost conclude paper with Anna.
 *  howsen $ $Revision: 1.8 $
 *  howsen $ $Revision: 1.8 $ Revision 1.3  2003/10/31 00:47:07  lemired
 *  howsen $ $Revision: 1.8 $ Still got a bug in TIOptimalWeight... it should be better than average... arghh!
 *  howsen $ $Revision: 1.8 $
 *  howsen $ $Revision: 1.8 $ Revision 1.2  2003/10/28 01:43:08  lemired
 *  howsen $ $Revision: 1.8 $ Lots of refactoring.
 *  howsen $ $Revision: 1.8 $
 *  howsen $ $Revision: 1.8 $ Revision 1.1  2003/10/27 17:21:15  lemired
 *  howsen $ $Revision: 1.8 $ Putting some order
 *  howsen $ $Revision: 1.8 $
 *  howsen $ $Revision: 1.8 $ Revision 1.20  2003/10/07 13:28:32  lemired
 *  howsen $ $Revision: 1.8 $ Did some tweaking...
 *  howsen $ $Revision: 1.8 $
 *  howsen $ $Revision: 1.8 $ Revision 1.19  2003/09/29 14:37:33  lemired
 *  howsen $ $Revision: 1.8 $ Fixed the optimalweight scheme.
 *  howsen $ $Revision: 1.8 $
 *  howsen $ $Revision: 1.8 $ Revision 1.18  2003/09/18 12:41:42  lemired
 *  howsen $ $Revision: 1.8 $ Still doing lots of boring research.
 *  howsen $ $Revision: 1.8 $ Revision 1.17 2003/08/22
 *  13:38:23 howsen *** empty log message *** Revision 1.16 2003/08/21 18:49:38
 *  lemired It should now compile nicely. Revision 1.15 2003/08/21 15:31:22
 *  lemired Not much. Added some functions to UtilMath and a comment to OW2steps
 *  Revision 1.14 2003/08/19 17:51:21 lemired I've been improving OptimalWeight.
 *  Revision 1.13 2003/08/12 11:52:11 lemired Added more regression testing.
 *  Revision 1.12 2003/08/08 13:10:01 ballm Partially fixed the add rating page
 *  - "s were in the wrong place Revision 1.11 2003/08/08 03:23:22 lemired
 *  addedUser/removedUser was broken in most implementation. I fixed that now.
 *  Revision 1.10 2003/08/07 13:16:06 lemired More javadoc improvments. Revision
 *  1.9 2003/08/07 00:37:42 lemired Mostly, I updated the javadoc.
 *
 *@author     Daniel Lemire
 *@created    September 17, 2003
 *@since      September 2002
 */
public class UtilMath {

  final public static float epsilon = 0.001f;


  /**
   *  Given a user, subtract its average
   *
   *@param  user  The user you want to modify
   *@return       The average (subtracted)
   */
  public static float subtractAverage( TIntFloatHashMap user ) {
    final float aver = average( user );
    user.transformValues(
      new TFloatFunction() {
        public float execute( float a ) {
          return a - aver;
        }
      } );
    return aver;
  }


  /**
   *  compute the difference in l infinity norm
   *
   *@param  user  an evaluation
   *@param  a     an array (vector)
   *@return       the difference
   */
  public static float linfdiff( TIntFloatHashMap user, float[] a ) {
    TIntFloatIterator uiter = user.iterator();
    float max = 0.0f;
    float current = 0.0f;
    while ( uiter.hasNext() ) {
      uiter.advance();
      current = Math.abs( uiter.value() - a[uiter.key()] );
      if ( max < current )
        max = current;

    }
    return max;
  }


  /**
   *  Compute the difference in l1 norm
   *
   *@param  user  some evaluation
   *@param  a     some vector
   *@return       the difference
   */
  public static float l1diff( TIntFloatHashMap user, float[] a ) {
    TIntFloatIterator uiter = user.iterator();
    float max = 0.0f;
    while ( uiter.hasNext() ) {
      uiter.advance();
      max += Math.abs( uiter.value() - a[uiter.key()] );
    }
    return max;
  }


  /**
   *  Description of the Method
   *
   *@param  user  Description of the Parameter
   *@param  a     Description of the Parameter
   *@return       Description of the Return Value
   */
  public static float l2diff( TIntFloatHashMap user, float[] a ) {
    TIntFloatIterator uiter = user.iterator();
    float max = 0.0f;
    while ( uiter.hasNext() ) {
      uiter.advance();
      max += ( uiter.value() - a[uiter.key()] ) * ( uiter.value() - a[uiter.key()] );
    }
    return (float) Math.sqrt( max );
  }


  /**
   *  Compute the l infinity norm
   *
   *@param  user     the evaluation
   *@param  average  the average of the evaluation
   *@return          the l infinity norm of user - average
   */
  public static float linfnorm( TIntFloatHashMap user, float average ) {
    TIntFloatIterator uiter = user.iterator();
    float max = 0.0f;
    float current = 0.0f;
    while ( uiter.hasNext() ) {
      uiter.advance();
      current = Math.abs( uiter.value() - average );
      if ( max < current )
        max = current;

    }
    return max;
  }


  /**
   *  Compute the normalized l1 norm (divided by number of samples)
   *
   *@param  user     an evaluation
   *@param  average  its average
   *@return          l1 norm of user -average
   */
  public static float l1norm( TIntFloatHashMap user, float average ) {
    TIntFloatIterator uiter = user.iterator();
    float total = 0.0f;
    while ( uiter.hasNext() ) {
      uiter.advance();
      total += Math.abs( uiter.value() - average );

    }
    return total / user.size();
  }


  /**
   *  Lp norm normalized by the number of ratings
   *
   *@param  user     some evaluation
   *@param  average  its average
   *@param  p        which lp norm to compute
   *@return          the normalized lp norm
   */
  public static float lpnorm( TIntFloatHashMap user, float average, float p ) {
    if ( Float.isInfinite( p ) )
      return linfnorm( user, average );
    else if ( p == 1.0f )
      return l1norm( user, average );
    TIntFloatIterator uiter = user.iterator();
    double total = 0.0f;
    while ( uiter.hasNext() ) {
      uiter.advance();
      total += Math.pow( Math.abs( uiter.value() - average ), p );

    }
    return (float) Math.pow( total / user.size(), 1 / p );
  }


  /**
   *  Compute the difference in lp norm
   *
   *@param  user  an evaluation
   *@param  v     some array
   *@param  p     which lp norm to use
   *@return       the difference
   */
  public static float lpdiff( TIntFloatHashMap user, float[] v, float p ) {
    if ( Float.isInfinite( p ) )
      return linfdiff( user, v );
    else if ( p == 1.0f )
      return l1diff( user, v );
    TIntFloatIterator uiter = user.iterator();
    double total = 0.0f;
    while ( uiter.hasNext() ) {
      uiter.advance();
//							if(uiter.key() > 0)
      //		System.out.println(uiter.value() + " key: " + uiter.key());
      total += Math.pow( Math.abs( uiter.value() - v[uiter.key()] ), p );

    }
    return (float) Math.pow( total / user.size(), 1 / p );
  }


  /**
   *  Compute the difference in lp norm
   *
   *@param  u  an evaluation
   *@param  v  another evaluation
   *@param  p  which lp norm to use
   *@return    the difference
   */
  public static float lpdiff( float[] u, float[] v, float p ) {
    double total = 0.0f;
    for ( int k = 0; k < u.length; ++k )
      total += Math.pow( Math.abs( u[k] - v[k] ), p );

    return (float) Math.pow( total / u.length, 1 / p );
  }


  /**
   *  Compute the average of a hashmap
   *
   *@param  user  the user
   *@return       the average
   */
  public static float average( TIntFloatHashMap user ) {
    if ( user.size() == 0 )
      return 0.0f;
    TIntFloatIterator iter = user.iterator();
    float aver = 0.0f;
    while ( iter.hasNext() ) {
      iter.advance();
      aver += iter.value();
    }
    return aver / user.size();
  }

  /**
   *  Compute the average of a hashmap except for one key
   *
   *@param  user  the user
   *@return       the average
   */
  public static float average( TIntFloatHashMap user , int k) {
    TIntFloatIterator iter = user.iterator();
    float aver = 0.0f;
    boolean ContainsK = false;
    while ( iter.hasNext() ) {
      iter.advance();
      if(iter.key() == k) {
        ContainsK = true;
        continue;
      }
      aver += iter.value();
    }
    if(! ContainsK ) 
      throw new CollaborativeFilteringException("Key not found : "+k);
    return aver / ( user.size() - 1);
  }

  /**
   *  Compute the average of an evaluation restricted to the intersection with
   *  another evaluation
   *
   *@param  user        the evaluation
   *@param  constraint  the evaluation serving as a constraint
   *@return             the average
   */
  public static float average( TIntFloatHashMap user,
      TIntFloatHashMap constraint ) {
    int[] ids = user.keys();
    float aver = 0.0f;
    int number = 0;
    for ( int k = 0; k < ids.length; ++k )
      if ( constraint.contains( ids[k] ) ) {
        aver += user.get( ids[k] );
        number++;
      }
    return aver / number;
  }


  /**
   *  Compute the average of a user
   *
   *@param  user    the user
   *@param  weight  Description of the Parameter
   *@return         the average
   */
  public static float average( TIntFloatHashMap user, float[] weight ) {
    if ( user.size() == 0 )
      return 0.0f;
    float aver = 0.0f;
    TIntFloatIterator iter = user.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      aver += iter.value() * weight[iter.key()];
    }
    return aver / user.size();
  }


  /**
   *  Compute the weighted average
   *
   *@param  user    evaluation
   *@param  weight  some weights
   *@return         weighted average
   */
  public static float average( float[] user, float[] weight ) {
    if ( user.length == 0 )
      return 0.0f;
    float aver = 0.0f;
    for ( int k = 0; k < user.length; ++k )
      aver += user[k] * weight[k];

    return aver / user.length;
  }


  /**
   *  Return true if the two arrays are identical
   *
   *@param  a  An array
   *@param  b  An array
   *@return    Whether or not they are equal
   */
  public static boolean compare( float[] a, float[] b ) {
    if ( a.length != b.length )
      return false;
    for ( int k = 0; k < a.length; ++k )
      if ( a[k] != b[k] )
        return false;

    return true;
  }


  /**
   *  Compute the average of some array
   *
   *@param  v  the array
   *@return    its average
   */
  public static float average( float[] v ) {
    if ( v.length == 0 )
      return 0.0f;
    return sum( v ) / v.length;
  }
  public static double average( double[] v ) {
    if ( v.length == 0 )
      return 0.0;
    return sum( v ) / v.length;
  }
  public static float variance( float[] v, float average ) {
    if ( v.length == 0 )
      return 0.0f;
    float sum = 0.0f;
    for (int k = 0; k < v.length; ++k)
      sum += (v[k] - average) * (v[k] - average); 
    return sum / v.length;
  }


  /**
   *  Description of the Method
   *
   *@param  v  Description of the Parameter
   *@param  u  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double average( double[] v, TIntFloatHashMap u ) {
    double sum = 0.0f;
    if ( u.size() == 0 )
      return 0.0f;
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      //if(iter.key() > 0)
      sum += v[iter.key()];
    }
    return sum / u.size();
  }


  /**
   *  Description of the Method
   *
   *@param  v  Description of the Parameter
   *@param  u  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static float average( float[] v, TIntFloatHashMap u ) {
    float sum = 0.0f;
    if ( u.size() == 0 )
      return 0.0f;
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      sum += v[iter.key()];
    }
    return sum / u.size();
  }


  /**
   *  Description of the Method
   *
   *@param  v  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static float sum( float[] v ) {
    float s = 0.0f;
    for ( int k = 0; k < v.length; ++k )
      s += v[k];

    return s;
  }
   public static double sum( double[] v ) {
    double s = 0.0;
    for ( int k = 0; k < v.length; ++k )
      s += v[k];

    return s;
  }



  /**
   *  Description of the Method
   *
   *@param  u        Description of the Parameter
   *@param  average  Description of the Parameter
   *@return          Description of the Return Value
   */
  public static float sum( TIntFloatHashMap u, float average ) {
    float sum = 0.0f;
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      sum += ( iter.value() - average );
    }
    return sum;
  }


  /**
   *  Find the max. of an array of values
   *
   *@param  ids  An array
   *@return      The largest value found
   */
  public static int max( int[] ids ) {
    if ( ids.length < 1 )
      return 0;
    int m = ids[0];
    for ( int k = 1; k < ids.length; ++k )
      m = m < ids[k] ? ids[k] : m;

    return m;
  }


  /**
   *  Find the max. of an array of values
   *
   *@param  ids  An array
   *@return      The largest value found
   */
  public static float max( float[] ids ) {
    if ( ids.length < 1 )
      return 0;
    float m = ids[0];
    for ( int k = 1; k < ids.length; ++k )
      m = m < ids[k] ? ids[k] : m;

    return m;
  }


  /**
   *  Description of the Method
   *
   *@param  ids  Description of the Parameter
   *@return      Description of the Return Value
   */
  public static float min( float[] ids ) {
    if ( ids.length < 1 )
      return 0;
    float m = ids[0];
    for ( int k = 1; k < ids.length; ++k )
      m = m > ids[k] ? ids[k] : m;

    return m;
  }


  /**
   *  Description of the Method
   *
   *@param  coef  Description of the Parameter
   *@param  v     Description of the Parameter
   */
  public static void multiply( float coef, float[] v ) {
    for ( int k = 0; k < v.length; ++k )
      v[k] *= coef;
  }
  
  public static float sqr(float x) {
    return x * x;
  }


  /**
   *  Print an array
   *
   *@param  vector  An array
   */
  public static void print( float[] vector ) {

    for ( int k = 0; k < vector.length; ++k )
      System.out.print( vector[k] + "\t" );

    System.out.println();
  }

  public static void print( short[] vector ) {

    for ( int k = 0; k < vector.length; ++k )
      System.out.print( vector[k] + "\t" );

    System.out.println();
  }
  public static void print( float[] vector, int maxterms ) {

    for ( int k = 0; k < Math.min(vector.length,maxterms); ++k )
      System.out.print( vector[k] + "\t" );

    System.out.println();
  }

  /**
   *  Print an array
   *
   *@param  vector  An array
   */
  public static void print( float[][] vector ) {

    for ( int k = 0; k < vector.length; ++k )
      print( vector[k] );

  }

  
    public static void print( double[][] vector ) {

    for ( int k = 0; k < vector.length; ++k )
      print( vector[k] );

  }

  /**
   *  Print an array
   *
   *@param  vector  An array
   */
  public static void print( double[] vector ) {

    for ( int k = 0; k < vector.length; ++k )
      System.out.print( vector[k] + "\t" );

    System.out.println();
  }


  /**
   *  Description of the Method
   *
   *@param  vector  Description of the Parameter
   */
  public static void print( int[] vector ) {
    for ( int k = 0; k < vector.length; ++k )
      if ( vector[k] != 0 )
        System.out.println( k + ": " + vector[k] );
        //System.out.print( vector[k] + "\t" );


    System.out.println();
  }


  /**
   *  Print an hashmap to the screen
   *
   *@param  u       An hashmap to be printed on screen
   *@param  maxkey  Description of the Parameter
   */
  public static void print( TIntFloatHashMap u, int maxkey ) {
    for ( int k = 1; k <= maxkey; ++k )
      if ( u.contains( k ) )
        System.out.print( u.get( k ) + "\t" );

      else
        System.out.print( "X\t" );


    System.out.println();
  }


  /**
   *  Description of the Method
   *
   *@param  u  Description of the Parameter
   */
  public static void print( TIntFloatHashMap u ) {
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      System.out.print( "v[" + iter.key() + "]=" + iter.value() + "\t" );
    }
    System.out.println();
  }


  /**
   *  Print an array to the screen, but only for the keys found in the hashmap
   *
   *@param  vector  Array containing values to be printed
   *@param  u       The hashmap containing the keys to be printed
   */
  public static void print( float[] vector, TIntFloatHashMap u ) {
    int[] itemkeys = u.keys();
    for ( int i = 0; i < itemkeys.length; ++i )
      System.out.print( vector[itemkeys[i]] + "\t" );

    System.out.println();
  }


  /**
   *  Count the number of differences between an user and some complete user
   *
   *@param  u       Description of the Parameter
   *@param  vector  Description of the Parameter
   *@param  factor  Description of the Parameter
   *@return         Description of the Return Value
   */
  public static int differences( TIntFloatHashMap u, float[] vector,
      float factor ) {
    int[] itemkeys = u.keys();
    int number = 0;
    for ( int i = 0; i < itemkeys.length; ++i ) {
      final int completed = (int) Math.round( vector[itemkeys[i]] / factor );
      final int orig = (int) Math.round( u.get( itemkeys[i] ) / factor );
      if ( completed != orig )
        ++number;

    }
    return number;
  }


  /**
   *  Description of the Method
   *
   *@param  u       Description of the Parameter
   *@param  vector  Description of the Parameter
   *@param  span    Description of the Parameter
   *@return         Description of the Return Value
   */
  public static float NMAE( TIntFloatHashMap u, float[] vector, float span ) {
    TIntFloatIterator iter = u.iterator();
    float nmae = 0.0f;
    final int number = u.size();
    while ( iter.hasNext() ) {
      iter.advance();
      nmae += Math.abs( ( vector[iter.key()] - iter.value() ) );
    }
    return nmae / ( number * span );
  }


  /**
   *  Description of the Method
   *
   *@param  u  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static float l1norm( TIntFloatHashMap u ) {
    TIntFloatIterator iter = u.iterator();
    float norm = 0.0f;
    while ( iter.hasNext() ) {
      iter.advance();
      norm += Math.abs( iter.value() );
    }
    return norm;
  }


  /**
   *  Description of the Method
   *
   *@param  u  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static float linfnorm( TIntFloatHashMap u ) {
    TIntFloatIterator iter = u.iterator();
    float norm = 0.0f;
    float abs;
    while ( iter.hasNext() ) {
      iter.advance();
      abs = Math.abs( iter.value() );
      if ( norm < abs )
        norm = abs;

    }
    return norm;
  }


  /**
   *  Make the array into a unit array
   *
   *@param  in  The array to be modified
   *@return     A reference to the array
   */
  public static float[] normalize( float[] in ) {
    double sumofsquares = 0;
    for ( int k = 0; k < in.length; ++k )
      sumofsquares += in[k] * in[k];

    final float norm = (float) Math.sqrt( sumofsquares );
    for ( int k = 0; k < in.length; ++k )
      in[k] /= norm;

    return in;
  }


  /**
   *  Make the array into a unit array
   *
   *@param  in  The array to be modified
   *@return     A reference to the array
   */
  public static float[] normalizeAbsoluteSum( float[] in ) {
    double sum = 0;
    for ( int k = 0; k < in.length; ++k )
      sum += Math.abs( in[k] );

    if ( sum == 0.0f )
      return in;
    for ( int k = 0; k < in.length; ++k )
      in[k] /= sum;

    return in;
  }


  /**
   *  Description of the Method
   *
   *@param  u  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static float energy( TIntFloatHashMap u ) {
    float sum = 0.0f;
    float temp;
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      temp = iter.value();
      sum += temp * temp;
    }
    return sum;
  }


  /**
   *  Description of the Method
   *
   *@param  u        Description of the Parameter
   *@param  average  Description of the Parameter
   *@return          Description of the Return Value
   */
  public static float energy( TIntFloatHashMap u, float average ) {
    float sum = 0.0f;
    float temp;
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      temp = iter.value();
      sum += ( temp - average ) * ( temp - average );
    }
    return sum;
  }


  /**
   *  Description of the Method
   *
   *@param  u           Description of the Parameter
   *@param  average     Description of the Parameter
   *@param  constraint  Description of the Parameter
   *@return             Description of the Return Value
   */
  public static float energy( TIntFloatHashMap u, float average,
      TIntFloatHashMap constraint ) {
    float sum = 0.0f;
    float temp;
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      if ( constraint.contains( iter.key() ) ) {
        temp = iter.value();
        sum += ( temp - average ) * ( temp - average );
      }
    }
    return sum;
  }


  /**
   *  Description of the Method
   *
   *@param  u  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static float energy( float[] u ) {
    float sum = 0.0f;
    for ( int k = 0; k < u.length; ++k )
      sum += ( u[k] ) * ( u[k] );

    return sum;
  }


  /**
   *  Description of the Method
   *
   *@param  u        Description of the Parameter
   *@param  average  Description of the Parameter
   *@return          Description of the Return Value
   */
  public static float energy( float[] u, float average ) {
    float sum = 0.0f;
    for ( int k = 0; k < u.length; ++k )
      sum += ( u[k] - average ) * ( u[k] - average );

    return sum;
  }


  /**
   *  Description of the Method
   *
   *@param  v  Description of the Parameter
   *@param  u  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static float energy( float[] v, TIntFloatHashMap u ) {
    float sum = 0.0f;
    int temp;
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      temp = iter.key();
      sum += v[temp] * v[temp];
    }
    return sum;
  }


  /**
   *  Description of the Method
   *
   *@param  v  Description of the Parameter
   *@param  u  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static float residualEnergy( float[] v, TIntFloatHashMap u ) {
    float sum = 0.0f;
    int temp;
    float currentValue;
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      currentValue = iter.value();
      temp = iter.key();
      sum += ( v[temp] - currentValue ) * ( v[temp] - currentValue );
    }
    return sum;
  }


  /**
   *  Description of the Method
   *
   *@param  u         Description of the Parameter
   *@param  complete  Description of the Parameter
   */
  public static void subtract( TIntFloatHashMap u, float[] complete ) {
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      iter.setValue( iter.value() - complete[iter.key()] );
    }
  }


  /**
   *  Description of the Method
   *
   *@param  u         Description of the Parameter
   *@param  lambda    Description of the Parameter
   *@param  complete  Description of the Parameter
   */
  public static void subtract( TIntFloatHashMap u, float lambda,
      float[] complete ) {
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      iter.setValue( iter.value() - lambda * complete[iter.key()] );
    }
  }


  /**
   *  Description of the Method
   *
   *@param  u      Description of the Parameter
   *@param  value  Description of the Parameter
   */
  public static void subtract( TIntFloatHashMap u, float value ) {
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      iter.setValue( iter.value() - value );
    }
  }


  /**
   *  Description of the Method
   *
   *@param  u      Description of the Parameter
   *@param  value  Description of the Parameter
   */
  public static void multiply( TIntFloatHashMap u, float value ) {
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      iter.setValue( iter.value() * value );
    }
  }


  /**
   *  Description of the Method
   *
   *@param  a  Description of the Parameter
   *@param  b  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static float scalarProduct( float[] a, float[] b ) {
    float sum = 0.0f;
    for ( int k = 0; k < a.length; ++k )
      sum += a[k] * b[k];

    return sum;
  }


  /**
   *  Description of the Method
   *
   *@param  a           Description of the Parameter
   *@param  b           Description of the Parameter
   *@param  constraint  Description of the Parameter
   *@return             Description of the Return Value
   */
  public static float scalarProduct( float[] a, float[] b,
      TIntFloatHashMap constraint ) {
    float sum = 0.0f;
    int temp;
    TIntFloatIterator iter = constraint.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      //	if(iter.key() > 0)
      //	{
      temp = iter.key();
      sum += a[temp] * b[temp];
      //}
    }
    return sum;
  }


  /**
   *  Description of the Method
   *
   *@param  u         Description of the Parameter
   *@param  complete  Description of the Parameter
   *@return           Description of the Return Value
   */
  public static float scalarProduct( TIntFloatHashMap u, float[] complete ) {
    float sum = 0.0f;
    TIntFloatIterator iter = u.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      //	if(iter.key() > 0)
      sum += iter.value() * complete[iter.key()];
    }
    return sum;
  }


  /**
   *  Description of the Method
   *
   *@param  u1  Description of the Parameter
   *@param  u2  Description of the Parameter
   *@return     Description of the Return Value
   */
  public static float scalarProduct( TIntFloatHashMap u1, TIntFloatHashMap u2 ) {
    float sum = 0.0f;
    TIntFloatIterator iter = u1.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      if ( u2.contains( iter.key() ) )
        sum += iter.value() * u2.get( iter.key() );

    }
    return sum;
  }


  /**
   *  Description of the Method
   *
   *@param  u1  Description of the Parameter
   *@param  a1  Description of the Parameter
   *@param  u2  Description of the Parameter
   *@param  a2  Description of the Parameter
   *@return     Description of the Return Value
   */
  public static float scalarProduct( TIntFloatHashMap u1, float a1,
      TIntFloatHashMap u2, float a2 ) {
    float sum = 0.0f;
    TIntFloatIterator iter = u1.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      if ( u2.contains( iter.key() ) )
        sum += ( iter.value() - a1 ) * ( u2.get( iter.key() ) - a2 );

    }
    return sum;
  }


  /**
   *  This is really a stupid algorithm that could be improved upon.
   *
   *@param  u  Description of the Parameter
   *@param  c  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static float l1match( TIntFloatHashMap u, float[] c ) {
    TIntFloatIterator iter = u.iterator();
    float bestlambda = 1.0f;
    float currentbest = measureMatch( u, c, bestlambda );
    while ( iter.hasNext() ) {
      iter.advance();
      float completedValue = c[iter.key()];
      if ( completedValue != 0.0f ) {
        float lambda = iter.value() / completedValue;
        float residual = measureMatch( u, c, lambda );
        if ( residual < currentbest ) {
          currentbest = residual;
          bestlambda = lambda;
        }
        if ( residual == currentbest )
          bestlambda = ( lambda + bestlambda ) / 2.0f;

      }
    }
    return bestlambda;
  }


  /**
   *  Description of the Method
   *
   *@param  u  Description of the Parameter
   *@param  c  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static float l2match( TIntFloatHashMap u, float[] c ) {
    TIntFloatIterator iter = u.iterator();
    float ScalarProduct = 0.0f;
    float CEnergy = 0.0f;
    float value;
    float cvalue;
    while ( iter.hasNext() ) {
      iter.advance();
      value = iter.value();
      cvalue = c[iter.key()];
      ScalarProduct += value * cvalue;
      CEnergy += cvalue * cvalue;
    }
    if ( CEnergy > 0.0f )
      return ScalarProduct / CEnergy;
    return 0.0f;
  }


  /**
   *  This is used by l1norm
   *
   *@param  u       - a user
   *@param  c       -
   *@param  lambda  -
   *@return         float
   */
  protected static float measureMatch( TIntFloatHashMap u, float[] c,
      float lambda ) {
    TIntFloatIterator iter = u.iterator();
    float norm = 0.0f;
    while ( iter.hasNext() ) {
      iter.advance();
      norm += Math.abs( iter.value() - lambda * c[iter.key()] );
    }
    return norm;
  }


  /*
   *  Follows a bunch of linear algebra operations on arrays. Nothing surprising.
   */
  /**
   *  Description of the Method
   *
   *@param  n  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double[] zeros( final int n ) {
    return new double[n];
  }


  /**
   *  Description of the Method
   *
   *@param  a  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double[] copy( final double[] a ) {
    double[] answer = new double[a.length];
    System.arraycopy( a, 0, answer, 0, a.length );
    return answer;
  }
  public static double correlation( final double[] a , final double[] b) {
    double av1 = average(a);
    double av2 = average(b);
    double product = 0;
    double energy1 = 0;
    double energy2 = 0;
    for(int k  = 0; k < a.length; ++k) {
      product += (a[k] - av1) * (b[k] -av2);
      energy1 += (a[k] - av1) * (a[k] - av1);
      energy2 += (b[k] - av2) * (b[k] - av2);
    }
    if(energy1 *energy2 == 0.0) return 0.0;
    return product / Math.sqrt(energy1 * energy2);
  }

  /**
   *  Description of the Method
   *
   *@param  a  Description of the Parameter
   *@param  b  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double innerproduct( final double[] a, final double[] b ) {
    double answer = 0;
    for ( int i = 0; i < a.length; ++i )
      answer += a[i] * b[i];

    return answer;
  }


  /**
   *  Description of the Method
   *
   *@param  a  Description of the Parameter
   *@param  b  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double dot( final double[] a, final double[] b ) {
    double answer = 0;
    for ( int i = 0; i < a.length; ++i )
      answer += a[i] * b[i];

    return answer;
  }


  /**
   *  Multiply a vector a by coefficient c
   *
   *@param  c  some coefficient
   *@param  a  the vector
   *@return    a newly allocated vector containing the result
   */
  public static double[] product( final double c, final double[] a ) {
    double[] answer = new double[a.length];
    for ( int i = 0; i < a.length; ++i )
      answer[i] = c * a[i];

    return answer;
  }


  /**
   *  Multiply a vector a by coefficient c
   *
   *@param  c  some coefficient
   *@param  a  the vector
   *@return    vector a
   */
  public static double[] productInPlace( final double c, final double[] a ) {
    for ( int i = 0; i < a.length; ++i )
      a[i] *= c;

    return a;
  }
  public static float[] productInPlace( final float c, final float[] a ) {
    for ( int i = 0; i < a.length; ++i )
      a[i] *= c;

    return a;
  }



  /**
   *  Description of the Method
   *
   *@param  a  Description of the Parameter
   *@param  b  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double[] add( final double[] a, final double[] b ) {
    double[] answer = new double[a.length];
    for ( int i = 0; i < a.length; ++i )
      answer[i] = a[i] + b[i];

    return answer;
  }
  /**
   *  Description of the Method
   *
   *@param  a  Description of the Parameter
   *@param  b  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double[] add( final double[] a, final double b, final double[] c ) {
    double[] answer = new double[a.length];
    for ( int i = 0; i < a.length; ++i )
      answer[i] = a[i] + b * c[i];

    return answer;
  }


  /**
   *  Description of the Method
   *
   *@param  a  Description of the Parameter
   *@param  b  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double[] subtract( final double[] a, final double[] b ) {
    double[] answer = new double[a.length];
    for ( int i = 0; i < a.length; ++i )
      answer[i] = a[i] - b[i];

    return answer;
  }


  /**
   *  Description of the Method
   *
   *@param  a  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double norm( double[] a ) {
    return (double) Math.sqrt( innerproduct( a, a ) );
  }
  
  public static double norm( double[][] a ) {
    double sum = 0.0;
    for(int row = 0; row < a.length;++row)
      sum += norm(a[row]);
    return sum / a.length;
  }


  /**
   *  Adds b to a in place
   *
   *@param  a  a vector
   *@param  b  another vector
   *@return    vector a modified
   */
  public static double[] addInPlace( final double[] a, final double[] b ) {
    for ( int i = 0; i < a.length; ++i )
      a[i] += b[i];

    return a;
  }


  /**
   *  Adds b to a in place
   *
   *@param  a  a vector
   *@param  b  another vector
   *@return    vector a modified
   */
  public static float[] addInPlace( final float[] a, final float[] b ) {
    for ( int i = 0; i < a.length; ++i )
      a[i] += b[i];

    return a;
  }


  /**
   *  Adds c*b to a in place
   *
   *@param  a  a vector
   *@param  b  a coefficient
   *@param  c  another vector
   *@return    vector a modified
   */
  public static float[] addInPlace( final float[] a, float c, final float[] b ) {
    for ( int i = 0; i < a.length; ++i )
      a[i] += c * b[i];

    return a;
  }
  /**
   *  Adds c*b to a in place
   *
   *@param  a  a vector
   *@param  b  a coefficient
   *@param  c  another vector
   *@return    vector a modified
   */
  public static double[] addInPlace( final double[] a, double c, final double[] b ) {
    for ( int i = 0; i < a.length; ++i )
      a[i] += c * b[i];

    return a;
  }


  /**
   *  Subtract b to a in place
   *
   *@param  a  a vector
   *@param  b  another vector
   *@return    vector a modified
   */
  public static double[] subtractInPlace( final double[] a, final double[] b ) {
    for ( int i = 0; i < a.length; ++i )
      a[i] -= b[i];

    return a;
  }


  /**
   *  Subtract b to a in place
   *
   *@param  a  a vector
   *@param  b  another vector
   *@return    vector a modified
   */
  public static float[] subtractInPlace( final float[] a, final float[] b ) {
    for ( int i = 0; i < a.length; ++i )
      a[i] -= b[i];

    return a;
  }


  /**
   *  Subtract b*c to a in place
   *
   *@param  a  a vector
   *@param  b  another vector
   *@param  c  Description of the Parameter
   *@return    vector a modified
   */
  public static float[] subtractInPlace( final float[] a, float c,
      final float[] b ) {
    for ( int i = 0; i < a.length; ++i )
      a[i] -= c * b[i];

    return a;
  }


  /**
   *  Multiply matrix A with vector b.
   *
   *@param  A  the matrix
   *@param  b  the vector
   *@return    A b
   */
  public static double[] matrixmultiply( final double[][] A, final double[] b ) {
    double[] answer = new double[b.length];
    // this assumes A is a square matrix
    for ( int row = 0; row < answer.length; ++row )
      for ( int col = 0; col < b.length; ++col )
        answer[row] += A[row][col] * b[col];

    return answer;
  }


  /**
   *  Multiply matrix transpose(A) with vector b.
   *
   *@param  A  the matrix
   *@param  b  the vector
   *@return    A b
   */
  public static double[] transposematrixmultiply( final double[][] A,
      final double[] b ) {
    double[] answer = new double[b.length];
    // this assumes A is a square matrix
    for ( int row = 0; row < answer.length; ++row )
      for ( int col = 0; col < b.length; ++col )
        answer[row] += A[col][row] * b[col];

    return answer;
  }


  /**
   *  Return the identity matrix
   *
   *@param  n  size of the matrix
   *@return    identity matrix
   */
  public static double[][] identity( final int n ) {
    double[][] answer = new double[n][n];
    for ( int k = 0; k < n; ++k )
      answer[k][k] = 1.0;

    return answer;
  }


  /**
   *  Description of the Method
   *
   *@param  m  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double[][] transpose( final double[][] m ) {
    double[][] answer = new double[m[0].length][m.length];
    for ( int row = 0; row < m.length; ++row )
      for ( int col = 0; col < m[row].length; ++col )
        answer[col][row] = m[row][col];


    return answer;
  }


  /**
   *  Description of the Method
   *
   *@param  A  Description of the Parameter
   *@param  x  Description of the Parameter
   *@param  b  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double error( final double[][] A, double[] x, final double[] b ) {
    return UtilMath.norm( UtilMath.subtractInPlace( UtilMath.matrixmultiply( A,
        x ), b ) );
  }


  /**
   *  Test whether a matrix is symmetric
   *
   *@param  A  some matrix
   *@return    whether the matrix is symmetric
   */
  public static boolean isSymmetric( final double[][] A ) {
    // this wouldn't work with sparse matrices, but we
    // should be ok here
    for ( int col = 0; col < A.length; ++col )
      for ( int row = 0; row < col; ++row )
        if ( A[col][row] != A[row][col] )
          return false;


    return true;
  }

  /**
   *  Fill the array with zeroes
   *
   *@param  B  some array
   */
  public static void fillWithZeroes( double[] B ) {
    for ( int k = 0; k < B.length; ++k )
      B[k] = 0.0;

  }

  /**
   *  Fill the array with zeroes
   *
   *@param  B  some array
   */
  public static void fillWithZeroes( float[] B ) {
    for ( int k = 0; k < B.length; ++k )
      B[k] = 0.0f;

  }

  /**
   *  Fill the arrays with zeroes
   *
   *@param  A  array of arrays
   */
  public static void fillWithZeroes( double[][] A ) {
    for ( int k = 0; k < A.length; ++k )
      fillWithZeroes( A[k] );
  }
  /**
   *  Fill the arrays with zeroes
   *
   *@param  A  array of arrays
   */
  public static void fillWithZeroes( float[][] A ) {
    for ( int k = 0; k < A.length; ++k )
      fillWithZeroes( A[k] );
  }
}

