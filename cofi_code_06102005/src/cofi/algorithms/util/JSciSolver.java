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


import JSci.maths.*;
import JSci.maths.matrices.*;
import JSci.maths.vectors.*;


/**
 *  Borrowing LU-Based solver from JSci 
 *  $Id: JSciSolver.java,v 1.2 2004/04/22 14:55:52 lemire Exp $ $Date: 2004/04/22 14:55:52 $ $Revision: 1.2 $ $Log: JSciSolver.java,v $
 *  $Id: JSciSolver.java,v 1.1 2003/12/08 15:37:42 lemired Exp $ $Date: 2004/04/22 14:55:52 $ $Revision: 1.2 $ Revision 1.2  2004/04/22 14:55:52  lemire
 *  $Id: JSciSolver.java,v 1.1 2003/12/08 15:37:42 lemired Exp $ $Date: 2004/04/22 14:55:52 $ $Revision: 1.2 $ The compileit script now runs under cygwin
 *  $Id: JSciSolver.java,v 1.1 2003/12/08 15:37:42 lemired Exp $ $Date: 2004/04/22 14:55:52 $ $Revision: 1.2 $
 *  $Id: JSciSolver.java,v 1.2 2004/04/22 14:55:52 lemire Exp $ $Date: 2004/04/22 14:55:52 $ $Revision: 1.2 $ Revision 1.1  2003/12/08 15:37:42  lemired
 *  $Id: JSciSolver.java,v 1.2 2004/04/22 14:55:52 lemire Exp $ $Date: 2004/04/22 14:55:52 $ $Revision: 1.2 $ Forgot to add those earlier
 *  $Id: JSciSolver.java,v 1.2 2004/04/22 14:55:52 lemire Exp $ $Date: 2004/04/22 14:55:52 $ $Revision: 1.2 $
 */
public class JSciSolver {
      public static double[] solve(double[][] A, double[] B) { 
        DoubleSquareMatrix DSQ = new DoubleSquareMatrix(A);
        DoubleVector DV = new DoubleVector(B);
        AbstractDoubleVector Answer = LinearMath.solve(DSQ,DV);
        return JSci.util.VectorToolkit.toArray(Answer);
       }
}
