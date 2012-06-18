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
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;
import JSci.maths.*;

/**
 *  Optimal Constant Weight algo from Bias-From-Mean
 *. Should be better than Bias-From-Mean.
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $ $Log: TIOptimalConstantWeight.java,v $
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $ Revision 1.4  2003/11/24 16:26:29  lemired
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $ Removed data package again
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $ Revision 1.3  2003/11/17 18:43:07  lemired
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $ Added new BiRuleOf3
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $ Revision 1.2  2003/11/11 13:25:58  lemired
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $ Added gpl headers
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $ Revision 1.1  2003/11/03 23:41:57  lemired
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $ Latest changes: should almost conclude paper with Anna.
 *  $Id: TIOptimalConstantWeight.java,v 1.4 2003/11/24 16:26:29 lemired Exp $ $Date: 2003/11/24 16:26:29 $ $Revision: 1.4 $
 *
 *@author     Daniel Lemire
 *@created    October 30, 2003
 *@since      July 2003
 */
public class TIOptimalConstantWeight extends PerItemAverage {
  /**
   *  The optimal weight matrix
   */
  protected float[][] mW;
 // float mGamma;


  /**
   *  Constructor for the OptimalWeight object
   *
   *@param  set    the training set
   *@param  gamma  Description of the Parameter
   */
  public TIOptimalConstantWeight( EvaluationSet set/*, float gamma*/) {
    super( set );
  //  mGamma = gamma;
    computeWeights();
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
   *  Compute the optimal weight matrix
   */
  protected void computeWeights() {
    mW = new float[mMaxItemID][mMaxItemID];
    int mSetsize = mSet.size();
    double[][] A = new double[mMaxItemID - 1][mMaxItemID - 1];
    double[] B = new double[mMaxItemID - 1];
    for ( int k = 0; k < mMaxItemID; ++k ) {
      System.out.println("TIOptimalConstantWeight k = "+k+ " mMaxItemID = "+ mMaxItemID);
      long beginmatrix = System.currentTimeMillis();
      UtilMath.fillWithZeroes( A );
      UtilMath.fillWithZeroes( B );
        // fill in the cost function
     //for ( int diag = 0; diag < A.length; ++diag )
       // A[diag][diag] = mGamma;
      // quantities of memory
      TIntObjectIterator t = mSet.iterator();
      int totalusers = 0;
      // count number of users containing k
      while ( t.hasNext() ) {
        t.advance();
        TIntFloatHashMap u = (TIntFloatHashMap) t.value();
        float average = UtilMath.average(u);
        if ( !u.contains( k ) )
          continue;
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
            A[jindex][lindex] += 1.0 / ( ( usize - 1 ) * ( usize - 1 ) );
            //if(Double.isNaN(A[jindex][lindex]))
              //throw new CollaborativeFilteringException("NaN detected.");
          }
          B[lindex] +=  (kvalue - average)  / ( usize - 1 );
         // if(Double.isNaN(B[lindex]))
           //   throw new CollaborativeFilteringException("NaN detected.");
        }
      }
      long endmatrix = System.currentTimeMillis();
      System.out.println("Matrix in "+((endmatrix-beginmatrix)/1000.0)+ " seconds");
      try {
        long beginsolve = System.currentTimeMillis();
        double norm = UtilMath.norm(A);
        System.out.println("norm is : "+norm);
        double gamma = norm * 0.01;
        if ( gamma < 0.001) gamma = 0.001;
        for ( int diag = 0; diag < A.length; ++diag )
          A[diag][diag] += gamma;
        double[] X = SymmLQ.solve(A,B);//ConjugateGradient.solve(A,B);
        long endsolve = System.currentTimeMillis();
        System.out.println("Solve in "+((endsolve-beginsolve)/1000.0)+ " seconds");
        /*double[] X = Minres.solve( A, B, 1E-5, 2 * totalusers, initialGuess( k, guess ), null, true );*/
        for ( int l = 0; l < mMaxItemID; ++l ) {
          if ( l < k )
            mW[k][l] = (float) X[l];
          else if ( l > k )
            mW[k][l] = (float) X[l - 1];
          if(Double.isNaN(mW[k][l]))
            throw new CollaborativeFilteringException("NaN detected.");

        }
      } catch ( NoConvergenceException nce ) {
        //mConverged = false;
        nce.printStackTrace();
        //UtilMath.print(A);
        // for now, we will throw an exception to debug it
        throw new CollaborativeFilteringException( nce.getMessage() );
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
  public float[] completeUser( TIntFloatHashMap u ) {
    float[] answer = new float[mMaxItemID];
    float average = UtilMath.average(u);
    TIntFloatIterator j = u.iterator();
    while ( j.hasNext() ) {
      j.advance();
      int key = j.key();
      for ( int k = 0; k < mMaxItemID; ++k ) {
          answer[k] += mW[k][key]  / u.size() ;
      }
    }
    for(int k = 0; k < mMaxItemID; ++k)
       answer[k]+= average;
    j = u.iterator();
    while ( j.hasNext() ) {
      j.advance();
      answer[j.key()] = j.value();
    }
    return answer;
  }


  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public String toString() {
    return "TIOptimalConstantWeight";//__mGamma"+mGamma;
  }



}

