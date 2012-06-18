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
package cofi.benchmark;

import JSci.maths.*;
import JSci.maths.matrices.DoubleSquareMatrix;
import JSci.maths.vectors.*;
import JSci.util.*;
import cofi.algorithms.*;
import cofi.algorithms.util.*;
import java.util.*;

/**
 *  This class was designed to benchmark the Java implementation of SymmLQ. 
 *
 *  $Id: BenchmarkSymmLQ.java,v 1.2 2004/04/22 14:55:52 lemire Exp $
 *  $Date: 2004/04/22 14:55:52 $ 
 *  $Author: lemire $ 
 *  $Revision: 1.2 $ 
 *  $Log: BenchmarkSymmLQ.java,v $
 *  Revision 1.2  2004/04/22 14:55:52  lemire
 *  The compileit script now runs under cygwin
 *
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
 *@author     Daniel Lemire
 *@since    August 6, 2003
 */
class BenchmarkSymmLQ {

  /**
   *  The main program for the BenchmarkSymmLQ class
   *
   *@param  arg  The command line arguments
   */
  public static void main( String[] arg ) {
    //deterministictest(600);
    test( 600 );
    test( 2000 );
    //deterministictest(10);
    //deterministictest(100);
    //deterministictest(600);
  }


  /**
   *  A unit test for JUnit
   *
   *@param  size  Description of the Parameter
   */
  public static void test( final int size ) {
    System.out.println( "Size = " + size );
    double[][] A = new double[size][size];
    double[] B = new double[size];
    System.out.println( "Populating A" );
    int number = Math.max( 100, size );
    for ( int i = 0; i < number; ++i ) {
      double[] u = random( size );
      for ( int col = 0; col < size; ++col ) {
        for ( int row = 0; row < size; ++row )
          A[row][col] += u[col] * u[row] / number;

        B[col] += u[col] / number;
      }
      if ( i % 100 == 0 )
        System.out.println( "remaining: " + ( 100.0 - i * 100.0 / number ) + "%" );

    }
    System.out.println( "going for the solution..." );
    try {
      long before = System.currentTimeMillis();
      double[] answer = SymmLQ.solve( A, B );
      long after = System.currentTimeMillis();
      System.out.println( "It took " + ( after - before ) / 1000.0 + " seconds!" );
      System.out.println( "Error is = " + UtilMath.error( A, answer, B ) );
      System.out.println( "Init Error is = " + UtilMath.error( A, new double[answer.length], B ) );
    } catch ( NoConvergenceException nce ) {
      System.out.println( "SymmLQ failed to converge!" );
    }
    try {
      long before = System.currentTimeMillis();
      double[] answer = Minres.solve( A, B );
      long after = System.currentTimeMillis();
      System.out.println( "It took " + ( after - before ) / 1000.0 + " seconds!" );
      System.out.println( "Error is = " + UtilMath.error( A, answer, B ) );
    } catch ( NoConvergenceException nce ) {
      System.out.println( "Minres did not converge!" );
    }
    DoubleSquareMatrix dsm = new DoubleSquareMatrix( A );
    DoubleVector dv = new DoubleVector( B );
    System.out.println( "[gmres] Using GMRES..." );
    long before = System.currentTimeMillis();
    try {
      AbstractDoubleVector dvX = LinearMath.solveGMRes( dsm, dv, 5000, 1E-15 );
      double[] x = VectorToolkit.toArray( dvX );
      System.out.println( "[gmres] Error is = " + UtilMath.error( A, x, B ) );
    } catch ( MaximumIterationsExceededException miee ) {
      System.err.println( "Did not converge in time!" );
    }
    long after = System.currentTimeMillis();
    System.out.println( "[gmres] It took " + ( after - before ) / 1000.0 + " seconds!" );

  }


  /**
   *  Description of the Method
   *
   *@param  size  Description of the Parameter
   */
  public static void deterministictest( final int size ) {
    System.out.println( "Size = " + size );
    double[][] A = new double[size][size];
    double[] B = new double[size];
    for ( int k = 0; k < size; ++k )
      B[k] = k % 2;
    System.out.println( "Populating A" );
    for ( int col = 0; col < size; ++col )
      for ( int row = 0; row < size; ++row )
        A[row][col] = col + row;

    System.out.println( "going for the solution..." );
    try {
      long before = System.currentTimeMillis();
      double[] answer = SymmLQ.solve( A, B );
      long after = System.currentTimeMillis();
      System.out.println( "It took " + ( after - before ) / 1000.0 + " seconds!" );
      System.out.println( "Error is = " + UtilMath.error( A, answer, B ) );
      System.out.println( "Init Error is = " + UtilMath.error( A, new double[answer.length], B ) );
    } catch ( NoConvergenceException nce ) {
      System.out.println( "SymmLQ failed to converge!" );
    }

    System.out.println( "going for the solution with minres..." );
    try {
      long before = System.currentTimeMillis();
      double[] answer = Minres.solve( A, B );
      long after = System.currentTimeMillis();
      System.out.println( "It took " + ( after - before ) / 1000.0 + " seconds!" );
      System.out.println( "Error is = " + UtilMath.error( A, answer, B ) );
    } catch ( NoConvergenceException nce ) {
      System.out.println( "Minres did not converge!" );
    }

    DoubleSquareMatrix dsm = new DoubleSquareMatrix( A );
    DoubleVector dv = new DoubleVector( B );
    System.out.println( "[gmres] Using GMRES..." );
    long before = System.currentTimeMillis();
    try {
      AbstractDoubleVector dvX = LinearMath.solveGMRes( dsm, dv, 500, 1E-15 );
      double[] x = VectorToolkit.toArray( dvX );
      System.out.println( "[gmres] Error is = " + UtilMath.error( A, x, B ) );
    } catch ( MaximumIterationsExceededException miee ) {
      System.err.println( "Did not converge in time!" );
    }
    long after = System.currentTimeMillis();
    System.out.println( "[gmres] It took " + ( after - before ) / 1000.0 + " seconds!" );

  }


  /**
   *  Description of the Method
   *
   *@param  N  Description of the Parameter
   *@return    Description of the Return Value
   */
  public static double[] random( final int N ) {
    double[] ans = new double[N];
    Random r = new Random( System.currentTimeMillis() );
    for ( int k = 0; k < N; ++k )
      ans[k] = r.nextFloat();
    return ans;
  }

}

