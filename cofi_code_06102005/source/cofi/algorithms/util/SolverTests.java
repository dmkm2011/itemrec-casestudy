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
package cofi.algorithms.util;

import junit.framework.*;
import cofi.data.*;
import cofi.algorithms.util.*;
import gnu.trove.*;
import java.util.*;

/**
*/
public class SolverTests extends TestCase {
  
    double[][] Asmall, Abig;
    double[] Bsmall, Bbig;
    
    public SolverTests(String name) {
        super(name);
    }

    public void setUp()  {
      Asmall = new double[10][10];
      Bsmall = new double[10];
      Abig = new double[1000][1000];
      Bbig = new double[1000];
    }

    public void tearDown() {
      Asmall = null;
      Bsmall = null;
      Abig = null;
      Bbig = null;
    }
    
    public void runTest() throws Exception {
      testSmall();
      testBig();
    }
    
    public void testSmall() throws NoConvergenceException {
      for(int k = 0; k < Asmall.length;++k) {
        Asmall[k][k] = 1.0;
        Bsmall[k] = k;
      }
      for(int k = 0; k < Asmall.length;++k) {
        for(int l = 0; l < Asmall.length;++l) {
          Asmall[k][l] += k * l /1000.0;
        }
      }
      UtilMath.print(Minres.solve(Asmall, Bsmall));
      UtilMath.print(SymmLQ.solve(Asmall,Bsmall));
      UtilMath.print(ConjugateGradient.solve(Asmall,Bsmall));
      UtilMath.print(JSciSolver.solve(Asmall,Bsmall));
    }
    
    
    public void testBig() throws NoConvergenceException {
      for(int k = 0; k < Abig.length;++k) {
        Abig[k][k] = 0.1;
        Bbig[k] = k;
      }
      for(int k = 0; k < Abig.length;++k) {
        for(int l = 0; l < Abig.length;++l) {
          Abig[k][l] += k * l /1000.0;
        }
      }
      for(int k = 0; k < Abig.length;++k) {
        for(int l = 0; l < Abig.length;++l) {
          Abig[k][l] += (k % 3) * (l % 3) /10.0;
        }
      }
      long before, after;
      before = System.currentTimeMillis();
      Minres.solve(Abig, Bbig);
      after = System.currentTimeMillis();
      System.out.println("Minres took "+(after-before) / 1000.0 +" seconds");
      before = System.currentTimeMillis();
      SymmLQ.solve(Abig,Bbig);
      after = System.currentTimeMillis();
      System.out.println("Symmlq took "+(after-before) / 1000.0 +" seconds");
      before = System.currentTimeMillis();
      ConjugateGradient.solve(Abig,Bbig);
      after = System.currentTimeMillis();
      System.out.println("ConjugateGradient took "+(after-before) / 1000.0 +" seconds");
      before = System.currentTimeMillis();
      JSciSolver.solve(Abig,Bbig);
      after = System.currentTimeMillis();
      System.out.println("LU Decomposition took "+(after-before) / 1000.0 +" seconds");
    }
    

    public static void main(String[] s) throws Exception {
      SolverTests owt = new SolverTests("solvers");
      owt.setUp();
      owt.runTest();
      owt.tearDown();
    }
}
