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
import gnu.trove.*;
import cofi.algorithms.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.util.*;

/**
 *  The first order O(1) STI CFS.
 *
 *
 *  $Id: STINonPersonalized.java,v 1.3 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $
 *  $Author: lemired $
 *  $Revision: 1.3 $
 *  $Log: STINonPersonalized.java,v $
 *  Revision 1.3  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.2  2003/10/28 01:43:08  lemired
 *  Lots of refactoring.
 *
 *  Revision 1.1  2003/10/27 17:21:15  lemired
 *  Putting some order
 *
 *  Revision 1.22  2003/08/22 13:38:23  howsen
 *  *** empty log message ***
 *
 *  Revision 1.21  2003/08/21 18:04:29  lemired
 *  Added toString method plus added necessary activation.jar for convenience.
 *
 *  Revision 1.20  2003/08/08 03:23:22  lemired
 *  addedUser/removedUser was broken in most implementation. I fixed that now.
 *
 *  Revision 1.19  2003/08/07 19:23:34  howsen
 *  *** empty log message ***
 *
 *  Revision 1.18  2003/08/07 15:31:01  lemired
 *   This should fix the problem reported by Marcel.
 *
 *  Revision 1.17  2003/08/07 13:16:05  lemired
 *  More javadoc improvments.
 *
 *  Revision 1.16  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author     Daniel Lemire
 *@since      December 2002
 */

public class STINonPersonalized
   extends PerItemAverage
{

   float mP;
   float[] mItemAverageWithoutMean;
   int[] mItemFrequency;

   /**
    *  Constructor for the STINonPersonalized object
    *
    *@param  set  the training set
    *@param  p    which lp norm to user (p=2 is good)
    */
   public STINonPersonalized(EvaluationSet set, float p)
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
      mItemFrequency = new int[mMaxItemID];
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
         ++mItemFrequency[uiter.key()];
         mItemAverageWithoutMean[uiter.key()] += (uiter.value() - average) /
      linf;
      }
   }
      }
      for (int k = 0; k < mItemAverageWithoutMean.length; ++k)
      {
   if (mItemFrequency[k] > 0)
   {
      mItemAverageWithoutMean[k] /= mItemFrequency[k];
   }
      }
   }

   /**
    *  Return an array that contains predictions for the ratings of the given
    *  user. Note that predictions over already rated items don't have to agree
       *  with the provided ratings. This algorithm takes time O(1) with respect to
    *  the number of users. This method automatically falls back on
    *  per item average when not enough information about the user is known.
    *
    *@param  u  a set of one-dimensional ratings
    *@return    an array containing predictions
    */
   public float[] completeUser(TIntFloatHashMap u)
   {
      return completeUser(u, getFallBack());
   }

   /**
    *  Return an array that contains predictions for the ratings of the given
    *  user. Note that predictions over already rated items don't have to agree
       *  with the provided ratings. This algorithm takes time O(1) with respect to
    *  the number of users.
    *
    *@param  u  a set of one-dimensional ratings
    *@param fallback whether you can fall back on per item average
    *@return    an array containing predictions
    */
   public float[] completeUser(TIntFloatHashMap u, boolean fallback)
   {
      if (u.size() < 2)
      {
   return super.completeUser(u);
      }
      float[] solution = new float[mMaxItemID];
      float average = UtilMath.average(u);
      float averageIA = UtilMath.average(mItemAverageWithoutMean, u);
      TIntFloatIterator iter = u.iterator();
      float energy = 0.0f;
      float product = 0.0f;
      while (iter.hasNext())
      {
   iter.advance();
   energy += (mItemAverageWithoutMean[iter.key()] - averageIA)
      * (mItemAverageWithoutMean[iter.key()] - averageIA);
   product += (mItemAverageWithoutMean[iter.key()] - averageIA)
      * (iter.value() - average);
      }
      float alpha = 0.0f;
      if (energy > 0.0f)
      {
   alpha = product / energy;
      }
      if ( (Math.abs(alpha) < UtilMath.epsilon) && fallback)
      {
   return super.completeUser(u);
      }
      for (int k = 0; k < mMaxItemID; ++k)
      {
   solution[k] = average +
      alpha * (mItemAverageWithoutMean[k] - averageIA);
      }
      return solution;
   }

   /**
    *  This must called after you remove a user
    *
    *@param  u  evaluation which was removed
    */
   public void removedUser(TIntFloatHashMap u)
   {
      float average = UtilMath.average(u);
      final float oldlinf = UtilMath.lpnorm(u, average, mP);
      if (oldlinf > UtilMath.epsilon)
      {
   TIntFloatIterator uiter = u.iterator();
   while (uiter.hasNext())
   {
      uiter.advance();
      if (mItemFrequency[uiter.key()] == 0)
      {
         throw new CollaborativeFilteringException(
      "Item Frequency is wrong!!!! -----**********");
      }
      mItemAverageWithoutMean[uiter.key()]
         -= (uiter.value() - average) /
         (mItemFrequency[uiter.key()] * oldlinf);
      --mItemFrequency[uiter.key()];
      if (mItemFrequency[uiter.key()] > 0)
      {
         mItemAverageWithoutMean[uiter.key()] *=
      ( (float) mItemFrequency[uiter.key()] + 1) /
      ( (float) mItemFrequency[uiter.key()]);
      }
   }
      }
      super.removedUser(u);
   }

   /**
    *  This must called after you add a user
    *
    *@param  u  evaluation which was added
    */
   public void addedUser(TIntFloatHashMap u)
   {
      super.addedUser(u);
      float average = UtilMath.average(u);
      final float newlinf = UtilMath.lpnorm(u, average, mP);
      if (newlinf > UtilMath.epsilon)
      {
   TIntFloatIterator uCloneiter = u.iterator();
   while (uCloneiter.hasNext())
   {
      uCloneiter.advance();
      ++mItemFrequency[uCloneiter.key()];
      mItemAverageWithoutMean[uCloneiter.key()] *=
         ( (float) mItemFrequency[uCloneiter.key()] - 1) /
         ( (float) mItemFrequency[uCloneiter.key()]);
      mItemAverageWithoutMean[uCloneiter.key()]
         += (uCloneiter.value() - average) /
         (mItemFrequency[uCloneiter.key()] * newlinf);
   }
      }
   }

   public String toString()
   {
      return "STINonPersonalized_mP=" + mP;
   }

}
