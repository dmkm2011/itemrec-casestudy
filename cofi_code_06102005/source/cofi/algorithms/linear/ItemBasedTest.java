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
public class ItemBasedTest extends TestCase {
  
    ItemBased em;
    
    public ItemBasedTest(String name) {
        super(name);
    }

    public void setUp()  {
      em = new ItemBased (getEvaluationSet(10, 10));
    }

    public void tearDown() {
      em = null;
    }
    
    public void runTest() throws Exception {
        TIntFloatHashMap u = new TIntFloatHashMap();
        for (int i = 0; i < 10; ++i) {
          u.put(i,i+1);
        }
        float[] predict = em.completeUser(u);
        //UtilMath.print(predict);
        for (int i = 0; i < 10; ++i) {
          System.out.println("i "+ i+" predict[i] = "+ predict[i]);
//          assertTrue((u.get(i)-predict[i])< 0.0001);
        }
       
    }
    
    
   
    public static EvaluationSet getEvaluationSet(int users, int keys) {
      EvaluationSet es = new EvaluationSet();
      for( int k = 0; k < users ; ++k) {
        TIntFloatHashMap u = new TIntFloatHashMap();
        for (int i = 0; i < keys; ++i) {
          u.put(i,(k%4)*(i+1));
        }
        es.put(k,u);
      }
      es.setMaxItemID(keys); 
      return es;
   }

    public static void main(String[] s) throws Exception {
      ItemBasedTest owt = new ItemBasedTest("anna");
      owt.setUp();
      owt.runTest();
      owt.tearDown();
    }
}


