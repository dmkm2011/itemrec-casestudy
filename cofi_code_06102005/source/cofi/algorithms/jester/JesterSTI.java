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
package cofi.algorithms.jester;

import cofi.data.*;
import cofi.algorithms.*;
import cofi.algorithms.util.*;
import gnu.trove.*;

/**
 *  This is an implementation of the STI Eigentaste algorithm
 * first described in the paper by Lemire in Scale and Translation Invariant
 * Collaborative Filtering Systems (Journal of Information Retrieval).
 *
 *
 *  $Id: JesterSTI.java,v 1.3 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $
 *  $Author: lemired $
 *  $Revision: 1.3 $
 *  $Log: JesterSTI.java,v $
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
 *  Revision 1.7  2003/08/19 17:51:21  lemired
 *  I've been improving OptimalWeight.
 *
 *  Revision 1.6  2003/08/07 13:16:05  lemired
 *  More javadoc improvments.
 *
 *  Revision 1.5  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author     Daniel Lemire
 *@since    August 6, 2003
 */
public class JesterSTI
   extends JesterClassical
{
   static int mDefaultN = 4;

   /**
    *  Constructor for the JesterSTI object
    *
    *@param  set  the traing set
    */
   public JesterSTI(EvaluationSet set)
   {
      super(set, mDefaultN, false);
   }

   /**
    *  Constructor for the JesterSTI object
    *
    *@param  set            the training set
    *@param  StandardItems  the standard item set
    */
   public JesterSTI(EvaluationSet set, int[] StandardItems)
   {
      super(set, StandardItems, mDefaultN, false);
   }

   /**
    *  Constructor for the JesterSTI object
    *
    *@param  set  the training set
    *@param  n    determines the number of clusters
    */
   public JesterSTI(EvaluationSet set, int n)
   {
      super(set, n, false);
   }

   /**
    *  Constructor for the JesterSTI object
    *
    *@param  set            the training set
    *@param  StandardItems  the standard items
    *@param  n              determines the number of clusters
    */
   public JesterSTI(EvaluationSet set, int[] StandardItems, int n)
   {
      super(set, StandardItems, n, false);
   }

   /**
    *  Compute the average of an evaluation over the standard items
    *
    *@param  user  the evaluation
    *@return       its average
    */
   protected double average(TIntFloatHashMap user)
   {
      double average = 0.0f;
      for (int k = 0; k < mStandardItems.length; ++k)
      {
   if (!user.contains(mStandardItems[k]))
   {
      System.err.println("[Error] Deep trouble: standard no good. item " +
             mStandardItems[k] + " not found");
      throw new CollaborativeFilteringException();
   }
   average += user.get(mStandardItems[k]);
      }
      average /= mStandardItems.length;
      return average;
   }

   /**
    *  Returns the l2 norm of the evaluation over the standard items
    *
    *@param  user     a given evaluation
    *@param  average  the average over the standard items of this evaluation
    *@return          the l2 norm over the standard items
    */
   protected double l2(TIntFloatHashMap user, double average)
   {
      double l2 = 0.0f;
      for (int k = 0; k < mStandardItems.length; ++k)
      {
   if (!user.contains(mStandardItems[k]))
   {
      System.err.println("[Error] Deep trouble: standard no good. item " +
             mStandardItems[k] + " not found");
      throw new CollaborativeFilteringException();
   }
   float value = user.get(mStandardItems[k]);
   l2 += (value - average) * (value - average);
   //l2 += Math.abs(value - average);
      }
      l2 = Math.sqrt(l2);
      return l2;
   }

   /**
    *  Compute the STI Karhunen-Loève matrix over the training set
    *
    *@return    the STI Karhunen-Loève matrix over the training set
    */
   protected double[][] computeKLMatrix()
   {
      double[][] matrix = new double[mStandardItems.length][mStandardItems.
   length];
      double[][] eigenvec = new double[2][mStandardItems.length];
      TIntObjectIterator useriter = mSet.iterator();
      while (useriter.hasNext())
      {
   useriter.advance();
   TIntFloatHashMap user = (TIntFloatHashMap) useriter.value();
   double[] vector = new double[mStandardItems.length];
   double average = average(user);
   double l2 = l2(user, average);
   for (int k = 0; k < vector.length; ++k)
   {
      if (!user.contains(mStandardItems[k]))
      {
         System.err.println(
      "[Error] Deep trouble: standard no good. item " +
      mStandardItems[k] + " not found");
         throw new CollaborativeFilteringException();
      }
      if (l2 > UtilMath.epsilon)
      {
         vector[k] = (user.get(mStandardItems[k]) - average) / l2;
      }
      else
      {
         vector[k] = 0;

      }
   }
   for (int k = 0; k < vector.length; ++k)
   {
      for (int l = 0; l < vector.length; ++l)
      {
         matrix[k][l] += vector[k] * vector[l] / mSet.getNumberOfUsers();
      }
   }
      }
      JSci.maths.DoubleSquareMatrix dsq = new JSci.maths.DoubleSquareMatrix(
   matrix);
      try
      {
   JSci.maths.DoubleVector[] eigenvector = new JSci.maths.DoubleVector[
      mStandardItems.length];
   double[] eigenvalues = JSci.maths.LinearMath.eigenSolveSymmetric(dsq,
      eigenvector);
   int max = 0;
   int secondmax = 1;
   for (int k = 0; k < mStandardItems.length; ++k)
   {
      if (eigenvalues[k] > eigenvalues[max])
      {
         if (eigenvalues[max] > eigenvalues[secondmax])
         {
      secondmax = max;
      max = k;
         }
         else
         {
      max = k;

         }
      }
      else if (eigenvalues[k] > eigenvalues[secondmax])
      {
         secondmax = k;

      }
      System.out.println("[debug]  eigenvalue = " + eigenvalues[k]);
      //System.out.println( eigenvector[k] );
   }
   System.out.println("[debug] Best = " + eigenvalues[max] +
          " second best = " + eigenvalues[secondmax]);
   // need to select the two worse cases
   eigenvec[0] = JSci.util.VectorToolkit.toArray(eigenvector[max]);
   eigenvec[1] = JSci.util.VectorToolkit.toArray(eigenvector[secondmax]);
      }
      catch (JSci.maths.MaximumIterationsExceededException miee)
      {
   miee.printStackTrace();
   return null;
   //make it fail!
      }
      return eigenvec;
   }

   /**
       *  Compute the scalar product between an evaluation and a dominant eigenvector
    *
    *@param  u    current evaluation
    *@param  vec  index of the eigenvector
    *@return      the value of the scalar product over the standard items
    */
   protected float scalarProduct(final TIntFloatHashMap u, int vec)
   {
      double average = average(u);
      double l2 = l2(u, average);
      float product = 0.0f;
      float WMaverage = 0.0f;
      for (int k = 0; k < mStandardItems.length; ++k)
      {
   WMaverage += mEigenVectors[vec][k];

      }
      WMaverage /= mStandardItems.length;
      if (l2 > UtilMath.epsilon)
      {
   for (int k = 0; k < mEigenVectors[vec].length; ++k)
   {
      if (!u.contains(mStandardItems[k]))
      {
         System.err.println(
      "[Error] Deep trouble: standard no good. item " +
      mStandardItems[k] + " not found");
         throw new CollaborativeFilteringException();
      }
      product += (mEigenVectors[vec][k] - WMaverage) *
         (u.get(mStandardItems[k]) - average) / l2;
   }
      }

      return product;
   }

   /**
    *  Predict the ratings of this user given his/her evaluation
    *
    *@param  u  the current evaluation
    *@return    a prediction over all items
    */
   public float[] completeUser(TIntFloatHashMap u)
   {
      float x = scalarProduct(u, 0);
      float y = scalarProduct(u, 1);
      float[] WithoutMean = mClusterAverages[ClusterIndex(x, mMaxX)][
   ClusterIndex(y, mMaxY)];
      float average = UtilMath.average(u);
      float WMaverage = 0.0f;
      TIntFloatIterator i = u.iterator();
      while (i.hasNext())
      {
   i.advance();
   WMaverage += WithoutMean[i.key()];
      }
      WMaverage /= u.size();
      float product = 0.0f;
      float energy = 0.0f;
      i = u.iterator();
      while (i.hasNext())
      {
   i.advance();
   product += (WithoutMean[i.key()] - WMaverage) * (i.value() - average);
   energy += (WithoutMean[i.key()] - WMaverage) *
      (WithoutMean[i.key()] - WMaverage);
      }
      float alpha = 0.0f;
      if (energy > 0.0f)
      {
   alpha = product / energy;
      }
      float[] answer = new float[WithoutMean.length];
      for (int k = 0; k < WithoutMean.length; ++k)
      {
   answer[k] = alpha * (WithoutMean[k] - WMaverage) + average;

      }
      return answer;
   }

   /**
    *  Compute averages over all eigenclusters
    */
   protected void computeAverages()
   {
      mClusterFrequencies = new int[2 * mN][2 * mN];
      mClusterAverages = new float[2 * mN][2 * mN][mMaxItemID];
      TIntObjectIterator t = mSet.iterator();
      while (t.hasNext())
      {
   t.advance();
   TIntFloatHashMap u = (TIntFloatHashMap) t.value();
   float x = scalarProduct(u, 0);
   float y = scalarProduct(u, 1);
   ++mClusterFrequencies[ClusterIndex(x, mMaxX)][ClusterIndex(y, mMaxY)];
   double average = average(u);
   double l2 = l2(u, average);
   if (l2 > UtilMath.epsilon)
   {
      TIntFloatIterator uiter = u.iterator();
      while (uiter.hasNext())
      {
         uiter.advance();
         mClusterAverages[ClusterIndex(x, mMaxX)][ClusterIndex(y, mMaxY)][
      uiter.key()] += (uiter.value() - average) / l2;
      }
   }
      }
      for (int k = 0; k < 2 * mN; ++k)
      {
   for (int l = 0; l < 2 * mN; ++l)
   {
      for (int i = 0; i < mClusterAverages[k][l].length; ++i)
      {
         if (mClusterFrequencies[k][l] > 0)
         {
      mClusterAverages[k][l][i] /= mClusterFrequencies[k][l];
         }
         else if (mClusterAverages[k][l][i] > 0.0f)
         {
      System.out.println("[Error] Your code is not sane.");
         }
         if (Float.isNaN(mClusterAverages[k][l][i]))
         {
      System.err.println("[Error] bug in the averages k= " + k +
             " l = " + l + " i = " + i);

         }
      }
   }
      }

   }

   public String toString()
   {
      return "JesterSTI";
   }

}
