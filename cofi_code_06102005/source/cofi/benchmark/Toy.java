/**
* (c) National Research Council of Canada, 2002-2003 by Daniel Lemire, Ph.D.
* Email lemire at ondelette dot com for support and details.
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
package cofi.benchmark;

import cofi.algorithms.*;
import cofi.algorithms.util.*;
import cofi.algorithms.memorybased.*;
import cofi.algorithms.stin.*;
import cofi.algorithms.basic.*;
import cofi.data.*;
import gnu.trove.*;
import java.io.*;
import java.util.*;

/**
 *  Some toy examples, testing. 
 * 
 *  $Id: Toy.java,v 1.1 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $ 
 *  $Author: lemired $ 
 *  $Revision: 1.1 $ 
 *  $Log: Toy.java,v $
 *  Revision 1.1  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.5  2003/10/28 01:43:08  lemired
 *  Lots of refactoring.
 *
 *  Revision 1.4  2003/08/19 17:51:21  lemired
 *  I've been improving OptimalWeight.
 *
 *  Revision 1.3  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author       Daniel Lemire
 *@since      September 4th 2002
 */
public class Toy {

  /**
   *  Complete the given user using the given cfs
   *
   *@param  cfs  Description of the Parameter
   *@param  u    Description of the Parameter
   */
  public static void complete( CollaborativeFilteringSystem cfs, TIntFloatHashMap u ) {
    float[] completed = cfs.completeUser( (TIntFloatHashMap) u.clone() );
    UtilMath.print( completed );
  }


