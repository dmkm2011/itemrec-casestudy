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

package cofi.algorithms;

import cofi.data.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.stin.*;
import cofi.algorithms.memorybased.*;
import gnu.trove.*;
import junit.framework.*;

/**
 * This is unit testing to make sure none of the
 * schemes are allowed to return NaN.
 */
public class NaNTests
   extends TestCase
{

   CollaborativeFilteringSystem[] cfs;

   public NaNTests(String name)
   {
      super(name);
   }

   public void setUp() throws Exception
   {
      CollaborativeFilteringSystem[] tmp =
   {
   new Average(getEvaluationSet(5000, 601, 5)),
   //new OptimalWeight(getEvaluationSet(5000, 601, 5)),
   new PerItemAverage(getEvaluationSet(5000, 601, 5)),
   new Pearson(getEvaluationSet(5000, 601, 5)),
   new STINonPersonalized(getEvaluationSet(5000, 601, 5), 2.0f),
   new STINonPersonalized2steps(getEvaluationSet(5000, 601, 5), 2.0f),
   new STINonPersonalizedNsteps(getEvaluationSet(5000, 601, 5), 2.0f, 1),
   new STINonPersonalizedNsteps(getEvaluationSet(5000, 601, 5), 2.0f, 2),
   new MeanSTINonPersonalized(getEvaluationSet(5000, 601, 5), 2.0f),
   new MeanSTINonPersonalized2steps(getEvaluationSet(5000, 601, 5), 2.0f),
   new STIPearson(getEvaluationSet(5000, 601, 5), 2.0f)
      };
      cfs = tmp;
      for (int i = 0; i < cfs.length; ++i)
      {
   cfs[i].setFallBack(false);
      }
   }

   public void tearDown() throws Exception
   {
      cfs = null;
   }

   public void runTest()
   {
      testNaN();

   }

   public void testNaN()
   {
      for (int i = 0; i < cfs.length; ++i)
      {
   {
      float[] predit = cfs[i].completeUser(new TIntFloatHashMap());
      for (int k = 0; k < predit.length; ++k)
      {
         assertTrue(!Float.isNaN(predit[k]));
      }
   }
   {
      TIntFloatHashMap usr = new TIntFloatHashMap();
      usr.put(1, 1);
      float[] predit = cfs[i].completeUser(usr);
      for (int k = 0; k < predit.length; ++k)
      {
         assertTrue(!Float.isNaN(predit[k]));
      }
   }
      }
   }

   public static EvaluationSet getEvaluationSet(int NumberOfUsers,
            int NumberOfItems,
            int DensityFactor)
   {
      EvaluationSet es = new EvaluationSet();
      for (int i = 0; i < Math.max(NumberOfItems / 2, NumberOfUsers / 2); ++i)
      {
   TIntFloatHashMap user = new TIntFloatHashMap();
   user.put(i % NumberOfItems, (NumberOfItems - i));
   es.put(i, user);
      }
      for (int i = Math.max(NumberOfItems / 2, NumberOfUsers / 2);
     i < NumberOfUsers; ++i)
      {
   es.put(i, new TIntFloatHashMap());
      }
      es.setMaxItemID(NumberOfItems);
      return es;
   }
}
