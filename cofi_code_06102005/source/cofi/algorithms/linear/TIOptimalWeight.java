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
package cofi.algorithms.linear;

import cofi.algorithms.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.util.*;
import cofi.data.*;
import gnu.trove.*;
import JSci.maths.*;

/**
 *  The translation invariant optimal weight collaborative filtering system.
 *  $Id: TIOptimalWeight.java,v 1.6 2003/11/24 16:26:29 lemired Exp $ $Date:
 *  2003/10/28 01:43:08 $ $Author: lemired $ $Revision: 1.6 $ $Log:
 *  TIOptimalWeight.java,v $ Revision 1.2 2003/10/28 01:43:08 lemired Lots of
 *  refactoring. Revision 1.1 2003/10/27 17:21:15 lemired Putting some order
 *  Revision 1.4 2003/09/18 12:41:42 lemired Still doing lots of boring
 *  research. Revision 1.3 2003/08/22 13:38:23 howsen *** empty log message ***
 *  Revision 1.2 2003/08/21 18:04:29 lemired Added toString method plus added
 *  necessary activation.jar for convenience. Revision 1.1 2003/08/19 23:15:04
 *  lemired TIOptimalWeight. Revision 1.9 2003/08/19 17:51:21 lemired I've been
 *  improving OptimalWeight. Revision 1.8 2003/08/12 11:52:11 lemired Added more
 *  regression testing. Revision 1.7 2003/08/07 13:16:05 lemired More javadoc
 *  improvments. Revision 1.6 2003/08/07 00:37:42 lemired Mostly, I updated the
 *  javadoc.
 *
 *@author     Daniel Lemire
 *@created    October 30, 2003
 *@since      July 2003
 */
