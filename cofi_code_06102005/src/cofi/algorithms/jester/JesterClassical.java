/**
 *  (c) National Research Council of Canada, 2002-2003 by Daniel Lemire, Ph.D.
 *  Email lemire at ondelette dot com for support and details.
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

import JSci.maths.matrices.DoubleSquareMatrix;
import JSci.maths.vectors.DoubleVector;
import cofi.algorithms.*;
import cofi.algorithms.util.*;
import cofi.data.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

/**
 * This is a full implementation of the Eigentaste 2 algorithm
 * first described by a paper by Goldberg et al.
 *
 *
 *  $Id: JesterClassical.java,v 1.3 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $
 *  $Author: lemired $
 *  $Revision: 1.3 $
 *  $Log: JesterClassical.java,v $
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
public class JesterClassical
   extends CollaborativeFilteringSystem
{
   /**
    *  Averages for the items in the standard data set
    */
   protected float[] PerStandardItemMean;
   /**
    *  Standard deviation for the items in the standard data set
    */
   protected float[] PerStandardItemStandardDeviation;
   /**
    *  Array index of the items in the standard data sets
    */
   protected int[] mStandardItems;
   /**
    *  Store the Eigentaste eigenvectors
    */
   protected double[][] mEigenVectors;
   /**
    *  Store the averages for each cluster of the eigenplane
    */
   protected float[][][] mClusterAverages;
   /**
    *  Store the item frequencies for each cluster of the eigenplane
    */
   protected int[][] mClusterFrequencies;
   /**
    *  range parameters for the eigenplane (used in defining the clusters)
    */
   protected float mMaxX, mMaxY;
   /**
    *  Determines the number of clusters to use
    */
   protected int mN = 4;
   /**
    *  By default, this contains the standard sets used for Jester
    */
   protected static int[] standard =
      {
      5 - 1, 7 - 1, 8 - 1, 13 - 1, 15 - 1, 16 - 1, 17 - 1, 18 - 1, 19 - 1,
      20 - 1};

   /**
    *  Constructor for the JesterClassical object
    *
    *@param  set                           training set
    *@param  StandardItems                 standard items to use
    *@param  ApplyEigentasteNormalization  whether to normalize the ratings as Goldber did
    */
   public JesterClassical(EvaluationSet set, int[] StandardItems,
        boolean ApplyEigentasteNormalization)
   {
      super(set);
      mStandardItems = StandardItems;
      init(ApplyEigentasteNormalization);
   }

   /**
    *  Constructor for the JesterClassical object
    *
    *@param  set                           Training set
    *@param  ApplyEigentasteNormalization  Whether to normalize the items as Goldberg did
    */
   public JesterClassical(EvaluationSet set,
        boolean ApplyEigentasteNormalization)
   {
      super(set);
      mStandardItems = standard;
      // standard for Jester 2.0 data set!
      init(ApplyEigentasteNormalization);
   }

   /**
    *  Constructor for the JesterClassical object
    *
    *@param  set                           The training set
    *@param  StandardItems                 the provided standard items
    *@param  n                             determines the number of clusters
    *@param  ApplyEigentasteNormalization  whether to apply the normalization Goldberg used
    */
   public JesterClassical(EvaluationSet set, int[] StandardItems, int n,
        boolean ApplyEigentasteNormalization)
   {
      super(set);
      mStandardItems = StandardItems;
      mN = n;
      init(ApplyEigentasteNormalization);
   }

   /**
    *  Constructor for the JesterClassical object
    *
    *@param  set                           the training set
    *@param  n                             determines the size of the cluster
    *@param  ApplyEigentasteNormalization  whether to apply the normalization Goldberg used
    */
   public JesterClassical(EvaluationSet set, int n,
        boolean ApplyEigentasteNormalization)
   {
      super(set);
      mStandardItems = standard;
      // standard for Jester 2.0 data set!
      mN = n;
      init(ApplyEigentasteNormalization);
   }

   // granularity

   /**
    *  Gets the standard attribute of the JesterClassical object
    *
    *@return    The standard value
    */
   public int[] getStandard()
   {
      return standard;
   }

   /**
    * Initialize the method (computes eigenclusters)
    *
    *@param  ApplyEigentasteNormalization  whether to apply the normalization Goldberg used
    */
   protected void init(boolean ApplyEigentasteNormalization)
   {
      System.out.println("Computing Per Item Mean and Standard Deviation");
      if (ApplyEigentasteNormalization)
      {
   computePerItemMeanAndStandardDeviation();
      }
      System.out.println("Computing Eigenvectors...");
      computeEigenVectors();
      UtilMath.print(mEigenVectors[0]);
      UtilMath.print(mEigenVectors[1]);
      System.out.println("Computing Clusters...");
      computeAverages();
      System.out.println("Done.");
   }

   /**
    *  Compute the mean and standard deviation used for the Goldberg-type normalization
    */
   public void computePerItemMeanAndStandardDeviation()
   {
      PerStandardItemMean = new float[mStandardItems.length];
      TIntObjectIterator t = mSet.iterator();
      int count = 0;
      while (t.hasNext())
      {
   t.advance();
   TIntFloatHashMap u = (TIntFloatHashMap) t.value();
   for (int k = 0; k < mStandardItems.length; ++k)
   {
      if (!u.contains(mStandardItems[k]))
      {
         System.out.println("Your user is...");
         UtilMath.print(u);
         System.out.println("Standard item  number " + k + " (" +
          mStandardItems[k] + ") is missing.");
         throw new CollaborativeFilteringException(
      "You are in trouble! Invalid standard set.");
      }
      PerStandardItemMean[k] += u.get(mStandardItems[k]);
   }
   ++count;
      }
      for (int k = 0; k < mStandardItems.length; ++k)
      {
   PerStandardItemMean[k] /= count;
      }
      PerStandardItemStandardDeviation = new float[mStandardItems.length];
      t = mSet.iterator();
      while (t.hasNext())
      {
   t.advance();
   TIntFloatHashMap u = (TIntFloatHashMap) t.value();
   for (int k = 0; k < mStandardItems.length; ++k)
   {
      if (!u.contains(mStandardItems[k]))
      {
         throw new CollaborativeFilteringException(
      "You are in trouble! Invalid standard set.");
      }
      PerStandardItemStandardDeviation[k] +=
         (u.get(mStandardItems[k]) - PerStandardItemMean[k])
         * (u.get(mStandardItems[k]) - PerStandardItemMean[k]) /
         (count * count);
   }
      }
      for (int k = 0; k < mStandardItems.length; ++k)
      {
   PerStandardItemStandardDeviation[k] = (float) Math.sqrt(
      PerStandardItemStandardDeviation[k]);
      }
   }

   /**
    * compute the eigenvectors
    */
   protected void computeEigenVectors()
   {
      mEigenVectors = computeKLMatrix();
      mMaxX = 0.0f;
      mMaxY = 0.0f;
      TIntObjectIterator t = mSet.iterator();
      while (t.hasNext())
      {
   t.advance();
   TIntFloatHashMap u = (TIntFloatHashMap) t.value();
   float x = scalarProduct(u, 0);
   float y = scalarProduct(u, 1);
   if (Math.abs(x) > mMaxX)
   {
      mMaxX = Math.abs(x);
   }
   if (Math.abs(y) > mMaxY)
   {
      mMaxX = Math.abs(y);
   }
      }
   }

   /**
    *  compute the average
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
   TIntFloatIterator uiter = u.iterator();
   while (uiter.hasNext())
   {
      uiter.advance();
      mClusterAverages[ClusterIndex(x, mMaxX)][ClusterIndex(y, mMaxY)][
         uiter.key()] += uiter.value();
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
      }
   }
      }

   }

   /**
    *  Compute the scalar products with the eigenvectors
    *
    *@param  u    the current evaluation
    *@param  vec  which eigenvector
    *@return      the value of the scalar product
    */
   protected float scalarProduct(final TIntFloatHashMap u, int vec)
   {
      float product = 0.0f;
      for (int k = 0; k < mEigenVectors[vec].length; ++k)
      {
   if (!u.contains(mStandardItems[k]))
   {
      System.err.println("[Error] Deep trouble: standard no good. item " +
             mStandardItems[k] + " not found");
      UtilMath.print(u);
      throw new CollaborativeFilteringException();
   }
   if (PerStandardItemMean != null)
   {
      if (PerStandardItemStandardDeviation[k] != 0.0)
      {
         product += (mEigenVectors[vec][k] - PerStandardItemMean[k])
      / PerStandardItemStandardDeviation[k] *
      u.get(mStandardItems[k]);
      }
   }
      }
      return product;
   }

   /**
    *  makes a prediction based on Eigentaste 2
    *
    *@param  u  the current evaluation
    *@return    a prediction for all ratings
    */
   public float[] completeUser(TIntFloatHashMap u)
   {
      float x = scalarProduct(u, 0);
      float y = scalarProduct(u, 1);
      return mClusterAverages[ClusterIndex(x, mMaxX)][ClusterIndex(y, mMaxY)];
   }

   /**
    *  Given a scalar product and a max range, this is used to compute
    * the corresponding eigencluster
    *
       *@param  x    a scalar product between an evaluation and a dominant eigenvector
    *@param  max  the range of the eigenplane
    *@return      a parameter that will be used to determine the corresponding eigencluster
    */
   protected int ClusterIndex(float x, float max)
   {
      int index = mN - 1;
      for (float val = Math.abs(x / max), cutoff = 0.5f;
     (val < cutoff) && (index > 0); cutoff /= 2, --index)
      {
   ;
      }
      if (x < 0.0)
      {
   return mN - 1 - index;
      }
      return mN + index;
   }

   /**
    *  Compute the Karhunen-Loève matrix used in the computation
    * of the eigenvectors
    *
    *@return    the Karhunen-Loève matrix
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
   for (int k = 0; k < vector.length; ++k)
   {
      if (!user.contains(mStandardItems[k]))
      {
         System.err.println(
      "[Error] Deep trouble: standard no good. item " +
      mStandardItems[k] + " no found in user " + useriter.key());
         UtilMath.print(user);
         throw new CollaborativeFilteringException();
      }
      vector[k] = user.get(mStandardItems[k]);
   }
   for (int k = 0; k < vector.length; ++k)
   {
      for (int l = 0; l < vector.length; ++l)
      {
         matrix[k][l] += vector[k] * vector[l] / mSet.getNumberOfUsers();
      }
   }
      }
      DoubleSquareMatrix dsq = new DoubleSquareMatrix(
   matrix);
      try
      {
   DoubleVector[] eigenvector = new DoubleVector[
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

   public String toString()
   {
      return "JesterClassical";
   }

}
