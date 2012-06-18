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
 *  This is an implementation of the Conjugate Gradient method in Java. 
 *  $Id: ConjugateGradient.java,v 1.2 2003/11/11 13:25:58 lemired Exp $ $Date: 2003/11/11 13:25:58 $ $Revision: 1.2 $ $Log: ConjugateGradient.java,v $
 *  $Id: ConjugateGradient.java,v 1.2 2003/11/11 13:25:58 lemired Exp $ $Date: 2003/11/11 13:25:58 $ $Revision: 1.2 $ Revision 1.2  2003/11/11 13:25:58  lemired
 *  $Id: ConjugateGradient.java,v 1.2 2003/11/11 13:25:58 lemired Exp $ $Date: 2003/11/11 13:25:58 $ $Revision: 1.2 $ Added gpl headers
 *  $Id: ConjugateGradient.java,v 1.2 2003/11/11 13:25:58 lemired Exp $ $Date: 2003/11/11 13:25:58 $ $Revision: 1.2 $
 *  $Id: ConjugateGradient.java,v 1.2 2003/11/11 13:25:58 lemired Exp $ $Date: 2003/11/11 13:25:58 $ $Revision: 1.2 $ Revision 1.1  2003/11/03 23:41:57  lemired
 *  $Id: ConjugateGradient.java,v 1.2 2003/11/11 13:25:58 lemired Exp $ $Date: 2003/11/11 13:25:58 $ $Revision: 1.2 $ Latest changes: should almost conclude paper with Anna.
 *  $Id: ConjugateGradient.java,v 1.2 2003/11/11 13:25:58 lemired Exp $ $Date: 2003/11/11 13:25:58 $ $Revision: 1.2 $
 */
public class ConjugateGradient {

  /**
   *  Solve min | A x - b| for b given A symmetric positive definite
   *
   *@param  A                           Some symmetric matrix
   *@param  b                           Some vector b
   *@return                             x
   *@exception  NoConvergenceException  if the method failed to converage
   */
  public static double[] solve( final double[][] A, final double[] b ) throws
      NoConvergenceException {
        double tol = 0.00001;int maxiter=500;
    double[] x = UtilMath.copy(b);// best guess
    double[] r = UtilMath.subtract(b,UtilMath.matrixmultiply(A,x));
    double[] d = r;
    double alpha = UtilMath.innerproduct(r,d);
    double init_res = UtilMath.norm(r);
    if(init_res < tol) return x; // case where we already have the solution (hack)
    for(int iter = 0; iter < maxiter; ++iter) {
      double[] s = UtilMath.matrixmultiply(A,d);
      double gamma = UtilMath.innerproduct(d,s);
      double tau =  tau = alpha / gamma;
      UtilMath.addInPlace(x, tau,d);
      UtilMath.addInPlace(r,-tau,s);
      double res_red = UtilMath.norm(r) / init_res;
      //System.out.println("Iter = "+iter+" res_red = "+res_red + " init_res = "+ init_res);
      if (res_red < tol) return x;
      double beta = 1 / alpha;
      double[] z = r;
      alpha = UtilMath.innerproduct(r,z);
      beta = beta*alpha;
      d = UtilMath.add(z, beta,d);
    }
    throw new NoConvergenceException();
  }
}
