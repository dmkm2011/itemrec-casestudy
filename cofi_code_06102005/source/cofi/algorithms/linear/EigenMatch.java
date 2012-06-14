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

import JSci.maths.*;
import cofi.data.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.*;
import cofi.algorithms.util.*;
import gnu.trove.*;
import java.io.*;
import java.util.*;

/**
 * Obselete class?
 *
 *  This class computes the transpose(U) * U matrix and uses its
 * eigenvectors to build a prediction. 
 *
 * Well, actually, it is a bit more complicated than that...
 * but one of the nice features of this scheme is that it is
 * first-order exact!!!  *
 * 
 * $Id:
 *  STIOptimalWeight.java,v 1.3 2003/08/21 18:49:38 lemired Exp $ $Date:
 *  2003/08/21 18:49:38 $ $Author: lemired $ $Revision: 1.5 $ $Log:
 *  STIOptimalWeight.java,v $ Revision 1.3 2003/08/21 18:49:38 lemired It should
 *  now compile nicely. Revision 1.2 2003/08/21 18:04:29 lemired Added toString
 *  method plus added necessary activation.jar for convenience. Revision 1.1
 *  2003/08/21 15:32:39 lemired This is the new STI Optimal Weight algorithm.
 *  Untested now. Revision 1.10 2003/08/19 23:13:57 lemired More work on
 *  OptimalWeight. Don't think it works well afterall. Revision 1.9 2003/08/19
 *  17:51:21 lemired I've been improving OptimalWeight. Revision 1.8 2003/08/12
 *  11:52:11 lemired Added more regression testing. Revision 1.7 2003/08/07
 *  13:16:05 lemired More javadoc improvments. Revision 1.6 2003/08/07 00:37:42
 *  lemired Mostly, I updated the javadoc.
 *
 *@author     Daniel Lemire
 *@created    August 21, 2003
 *@since      July 2003
 */
public final class EigenMatch extends PerItemAverage {
  /**
   *  Just contains a bunch of vectors
   */
  protected Vector mW = new Vector();
  
  protected int mOrder;
  boolean mSubtractAverage;

  /**
   *  Whether we have an optimal solution for real!
   */
  protected boolean mConverged = true;

    double mTol;
    int mMaxIters;
    boolean mNormalMatch;
  
   public EigenMatch( EvaluationSet set ) {
     this(set, 1, 1E-10,1000, true, false);
     //int Order, float mTol /*1E-10*/, int MaxIters
   }
   
   
   public EigenMatch( EvaluationSet set , int order, boolean SubtractAverage, boolean NormalMatch) {
     this(set, order, 1E-10,1000, SubtractAverage, NormalMatch);
     //int Order, float mTol /*1E-10*/, int MaxIters
   }
 
