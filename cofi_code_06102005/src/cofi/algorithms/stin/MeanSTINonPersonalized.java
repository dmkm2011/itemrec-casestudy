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
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

/**
 *  The first order O(1) STI CFS which penalizes frequent voters. MeanSTI schemes differ from STI scheme in that
    *  the scale is defined differently. We user vector norm/ number of ratings for
 *  the MeantSTI ones. For research and non commercial purposes.
 *
 *
 *  $Id: MeanSTINonPersonalized.java,v 1.3 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $
 *  $Author: lemired $
 *  $Revision: 1.3 $
 *  $Log: MeanSTINonPersonalized.java,v $
 *  Revision 1.3  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.2  2003/10/28 01:43:08  lemired
 *  Lots of refactoring.
 *
 *  Revision 1.1  2003/10/27 17:21:15  lemired
 *  Putting some order
 *
 *  Revision 1.9  2003/08/22 13:38:23  howsen
 *  *** empty log message ***
 *
 *  Revision 1.8  2003/08/21 18:04:29  lemired
 *  Added toString method plus added necessary activation.jar for convenience.
 *
 *  Revision 1.7  2003/08/07 13:16:05  lemired
 *  More javadoc improvments.
 *
 *  Revision 1.6  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author     Daniel Lemire
 *@since      December 2002
 */
public class MeanSTINonPersonalized
   extends PerItemAverage
{

   float mP;
   float[] mItemAverageWithoutMean;

   /**
    *  Constructor for the MeanSTINonPersonalized object
    *
    *@param  set the training set
    *@param  p which lp norm to use (p=2 is good)
    */
   public MeanSTINonPersonalized(EvaluationSet set, float p)
   {
      super(set);
      mP = p;
      computeItemAverage();
   }

   /**
    *  Computer per item average
    */
   protected void computeItemAverage()
   {
      int[] ItemFrequency = new int[mMaxItemID];
      mItemAverageWithoutMean = new float[mMaxItemID];
      TIntObjectIterator t = mSet.iterator();
      int TotalNumber = 0;
      while (t.hasNext())
      {
   t.advance();
   ++TotalNumber;
   TIntFloatHashMap RunningU = (TIntFloatHashMap) t.value();
   float average = UtilMath.average(RunningU);
   float linf = UtilMath.lpnorm(RunningU, average, mP);
   if (linf > UtilMath.epsilon)
   {
      TIntFloatIterator uiter = (RunningU).iterator();
      while (uiter.hasNext())
      {
         uiter.advance();
         ItemFrequency[uiter.key()] += 1;
         mItemAverageWithoutMean[uiter.key()] += (uiter.value() - average) *
      RunningU.size() / linf;
      }
   }
      }
      for (int k = 0; k < mItemAverageWithoutMean.length; ++k)
      {
   if (ItemFrequency[k] > 0)
   {
      mItemAverageWithoutMean[k] /= ItemFrequency[k];

   }
      }
   }

   /**
    *  Return an array that contains predictions for the ratings of the given
    *  user. Note that predictions over already rated items don't have to agree
       *  with the provided ratings. This algorithm takes time O(1) with respect to
    *  the number of users.
    *
    *@param  u  a set of one-dimensional ratings
    *@return    an array containing predictions
    */
   public float[] completeUser(TIntFloatHashMap u)
   {
      float average = UtilMath.average(u);
      if (false)
      {
   System.out.println(" [STINonPersonalized][completeUser]average = " +
          average);
      }
      float averageIA = UtilMath.average(mItemAverageWithoutMean, u);
      TIntFloatIterator iter = u.iterator();
      float energy = 0.0f;
      float product = 0.0f;
      while (iter.hasNext())
      {
   iter.advance();
   energy += (mItemAverageWithoutMean[iter.key()] - averageIA) *
      (mItemAverageWithoutMean[iter.key()] - averageIA);
   product += (mItemAverageWithoutMean[iter.key()] - averageIA) *
      (iter.value() - average);
      }
      float alpha = 0.0f;
      if (energy > 0.0f)
      {
   alpha = product / energy;
      }
      float[] solution = new float[mMaxItemID];
      for (int k = 0; k < mMaxItemID; ++k)
      {
   solution[k] = average +
      alpha * (mItemAverageWithoutMean[k] - averageIA);
      }
      return solution;
   }

   public String toString()
   {
      return "MeanSTINonPersonalized_mP=" + mP;
   }

}
