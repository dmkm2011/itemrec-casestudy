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

/**
 *  This is an implementation of the Minimum Residual method in Java. $Id:
 *  Minres.java,v 1.5 2003/08/22 13:38:23 howsen Exp $ $Date: 2003/08/22
 *  13:38:23 $ $Author: lemired $ $Revision: 1.2 $ $Log: Minres.java,v $
 *  13:38:23 $ $Author: lemired $ $Revision: 1.2 $ Revision 1.2  2003/11/11 13:25:58  lemired
 *  13:38:23 $ $Author: lemired $ $Revision: 1.2 $ Added gpl headers
 *  13:38:23 $ $Author: lemired $ $Revision: 1.2 $
 *  13:38:23 $ $Author: lemired $ $Revision: 1.2 $ Revision 1.1  2003/10/27 17:21:15  lemired
 *  13:38:23 $ $Author: lemired $ $Revision: 1.2 $ Putting some order
 *  13:38:23 $ $Author: lemired $ $Revision: 1.2 $
 *  13:38:23 $ $Author: lemired $ $Revision: 1.2 $ Revision 1.6  2003/10/07 13:28:32  lemired
 *  13:38:23 $ $Author: lemired $ $Revision: 1.2 $ Did some tweaking...
 *  13:38:23 $ $Author: lemired $ $Revision: 1.2 $ Revision
 *  1.5 2003/08/22 13:38:23 howsen *** empty log message *** Revision 1.4
 *  2003/08/19 17:51:21 lemired I've been improving OptimalWeight. Revision 1.3
 *  2003/08/07 13:16:05 lemired More javadoc improvments. Revision 1.2
 *  2003/08/07 00:37:42 lemired Mostly, I updated the javadoc.
 *
 *@author     Daniel Lemire
 *@created    October 1, 2003
 *@since      August 6, 2003
 */
public class Minres {

  /**
   *  Solve min | A x - b| for b given A symmetric
   *
   *@param  A                           Some symmetric matrix
   *@param  b                           Some vector b
   *@return                             x
   *@exception  NoConvergenceException  if the method failed to converage
   */
  public static double[] solve( final double[][] A, final double[] b ) throws
      NoConvergenceException {
    return solve( A, b, 1E-6, 500, UtilMath.zeros( A.length ),
        UtilMath.identity( A.length ), true );
  }