  /**
   *  Constructor for the OptimalWeight object
   *
   * Order must be a non-zero positive number!
   * 
   *@param  set    the training set
   *@param  Order  Description of the Parameter
   */
  public EigenMatch( EvaluationSet set, int Order, double Tol /*1E-10*/, int MaxIters, boolean SubtractAverage , boolean NormalMatch) {
    super( set );
    if(Order < 1) throw new IllegalArgumentException("Order must be at least one, it is "+Order);
    mOrder = Order;
    mNormalMatch = NormalMatch;
    mTol = Tol;
    mMaxIters = MaxIters;
    mSubtractAverage = SubtractAverage;
    if(Order <= 0) throw new IllegalArgumentException("Order must be a non-zero positive number!");
    computeVectors();
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
   *  Gets the vectors attribute of the STIOptimalWeight object
   *
   *@return    The vectors value
   */
  public float[][] getVectors() {
    return (float[][]) mW.toArray();
  }

  private float[][] computeMatrix(final boolean over_residual) {
    //System.out.println( "[debug] allocating " + ( mMaxItemID * mMaxItemID * 8 ) / ( 1024.0 * 1024.0 ) + " Megs" );
    float[][] A = new float[mMaxItemID][mMaxItemID];
    //System.out.println( "[debug] allocation ok!" );
    TIntObjectIterator t = mSet.iterator();
    while ( t.hasNext() ) {
      t.advance();
      TIntFloatHashMap u = (TIntFloatHashMap) t.value();
      if((mSubtractAverage) && (! over_residual)){
        u = (TIntFloatHashMap) u.clone();
        float average = UtilMath.average(u);
        UtilMath.subtract(u,average);
      }
      if( over_residual ) {
        u = (TIntFloatHashMap) u.clone();
        float[] p = completeUser(u);
        TIntFloatIterator j = u.iterator();
        while ( j.hasNext() ) {
          j.advance();
          j.setValue(j.value() - p[j.key()]);
        }
      }
      float uu = selfWeightedProduct(u);
      if(uu == 0.0) continue;
      TIntFloatIterator j1 = u.iterator();
      while ( j1.hasNext() ) {
        j1.advance();
         if( mPerItemAverageFrequency[j1.key()] <= 0) continue;
        TIntFloatIterator j2 = u.iterator();
        while ( j2.hasNext() ) {
          j2.advance();
          if( mPerItemAverageFrequency[j2.key()] <= 0) continue;
          A[j1.key()][j2.key()] += j1.value() /  mPerItemAverageFrequency[j1.key()] 
             * j2.value() / mPerItemAverageFrequency[j2.key()]
             * 1.0f /  mSet.size() 
             * 1.0f / uu ;
          if(A[j1.key()][j2.key()] != A[j1.key()][j2.key()]) 
            throw new CollaborativeFilteringException("You are in trouble!!!!! "+A[j1.key()][j2.key()]);
        }
      }
    }
    return A;
  }

  
  private float[] bestEigenVector(float[][] A) {
    float[] guess = new float[A[0].length];
    System.arraycopy(mPerItemAverage,0,guess,0,mPerItemAverage.length);
    normalize(guess);
    //System.out.println("frequencies");
    //UtilMath.print( mPerItemAverageFrequency );
    //System.out.println("pia values");
    //UtilMath.print( mPerItemAverage );
    //System.out.println("Normalized pia");
    //UtilMath.print(guess);
    //System.out.println();
    float[] newguess = multiply(A,guess, new float[A.length]);
    //UtilMath.print(newguess);
    double error = diff(newguess, guess);
    //System.out.println(error);
    int iteration = 1;
    while(error > mTol) {
      //System.out.println(error);
      if(++iteration > mMaxIters) break;
      float[] oldguess = guess;
      for (int k = 0; k < oldguess.length; ++k) oldguess[k] = 0.0f;
      guess = newguess;
      newguess = multiply(A,guess,oldguess);
      normalize(newguess);
      //UtilMath.print(newguess);
      //System.out.println();
      error = diff(newguess, guess);
    }
    if(error <= mTol) 
      mConverged = true;
    else {
      System.err.println("[WARNING] Failed to converge. Residual is "+error+" (iteration = "+iteration+")");
      mConverged = false;
    }
    return newguess;
  }
  
  private static double diff(float[] a, float[] b) {
    double diff = 0.0;
    for (int k = 0; k < a.length; ++k)
      diff += ( a[k] - b[k] ) * ( a[k] - b[k] );
    return diff;
  }
  
  private void normalize(float[] v) {
     double norm = 0.0f;
     for(int k = 0; k < v.length; ++k) {
       if( mPerItemAverageFrequency[k] > 0 )
         norm += v[k]*v[k] / mPerItemAverageFrequency[k]; 
     }
     norm = Math.sqrt(norm);
     if (norm == 0.0) return;
     for(int k = 0; k < v.length;++k)
       v[k] = (float) ( v[k] / norm );
     
  }
  
  private static float[] multiply(float[][] A, float[] x, float[] ans) {
    //float[] ans = new float[A.length];
    for(int k = 0; k < A.length; ++k) {
      float[] arow = A[k];
      for (int l = 0; l < A[k].length; ++l) {
        ans[k] += arow[l] * x[l];
      }
    }    
    return ans;
  }
  
  /**
   *  Compute the optimal vectors
   */
  protected void computeVectors() {
    float[] w = bestEigenVector(computeMatrix(false));
    mW.add(w);
    while(mW.size() < mOrder) {
      w = bestEigenVector(computeMatrix(true));
      mW.add(w);
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
    float average = UtilMath.average(u);
    //System.out.println("completing!");
    float[] answer = new float[mMaxItemID];
    // first, compute Gram-Schmidt for over S(u)
    // could be much faster, but this will do for now!
    Vector basis = new Vector();
    if(mSubtractAverage) {
      u = ( TIntFloatHashMap ) u.clone();
      UtilMath.subtract(u,average);
      float[] constant = new float[mMaxItemID];
      for (int k = 0; k < constant.length; ++k) constant[k] = 1.0f;
      basis.add( constant );
    }
    for ( int k = 0; k < mW.size(); ++k ) { 
      if((mNormalMatch ? UtilMath.scalarProduct((float[]) mW.get(k), (float[])mW.get(k), u ) : weightedProduct( (float[])mW.get(k), (float[])mW.get(k), u )) < UtilMath.epsilon) 
        continue; // don't bother!
      float[] newvec = new float[((float[])mW.get(k)).length];
      System.arraycopy( mW.get(k), 0, newvec, 0, newvec.length );
      for ( Enumeration e = basis.elements(); e.hasMoreElements();  ) {
        float[] last = (float[]) e.nextElement();
        float bottom = mNormalMatch ? UtilMath.scalarProduct( last, last, u ) : weightedProduct( last, last, u );
        float top = mNormalMatch ? UtilMath.scalarProduct( last, last, u ) : weightedProduct( newvec, last, u );
        float alpha = 0.0f;
        if ( bottom > 0.0f ) {
          alpha = top / bottom;
          if(alpha != 0.0f)
            UtilMath.subtractInPlace( newvec, alpha, last );
        }
      }
      basis.add( newvec );
    }
    for ( Enumeration e = basis.elements(); e.hasMoreElements();  ) {
      float[] last = (float[]) e.nextElement();
      float bottom = mNormalMatch ? UtilMath.scalarProduct( last, last, u ) : weightedProduct( last, last, u );
      float top = mNormalMatch ? UtilMath.scalarProduct( u, last ) : weightedProduct( u, last );
      float alpha = 0.0f;
      if ( bottom > 0.0f ) {
        alpha = top / bottom;
      }
      //System.out.println("alpha = "+alpha);
      UtilMath.addInPlace( answer, alpha, last );
    }
    if(mSubtractAverage) {
      for(int k = 0 ; k < answer.length; ++k) {
        answer[k] += average;
      }
    }
    return answer;
  }


  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public String toString() {
    return "EigenMatch_mConverged=" + mConverged + "_Order=" + mW.size()+ "_SubtractAverage="+ mSubtractAverage;
  }
  
  public float weightedProduct(float[] a, float[] b, TIntFloatHashMap over) {
    TIntFloatIterator iter = over.iterator();
    float sum = 0.0f;
    while(iter.hasNext()) {
      iter.advance();
      if(mPerItemAverageFrequency[iter.key()] > 0)
        sum += a[iter.key()] * b[iter.key()] / mPerItemAverageFrequency[iter.key()]; 
    }
    return sum;
  }

  public float weightedProduct(TIntFloatHashMap u, float[] v) {
    TIntFloatIterator uiter = u.iterator();
    float sum = 0.0f;
    while(uiter.hasNext()) {
      uiter.advance();
      if( mPerItemAverageFrequency[uiter.key()] > 0 )
      sum += uiter.value() * v[uiter.key()] / mPerItemAverageFrequency[uiter.key()];
      // else, I don't know... for now, just ignore them!
    }
    return  sum ;
  }

  
  public float weightedProduct(TIntFloatHashMap u, TIntFloatHashMap v) {
    TIntFloatIterator uiter = u.iterator();
    float sum = 0.0f;
    while(uiter.hasNext()) {
      uiter.advance();
      if(!v.containsKey(uiter.key())) continue;
      if( mPerItemAverageFrequency[uiter.key()] > 0 )
      sum += uiter.value() * v.get(uiter.key()) / mPerItemAverageFrequency[uiter.key()];
      // else, I don't know... for now, just ignore them!
    }
    return  sum ;
  }

  public float selfWeightedProduct(TIntFloatHashMap u) {
    TIntFloatIterator uiter = u.iterator();
    float sum = 0.0f;
    while(uiter.hasNext()) {
      uiter.advance();
      if( mPerItemAverageFrequency[uiter.key()] > 0 )
      sum += uiter.value() * uiter.value() / mPerItemAverageFrequency[uiter.key()];
      // else, I don't know... for now, just ignore them!
    }
    return  sum ;
  }
}


