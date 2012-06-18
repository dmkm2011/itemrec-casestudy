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

import cofi.data.*;
import cofi.algorithms.*;
import cofi.algorithms.util.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

/**
 *  Just implement the b-a+c=x imagined by Daniel and Anna
 *@author     Daniel Lemire
 */
public class FittedConstantBias extends CollaborativeFilteringSystem {

  float[][] mAnnaWeight, mVariance;
  short[][] Frequency;

  
  public FittedConstantBias( EvaluationSet set) {
    super( set );
    computeAnnaWeight();
  }
  

  /**
   *  Return an array that contains predictions for the ratings of the given
   *  user.
   *
   *@param  u  a set of one-dimensional ratings
   *@return    an array containing predictions
   */
  public float[] completeUser( TIntFloatHashMap u ) {
    float average = UtilMath.average(u);
    float[] answer = new float[mMaxItemID];
    //float[] freq = new float[mMaxItemID];
    double[] values = new double[u.size()];
    double[][] experts = new double[u.size()][u.size()];
    int k = 0;
    TIntFloatIterator j = u.iterator();
    while ( j.hasNext() ) {
      j.advance();
      values[k] = j.value();
      TIntFloatIterator i = u.iterator();
      int l = 0;
      while (i.hasNext()) {
        i.advance();
        experts[k][l] = mAnnaWeight[j.key()][i.key()] +i.value();
        ++l;
      }
      ++k;
    }
    
    //System.out.println("-experts-");
    //UtilMath.print(experts);
    //System.out.println("-values-");
    //UtilMath.print(values);
    try {
            double[] coefs = JSciSolver.solve(experts,values);
            /*double[] coefs = new double[experts.length];
            float sum = 0;
            for( k = 0; k < coefs.length; ++k) {
              coefs[k] = UtilMath.correlation(experts[k],values);
              if(coefs[k] < 0) coefs[k] = 0; 
              //coefs[k] *= Math.abs(coefs[k]);
              sum += Math.abs(coefs[k]);
            }
            for(k = 0; k < coefs.length; ++k) {
              coefs[k] /= sum;
            }            
        //    System.out.println("- coefs-");
      //       UtilMath.print(coefs);*/
            j = u.iterator();
            int l = 0;
            while(j.hasNext()) {
              j.advance();
              for ( k = 0; k < mMaxItemID; ++k ) {  
                answer[k] +=  (mAnnaWeight[k][j.key()]+j.value() )* coefs[l];
              }
              ++l;
            }
            return answer;
    } catch(Exception e) {
              throw new CollaborativeFilteringException();
    }

  }
  

  /**
   *  As the name implies, it computes Anna's weight. This method is called only
   *  once. Anna here is Anna Maclachlan, researcher and colleague at NRC.
   */
  private void computeAnnaWeight() {
    mAnnaWeight = new float[mMaxItemID][mMaxItemID];
    short[][] Frequency = new short[mMaxItemID][mMaxItemID];
    TIntObjectIterator t = mSet.iterator();
    while ( t.hasNext() ) {
      t.advance();
      TIntFloatHashMap CurrentEvaluation = (TIntFloatHashMap) t.value();
//      float average = UtilMath.average(CurrentEvaluation);
      TIntFloatIterator k = CurrentEvaluation.iterator();
      while ( k.hasNext() ) {
        k.advance();
        TIntFloatIterator l = CurrentEvaluation.iterator();
        while ( l.hasNext() ) {
          l.advance();
          ++Frequency[l.key()][k.key()];
          mAnnaWeight[l.key()][k.key()] += l.value() - k.value();
        }
      }
    }
    //System.out.println("freq[0][...]");
    //UtilMath.print(Frequency[0]);
    for ( int k = 0; k < mMaxItemID; ++k )
      for ( int l = 0; l < mMaxItemID; ++l )
        if ( Frequency[k][l] > 0 )
          mAnnaWeight[k][l] /= Frequency[k][l];
      //  else mAnnaWeight[k][l] = 0.0f;
  }


  /**
   *  This must called after you remove a user
   *
   *@return    Description of the Return Value
   */
//   public void removedUser(TIntFloatHashMap u)
//  {
  //}

  /**
   *  This must called after you add a user
   *
   *@return    Description of the Return Value
   */
  //public void addedUser(TIntFloatHashMap u)
  //{
  //}

  public String toString() {
    return "FittedConstantBias";
  }
  
  public static void main(String[] args) {
     EvaluationSet es = new EvaluationSet();
     for( int k = 0; k < 10 ; ++k) {
        TIntFloatHashMap u = new TIntFloatHashMap();
        u.put(0,k);
        u.put(1,2*k+3);
        u.put(2,3*k+5);
        es.put(k,u);
      }
      es.setMaxItemID(3);
      TIntFloatHashMap test = new TIntFloatHashMap();
      test.put(0,9);
      test.put(1,11);
      //test.put(1,2*10+3);
      System.out.println("-------------");
      FittedConstantBias pr0 = new FittedConstantBias(es);
      UtilMath.print(pr0.completeUser(test));
      System.out.println("-------------");

  }
  
}

