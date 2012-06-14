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
import cofi.algorithms.util.*;

/**
 *  The second order O(1) STI CFS which penalizes frequent voters. MeanSTI schemes differ from STI scheme in
 *  that the scale is defined differently. We user vector norm/ number of
 *  ratings for the MeantSTI ones. For research and non commercial purposes.
 *
 *
 *  $Id: MeanSTINonPersonalized2steps.java,v 1.3 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $
 *  $Author: lemired $
 *  $Revision: 1.3 $
 *  $Log: MeanSTINonPersonalized2steps.java,v $
 *  Revision 1.3  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.2  2003/10/28 01:43:08  lemired
 *  Lots of refactoring.
 *
 *  Revision 1.1  2003/10/27 17:21:15  lemired
 *  Putting some order
 *
 *  Revision 1.12  2003/08/22 13:38:23  howsen
 *  *** empty log message ***
 *
 *  Revision 1.11  2003/08/21 18:04:29  lemired
 *  Added toString method plus added necessary activation.jar for convenience.
 *
 *  Revision 1.10  2003/08/08 03:23:22  lemired
 *  addedUser/removedUser was broken in most implementation. I fixed that now.
 *
 *  Revision 1.9  2003/08/07 15:46:53  lemired
 *  Ok. I had the wrong file there. This should compile.
 *
 *  Revision 1.8  2003/08/07 15:31:01  lemired
 *   This should fix the problem reported by Marcel.
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
public class MeanSTINonPersonalized2steps
   extends MeanSTINonPersonalized
{

   double[] mItemAverageSecondOrder;

   /**
    *  Constructor for the STINonPersonalized2steps object
    *
    *@param  set  the training set
    *@param  p    which lp norm to user (p=2 is good)
    */
   public MeanSTINonPersonalized2steps(EvaluationSet set, float p)
   {
      super(set, p);
      computeSecondOrderItemAverage();
   }

   /**
    *  Compute the second order per item average
    */
   protected void computeSecondOrderItemAverage()
   {
      int[] ItemFrequency = new int[mMaxItemID];
      mItemAverageSecondOrder = new double[mMaxItemID];
      TIntObjectIterator t = mSet.iterator();
      int TotalNumber = 0;
      while (t.hasNext())
      {
   t.advance();
   ++TotalNumber;
   TIntFloatHashMap RunningU = (TIntFloatHashMap) ( (TIntFloatHashMap) t.
      value()).clone();
   float average = UtilMath.average(RunningU);
   float[] completed = super.completeUser(RunningU);
   float linf = UtilMath.lpdiff(RunningU, completed, mP);
   linf = UtilMath.lpnorm(RunningU, average, 2);
   if (linf > UtilMath.epsilon)
   {
      TIntFloatIterator uiter = RunningU.iterator();
      while (uiter.hasNext())
      {
         uiter.advance();
         ItemFrequency[uiter.key()] += 1;
         mItemAverageSecondOrder[uiter.key()] +=
      (uiter.value() - completed[uiter.key()]) * RunningU.size() /
      linf;
      }
   }
      }
      for (int k = 0; k < mItemAverageSecondOrder.length; ++k)
      {
   if (ItemFrequency[k] > 0)
   {
      mItemAverageSecondOrder[k] /= ItemFrequency[k];
   }
   else
   {
      mItemAverageSecondOrder[k] = 0.0f;

   }
      }
   }

   /**
    *  Return an array that contains predictions for the ratings of the given
    *  user. Note that predictions over already rated items don't have to agree
       *  with the provided ratings. This algorithm takes time O(1) with respect to
    *  the number of users.
    *
    * This implementation doesn't have a fallback on PerItem Average.
    *
    *@param  u  a set of one-dimensional ratings
    *@return    an array containing predictions
    */
   public float[] completeUser(TIntFloatHashMap u)
   {
      /*
    *  I would need to double check this code because it was written hastily.
       */
      float[] completed = super.completeUser(u);
      TIntFloatHashMap RunningU = (TIntFloatHashMap) u.clone();
      TIntFloatIterator uiter = (RunningU).iterator();
      while (uiter.hasNext())
      {
   uiter.advance();
   uiter.setValue(uiter.value() - completed[uiter.key()]);
      }
      float averageIA = (float) UtilMath.average(mItemAverageSecondOrder, u);
      float averageIAold = UtilMath.average(mItemAverageWithoutMean, u);

      float enerIA = 0.0f;
      float productIA = 0.0f;
      TIntFloatIterator iter = RunningU.iterator();
      while (iter.hasNext())
      {
   iter.advance();
   enerIA += (mItemAverageWithoutMean[iter.key()] - averageIAold) *
      (mItemAverageWithoutMean[iter.key()] - averageIAold);
   productIA += (mItemAverageWithoutMean[iter.key()] - averageIAold) *
      (mItemAverageSecondOrder[iter.key()] - averageIA);
      }
      float a = 0.0f;
      if (enerIA > 0.0f)
      {
   a = productIA / enerIA;
      }
      float[] newmatch = new float[mMaxItemID];
      for (int k = 0; k < mMaxItemID; ++k)
      {
   newmatch[k] = (float) mItemAverageSecondOrder[k] - averageIA -
      a * (mItemAverageWithoutMean[k] - averageIAold);
      }
      iter = RunningU.iterator();
      float energy = 0.0f;
      float product = 0.0f;
      while (iter.hasNext())
      {
   iter.advance();
   energy += newmatch[iter.key()] * newmatch[iter.key()];
   product += newmatch[iter.key()] * iter.value();
      }
      float alpha = 0.0f;
      if (energy > 0.0f)
      {
   alpha = product / energy;
      }
      float[] solution = new float[mMaxItemID];
      for (int k = 0; k < mMaxItemID; ++k)
      {
   solution[k] = completed[k] + (float) (alpha * newmatch[k]);
      }
      return solution;
   }

   public String toString()
   {
      return "MeanSTINonPersonalized2steps_mP=" + mP;
   }

   /*
       *  public void updateUser( TIntFloatHashMap u, int itemNum, float newVal ) {
    *  throw new CollaborativeFilteringException( "Please don't call this method, it isn't implemented yet!" );
    *  }
    */
}
