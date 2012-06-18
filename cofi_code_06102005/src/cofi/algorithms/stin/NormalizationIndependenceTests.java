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

import cofi.data.*;
import cofi.algorithms.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.util.*;
import cofi.algorithms.memorybased.*;
import gnu.trove.map.hash.TIntFloatHashMap;
import junit.framework.*;

public class NormalizationIndependenceTests
   extends TestCase
{

   CollaborativeFilteringSystem[] cfs1;
   CollaborativeFilteringSystem[] cfs2;

   public NormalizationIndependenceTests(String name)
   {
      super(name);
   }

   public void setUp() throws Exception
   {
      CollaborativeFilteringSystem[] tmp =
   {
   new Average(getEvaluationSet(1000, 101, 5)),
   new PerItemAverage(getEvaluationSet(1000, 101, 5)),
   new MeanSTINonPersonalized(getEvaluationSet(1000, 101, 5), 2.0f),
   new MeanSTINonPersonalized2steps(getEvaluationSet(1000, 101, 5), 2.0f),
   //new OptimalWeight(getEvaluationSet(1000, 601, 5)),
   new STINonPersonalized(getEvaluationSet(1000, 101, 5), 2.0f),
   new STINonPersonalized2steps(getEvaluationSet(1000, 101, 5), 2.0f),
   new STINonPersonalizedNsteps(getEvaluationSet(1000, 101, 5), 2.0f, 1),
   new Pearson(getEvaluationSet(1000, 101, 5)),
   new STIPearson(getEvaluationSet(1000, 101, 5), 2.0f),
   new STINonPersonalizedNsteps(getEvaluationSet(1000, 101, 5), 2.0f, 2)
      };
      cfs1 = tmp;
      CollaborativeFilteringSystem[] tmp2 =
   {
   new Average(getEvaluationSet2(1000, 101, 5)),
   new PerItemAverage(getEvaluationSet2(1000, 101, 5)),
   new MeanSTINonPersonalized(getEvaluationSet2(1000, 101, 5), 2.0f),
   new MeanSTINonPersonalized2steps(getEvaluationSet2(1000, 101, 5), 2.0f),
   //new OptimalWeight(getEvaluationSet2(5000, 601, 5)),
   new STINonPersonalized(getEvaluationSet2(1000, 101, 5), 2.0f),
   new STINonPersonalized2steps(getEvaluationSet2(1000, 101, 5), 2.0f),
   new STINonPersonalizedNsteps(getEvaluationSet2(1000, 101, 5), 2.0f, 1),
   new Pearson(getEvaluationSet2(1000, 101, 5)),
   new STIPearson(getEvaluationSet2(1000, 101, 5), 2.0f),
   new STINonPersonalizedNsteps(getEvaluationSet2(1000, 101, 5), 2.0f, 2)
      };
      cfs2 = tmp2;
      for (int i = 0; i < cfs1.length; ++i)
      {
   cfs1[i].setFallBack(false);
   cfs2[i].setFallBack(false);
      }
   }

   public void tearDown() throws Exception
   {
      cfs1 = null;
      cfs2 = null;
   }

   public void runTest()
   {
      testNormalization();

   }

   public void testNormalization()
   {
      for (int i = 0; i < cfs1.length; ++i)
      {
   TIntFloatHashMap usr = new TIntFloatHashMap();
   usr.put(1, 1);
   usr.put(10, 2);
   float[] predit1 = cfs1[i].completeUser(usr);
   TIntFloatHashMap usr2 = new TIntFloatHashMap();
   usr2.put(1, 3 * 1 + 10);
   usr2.put(10, 3 * 2 + 10);
   float[] predit2 = cfs2[i].completeUser(usr2);
   for (int k = 0; k < predit1.length; ++k)
   {
      assertTrue("testing algo " + i + " pos " + k + " predit1[k] = " +
           predit1[k] + " predit2[k]=" + predit2[k],
           Math.abs(3 * predit1[k] + 10 - predit2[k]) <
           UtilMath.epsilon);
   }
      }
   }

   public static EvaluationSet getEvaluationSet(int NumberOfUsers,
            int NumberOfItems,
            int DensityFactor)
   {
      EvaluationSet es = new EvaluationSet();
      for (int i = 0; i < NumberOfItems; ++i)
      {
   int begin = (DensityFactor * i) % NumberOfUsers;
   int end = (DensityFactor * (i + 2)) % NumberOfUsers;
   for (int u = begin; u < end; ++u)
   {
      es.add(u, i, i); //always rate item i with value i
   }
   es.add(0, i, i); // make sure item is rated
   es.add(2, i, i); // make sure item is rated
   es.add(13, i, i); // make sure item is rated
      }
      es.setMaxItemID(NumberOfItems);
      return es;
   }

   public static EvaluationSet getEvaluationSet2(int NumberOfUsers,
             int NumberOfItems,
             int DensityFactor)
   {
      EvaluationSet es = new EvaluationSet();
      for (int i = 0; i < NumberOfItems; ++i)
      {
   int begin = (DensityFactor * i) % NumberOfUsers;
   int end = (DensityFactor * (i + 2)) % NumberOfUsers;
   for (int u = begin; u < end; ++u)
   {
      es.add(u, i, 3 * i + 10); //always rate item i with value i
   }
   es.add(0, i, 3 * i + 10); // make sure item is rated
   es.add(2, i, 3 * i + 10); // make sure item is rated
   es.add(13, i, 3 * i + 10); // make sure item is rated
      }
      es.setMaxItemID(NumberOfItems);
      return es;
   }
}
