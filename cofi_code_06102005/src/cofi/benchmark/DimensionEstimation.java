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

import JSci.maths.matrices.DoubleSquareMatrix;
import JSci.maths.vectors.DoubleVector;
import cofi.algorithms.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.util.*;
import cofi.data.*;
import gnu.trove.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

import java.io.*;

/**
 *  This class can be used to evaluate the overall dimension of the ratings by
 *  considering a subset of indices and a subset of users. For research and non
 *  commercial purposes.
 *
 * 
 *  $Id: DimensionEstimation.java,v 1.1 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $ 
 *  $Author: lemired $ 
 *  $Revision: 1.1 $ 
 *  $Log: DimensionEstimation.java,v $
 *  Revision 1.1  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.4  2003/10/28 01:43:08  lemired
 *  Lots of refactoring.
 *
 *  Revision 1.3  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author       Daniel Lemire
 *@since      December 2002
 */
public class DimensionEstimation {
  EvaluationSet mSet;
  float[] mItemAverageWithoutMean;


  /**
   *  Constructor for the DimensionEstimation object
   *
   *@param  set  Description of the Parameter
   */
  public DimensionEstimation( EvaluationSet set ) {
    mSet = set;
    mItemAverageWithoutMean = ( new NonPersonalized( set ) ).completeUser( new TIntFloatHashMap() );
  }


  /**
   *  Description of the Method
   */
  public void computeKLMatrixBackward() {
    // first, find the 5 most populated users
    TIntObjectIterator iter = mSet.iterator();
    iter.advance();
    TIntFloatHashMap u1 = (TIntFloatHashMap) iter.value();
    iter.advance();
    TIntFloatHashMap u2 = (TIntFloatHashMap) iter.value();
    iter.advance();
    TIntFloatHashMap u3 = (TIntFloatHashMap) iter.value();
    iter.advance();
    TIntFloatHashMap u4 = (TIntFloatHashMap) iter.value();
    iter.advance();
    TIntFloatHashMap u5 = (TIntFloatHashMap) iter.value();
    while ( iter.hasNext() ) {
      iter.advance();
      TIntFloatHashMap runningU = (TIntFloatHashMap) iter.value();
      if ( runningU.size() > u1.size() ) {
        u1 = runningU;
        continue;
      }
      if ( runningU.size() > u2.size() ) {
        u2 = runningU;
        continue;
      }
      if ( runningU.size() > u3.size() ) {
        u3 = runningU;
        continue;
      }
      if ( runningU.size() > u4.size() ) {
        u4 = runningU;
        continue;
      }
      if ( runningU.size() > u5.size() ) {
        u5 = runningU;
        continue;
      }
    }
    computeKLMatrixBackward( u1, u2, u3, u4, u5 );
  }


  /**
   *  Description of the Method
   *
   *@param  u1  Description of the Parameter
   *@param  u2  Description of the Parameter
   *@param  u3  Description of the Parameter
   *@param  u4  Description of the Parameter
   *@param  u5  Description of the Parameter
   */
  public void computeKLMatrixBackward( TIntFloatHashMap u1, TIntFloatHashMap u2, TIntFloatHashMap u3, TIntFloatHashMap u4, TIntFloatHashMap u5 ) {
    double[][] matrix = new double[5][5];
    int candidate = 0;
    TIntFloatIterator iter = u1.iterator();
    float av1 = UtilMath.average( u1 );
    float av2 = UtilMath.average( u2 );
    float av3 = UtilMath.average( u3 );
    float av4 = UtilMath.average( u4 );
    float av5 = UtilMath.average( u5 );
    float[] averages = new float[5];
    while ( iter.hasNext() ) {
      iter.advance();
      if ( u2.contains( iter.key() ) && u3.contains( iter.key() ) && u4.contains( iter.key() ) && u5.contains( iter.key() ) ) {
        candidate++;
        float[] vector = new float[5];

        vector[0] = iter.value() - av1 - mItemAverageWithoutMean[iter.key()];
        vector[1] = u2.get( iter.key() ) - av2 - mItemAverageWithoutMean[iter.key()];
        vector[2] = u3.get( iter.key() ) - av3 - mItemAverageWithoutMean[iter.key()];
        vector[3] = u4.get( iter.key() ) - av4 - mItemAverageWithoutMean[iter.key()];
        vector[4] = u5.get( iter.key() ) - av5 - mItemAverageWithoutMean[iter.key()];
        //System.out.println("average over 5 = "+UtilMath.average(vector));
        for ( int k = 0; k < 5; ++k )
          averages[k] += vector[k];

        for ( int k = 0; k < 5; ++k )
          for ( int l = 0; l < 5; ++l )
            matrix[k][l] += vector[k] * vector[l] / 10.0;

      }
    }
    System.out.println( "[debug] Found " + candidate + " candidates!" );

    DoubleSquareMatrix dsq = new DoubleSquareMatrix( matrix );
    try {
      DoubleVector[] eigenvector = new DoubleVector[5];
      double[] eigenvalues = JSci.maths.LinearMath.eigenSolveSymmetric( dsq, eigenvector );
      for ( int k = 0; k < 5; ++k ) {
        System.out.println( "-->" + eigenvalues[k] );
        System.out.println( eigenvector[k] );
      }
    } catch ( JSci.maths.MaximumIterationsExceededException miee ) {}

  }