public class TIOptimalWeight
     extends PerItemAverage {
  /**
   *  The optimal weight matrix
   */
  protected float[][] mW;
  /**
   *  Description of the Field
   */
  //protected float mGamma;
  // weight factor, 0.1 is arbitrary here
  /**
   *  Whether we have an optimal solution for real!
   */
  protected boolean mConverged = true;


  /**
   *  Constructor for the OptimalWeight object
   *
   *@param  set    the training set
   *@param  gamma  Description of the Parameter
   */
  public TIOptimalWeight( EvaluationSet set/*, float gamma */) {
    super( set );
    //mGamma = gamma;
    computeWeights();
  }


  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public boolean hasConverged() {
    return mConverged;
  }


  /**
   *  Gets the weights attribute of the TIOptimalWeight object
   *
   *@return    The weights value
   */
  public float[][] getWeights() {
    return mW;
  }


  /**
   *  Description of the Method
   *
   *@param  itemid  Description of the Parameter
   *@return         Description of the Return Value
   */
  private double[] initialGuess( int itemid ) {
    double[] ans = new double[mMaxItemID - 1];
    for ( int k = 0; k < itemid; ++k )
      if ( mPerItemAverage[k] != 0.0f )
        ans[k] = mPerItemAverage[itemid] / mPerItemAverage[k];
    for ( int k = itemid + 1; k < mMaxItemID; ++k )
      if ( mPerItemAverage[k] != 0.0f )
        ans[k - 1] = mPerItemAverage[itemid] / mPerItemAverage[k];
    return ans;
  }


  /**
   *  Description of the Method
   *
   *@param  itemid  Description of the Parameter
   *@param  ans     Description of the Parameter
   *@return         Description of the Return Value
   */
  private double[] initialGuess( int itemid, double[] ans ) {
    //double[] ans = new double[mMaxItemID - 1];
    for ( int k = 0; k < itemid; ++k )
      if ( mPerItemAverage[k] != 0.0f )
        ans[k] = mPerItemAverage[itemid] / mPerItemAverage[k];
    for ( int k = itemid + 1; k < mMaxItemID; ++k )
      if ( mPerItemAverage[k] != 0.0f )
        ans[k - 1] = mPerItemAverage[itemid] / mPerItemAverage[k];
    return ans;
  }


  /**
   *  Compute the optimal weight matrix
   */
  protected void computeWeights() {
    mW = new float[mMaxItemID][mMaxItemID];
    int mSetsize = mSet.size();
    double[][] A = new double[mMaxItemID - 1][mMaxItemID - 1];
    double[] B = new double[mMaxItemID - 1];
    double[] guess = new double[mMaxItemID - 1];
    for ( int k = 0; k < mMaxItemID; ++k ) {
      System.out.println("TIOptimalWeight k = "+k+ " mMaxItemID = "+ mMaxItemID);
      long beginmatrix = System.currentTimeMillis();
      fillWithZeroes( A );
      fillWithZeroes( B );
      // this way, we avoid allocating and deallocating large
        // fill in the cost function
      // quantities of memory
      TIntObjectIterator t = mSet.iterator();
      int totalusers = 0;
      // count number of users containing k
      while ( t.hasNext() ) {
        t.advance();
        TIntFloatHashMap u = (TIntFloatHashMap) t.value();
        if ( !u.contains( k ) )
          continue;
        float average = UtilMath.average( u, k );
        final float usize = u.size();
        ++totalusers;
        float kvalue = u.get( k );
        TIntFloatIterator l = u.iterator();
        while ( l.hasNext() ) {
          l.advance();
          int lindex = l.key();
          if ( lindex == k )
            continue;
          if ( lindex > k )
            --lindex;
            // pack
          final float lvalue = l.value();
          TIntFloatIterator j = u.iterator();
          while ( j.hasNext() ) {
            j.advance();
            int jindex = j.key();
            if ( jindex == k )
              continue;
            if ( jindex > k )
              --jindex;
              // pack
            A[jindex][lindex] += ( j.value() - average ) * ( lvalue - average ) / ( ( usize - 1 ) * ( usize - 1 ) );
          }
          B[lindex] += ( lvalue - average ) * ( kvalue - average ) / ( usize - 1 );
        }
      }
        long endmatrix = System.currentTimeMillis();
        System.out.println("Matrix in "+((endmatrix-beginmatrix)/1000.0)+ " seconds");
      try {
        long beginsolve = System.currentTimeMillis();
        double norm = UtilMath.norm(A);
        System.out.println("norm is : "+norm);
        for ( int diag = 0; diag < A.length; ++diag )
          A[diag][diag] += norm * 0.01;
        double[] X = ConjugateGradient.solve(A,B);
        long endsolve = System.currentTimeMillis();
        System.out.println("Solve in "+((endsolve-beginsolve)/1000.0)+ " seconds");
        /*double[] X = Minres.solve( A, B, 1E-5, 2 * totalusers, initialGuess( k, guess ), null, true );*/
        for ( int l = 0; l < mMaxItemID; ++l )
          if ( l < k )
            mW[k][l] = (float) X[l];
          else if ( l > k )
            mW[k][l] = (float) X[l - 1];
      } catch ( NoConvergenceException nce ) {
        //mConverged = false;
        nce.printStackTrace();
        // for now, we will throw an exception to debug it
        throw new CollaborativeFilteringException( nce.getMessage() );
      }
    }
    if ( !mConverged )
      System.err.println( "[OptimalWeight] It wasn't possible to compute the coefficients optimally. Solutions might be invalid." );

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
  public float[] completeUser( TIntFloatHashMap u ) {
    float[] answer = new float[mMaxItemID];
    float average = UtilMath.average( u );
    TIntFloatIterator j = u.iterator();
    while ( j.hasNext() ) {
      j.advance();
      float value = j.value();
      int key = j.key();
      for ( int k = 0; k < mMaxItemID; ++k ) {
          answer[k] += mW[k][key] * ( value - average ) / u.size() ;
      }
    }
    for ( int k = 0; k < answer.length; ++k )
      answer[k] += average;
     j = u.iterator();
    while ( j.hasNext() ) {
      j.advance();
      answer[j.key()]=j.value();
    }
    return answer;
  }


  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public String toString() {
    return "TIOptimalWeight";//_mGamma_" + mGamma + "_mConverged_" + mConverged +
        //"_mGamma_" + mGamma;
  }


  /**
   *  Description of the Method
   *
   *@param  B  Description of the Parameter
   */
  public static void fillWithZeroes( double[] B ) {
    for ( int k = 0; k < B.length; ++k )
      B[k] = 0.0f;

  }


  /**
   *  Description of the Method
   *
   *@param  A  Description of the Parameter
   */
  public static void fillWithZeroes( double[][] A ) {
    for ( int k = 0; k < A.length; ++k )
      fillWithZeroes( A[k] );
  }
  
  public static void main(String arg[]) {
      EvaluationSet es = new EvaluationSet();
      TIntFloatHashMap u = new TIntFloatHashMap();
      u.put(0,1); u.put(1,2);
      es.put(0,u);
      TIntFloatHashMap v = new TIntFloatHashMap();
      v.put(1,2); v.put(2,3);
      es.put(1,v);
      es.setMaxItemID(4);
      TIOptimalWeight ow = new TIOptimalWeight(es);
      TIntFloatHashMap w = new TIntFloatHashMap();
      w.put(0,1); w.put(2,3);
      UtilMath.print(ow.completeUser(w));
  }

}