  /**
   *  Solves the min||A x - b|| problem using the minimum residual method over
   *  the Euclidean norm. A must be symmetric, but can be indefinite. It can be
   *  shown that either A x = b up to numerical error or else best solution is
   *  when x = 0. I expect that this code could be easily optimized to be twice
   *  as fast.
   *
   *@param  A                           The big matrix
   *@param  b                           The right-hand-side term
   *@param  max_it                      number of iterations allowed.
   *@param  x                           initial guess
   *@param  L                           preconditioning matrix
   *@param  tol                         tolerance
   *@param  LIsIdentity                 Description of the Parameter
   *@return                             x
   *@exception  NoConvergenceException  if the method failed to converge
   */
  public static double[] solve( final double[][] A, final double[] b,
      final double tol, final int max_it,
      final double[] x, double[][] L,
      boolean LIsIdentity ) throws
      NoConvergenceException {
    double[] r0 = UtilMath.subtract( b, UtilMath.matrixmultiply( A, x ) );
    double[] w = LIsIdentity ? r0 :
        UtilMath.transposematrixmultiply( L, UtilMath.matrixmultiply( L, r0 ) );
    //UtilMath.matrixmultiply( UtilMath.transpose( L ), UtilMath.matrixmultiply( L, r0 ) );
    double beta = Math.sqrt( UtilMath.dot( w, r0 ) );
    if ( beta < 1E-15 )
      return x;
      //not sure what to do here

    double[] q = UtilMath.product( 1 / beta, r0 );
    w = UtilMath.product( 1 / beta, w );
    double init_res = UtilMath.norm( r0 );
    double c = -1.0;
    double s = 0.0;
    double c_old = 1.0;
    double s_old = 0.0;
    double eta = beta;
    double[] q_old = UtilMath.zeros( x.length );
    double[] v_old = UtilMath.zeros( x.length );
    double[] v_oold = UtilMath.zeros( x.length );
    double[] q_tilde;
    double[] w_old;
    double[] v;
    for ( int iter = 1; iter < max_it; ++iter ) {
      q_tilde = UtilMath.subtractInPlace( UtilMath.matrixmultiply( A, w ),
          UtilMath.productInPlace( beta, q_old ) );
      double alpha = UtilMath.dot( w, q_tilde );
      UtilMath.subtractInPlace( q_tilde, UtilMath.product( alpha, q ) );
      w_old = UtilMath.copy( w );
      w = LIsIdentity ? UtilMath.copy( q_tilde ) :

      UtilMath.transposematrixmultiply( L, UtilMath.matrixmultiply( L, q_tilde ) );
      //w = UtilMath.matrixmultiply( UtilMath.transpose( L ), UtilMath.matrixmultiply( L, q_tilde ) );
      double beta_old = beta;
      beta = Math.sqrt( UtilMath.dot( w, q_tilde ) );
      double gamma_tilde = -c * alpha - c_old * s * beta_old;
      double gamma = Math.sqrt( gamma_tilde * gamma_tilde + beta * beta );
      double delta = s * alpha - c_old * c * beta_old;
      double epsilon = s_old * beta_old;
      c_old = c;
      s_old = s;
      if ( gamma < 1E-15 )
        return x;
      c = gamma_tilde / gamma;
      s = beta / gamma;
      v = UtilMath.productInPlace( 1 / gamma,
          UtilMath.subtractInPlace(
          UtilMath.subtract( w_old,
          UtilMath.product( epsilon, v_oold ) ), UtilMath.product( delta, v_old ) ) );
      UtilMath.addInPlace( x, UtilMath.product( c * eta, v ) );
      if ( beta < 1E-15 )
        return x;
      v_oold = UtilMath.copy( v_old );
      v_old = UtilMath.copy( v );
      eta = s * eta;
      q_old = UtilMath.copy( q );
      q = UtilMath.product( 1 / beta, q_tilde );
      w = UtilMath.productInPlace( 1 / beta, w );
      double res_red = UtilMath.norm( UtilMath.subtract( b,
          UtilMath.matrixmultiply( A, x ) ) ) / init_res;
      System.out.println( "[minres] iter = " + iter + " / " + max_it +
          " res_red = " + res_red + " tol = " + tol );
      if ( res_red < tol )
        return x;
            // converged
    }
    throw new NoConvergenceException();
  }


  /**
   *  The main program for the Minres class
   *
   *@param  arg                         The command line arguments
   *@exception  NoConvergenceException  if the method failed to converge
   */
  public static void main( String[] arg ) throws NoConvergenceException {
    double[][] A =
        {
        {
        1.0f, 2.0f, 3.21f}
        ,
        {
        2.0f, 1.0f, 3.0f}
        ,
        {
        3.21f, 3.0f, -9.89f}
        };
    double[] b =
        {
        -2.11f, 3.1f, 0.04f};
    double[] x = solve( A, b );
    System.out.println( "Next two lines should be identical!" );
    UtilMath.print( UtilMath.matrixmultiply( A, x ) );
    UtilMath.print( b );
    double[][] A2 =
        {
        {
        1.0f, 1.0f, 1.0f}
        ,
        {
        1.0f, 1.0f, 0.0f}
        ,
        {
        1.0f, 0.0f, 1.0f}
        };
    double[] b2 =
        {
        1.0f, 3.1416f, 0.0f};
    x = solve( A2, b2 );
    System.out.println( "Next two lines should be identical!" );
    UtilMath.print( UtilMath.matrixmultiply( A2, x ) );
    UtilMath.print( b2 );
    double[][] A3 =
        {
        {
        1.0f, 1.0f, 0.0f}
        ,
        {
        1.0f, 1.0f, 0.0f}
        ,
        {
        0.0f, 0.0f, 0.0f}
        };
    double[] b3 =
        {
        1.0f, -1.0f, 0.0f};
    x = solve( A3, b3 );
    System.out.println( "Next line should be zero (best solution)" );
    UtilMath.print( UtilMath.matrixmultiply( A3, x ) );
  }

}