  /**
   *  Description of the Method
   */
  public void computeKLMatrix() {
    int[] userids = mSet.keys();
    int[] ItemFrequency = new int[mSet.getMaxItemID()];
    for ( int k = 0; k < userids.length; ++k ) {
      final TIntFloatHashMap runningU = (TIntFloatHashMap) mSet.get( userids[k] );
      TIntFloatIterator iter = runningU.iterator();
      while ( iter.hasNext() ) {
        iter.advance();
        ItemFrequency[iter.key()] += 1;
      }
    }
    int index1 = 1;
    int index2 = 2;
    int index3 = 3;
    int index4 = 4;
    int index5 = 5;
    for ( int k = 5; k < ItemFrequency.length; ++k ) {
      if ( ItemFrequency[k] > ItemFrequency[index1] ) {
        index1 = k + 1;
        continue;
      }
      if ( ItemFrequency[k] > ItemFrequency[index2] ) {
        index2 = k + 1;
        continue;
      }
      if ( ItemFrequency[k] > ItemFrequency[index3] ) {
        index3 = k + 1;
        continue;
      }
      if ( ItemFrequency[k] > ItemFrequency[index4] ) {
        index4 = k + 1;
        continue;
      }
      if ( ItemFrequency[k] > ItemFrequency[index5] ) {
        index5 = k + 1;
        continue;
      }
    }
    System.out.println( "Found indexes " + index1 + " " + index2 + " " + index3 + " " + index4 + " " + index5 );
    computeKLMatrix( index1, index2, index3, index4, index5 );

  }


  /**
   *  Description of the Method
   *
   *@param  index1  Description of the Parameter
   *@param  index2  Description of the Parameter
   *@param  index3  Description of the Parameter
   *@param  index4  Description of the Parameter
   *@param  index5  Description of the Parameter
   */
  public void computeKLMatrix( int index1, int index2, int index3, int index4, int index5 ) {
    double[][] matrix = new double[5][5];
    TIntObjectIterator useriter = mSet.iterator();
    int candidate = 0;
    while ( useriter.hasNext() ) {
      useriter.advance();
      TIntFloatHashMap user = (TIntFloatHashMap) useriter.value();
      if ( user.contains( index1 ) && user.contains( index2 ) && user.contains( index3 ) && user.contains( index4 ) && user.contains( index5 ) ) {
        float value = 0.2f;
        //System.out.println("[debug] Found a candidate!!!");
        candidate++;
        float average = UtilMath.average( user );
        //average = 0.0f;
        float[] vector = new float[5];
        vector[0] = user.get( index1 ) - average - mItemAverageWithoutMean[index1];
        vector[1] = user.get( index2 ) - average - mItemAverageWithoutMean[index2];
        vector[2] = user.get( index3 ) - average - mItemAverageWithoutMean[index3];
        vector[3] = user.get( index4 ) - average - mItemAverageWithoutMean[index4];
        vector[4] = user.get( index5 ) - average - mItemAverageWithoutMean[index5];
        for ( int k = 0; k < 5; ++k )
          for ( int l = 0; l < 5; ++l )
            matrix[k][l] += vector[k] * vector[l] / 10.0;

      }
    }
    System.out.println( "[debug] Found " + candidate + " candidates!" );
    DoubleSquareMatrix dsq = new DoubleSquareMatrix( matrix );
    try {
      DoubleVector[] eigenvector = new DoubleVector[5];
      double[] eigenvalues = JSci.maths.LinearMath.eigenSolveSymmetric( dsq, eigenvector );
      for ( int k = 0; k < 5; ++k ) {
        System.out.println( "-->" + eigenvalues[k] );
        System.out.println( eigenvector[k] );
      }
    } catch ( JSci.maths.MaximumIterationsExceededException miee ) {}

  }


  /**
   *  The main program for the DimensionEstimation class
   *
   *@param  args             The command line arguments
   *@exception  IOException  Description of the Exception
   */
  public static void main( String[] args ) throws IOException {
    String DataFileName = System.getProperty( "user.home" ) + "/CFData/vote.bin";
    DimensionEstimation de = new DimensionEstimation( EvaluationSet.readRatings( new File( DataFileName ), 500000, 50, 20 ) );
    System.out.println( "User space" );
    de.computeKLMatrix();
    System.out.println( "Item space" );
    de.computeKLMatrixBackward();
  }

}

