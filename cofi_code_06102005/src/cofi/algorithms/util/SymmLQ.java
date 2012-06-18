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

import cofi.algorithms.*;

// (c) National Research Council of Canada
// Daniel Lemire, Ph.D.
//
//	Ported to Python by Daniel Lemire, Ph.D. (National Research Council of Canada)
//	using an implementation in Fortran/Matlab by Michael A. Saunders.
//
// We don't use preconditionning in this version, and we allow the Krylov
// basis to be initialized using a different "guess".
//
//
// July 2003
//
//
// You are allowed to use this code for any purpose.
// I suggest you give me credit (and Michael A. Saunders as well).
//
//

//
//	This is documentation from Michael's code. He worries a lot about istop,
// but current Python implementation doesn't even return istop as it isn't
// all that useful in practice. It is easy to check whether the algorithm
// worked or not, so I'm not sure this is very much needed.
//
//	istop			output		An integer giving the reason for termination.
//
//						 -1				The matrix Abar appears to be a multiple of
//											 the identity. The solution is a multiple of b.
//
//							0				b = 0, so the exact solution is x = 0.
//											 No iterations were performed.
//
//							1				Norm(rbar) appears to be less than
//											 the value	rtol * norm(Abar) * norm(xbar).
//											 The solution in	x	should be acceptable.
//
//							2				Norm(rbar) appears to be less than
//											 the value eps * norm(Abar) * norm(xbar).
//											 This means that the residual is as small as
//											 seems reasonable on this machine.
//
//							3				Norm(Abar) * norm(xbar) exceeds norm(b)/eps,
//											 which should indicate that x has essentially
//											 converged to an eigenvector of A
//											 corresponding to the eigenvalue shift.
//
//							4				acond (see below) has exceeded 0.1/eps, so
//											 the matrix Abar must be very ill-conditioned.
//											 x may not contain an acceptable solution.
//
//							5				The iteration limit was reached before any of
//											 the previous criteria were satisfied.
//
//							6				The matrix defined by aprod does not appear
//											 to be symmetric.
//											 For certain vectors y = Av and r = Ay, the
//											 products y'y and r'v differ significantly.
//
//							7				The matrix defined by msolve does not appear
//											 to be symmetric.
//											 For vectors satisfying My = v and Mr = y, the
//											 products y'y and r'v differ significantly.
//
//							8				An inner product of the form x'(M\x) was
//											 not positive, so the preconditioned matrix
//											 M does not appear to be positive definite.
//
//											 If istop >= 5, the final	x	may not be an
//											 acceptable solution.
//
// We could return this to the user, but I don't do it.
//
// Messages	=[' beta2 = 0. If M = I, b and x are eigenvectors	',
//		' beta1 = 0. The exact solution is x = 0		 ',
//		' Requested accuracy achieved, as determined by rtol',
//		' Reasonable accuracy achieved, given eps			',
//		' x has converged to an eigenvector				 ',
//		' acond has exceeded 0.1/eps						',
//		' The iteration limit was reached					',
//		' aprod does not define a symmetric matrix		 ',
//		' msolve does not define a symmetric matrix		 ',
//		' msolve does not define a pos-def preconditioner	']
//
//

/**
 *  This is an implementation of the Symmetric LQ algorithm to minimize | Ax -
 *  b| for A symmetric. $Id: SymmLQ.java,v 1.4 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $ $Author: lemired $ $Revision: 1.4 $ $Log:
 *  SymmLQ.java,v $ Revision 1.2 2003/10/28 01:43:08 lemired Lots of
 *  refactoring. Revision 1.1 2003/10/27 17:21:15 lemired Putting some order
 *  Revision 1.6 2003/08/22 13:38:23 howsen *** empty log message *** Revision
 *  1.5 2003/08/07 13:16:06 lemired More javadoc improvments. Revision 1.4
 *  2003/08/07 00:37:42 lemired Mostly, I updated the javadoc.
 *
 *@author     Daniel Lemire
 *@created    October 30, 2003
 *@since      July 12, 2003
 */
public class SymmLQ {

