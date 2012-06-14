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
import gnu.trove.*;
import cofi.algorithms.*;
import cofi.algorithms.util.*;
import cofi.algorithms.basic.*;

/**
 *  An STI version of the memory-based Pearson-like scheme.
 *
 *
 *  $Id: STIPearson.java,v 1.3 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $
 *  $Author: lemired $
 *  $Revision: 1.3 $
 *  $Log: STIPearson.java,v $
 *  Revision 1.3  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.2  2003/10/28 01:43:08  lemired
 *  Lots of refactoring.
 *
 *  Revision 1.1  2003/10/27 17:21:15  lemired
 *  Putting some order
 *
 *  Revision 1.20  2003/08/22 13:38:23  howsen
 *  *** empty log message ***
 *
 *  Revision 1.19  2003/08/21 18:04:29  lemired
 *  Added toString method plus added necessary activation.jar for convenience.
 *
 *  Revision 1.18  2003/08/12 11:52:11  lemired
 *  Added more regression testing.
 *
 *  Revision 1.17  2003/08/08 23:56:33  lemired
 *  Made PredictRatings and STIPearson more paranoid. Fixed a bug in STIPearson where
 *  a NaN was returned (again).
 *
 *  Revision 1.16  2003/08/08 03:23:22  lemired
 *  addedUser/removedUser was broken in most implementation. I fixed that now.
 *
 *  Revision 1.15  2003/08/07 21:48:10  lemired
 *  Made some of the servlets smarter, faster by using the
 *  fact that we know who the user is. For example, one of them presented
 *  the user with a rating page without checking if the user was present
 *  and another one was looping over all items to display the items
 *  rated whereas such a loop is not needed.
 *
 *  Revision 1.14  2003/08/07 15:46:53  lemired
 *  Ok. I had the wrong file there. This should compile.
 *
 *  Revision 1.13  2003/08/07 15:31:01  lemired
 *   This should fix the problem reported by Marcel.
 *
 *  Revision 1.12  2003/08/07 13:16:06  lemired
 *  More javadoc improvments.
 *
 *  Revision 1.11  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author     Daniel Lemire
 *@since      December 2002
 */

