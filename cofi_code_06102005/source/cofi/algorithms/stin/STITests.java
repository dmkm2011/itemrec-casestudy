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
package cofi.algorithms.stin;

import java.util.*;
import cofi.data.*;
import gnu.trove.*;
import junit.framework.*;
import cofi.algorithms.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.util.*;
import cofi.algorithms.memorybased.*;

/**
 * This class is very incomplete. We should at least
 * test that schemes which claim to be STI are STI!
 */
public class STITests
   extends TestCase
{

   Average a;
   STINonPersonalized stin;
   STINonPersonalized2steps stin2;
   STINonPersonalizedNsteps stin1n;
   STINonPersonalizedNsteps stin2n;
   STIPearson stip;

   public STITests(String name)
   {
      super(name);
   }

   public void setUp() throws Exception
   {
      a = new Average(getEvaluationSet(5000, 100, 21));
      a.setFallBack(false);
      stin = new STINonPersonalized(getEvaluationSet(5000, 100, 21), 2.0f);
      stin.setFallBack(false);
      stin2 = new STINonPersonalized2steps(getEvaluationSet(5000, 100, 21),
             2.0f);
      stin2.setFallBack(false);
      stin1n = new STINonPersonalizedNsteps(getEvaluationSet(5000, 100, 21),
              2.0f, 1);
      stin1n.setFallBack(false);
      stin2n = new STINonPersonalizedNsteps(getEvaluationSet(5000, 100, 21),
              2.0f, 2);
      stin2n.setFallBack(false);
      stip = new STIPearson(getEvaluationSet(5000, 100, 21), 2.0f);
      stip.setFallBack(false);
   }

   public void tearDown() throws Exception
   {
      a = null;
      stin = null;
      stin2 = null;
      stin1n = null;
      stin2n = null;
      stip = null;
   }

   public void runTest()
   {
      testAverage();
      testUpdate();
   }

   public void testAverage()
   {
      TIntFloatHashMap test = new TIntFloatHashMap();
      test.put(0, 10);
      test.put(10, 10);
      test.put(44, 10);
      // all STI methods should return an array filled with 10's
      assertEquals( (int) Math.round(UtilMath.average(a.completeUser(test),
   test)), 10);
      assertEquals( (int) Math.round(UtilMath.average(stip.completeUser(test),
   test)), 10);
      assertEquals( (int) Math.round(UtilMath.average(stin.completeUser(test),
   test)), 10);
      assertEquals( (int) Math.round(UtilMath.average(stin2.completeUser(test),
   test)), 10);
      assertEquals( (int) Math.round(UtilMath.average(stin1n.completeUser(test),
   test)), 10);
      assertEquals( (int) Math.round(UtilMath.average(stin2n.completeUser(test),
   test)), 10);
      //
      test.put(0, 10);
      test.put(10, 10);
      test.put(44, 10);
      test.put(1, 20);
      test.put(11, 20);
      test.put(45, 20);
      float[] small_a = a.completeUser(test);
      float[] small_stin = stin.completeUser(test);
      float[] small_stin2 = stin2.completeUser(test);
      float[] small_stip = stip.completeUser(test);
      assertEquals( (int) Math.round(UtilMath.average(stin.completeUser(test),
   test)), 15);
      assertEquals( (int) Math.round(UtilMath.average(stin2.completeUser(test),
   test)), 15);
      assertEquals( (int) Math.round(UtilMath.average(stin1n.completeUser(test),
   test)), 15);
      assertEquals( (int) Math.round(UtilMath.average(stin2n.completeUser(test),
   test)), 15);
      assertEquals( (int) Math.round(UtilMath.average(stip.completeUser(test),
   test)), 15);

      // now the average should be 15!
      assertEquals( (int) Math.round(UtilMath.average(a.completeUser(test),
   test)), 15);
      assertEquals( (int) Math.round(UtilMath.average(stin.completeUser(test),
   test)), 15);
      assertEquals( (int) Math.round(UtilMath.average(stin2.completeUser(test),
   test)), 15);
      assertEquals( (int) Math.round(UtilMath.average(stin1n.completeUser(test),
   test)), 15);
      assertEquals( (int) Math.round(UtilMath.average(stin2n.completeUser(test),
   test)), 15);
      assertEquals( (int) Math.round(UtilMath.average(stip.completeUser(test),
   test)), 15);
      //
      test.put(0, 20);
      test.put(10, 20);
      test.put(44, 20);
      test.put(1, 40);
      test.put(11, 40);
      test.put(45, 40);
      float[] big_a = a.completeUser(test);
      float[] big_stin = stin.completeUser(test);
      float[] big_stin2 = stin2.completeUser(test);
      float[] big_stip = stip.completeUser(test);
      // this should make the average 30!
      assertEquals( (int) Math.round(UtilMath.average(a.completeUser(test),
   test)), 30);
      assertEquals( (int) Math.round(UtilMath.average(stin.completeUser(test),
   test)), 30);
      assertEquals( (int) Math.round(UtilMath.average(stin2.completeUser(test),
   test)), 30);
      assertEquals( (int) Math.round(UtilMath.average(stin1n.completeUser(test),
   test)), 30);
      assertEquals( (int) Math.round(UtilMath.average(stin2n.completeUser(test),
   test)), 30);
      assertEquals( (int) Math.round(UtilMath.average(stip.completeUser(test),
   test)), 30);
      // now we check it is at a ratio of 2 throughout
      for (int k = 0; k < small_a.length; ++k)
      {
   assertEquals( (int) Math.round(2 * small_a[k]),
          (int) Math.round(big_a[k]));
   assertEquals( (int) Math.round(2 * small_stin[k]),
          (int) Math.round(big_stin[k]));
   assertEquals( (int) Math.round(2 * small_stip[k]),
          (int) Math.round(big_stip[k]));
   assertEquals( (int) Math.round(2 * small_stin2[k]),
          (int) Math.round(big_stin2[k]));
      }
   }

   /**
    * This test could be far more sophisticated, but I'm tired!
    */
   public void testUpdate()
   {
      TIntFloatHashMap ratings = new TIntFloatHashMap();
      ratings.put(0, 20);
      ratings.put(10, 20);
      ratings.put(44, 20);
      ratings.put(1, 40);
      ratings.put(11, 40);
      ratings.put(45, 40);
      // get some predictions
      float[] a_pred = a.completeUser(ratings);
      float[] stin_pred = stin.completeUser(ratings);
      float[] stin2_pred = stin2.completeUser(ratings);
      float[] stip_pred = stip.completeUser(ratings);

      // now do an intensive loop
      for (int k = 0; k < 500; ++k)
      {
   // we begin by adding it
   a.addedUser(ratings);
   stin.addedUser(ratings);
   stin2.addedUser(ratings);
   stip.addedUser(ratings);
   // and then we remove it!
   a.removedUser(ratings);
   stin.removedUser(ratings);
   stin2.removedUser(ratings);
   stip.removedUser(ratings);
      }
      // get some predictions
      float[] a_pred2 = a.completeUser(ratings);
      float[] stin_pred2 = stin.completeUser(ratings);
      float[] stin2_pred2 = stin2.completeUser(ratings);
      float[] stip_pred2 = stip.completeUser(ratings);
      for (int k = 0; k < a_pred2.length; ++k)
      {
   assertEquals( (int) Math.round(a_pred[k]), (int) Math.round(a_pred2[k]));
   assertEquals( (int) Math.round(stin_pred[k]),
          (int) Math.round(stin_pred2[k]));
   assertEquals( (int) Math.round(stip_pred[k]),
          (int) Math.round(stip_pred2[k]));
   assertEquals(" index  " + k, (int) Math.round(stin2_pred[k]),
          (int) Math.round(stin2_pred2[k]));
      }
   }

   public static EvaluationSet getEvaluationSet(int NumberOfUsers,
            int NumberOfItems,
            int DensityFactor)
   {
      EvaluationSet es = new EvaluationSet();
      TObjectIntHashMap R = new TObjectIntHashMap(new PearsonHashingStrategy());
      HashMap hash = new HashMap();
      for (int i = 0; i < NumberOfItems; ++i)
      {
   int begin = (DensityFactor * i) % NumberOfUsers;
   int end = (DensityFactor * (i + 2)) % NumberOfUsers;
   for (int u = begin; u < end; ++u)
   {
      es.add(u, i, i); //always rate item i with value i
   }
      }
      for (int u = 0; u < NumberOfUsers; ++u)
      {
   es.add(u, u % NumberOfItems, 0);
      }
      es.setMaxItemID(NumberOfItems);
      return es;
   }
}