  // java badly needs default values
  /**
   *  Solves A x = b iteratively. A must be symmetric.
   *
   *@param  A                           The big matrix
   *@param  b                           The right-hand-side term
   *@return                             x
   *@exception  NoConvergenceException  if the algorithm fails to converge
   */
  public static double[] solve( final double[][] A, final double[] b ) throws
      NoConvergenceException {
    int iterations = Math.max( A.length, 50 );
    return solve( A, b, 1E-8, iterations, 1E-14 );
  }


  /*
   *  Return x such that it minimizes | A x - b | in the l2 norm using
   *  the symmetric lq method.
   *  WARNING: It assumes that A is symmetric. No checks are done.
   *  See:     A = array([[1,    1,  0],[1,    1,  0],  [0,0,  0]], 'd')
   *  b = array([1,-1,0],'d')
   *  x = symmlq(A,b )
   *  print "Next line should be zero (best solution)"
   *  print matrixmultiply(A,x)
   *  C.C. Paige and M.A. Saunders,	Solution of Sparse Indefinite
   *  Systems of Linear Equations, SIAM J. Numer. Anal. 12, 4,
   *  September 1975, pp. 617-629.
   */
  /**
   *  Solves A x = b iteratively. A must be symmetric.
   *
   *@param  A                           The big matrix
   *@param  b                           The right-hand-side term
   *@param  rtol                        Tolerance
   *@param  itnlim                      Number of iterations allowed
   *@param  eps                         Very small value
   *@return                             the solution (x)
   *@exception  NoConvergenceException  if the method failed to converge
   */
  public static double[] solve( final double[][] A, final double[] b,
      final double rtol, final int itnlim,
      final double eps ) throws NoConvergenceException {
    //if(!isSymmetric(A)) throw new CollaborativeFilteringException("A is not symmetric!");
    final int n = A.length;
    // expect A to be nxn matrix
    double[] X = UtilMath.zeros( n );
    //System.out.println("[error] = "+error(A,X,b));
    // will hold the answer
    //	Set up y for the first Lanczos vector v1.
    //	y is really beta1 * P * v1	where	P = C^(-1).
    //	y and beta1 will be zero if b = 0.
    double[] y = UtilMath.copy( b );
    // y = b
    double[] r1 = UtilMath.copy( b );
    // r1 = b
    double b1 = y[0];
    double beta1 = UtilMath.innerproduct( r1, y );
    // beta1 = <r1,y> = <b,b>
    //	If b = 0 exactly, stop with x = 0.
    if ( beta1 == 0 )
      return X;
    // istop = 0
    //	Here and later, v is really P * (the Lanczos v).
    beta1 = Math.sqrt( beta1 );
    // beta1 = norm(b)
    double s = 1 / beta1;
    // s = 1/ | b |
    double[] v = UtilMath.product( s, y );
    // v = b / | b |
    // v is b normalized
    y = UtilMath.matrixmultiply( A, v );
    // y = A b/|b|
    //	Set up y for the second Lanczos vector.
    //	Again, y is beta * P * v2	where	P = C^(-1).
    //	y and beta will be zero or very small if Abar = I or constant * I.
    double alfa = UtilMath.innerproduct( v, y );
    // alpha = <A b/|b| ,b/|b| >
    UtilMath.addInPlace( y, UtilMath.product( -alfa / beta1, r1 ) );
    // y = A b/|b| - <A b/|b| ,b/|b| > * b/|b|
    //	Make sure	r2	will be orthogonal to the first	v.
    //z	= innerproduct(v, y) // should be zero since y and v are ortho
    //print " z = ", z
    //s	= innerproduct(v, v) // should be one
    //print " s = ", s
    //y	+= (- z / s) * v // shouldn't change y
    double[] r2 = UtilMath.copy( y );
    // r2 = A b/|b| - <A b/|b| ,b/|b| > * b/|b|
    double oldb = beta1;
    // oldb = | b |
    double beta = UtilMath.innerproduct( r2, y );
    // beta = | r2 |^2
    //if (beta < 0): should never happen!
    //	print "An inner product of the form <x , A x> was not positive"
    //	return x //	istop = 8
    //	Cause termination (later) if beta is essentially zero.
    beta = Math.sqrt( beta );
    // beta = | r2 |
    int istop = 5;
    if ( beta <= eps ) {
      if ( Math.abs( alfa ) > eps ) {
        X = UtilMath.product( 1 / alfa, b );
        return UtilMath.product( 1 / alfa, b );
      }
      else
        return b;
    }
    //
    // If beta == 0 happens, then
    //			A b / | b | =	<A b/|b| ,b/|b| > *	b/|b|
    // so, we can choose
    //	A ( b / <A b/|b| ,b/|b| >	) = b
    // to solve the problem. If so, we may have <A b/|b|, b/|b| >	= 0
    // and so, the problem is still there. The only way this can be happening
    // is if A b = 0. Now, this means that <b,Ax> = 0 for all x because
    // A = transpose(A). This means we have no hope of approaching the solution.
    // However, to minimize |Ax -b|, the best choice when <Ax,b>=0 for all x, is
    // to choose x such that |Ax|=0, so choose x =b!
    //
    //	See if the local reorthogonalization achieved anything.
    //denom = Math.sqrt( s ) * Math.sqrt( innerproduct(r2, r2) ) +	eps // denom =	|r2 |	/ Math.sqrt(| b |)	 + eps
    //s		 = 0 // z / denom // should be zero
    //t		 = innerproduct( v, r2) // <b / | b | ,	A b/|b|	- <A b/|b| ,b/|b| > *	b/|b|	= r2 >
    //t		 = t / denom // <b/|b|, r2/|r2|>
    // t should be zero
    //	Initialize other quantities.
    double cgnorm = beta1;
    // cgnorm = | b |
    double rhs2 = 0;
    double tnorm = alfa * alfa + beta * beta;
    //	| <A b/|b| ,b/|b| > | ** 2 + < r2, r2 >^2
    double gbar = alfa;
    // gbar =	<A b/|b| ,b/|b| >
    double diag = gbar;
    double bstep = 0;
    double ynorm2 = 0;
    double dbar = beta;
    // |r2|^2
    double snprod = 1;
    double gmax = Math.abs( alfa ) + eps;
    //	gmax = |	<A b/|b| ,b/|b| >	| + eps
    double rhs1 = beta1;
    // rhs1 = | b |
    double lqnorm = Math.sqrt( rhs1 * rhs1 + rhs2 * rhs2 );
    //x1cg	 = 0
    double gmin = gmax;
    double qrnorm = beta1;
    double[] w = UtilMath.zeros( n );
    //	------------------------------------------------------------------
    //	Main iteration loop.
    //	------------------------------------------------------------------
    //	Estimate various norms and test for convergence.
    int itn = 0;
    // so that we can tell after the loop how long it took
    for ( itn = 0; itn < itnlim; ++itn ) {
      double anorm = Math.sqrt( tnorm );
      double ynorm = Math.sqrt( ynorm2 );
      double epsa = anorm * eps;
      double epsx = anorm * ynorm * eps;
      double epsr = anorm * ynorm * rtol;
      diag = gbar;
      if ( diag == 0 )
        diag = epsa;

      lqnorm = Math.sqrt( rhs1 * rhs1 + rhs2 * rhs2 );
      qrnorm = snprod * beta1;
      cgnorm = qrnorm * beta / Math.abs( diag );
      //System.out.println("epsr = "+epsr + " cgnorm = "+cgnorm);
      //		 Estimate	Cond(A).
      //		 In this version we look at the diagonals of	L	in the
      //		 factorization of the tridiagonal matrix,	T = L*Q.
      //		 Sometimes, T(k) can be misleadingly ill-conditioned when
      //		 T(k+1) is not, so we must be careful not to overestimate acond.
      double acond = 0.0f;
      if ( lqnorm < cgnorm )
        acond = gmax / gmin;

      else
        acond = gmax / Math.min( gmin, Math.abs( diag ) );

        // end of if

      double zbar = rhs1 / diag;
      double z = ( snprod * zbar + bstep ) / beta1;
      //x1lq	 = x[0] + b1 * bstep / beta1
      //x1cg	 = x[0] + w[0] * zbar	+	b1 * z
      //		See if any of the stopping criteria are satisfied.
      //		 In rare cases, istop is already -1 from above (Abar = const * I). (No longer true, DL)
      if ( acond >= 0.1 / eps ) {
        istop = 4;
        break;
      }
      if ( epsx >= beta1 ) {
        istop = 3;
        break;
      }
      if ( cgnorm <= epsx ) {
        istop = 2;
        break;
      }
      //if( cgnorm <= rtol) {
      if ( cgnorm <= epsr ) {
        istop = 1;
        break;
      }
      //		 Obtain the current Lanczos vector	v = (1 / beta)*y
      //		 and set up	y	for the next iteration.
      s = 1 / beta;
      v = UtilMath.product( s, y );
      y = UtilMath.matrixmultiply( A, v );
      UtilMath.addInPlace( y, UtilMath.product( -beta / oldb, r1 ) );
      alfa = UtilMath.innerproduct( v, y );
      UtilMath.addInPlace( y, UtilMath.product( -alfa / beta, r2 ) );
      r1 = UtilMath.copy( r2 );
      r2 = UtilMath.copy( y );
      oldb = beta;
      beta = UtilMath.innerproduct( r2, y );
      if ( beta < 0 )
        throw new CollaborativeFilteringException(
            " The matrix appears not to be symmetric. " );
      beta = Math.sqrt( beta );
      tnorm += alfa * alfa + oldb * oldb + beta * beta;
      //		 Compute the next plane rotation for Q.
      double gamma = Math.sqrt( gbar * gbar + oldb * oldb );
      double cs = gbar / gamma;
      double sn = oldb / gamma;
      double delta = cs * dbar + sn * alfa;
      gbar = sn * dbar - cs * alfa;
      double epsln = sn * beta;
      dbar = -cs * beta;
      //		 Update	X.
      z = rhs1 / gamma;
      s = z * cs;
      double t = z * sn;
      UtilMath.addInPlace( X,
          UtilMath.add( UtilMath.product( s, w ),
          UtilMath.product( t, v ) ) );
      w = UtilMath.add( UtilMath.product( sn, w ), UtilMath.product( -cs, v ) );
      //		 Accumulate the step along the direction	b, and go round again.
      bstep += snprod * cs * z;
      snprod *= sn;
      gmax = Math.max( gmax, gamma );
      gmin = Math.min( gmin, gamma );
      ynorm2 += z * z;
      rhs1 = rhs2 - delta * z;
      rhs2 = -epsln * z;
    }
    //System.out.println("Reached iteration "+itn);
    if ( istop == 5 ) {
      System.err.println( "[Warning] Could not converge in the given number of iterations. Solution might be invalid." );
      throw new NoConvergenceException();
    }
    //else System.out.println("Stop for reason "+istop);
    //	------------------------------------------------------------------
    //	End of main iteration loop.
    //	------------------------------------------------------------------
    //	Move to the CG point if it seems better.
    //	In this version of SYMMLQ, the convergence tests involve
    //	only cgnorm, so we're unlikely to stop at an LQ point,
    //	EXCEPT if the iteration limit interferes.
    if ( cgnorm < lqnorm ) {
      double zbar = rhs1 / diag;
      bstep += snprod * zbar;
      //ynorm	= Math.sqrt( ynorm2	+	zbar**2 )
      UtilMath.addInPlace( X, UtilMath.product( zbar, w ) );
      //x	+= zbar * w
    }
    //	Add the step along	b.
    bstep /= beta1;
    y = UtilMath.copy( b );
    UtilMath.addInPlace( X, UtilMath.product( bstep, y ) );
    //	Compute the final residual,	r1 = b - (A )*x.
    //y			= matrixmultiply(A,x)
    //r1		 = b - y
    //rnorm	= Math.sqrt(innerproduct ( r1, r1 ))
    //xnorm	= Math.sqrt(innerproduct(	x, x) )
    return X;
  }


  /**
   *  The main program for the SymmLQ class
   *
   *@param  arg                         The command line arguments
   *@exception  NoConvergenceException  if the method fails to convergence
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