public class STIPearson
   extends PerItemAverage
{

   boolean mPearsonWithCaseAmplification;
   boolean mPearsonWithUserFrequency;
   float mCaseAmplification;

//	float[] mAverages;

//	float[] mNorms;

   TObjectFloatHashMap mAverages;
   // this will allow us to have constant time updates

   TObjectFloatHashMap mNorms;
   // this will allow us to have constant time updates

   float mP;

   /**
    *  Constructor for the STIPearson object
    *
    *@param  set  the training set
    *@param  p    which lp norm to user (p=2 is good)
    *@param  CaseAmplification  whether to use case amplification
    */

   public STIPearson(EvaluationSet set, boolean CaseAmplification, float p)
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
    *@param  p    which lp norm to use (p=2 is good)
    */

   public STIPearson(EvaluationSet set, float p)
   {

      this(set, true, p);

   }

   /**
    *  Return an array that contains predictions for the ratings of the given
    *  user. Note that predictions over already rated items don't have to agree
    *  with the provided ratings. This algorithm takes time O(m) where m is the
    *  number of users.
    *
    * This implementation will fallback on per item average when it fails (alpha near zero)
    *
    *@param  u  a set of one-dimensional ratings
    *@param fallback whether to fallback on peritemaverage
    *@return    an array containing predictions
    */
   public float[] completeUser(TIntFloatHashMap u)
   {
      return completeUser(u, getFallBack());
   }

   /**
    *  Return an array that contains predictions for the ratings of the given
    *  user. Note that predictions over already rated items don't have to agree
    *  with the provided ratings. This algorithm takes time O(m) where m is the
    *  number of users.
    *
    * This implementation will fallback on per item average when it fails (alpha near zero)
    *
    *@param  u  a set of one-dimensional ratings
    *@param fallback whether to fallback on peritemaverage
    *@return    an array containing predictions
    */
   public float[] completeUser(TIntFloatHashMap u, boolean fallback)
   {
      if ( (u.size() < 2) && (fallback))
      {
   return super.completeUser(u); //fallback
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
   energy += (complete[iter.key()] - averageIA)
      * (complete[iter.key()] - averageIA);
   product += (complete[iter.key()] - averageIA)
      * (iter.value() - average);
      }
      //System.out.println("product = "+product+" mFallBack = "+mFallBack+" fallback = "+fallback);
      float alpha = 0.0f;
      if (energy > 0.0f)
      {
   alpha = product / energy;
      }
      if ( (Math.abs(alpha) < UtilMath.epsilon) && fallback)
      {
   return super.completeUser(u); // fallback
      }
      //System.out.println("generating solution as "+average+" + "+alpha+ "* ...  ");
      float[] solution = new float[mMaxItemID];
      for (int k = 0; k < mMaxItemID; ++k)
      {
   solution[k] = average + alpha * (complete[k] - averageIA);
   if (Float.isNaN(solution[k]))
   {
      throw new CollaborativeFilteringException(
         "STIPearson is about to return NaN! Aborting!");
   }
      }
      return solution;
   }

   /**
    *  Compute Pearson without taking into account item frequency.
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
      if ( (norm1 < UtilMath.epsilon) || (norm2 < UtilMath.epsilon))
      {
   return 0.0f;
      }
      float product = 0.0f;
      float placeholder1 = 0.0f;
      float placeholder2 = 0.0f;
      TIntFloatIterator iter = u1.iterator();
      while (iter.hasNext())
      {
   iter.advance();
//		for ( int k = 0; k < ids1.length; ++k ) {
   //currentindex = iter.key();//ids1[k];
   if (u2.containsKey(iter.key()))
   {
      placeholder1 = (iter.value() /*u1.get( currentindex )*/ - av1) /
         norm1;
      placeholder2 = (u2.get(iter.key()) - av2) / norm2;
      product += placeholder1 * placeholder2;
   }
      }
      return product;
   }

   /**
    *  Precompute averages and norms for faster predictions.
    */

   private void precomputeAveragesAndNorms()
   {
      //int[] userids = mSet.keys();
      mAverages = new TObjectFloatHashMap(new PearsonHashingStrategy());
      //new float[userids.length];
      mNorms = new TObjectFloatHashMap(new PearsonHashingStrategy());
      //new float[userids.length];
      TIntObjectIterator iter = mSet.iterator();
      //System.out.println(mSet.size());
      int total = 0;
      while (iter.hasNext())
      {
   iter.advance();
   ++total;
   TIntFloatHashMap RunningU = (TIntFloatHashMap) iter.value();
   float av = UtilMath.average(RunningU);
   float nu = UtilMath.lpnorm(RunningU, av, mP);
   // there is no way in Java to truly differentiate
   // two objects that have the same content
   //System.out.println("av: " + av);
   /*			if ( mAverages.contains( RunningU ) ) {
       System.out.println( "HASH CODE: " + RunningU.hashCode() );
       TObjectFloatIterator itr = mAverages.iterator();
       boolean hashmatch = false;
       while ( itr.hasNext() ) {
        itr.advance();
        if(itr.key().hashCode() == RunningU.hashCode()) {
         hashmatch = true;
        }
        //System.out.println( "HASH IN: " + itr.key().hashCode() );
       }
       if (hashmatch == true) System.err.println("It seems like it was already in (same hashcode)");
       else System.err.println("Something weird is going on.");
       System.err.println("I've added "+total+" users so far out of "+mSet.size());
       throw new CollaborativeFilteringException( "Trying to add a user for the second time !" );
      }*/
   if (!mAverages.contains(RunningU))
   {
      mAverages.put(RunningU, av);
      //if ( mNorms.contains( RunningU ) )
      //	throw new CollaborativeFilteringException( "Trying to add a user for the second time !" );
   }
   if (!mNorms.contains(RunningU))
   {
      mNorms.put(RunningU, nu);
   }
      }
   }

   /**
    *  This must called after you remove a user
    *
    *@param  u  evaluation which was removed
    */
   public void removedUser(TIntFloatHashMap u)
   {
      super.removedUser(u);
      if (!mAverages.contains(u))
      {
   throw new CollaborativeFilteringException(
      "You claim to remove a user that you never added!");
      }
      if (!mNorms.contains(u))
      {
   throw new CollaborativeFilteringException(
      "You claim to remove a user that you never added!");
      }
      mNorms.remove(u);
      mAverages.remove(u);

   }

   /**
    *  This must called after you add a user
    *
    *@param  u  evaluation which was added
    */
   public void addedUser(TIntFloatHashMap u)
   {
      super.addedUser(u);
      float av = UtilMath.average(u);
      float nu = UtilMath.lpnorm(u, av, mP);
      if (mAverages.contains(u))
      {
   throw new CollaborativeFilteringException(
      "You are adding a user already present!!!");
      }
      if (mNorms.contains(u))
      {
   throw new CollaborativeFilteringException(
      "You are adding a user already present!!!");
      }
      mAverages.put(u, av);
      mNorms.put(u, nu);
   }

   /**
    *  This is where the main job is done regarding prediction. Expensive!
    *
    *@param  u  the current user
    *@return    something that's use by completeUser (through regression)
    */

   public float[] sumOfUsers(TIntFloatHashMap u)
   {
      float[] complete = new float[mMaxItemID];
      //int[] userids = mSet.keys();
      float[] weight = new float[mSet.size()];
      float average = UtilMath.average(u);
      float normu = UtilMath.lpnorm(u, average, mP);
      if (normu < UtilMath.epsilon)
      {
   return complete; // no need to go further!
      }
      TIntObjectIterator iter = mSet.iterator();
      int k = 0;
      while (iter.hasNext())
      {
   iter.advance();
//		for ( int k = 0; k < userids.length; ++k ) {
   TIntFloatHashMap RunningU = (TIntFloatHashMap) iter.value(); //mSet.get( userids[k] );
   if ( (!mAverages.contains(RunningU)) || (!mNorms.contains(RunningU)))
   {
//				System.err.println( "Something is out of sync!" );
      float av = UtilMath.average(RunningU);
      float nu = UtilMath.lpnorm(RunningU, av, mP);
      mAverages.put(u, av);
      mNorms.put(u, nu);
   }
   float av = mAverages.get(RunningU);
   //[k];
   float nu = mNorms.get(RunningU);
   //[k];
   weight[k++] = pearsonWithoutUserFrequency(u, average, normu, RunningU,
      av, nu);
      }
      if (mPearsonWithCaseAmplification)
      {
   Pearson.caseAmplification(weight, mCaseAmplification);
      }
      float[] ItemAmplitude = new float[mMaxItemID];
      iter = mSet.iterator();
      k = 0;
      while (iter.hasNext())
      {
//		for ( int k = 0; k < userids.length; ++k ) {
   iter.advance();
   final TIntFloatHashMap runningU = (TIntFloatHashMap) iter.value(); //(TIntFloatHashMap) mSet.get( userids[k] );
   TIntFloatIterator runningUiter = runningU.iterator();
   while (runningUiter.hasNext())
   {
      runningUiter.advance();
      ItemAmplitude[runningUiter.key()] += Math.abs(weight[k]);
   }
   ++k;
      }
      iter = mSet.iterator();
//		int CurrentItemIndex;
//		for ( int k = 0; k < userids.length; ++k ) {
      k = 0;
      while (iter.hasNext())
      {
   iter.advance();
   final TIntFloatHashMap runningU = (TIntFloatHashMap) iter.value(); //mSet.get( userids[k] );
   //float av = UtilMath.average( runningU );
   float av = mAverages.get(runningU);
   //[k];
   float nu = mNorms.get(runningU);
   if (nu < UtilMath.epsilon)
   {
      continue;
   }
   //[k];
   float ia;
   TIntFloatIterator RunningUiter = runningU.iterator();
   while (RunningUiter.hasNext())
   {
      RunningUiter.advance();
      ia = ItemAmplitude[RunningUiter.key()];
      if (ia > 0)
      {

         //complete[iter.key()] += weight[k] * ( iter.value() - av ) / ia;
         complete[RunningUiter.key()] += weight[k] *
      (RunningUiter.value() - av) / (ia * nu);
      }
   }
   ++k;
      }
      return complete;
   }

   public String toString()
   {
      return "STIPearson_mP=" + mP;
   }

}
