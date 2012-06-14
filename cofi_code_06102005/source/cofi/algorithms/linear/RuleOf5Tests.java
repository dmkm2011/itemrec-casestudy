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
package cofi.algorithms.linear;

import junit.framework.*;
import cofi.data.*;
import cofi.algorithms.util.*;
import gnu.trove.*;
import java.util.*;

/**
*/
public class RuleOf5Tests extends TestCase {
  
    RuleOf5 em, embig;
    
    public RuleOf5Tests(String name) {
        super(name);
    }

    public void setUp()  {
      em = new RuleOf5 (getEvaluationSet(10, 10));
      //float[][] weights = em.getWeight();
      //for(int k = 0 ; k < weights.length; ++k)
        //UtilMath.print(weights[k]);
//      em2 = new Anna (getEvaluationSet(30, 30),2);
      embig = new RuleOf5 (getEvaluationSet(2000, 605));
      //emreallybig = new Anna (getEvaluationSet(10000, 2000));
    }

    public void tearDown() {
      em = null;
      embig = null;
     
    }
    
    public void runTest() throws Exception {
    }
    
    
    public static float f(int k ) {
      return (float) Math.sin(k);
    }
    
    public static EvaluationSet getEvaluationSet(int users, int keys) {
      EvaluationSet es = new EvaluationSet();
      for( int k = 0; k < users ; ++k) {
        TIntFloatHashMap u = new TIntFloatHashMap();
        //System.out.println("k = "+k);
        for (int i = 0; i < keys; ++i)
          if( ( 3*k/2 + 2*i) % 11 == 0)
            u.put(i,(k % 4 + 1)* f(i));
        //UtilMath.print(u);
        es.put(k,u);
      }
      TIntFloatHashMap u = new TIntFloatHashMap();
      for (int i = 0; i < keys; ++i)
        u.put(i,f(i));
      //UtilMath.print(u);
      es.put(users,u);
      es.setMaxItemID(keys);
      return es;
    }

    public static void main(String[] s) throws Exception {
      RuleOf5Tests owt = new RuleOf5Tests("anna");
      owt.setUp();
      owt.tearDown();
    }
}


