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
import cofi.algorithms.*;
import cofi.algorithms.util.*;
import cofi.algorithms.basic.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

/**
 *  An STI version of the memory-based Pearson-like scheme which penalizes frequent voters. MeanSTI schemes
 *  differ from STI scheme in that the scale is defined differently. We user
 *  vector norm/ number of ratings for the MeantSTI ones. For research and non
 *  commercial purposes.
 *
 *
 *  $Id: MeanSTIPearson.java,v 1.4 2003/12/12 14:41:12 lemired Exp $
 *  $Date: 2003/12/12 14:41:12 $
 *  $Author: lemired $
 *  $Revision: 1.4 $
 *  $Log: MeanSTIPearson.java,v $
 *  Revision 1.4  2003/12/12 14:41:12  lemired
 *  factory for more tunable benchmarks
 *
 *  Revision 1.3  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.2  2003/10/28 01:43:08  lemired
 *  Lots of refactoring.
 *
 *  Revision 1.1  2003/10/27 17:21:15  lemired
 *  Putting some order
 *
 *  Revision 1.8  2003/08/22 13:38:23  howsen
 *  *** empty log message ***
 *
 *  Revision 1.7  2003/08/21 18:04:29  lemired
 *  Added toString method plus added necessary activation.jar for convenience.
 *
 *  Revision 1.6  2003/08/07 13:16:05  lemired
 *  More javadoc improvments.
 *
 *  Revision 1.5  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author     Daniel Lemire
 *@since      December 2002
 */
