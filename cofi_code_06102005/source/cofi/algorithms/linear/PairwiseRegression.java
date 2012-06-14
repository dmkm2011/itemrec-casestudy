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
import gnu.trove.*;

/**
 *  This is an implementation of the Anna collaborative filtering method. $Id: PairwiseRegression.java,v 1.1 2003/12/07 21:55:40 lemired Exp $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ $Log: PairwiseRegression.java,v $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.1  2003/12/07 21:55:40  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ More hacking
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.1  2003/12/05 21:47:08  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ I think I finally got rule of 3 right
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.4  2003/12/04 03:49:23  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Some hacking after my discussions with ANna
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.3  2003/11/30 22:03:46  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Will now prevent 0/0 bug
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.2  2003/11/24 16:33:07  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Added ruleof3 scheme as a comparison
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.1  2003/11/24 16:31:15  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Getting ready...
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.2  2003/11/17 19:28:31  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ New BiRuleOf3 scheme
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.1  2003/11/17 18:43:07  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Added new BiRuleOf3
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.2  2003/11/12 17:37:51  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Had a bug, fixed it
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.1  2003/11/12 17:36:20  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Adding BiRuleOf6
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.5  2003/11/11 13:25:58  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Added gpl headers
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.4  2003/11/09 23:48:34  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ progres with Anna
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.3  2003/10/31 00:47:07  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Still got a bug in TIOptimalWeight... it should be better than average... arghh!
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.2  2003/10/28 01:43:08  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Lots of refactoring.
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.1  2003/10/27 17:21:15  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Putting some order
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.4  2003/10/07 13:28:32  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Did some tweaking...
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.3  2003/09/29 14:37:33  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Fixed the optimalweight scheme.
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.2  2003/09/25 23:17:22  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Finished Anna code.
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Revision 1.1  2003/09/25 18:52:18  lemired
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $ Anna's algorithm was added.
 *  $Date: 2003/12/07 21:55:40 $ $Author: lemired $ $Revision: 1.1 $
 *
 *@author     Daniel Lemire
 *@created    September 25, 2003
 *@since      September 2003
 */
public class PairwiseRegression extends CollaborativeFilteringSystem {

  float[][][] mAnnaWeight;
  short[][] mFrequency;
  int mDegree;
  boolean mSubtractAverage = true;
  
  public PairwiseRegression( EvaluationSet set, int degree, boolean subtractaverage ) {
    super( set );
    mDegree = degree;
    mSubtractAverage = subtractaverage;
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
    int[] freq = new int[mMaxItemID];
    TIntFloatIterator j = u.iterator();
    while ( j.hasNext() ) {
      j.advance();
      if( !mSubtractAverage || (j.value() != average)) {
        for ( int k = 0; k < mMaxItemID; ++k ) {
          if( k == j.key()) continue;
          float moment = 1;
          answer[k] += mAnnaWeight[k][j.key()][0] * moment * mFrequency[k][j.key()]; 
          for(int d = 1; d < mDegree + 1; ++d) {
            if( mSubtractAverage ) 
              moment *= ( j.value() - average);
            else 
              moment *= ( j.value() /*- average*/);
            answer[k] += mAnnaWeight[k][j.key()][d] * moment * mFrequency[k][j.key()]; 
          }
          freq[k]+=mFrequency[k][j.key()];
        }          
      }
    }
    for ( int k = 0; k < mMaxItemID; ++k ) {
      if(freq[k] > 0) {
        answer[k] /= freq[k]; 
        if( mSubtractAverage ) answer[k] += average;
      } else {
        answer[k] = average;
      }
    }
    /*j = u.iterator();
    while ( j.hasNext() ) {
      j.advance();
      answer[j.key()] = j.value();
    }*/
    return answer;
  }
  

  /**
   *  As the name implies, it computes Anna's weight. This method is called only
   *  once. Anna here is Anna Maclachlan, researcher and colleague at NRC.
   */
  private void computeAnnaWeight() {
    mAnnaWeight = new float[mMaxItemID][mMaxItemID][mDegree + 1];
    float[][][] moments = new float[mMaxItemID][mMaxItemID][2 * mDegree + 1];
    float[][][] LeftSide = new float[mMaxItemID][mMaxItemID][2 * mDegree + 1];
    mFrequency = new short[mMaxItemID][mMaxItemID];
    TIntObjectIterator t = mSet.iterator();
    while ( t.hasNext() ) {
      t.advance();
      TIntFloatHashMap CurrentEvaluation = (TIntFloatHashMap) t.value();
      float average = UtilMath.average(CurrentEvaluation);
      TIntFloatIterator k = CurrentEvaluation.iterator();
      while ( k.hasNext() ) {
        k.advance();
        if ( k.value() == average )
          continue;// we cannot divide by zero!
        TIntFloatIterator l = CurrentEvaluation.iterator();
        while ( l.hasNext() ) {
          l.advance();
          if (!mSubtractAverage || ( l.value() != average)) {
            ++mFrequency[l.key()][k.key()];
            float moment = 1.0f;
            float leftmoment = (k.value() /*- average*/);
            if( mSubtractAverage ) leftmoment -= average; 
            moments[l.key()][k.key()][0] += moment;
            LeftSide[l.key()][k.key()][0] += leftmoment; 
            for(int d = 1; d < 2 * mDegree + 1; ++d) {
              if( mSubtractAverage ) moment *=  (l.value() - average); else moment *=  l.value();
              if( mSubtractAverage ) leftmoment *=  (l.value() - average); else leftmoment *=  l.value();
              moments[l.key()][k.key()][d] += moment;
              LeftSide[l.key()][k.key()][d] += leftmoment;
            }
          }
        }
      }
    }
    for ( int k = 0; k < mMaxItemID; ++k ) {
      for ( int l = 0; l < mMaxItemID; ++l ) {
        if ( mFrequency[k][l] > 0 ) {
          double[][] matrix = new double[mDegree + 1][mDegree + 1];
          double[] b = new double[mDegree + 1];
          for(int d1 = 0; d1 < mDegree + 1; ++d1) {
            for(int d2 = 0; d2 < mDegree + 1; ++d2) {
              matrix[d1][d2] = moments[k][l][d1+d2];
            }
            b[d1] = LeftSide[k][l][d1];
          }
          //System.out.println("Matrix problem");
          //UtilMath.print(matrix);
          //System.out.println();
          //UtilMath.print(b);
          //System.out.println();
          try {
            double[] x = SymmLQ.solve(matrix,b);
            //UtilMath.print(x);
            //System.out.println();
            for (int d = 0; d < mDegree + 1; ++d) {
              mAnnaWeight[l][k][d] = (float)x[d];
            }
          } catch(NoConvergenceException e) {
              throw new CollaborativeFilteringException();
          }
          //System.out.println("k = "+k+" l = "+l);
          //UtilMath.print(moments[k][l]);
          //UtilMath.print(LeftSide[k][l]);
          //UtilMath.print(mAnnaWeight[l][k]);
          
        }
      }
    }
  }

  public String toString() {
    return "PairwiseRegression_degree_"+mDegree+"_subaverage_"+mSubtractAverage;
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
      test.put(0,10);
      //test.put(1,2*10+3);
      System.out.println("-------------");
      PairwiseRegression pr0 = new PairwiseRegression(es,0,false);
      UtilMath.print(pr0.completeUser(test));
      System.out.println("-------------");
      PairwiseRegression pr1 = new PairwiseRegression(es,1,false);
      UtilMath.print(pr1.completeUser(test));

  }
  
}