  /**
   *  The main program for the Toy class
   *
   *@param  args  The command line arguments
   */
  public static void main( String[] args ) {

    final int N = 7;
    final int SizeFactor = 1;
    Random r = new Random();
    TIntFloatHashMap completeUser = new TIntFloatHashMap();
    completeUser.put( 1, 1.0f );
    completeUser.put( 2, 2.0f );
    completeUser.put( 3, 3.0f );
    completeUser.put( 4, 4.0f );
    completeUser.put( 5, 5.0f );
    completeUser.put( 6, 4.0f );
    completeUser.put( 7, 5.0f );
    TIntFloatHashMap completeUser2 = new TIntFloatHashMap();
    completeUser2.put( 1, 4.0f );
    completeUser2.put( 2, 3.0f );
    completeUser2.put( 3, 2.0f );
    completeUser2.put( 4, 1.0f );
    completeUser2.put( 5, 1.0f );
    completeUser2.put( 6, 2.0f );
    completeUser2.put( 7, 4.0f );
    EvaluationSet uset = new EvaluationSet();
    boolean deterministic = true;
    boolean secondorder = false;
    if ( args.length > 0 )
      if ( args[0].equals( "random" ) ) {
        deterministic = false;
        System.out.println( "enabling random" );
      }
    if ( deterministic ) {
      // deterministic hiding
      if ( secondorder )
        // second-order system
        for ( int k = 0; k < N; ++k ) {
          TIntFloatHashMap copy = (TIntFloatHashMap) completeUser.clone();
          copy.remove( k + 1 );
          uset.put( k + 1, copy );
        }

      for ( int k = 0; k < N; ++k ) {
        TIntFloatHashMap copy = (TIntFloatHashMap) completeUser2.clone();
        TIntFloatIterator iter = copy.iterator();
        while ( iter.hasNext() )
          iter.advance();
        copy.remove( k + 1 );
        uset.put( k + N + 1, copy );
      }
    }
    else {
      for ( int k = 0; k < N * SizeFactor; ++k )
        // remove one value
        while ( secondorder ) {
          TIntFloatHashMap copy = (TIntFloatHashMap) completeUser.clone();
          System.out.println( r.nextInt( N ) + 1 );
          copy.remove( r.nextInt( N ) + 1 );
          TIntFloatIterator iter = copy.iterator();
          boolean allzero = true;
          while ( iter.hasNext() ) {
            iter.advance();
            if ( iter.value() != 0.0f )
              allzero = false;

          }
          if ( !allzero ) {
            uset.add( k, copy );
            break;
          }
        }

      for ( int k = 0; k < N * SizeFactor; ++k )
        // remove one value
        while ( true ) {
          TIntFloatHashMap copy = (TIntFloatHashMap) completeUser2.clone();
          copy.remove( r.nextInt( N ) + 1 );

          TIntFloatIterator iter = copy.iterator();
          boolean allzero = true;
          while ( iter.hasNext() ) {
            iter.advance();
            if ( iter.value() != 0.0f )
              allzero = false;

          }
          if ( !allzero ) {
            uset.add( k + N + 1, copy );
            break;
          }
        }

    }
    System.out.println( "" );
    int[] keys = uset.keys();
    for ( int k = 0; k < keys.length; ++k ) {
      System.out.println( "UserID=" + keys[k] );
      UtilMath.print( (TIntFloatHashMap) uset.get( keys[k] ), uset.getMaxItemID() );
    }
    Pearson p = new Pearson( (EvaluationSet) uset.clone() );
    NonPersonalized np = new NonPersonalized( (EvaluationSet) uset.clone() );
    STINonPersonalized stinp = new STINonPersonalized( (EvaluationSet) uset.clone(), 1.0f );
    STINonPersonalized2steps stinp2steps = new STINonPersonalized2steps( (EvaluationSet) uset.clone(), 1.0f );
    STINonPersonalizedNsteps stinpn1steps = new STINonPersonalizedNsteps( (EvaluationSet) uset.clone(), 1.0f, 1 );
    STINonPersonalizedNsteps stinpn2steps = new STINonPersonalizedNsteps( (EvaluationSet) uset.clone(), 1.0f, 2 );
    STINonPersonalizedNsteps stinpn3steps = new STINonPersonalizedNsteps( (EvaluationSet) uset.clone(), 1.0f, 3 );
    STINonPersonalizedNsteps stinpn4steps = new STINonPersonalizedNsteps( (EvaluationSet) uset.clone(), 1.0f, 4 );
    STIPearson stip = new STIPearson( (EvaluationSet) uset.clone(), 2.0f );
    Average a = new Average( (EvaluationSet) uset.clone() );
    System.out.println( "Reproduce Constants" );
    TIntFloatHashMap constant = new TIntFloatHashMap();
    constant.put( 1, 3.1416f );
    constant.put( 2, 3.1416f );
    constant.put( 3, 3.1416f );
    System.out.println( "Data" );
    UtilMath.print( constant, uset.getMaxItemID() );
    System.out.println( "Average" );
    complete( a, constant );
    System.out.println( "Non personalized" );
    complete( np, constant );
    System.out.println( "STI Non personalized" );
    complete( stinp, constant );
    System.out.println( "Pearson" );
    complete( p, constant );
    System.out.println( "STIPearson" );
    complete( stip, constant );
    System.out.println( "STI  Non personalized 2 steps" );
    complete( stinp2steps, constant );
    System.out.println( "First-Order exact" );
    TIntFloatHashMap u = new TIntFloatHashMap();
    for ( int k = 1; k < N; ++k )
      u.put( k, completeUser2.get( k ) );
    System.out.println( "Data" );
    UtilMath.print( u, uset.getMaxItemID() );
    System.out.println( "Solution" );
    UtilMath.print( completeUser2, uset.getMaxItemID() );
    System.out.println( "Non personalized" );
    complete( np, u );
    System.out.println( "STI Non personalized" );
    complete( stinp, u );
    System.out.println( "STI  Non personalized 2 steps" );
    complete( stinp2steps, u );
    System.out.println( "STI  Non personalized N=1 steps" );
    complete( stinpn1steps, u );
    System.out.println( "STI  Non personalized N=2 steps" );
    complete( stinpn2steps, u );
    System.out.println( "STI  Non personalized N=3 steps" );
    complete( stinpn3steps, u );
    System.out.println( "STI  Non personalized N=4 steps" );
    complete( stinpn4steps, u );
    System.out.println( "Pearson" );
    complete( p, u );
    System.out.println( "STIPearson" );
    complete( stip, u );
  }

}