public class MeanSTIPearson
   extends PerItemAverage
{

   float[] mAverages;
   float[] mNorms;
   float mP;
   boolean mPearsonWithCaseAmplification;
   boolean mPearsonWithUserFrequency;
   float mCaseAmplification;

   /**
    *  Constructor for the STIPearson object
    *
    *@param  set  the training set
    *@param  p    which lp norm to user (p=2 is good)
    *@param  CaseAmplification  whether to user case amplification
    */
   public MeanSTIPearson(EvaluationSet set, boolean CaseAmplification, float p)
   {
      super(set);
      mP = p;
      mPearsonWithCaseAmplification = CaseAmplification;
      mPearsonWithUserFrequency = false;
      mCaseAmplification = 2.5f;
      precomputeAveragesAndNorms();

   }

   /**
    *  Constructor for the STIPearson object
    *
    *@param  set  the training set
    *@param  p    which lp norm to user (p=2 is good)
    */
   public MeanSTIPearson(EvaluationSet set, float p)
   {
      this(set, true, p);
   }

   /**
    *  Return an array that contains predictions for the ratings of the given
    *  user. Note that predictions over already rated items don't have to agree
    *  with the provided ratings. This algorithm takes time O(m) where m is the
    *  number of users.
    *
    *@param  u  a set of one-dimensional ratings
    *@return    an array containing predictions
    */
   public float[] completeUser(TIntFloatHashMap u)
   {
      if (u.size() == 0)
      {
   return new float[mMaxItemID];
      }
      float[] complete = sumOfUsers(u);
      float average = UtilMath.average(u);
      float averageIA = UtilMath.average(complete, u);
      TIntFloatIterator iter = u.iterator();
      float energy = 0.0f;
      float product = 0.0f;
      while (iter.hasNext())
      {
   iter.advance();
   energy += (complete[iter.key()] - averageIA) *
      (complete[iter.key()] - averageIA);
   product += (complete[iter.key()] - averageIA) *
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
   solution[k] = average + alpha * (complete[k] - averageIA);
      }
      return solution;
   }

   /**
    *  Compute the Pearson correlation without taking into account
    * the item frequency.
    *
    *@param  u1     first evaluation
    *@param  av1    average of the first evaluation
    *@param  norm1  norm of the first evaluation
    *@param  u2     second evaluation
    *@param  av2    second average
    *@param  norm2  second norm
    *@return        a Pearson correlation-like value
    */
   public float pearsonWithoutUserFrequency(TIntFloatHashMap u1, float av1,
              float norm1, TIntFloatHashMap u2,
              float av2, float norm2)
   {
     if((norm1 ==0.0f) || (norm2 == 0.0f)) return 0.0f;
      int[] ids1 = u1.keys();
      float product = 0.0f;
      int currentindex;
      float placeholder1 = 0.0f;
      float placeholder2 = 0.0f;
      //int number = 0;
      for (int k = 0; k < ids1.length; ++k)
      {
   currentindex = ids1[k];
   if (u2.containsKey(currentindex))
   {
      //number++;
      placeholder1 = (u1.get(currentindex) - av1) / norm1;
      placeholder2 = (u2.get(currentindex) - av2) / norm2;
      product += placeholder1 * placeholder2;
   }
      }
      return product;
   }

   /**
    *  Precompute averages and norms for faster predictions
    */
   private void precomputeAveragesAndNorms()
   {
      int[] userids = mSet.keys();
      mAverages = new float[userids.length];
      mNorms = new float[userids.length];
      for (int k = 0; k < userids.length; ++k)
      {
   TIntFloatHashMap RunningU = (TIntFloatHashMap) mSet.get(userids[k]);
   float av = UtilMath.average(RunningU);
   if(RunningU.size()> 0) {
     float nu = UtilMath.lpnorm(RunningU, av, mP) / RunningU.size();
     mNorms[k] = nu;
   } else mNorms[k] = 0; 
   mAverages[k] = av;
   
      }
   }

   /**
    *  Compute weighted average
    *
    *@param  u  current evaluation
    *@return    weighted average
    */
   public float[] sumOfUsers(TIntFloatHashMap u)
   {
      float[] complete = new float[mMaxItemID];
      int[] userids = mSet.keys();
      float[] weight = new float[userids.length];
      float average = UtilMath.average(u);
      float normu = UtilMath.lpnorm(u, average, mP) / u.size();
      for (int k = 0; k < userids.length; ++k)
      {
   TIntFloatHashMap RunningU = (TIntFloatHashMap) mSet.get(userids[k]);
   float av = mAverages[k];
   float nu = mNorms[k];
   if(nu == 0.0f) return complete;
   weight[k] = pearsonWithoutUserFrequency(u, average, normu, RunningU,
             av, nu);
      }
      if (mPearsonWithCaseAmplification)
      {
   Pearson.caseAmplification(weight, mCaseAmplification);
      }
      float[] ItemAmplitude = new float[mMaxItemID];
      for (int k = 0; k < userids.length; ++k)
      {
   final TIntFloatHashMap runningU = (TIntFloatHashMap) mSet.get(userids[
      k]);
   TIntFloatIterator iter = runningU.iterator();
   while (iter.hasNext())
   {
      iter.advance();
      ItemAmplitude[iter.key()] += Math.abs(weight[k]);
   }
      }
      int CurrentItemIndex;
      for (int k = 0; k < userids.length; ++k)
      {
   final TIntFloatHashMap runningU = (TIntFloatHashMap) mSet.get(userids[
      k]);
   TIntFloatIterator iter = runningU.iterator();
   //float av = UtilMath.average( runningU );
   float av = mAverages[k];
   float nu = mNorms[k];
   float ia;
   while (iter.hasNext())
   {
      iter.advance();
      ia = ItemAmplitude[iter.key()];
      if (ia > 0)
      {

         //complete[iter.key() ] += weight[k] * ( iter.value() - av ) / ia;
         complete[iter.key()] += weight[k] * (iter.value() - av) /
      (ia * nu);
      }
   }
      }
      return complete;
   }

   /*
       *  public void updateUser( TIntFloatHashMap u, int itemNum, float newVal ) {
    *  throw new CollaborativeFilteringException( "Please don't call this method, it isn't implemented yet!" );
    *  }
    */
   public String toString()
   {
      return "MeanSTIPearson_mP=" + mP;
   }
}
