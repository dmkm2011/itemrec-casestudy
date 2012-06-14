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
 *  This is an implementation of the Anna collaborative filtering method. $Id: BiRuleOf5.java,v 1.4 2003/12/04 03:49:23 lemired Exp $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ $Log: BiRuleOf5.java,v $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.4  2003/12/04 03:49:23  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Some hacking after my discussions with ANna
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.3  2003/11/17 18:43:07  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Added new BiRuleOf3
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.2  2003/11/12 17:37:51  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Had a bug, fixed it
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.1  2003/11/12 17:36:20  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Adding BiRuleOf6
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.5  2003/11/11 13:25:58  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Added gpl headers
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.4  2003/11/09 23:48:34  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ progres with Anna
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.3  2003/10/31 00:47:07  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Still got a bug in TIOptimalWeight... it should be better than average... arghh!
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.2  2003/10/28 01:43:08  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Lots of refactoring.
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.1  2003/10/27 17:21:15  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Putting some order
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.4  2003/10/07 13:28:32  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Did some tweaking...
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.3  2003/09/29 14:37:33  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Fixed the optimalweight scheme.
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.2  2003/09/25 23:17:22  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Finished Anna code.
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Revision 1.1  2003/09/25 18:52:18  lemired
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $ Anna's algorithm was added.
 *  $Date: 2003/12/04 03:49:23 $ $Author: lemired $ $Revision: 1.4 $
 *
 *@author     Daniel Lemire
 *@created    September 25, 2003
 *@since      September 2003
 */
public class BiRuleOf5 extends CollaborativeFilteringSystem {

  float[][] mAnnaWeightHigh;
  float[][] mAnnaWeightLow;
  short[][] mFrequencyHigh;
  short[][] mFrequencyLow;
  
  public BiRuleOf5( EvaluationSet set) {
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
    int[] freq = new int[mMaxItemID];
    TIntFloatIterator j = u.iterator();
    while ( j.hasNext() ) {
      j.advance();
      if(j.value() > average) {
        for ( int k = 0; k < mMaxItemID; ++k ) {
            if( k == j.key()) continue;
            answer[k] += (mAnnaWeightHigh[k][j.key()] * ( j.value() - average )) * mFrequencyHigh[k][j.key()];
            freq[k] += mFrequencyHigh[k][j.key()];
        }
      } else if (j.value() < average) {
        for ( int k = 0; k < mMaxItemID; ++k ) {
            if( k == j.key()) continue;
            answer[k] += (mAnnaWeightLow[k][j.key()] * ( j.value() - average )) * mFrequencyLow[k][j.key()];
            freq[k] += mFrequencyLow[k][j.key()];
        }
      }
    }
    for ( int k = 0; k < mMaxItemID; ++k ) {
      if(freq[k] > 0) {
        answer[k] /= freq[k];
        answer[k] += average;
      } else {
        answer[k] = average;
      }
    }
 /*   j = u.iterator();
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
    mAnnaWeightHigh = new float[mMaxItemID][mMaxItemID];
    mAnnaWeightLow = new float[mMaxItemID][mMaxItemID];
    mFrequencyHigh = new short[mMaxItemID][mMaxItemID];
    mFrequencyLow = new short[mMaxItemID][mMaxItemID];
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
          if( l.value() == average) continue;
          if (( l.value() > average ) && (k.value() > average)) {
            ++mFrequencyHigh[l.key()][k.key()];
            mAnnaWeightHigh[l.key()][k.key()] += (l.value() - average) / (k.value() - average);
          }
          if (( l.value() < average) && (k.value() < average)) {
            ++mFrequencyLow[l.key()][k.key()];
            mAnnaWeightLow[l.key()][k.key()] += (l.value() - average) / (k.value() - average);
          }
        }
      }
    }
    for ( int k = 0; k < mMaxItemID; ++k )
    for ( int l = 0; l < mMaxItemID; ++l ) {
        if ( mFrequencyHigh[k][l] > 0 )
          mAnnaWeightHigh[k][l] /= mFrequencyHigh[k][l];
        if ( mFrequencyLow[k][l] > 0 )
          mAnnaWeightLow[k][l] /= mFrequencyLow[k][l];

    }
  }

  public String toString() {
    return "BiRuleOf5";
  }
  
}

