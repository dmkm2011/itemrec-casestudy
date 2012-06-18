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
package cofi.algorithms.memorybased;

import cofi.data.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;
import junit.framework.*;
import cofi.algorithms.util.*;

public class MemoryBasedTests
   extends TestCase
{

   Pearson p;
   STIPearson stip;

   public MemoryBasedTests(String name)
   {
      super(name);
   }

   public void setUp() throws Exception
   {
      stip = new STIPearson(getEvaluationSet(1000, 601, 5), 2.0f);
      p = new Pearson(getEvaluationSet(1000, 601, 5));
   }

   public void tearDown() throws Exception
   {
      p = null;
      stip = null;
   }

   public void runTest()
   {
      testKnownOutput();
   }

   public void testKnownOutput()
   {
      {
   TIntFloatHashMap usr = new TIntFloatHashMap();
   float average = 0.0f;
   for (int i = 0; i < 601; ++i)
   {
      average += i / (float) 601;
   }
   for (int k = 0; k < 10; ++k)
   {
      usr.put(k, k - 4.5f);
   }
   if (UtilMath.average(usr) != 0.0f)
   {
      throw new RuntimeException("bug " + UtilMath.average(usr));
   }
   float[] ppredict = stip.completeUser(usr);
   for (int k = 0; k < ppredict.length; ++k)
   {
      assertTrue(" ppredict = " + ppredict[k] + "  k = " + k + " r = " +
           (ppredict[k] - k + 4.5),
           Math.abs(ppredict[k] - k + 4.5) < 0.1);
   }
      }
      { // it appears like Pearson is not first order exact!
   TIntFloatHashMap usr = new TIntFloatHashMap();
   float average = 0.0f;
   for (int i = 0; i < 601; ++i)
   {
      average += i / (float) 601;
   }
   for (int k = 0; k < 10; ++k)
   {
      usr.put(k, k - 4.5f);
   }
   if (UtilMath.average(usr) != 0.0f)
   {
      throw new RuntimeException("bug " + UtilMath.average(usr));
   }
   float[] ppredict = p.completeUser(usr);
   for (int k = 0; k < ppredict.length; ++k)
   {
      assertTrue(" ppredict = " + ppredict[k] + "  k = " + k +
           "  ppredict[k] - k + average = " +
           (ppredict[k] - k + average),
           Math.abs(ppredict[k] - k + average) < 0.1);
      //assertEquals((int)Math.round(ppredict[k]),(int)Math.round(k - average));
   }
      }
   }

   public static EvaluationSet getEvaluationSet(int NumberOfUsers,
            int NumberOfItems,
            int DensityFactor)
   {
      // very simple, made of one user repeated again and
      // again...
      EvaluationSet es = new EvaluationSet();
      float average = 0.0f;
      for (int i = 0; i < NumberOfItems; ++i)
      {
   average += i / (float) NumberOfItems;
      }
      for (int i = 0; i < NumberOfItems; ++i)
      {
   for (int k = 0; k < NumberOfUsers; ++k)
   {
      es.add(k, i, i - average);
   }
      }
      if (UtilMath.average( (TIntFloatHashMap) es.get(0)) != 0.0f)
      {
   throw new RuntimeException("bug");
      }
      es.setMaxItemID(NumberOfItems);
      return es;
   }
}
