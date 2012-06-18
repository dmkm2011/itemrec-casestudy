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
import JSci.maths.matrices.*;
import JSci.maths.vectors.*;

import cofi.data.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.util.*;
import cofi.algorithms.*;
import gnu.trove.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

import java.io.*;
import java.util.*;

/**
 * Obselete class.
 *
 *  This class was originally meant to represent an STI version of the
 * optimal weight collaborative filtering system. 
 *  In fact, it wouldn't be "scale" invariant
 * and "translation invariant in a weak sense".
 *
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
public class STIOptimalWeight extends PerItemAverage {
  /**
   *  Just contains a bunch of vectors
   */
  protected float[][] mW;

  /**
   *  Whether we have an optimal solution for real!
   */
  protected boolean mConverged = true;

  protected boolean mMu = false;
  protected boolean mTranslation = true;
  /**
   *  Constructor for the OptimalWeight object
   *
   * Order must be a non-zero positive number!
   * 
   *@param  set    the training set
   *@param  Order  Description of the Parameter
   */
  public STIOptimalWeight( EvaluationSet set, int Order , boolean mu, boolean translation) {
    super( set );
    if(Order <= 0) throw new IllegalArgumentException("Order must be a non-zero positive number!");
    mW = new float[Order][mMaxItemID];
    mMu = mu;
    mTranslation = translation;
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
    return mW;
  }


  /**
   *  Compute the optimal vectors
   */
  protected void computeVectors() {
    long totalbefore = System.currentTimeMillis();
    System.out.println( "[debug] computeVectors" );
    System.out.println( "[debug] allocating " + ( mMaxItemID * mMaxItemID * 8 ) / ( 1024.0 * 1024.0 ) + " Megs" );
    double[][] A = new double[mMaxItemID][mMaxItemID];
    System.out.println( "[debug] allocation ok!" );

    TIntObjectIterator t = mSet.iterator();
    while ( t.hasNext() ) {
      t.advance();
      TIntFloatHashMap u = (TIntFloatHashMap) t.value();
      float average = UtilMath.average(u);
      float norm = 0.0f;
      if(mTranslation) 
        norm = UtilMath.lpnorm(u,average,2.0f);
      else
        norm = UtilMath.lpnorm(u,0.0f,2.0f);
      TIntFloatIterator j1 = u.iterator();
      while ( j1.hasNext() ) {
        j1.advance();
        TIntFloatIterator j2 = u.iterator();
        while ( j2.hasNext() ) {
          j2.advance();
          if(mTranslation) { 
            if(mMu)
              A[j1.key()][j2.key()] += (j1.value() - average ) * (j2.value()  - average)    / ( mSet.size() * mMaxItemID * mMaxItemID * norm * norm );
            else
              A[j1.key()][j2.key()] += (j1.value() - average ) * (j2.value()  - average)   * u.size()* u.size()  / ( mSet.size() * mMaxItemID * mMaxItemID * norm * norm );
          } else {
            if(mMu)
              A[j1.key()][j2.key()] += (j1.value() ) * (j2.value() )    / ( mSet.size() * mMaxItemID * mMaxItemID * norm * norm );
            else
              A[j1.key()][j2.key()] += (j1.value() ) * (j2.value() )   * u.size()* u.size()  / ( mSet.size() * mMaxItemID * mMaxItemID * norm * norm );

          }
        }
      }
    }
    try {
      System.out.println( "[debug] eigensolve" );
      long before = System.currentTimeMillis();
      DoubleSquareMatrix alpha = new DoubleSquareMatrix( A );
      DoubleVector[] beta = new DoubleVector[A[0].length];
      double[] eigen = LinearMath.eigenSolveSymmetric( alpha, beta );
      tri( eigen, beta );
      if(true) {
        double sum = 0.0f, energysum = 0.0f;
        for(int k = 0; k < eigen.length; ++k) {
          energysum += eigen[ k ] * eigen[ k ];
        }
        for(int k = 0; k < eigen.length; ++k) {
          sum += eigen[ k ];
        }
        for(int k = 0; k < 20; ++k)
          System.out.println("[temdebug] eigenvalue ["
        +	k + "] = "
        + eigen[k] +" ( "
        + eigen[k]*eigen[k]/energysum+", "
        + eigen[k]/sum +" , "
        +eigen[k]/Math.sqrt(energysum)+") ");
      }
      for ( int i = 0; i < mW.length; i++ )
        for ( int l = 0; l < beta[i].dimension(); l++ )
          mW[i][l] = (float) beta[i].getComponent( l );
      long after = System.currentTimeMillis();
      System.out.println( "[debug] eigensolve succeeded in " + ( after - before ) / 1024.0 + " s" );
    } catch ( MaximumIterationsExceededException nce ) {
      mConverged = false;
      nce.printStackTrace();
      // for now, we will throw an exception to debug it
      throw new CollaborativeFilteringException( nce.getMessage() );
    }
    long totalafter = System.currentTimeMillis();
    System.out.println( "[debug] in total it took " + ( totalafter - totalbefore ) / 1024.0 + " s" );
    if ( !mConverged )
      System.err.println( "[STIOptimalWeight] It wasn't possible to compute the coefficients optimally. Solutions might be invalid." );
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
    if(mTranslation)
      for(int k = 0; k < mMaxItemID; ++k) answer[k] = average;
    // first, compute Gram-Schmidt for over S(u)
    // could be much faster, but this will do for now!
    Vector basis = new Vector();
    basis.add( mW[0] );
    for ( int k = 1; k < mW.length; ++k ) {
      if(UtilMath.scalarProduct( mW[k], mW[k], u ) < UtilMath.epsilon) continue; // don't bother!
      float[] newvec = new float[mW[k].length];
      System.arraycopy( mW[k], 0, newvec, 0, newvec.length );
      for ( Enumeration e = basis.elements(); e.hasMoreElements();  ) {
        float[] last = (float[]) e.nextElement();
        float bottom = UtilMath.scalarProduct( last, last, u );
        float top = UtilMath.scalarProduct( newvec, last, u );
        float alpha = 0.0f;
        if ( bottom > 0.0f ) {
          alpha = top / bottom;
          if(alpha != 0.0f)
            UtilMath.subtractInPlace( newvec, alpha, last );
        }
      }
      basis.add( newvec );
    }
    //
    final float energy = UtilMath.scalarProduct( u, u );
    double current_energy = energy;
    if(energy == 0.0f) return answer;// don't bother
    // then compute the coefficients
    //System.out.println("top");
    for ( Enumeration e = basis.elements(); e.hasMoreElements();  ) {
      float[] last = (float[]) e.nextElement();
      float bottom = UtilMath.scalarProduct( last, last, u );
      float top = UtilMath.scalarProduct( u, last );
      float alpha = 0.0f;
      if ( bottom > 0.0f ) {
        alpha = top / bottom;
        current_energy -= top * top / bottom;
      //	System.out.println(current_energy / energy);
/*			if(false) { //current_energy < - 0.2) {// 20% tolerance
          // we check to see if the basis really is orthogonal
          for ( Enumeration e1 = basis.elements(); e1.hasMoreElements();  ) {
              float[] first = (float[]) e1.nextElement();
              for ( Enumeration e2 = basis.elements(); e2.hasMoreElements();  ) {
                float[] second = (float[]) e2.nextElement();
                double product = UtilMath.scalarProduct(first,second,u)/Math.sqrt(UtilMath.scalarProduct(first,first,u)*UtilMath.scalarProduct(second,second,u));
                System.out.print(Math.round(product * 10000) /10000 + " ");
              }
              System.out.println();
          }
          System.out.println("Energy: "+energy);
          double percent = 0.0f;
          for ( Enumeration e1 = basis.elements(); e1.hasMoreElements();  ) {
              float[] first = (float[]) e1.nextElement();
              float b = UtilMath.scalarProduct( first, first, u );
              float t = UtilMath.scalarProduct( u, first );
              if ( b > 0.0f ) {
                  System.out.println(t*t/b+" ("+(t*t/b)/energy+")");
                  percent += (t*t/b)/energy;
              } else {
                System.out.println(0.0f);
              }
          }
          System.out.println(" should be lower than 1 : "+percent);
          throw new CollaborativeFilteringException("Damn! A bug! Negative Energy Dectected!");
        }*/
      }
      //if (( top * top / bottom ) / energy < 0.001f) continue; // don't bother with bad matches!
      if( current_energy / energy < UtilMath.epsilon ) break; // do not continue matching
      UtilMath.addInPlace( answer, alpha, last );
    }
    return answer;
  }


  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public String toString() {
    return "STIOptimalWeight_mConverged_" + mConverged+"_Order_"+mW.length+"_mMu_"+mMu+"_mTranslation_"+mTranslation;
  }


  /**
   *  Selection sort
   *
   *@param  v    Description of the Parameter
   *@param  mat  Description of the Parameter
   */
  private static void tri( double[] v, DoubleVector[] mat ) {
    double temp;
    DoubleVector arraytemp;
    boolean doitTrier = true;
    while ( doitTrier ) {
      doitTrier = false;
      for ( int k = 0; k < v.length - 1; k++ )
        if ( v[k] < v[k + 1] ) {
          temp = v[k + 1];
          v[k + 1] = v[k];
          v[k] = temp;
          doitTrier = true;
          arraytemp = mat[k + 1];
          mat[k + 1] = mat[k];
          mat[k] = arraytemp;
        }
    }
  }
}


