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
 *  Just implement the b-a+c=x imagined by Daniel and Anna
 *@author     Daniel Lemire
 */
public class BiConstantBias extends CollaborativeFilteringSystem {

  float[][] mHigh, mLow;
  short[][] mFrequencyHigh, mFrequencyLow;
  float[] mfreq;
  
  public BiConstantBias( EvaluationSet set) {
    super( set );
    mfreq = new float[mMaxItemID];
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
    for (int k = 0; k < mfreq.length; ++k) mfreq[k] = 0.0f;
    TIntFloatIterator j = u.iterator();
    while ( j.hasNext() ) {
      j.advance();
      if( j.value() > average ) {
        for ( int k = 0; k < mMaxItemID; ++k ) {
            if( k == j.key()) continue;
            answer[k] += (mHigh[k][j.key()] + j.value()) * mFrequencyHigh[k][j.key()];
            mfreq[k] += mFrequencyHigh[k][j.key()];
        }
      } else if (j.value() < average) {
        for ( int k = 0; k < mMaxItemID; ++k ) {
            if( k == j.key()) continue;
            answer[k] += (mLow[k][j.key()] + j.value()) * mFrequencyLow[k][j.key()];
            mfreq[k] += mFrequencyLow[k][j.key()];
        }
      }
    }
    for ( int k = 0; k < mMaxItemID; ++k ) {
      if(mfreq[k] > 0 ) answer[k] /= mfreq[k];
      else answer[k] = average;
    }
    return answer;
  }
  

  /**
   *  As the name implies, it computes Anna's weight. This method is called only
   *  once. Anna here is Anna Maclachlan, researcher and colleague at NRC.
   */
  private void computeAnnaWeight() {
    mHigh = new float[mMaxItemID][mMaxItemID];
    mLow = new float[mMaxItemID][mMaxItemID];
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
        TIntFloatIterator l = CurrentEvaluation.iterator();
        while ( l.hasNext() ) {
          l.advance();
          if ((l.value() > average) && (k.value() > average)) {
            mHigh[l.key()][k.key()] += l.value() - k.value();
            ++mFrequencyHigh[l.key()][k.key()];
          } else if ((l.value() < average) && (k.value() < average)) {
            mLow[l.key()][k.key()] += l.value() - k.value();
            ++mFrequencyLow[l.key()][k.key()];
          }
        }
      }
    }
    //System.out.println("freq[0][...]");
    //UtilMath.print(Frequency[0]);
    for ( int k = 0; k < mMaxItemID; ++k )
    for ( int l = 0; l < mMaxItemID; ++l ) {
        if ( mFrequencyHigh[k][l] > 0 )
          mHigh[k][l] /= mFrequencyHigh[k][l];
      //  else mAnnaWeight[k][l] = 0.0f;
        if ( mFrequencyLow[k][l] > 0 )
          mLow[k][l] /= mFrequencyLow[k][l];
    }
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
    return "BiConstantBias";
  }
  
}

