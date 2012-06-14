/**
 *  (c) National Research Council of Canada, 2002-2003 by Daniel Lemire, Ph.D.
 *  Email lemire at ondelette dot com for support and details.
 */
/**
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation (version 2). This program is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details. You should have received a copy of
 *  the GNU General Public License along with this program; if not, write to the
 *  Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 *  02111-1307, USA.
 */
package cofi.algorithms.linear;
import cofi.algorithms.*;
import cofi.algorithms.util.*;

import cofi.data.*;
import gnu.trove.*;

/**
 *  Just implement the b-a+c=x imagined by Daniel and Anna
 *
 *@author     Daniel Lemire
 *@created    December 8, 2003
 */
public class ConstantBias extends CollaborativeFilteringSystem {

  float[][] mAnnaWeight, mVariance;
  short[][] mFrequency;
  float[] mfreq;
  int mType;


  /**
   *  Constructor for the ConstantBias object
   *
   *@param  set   Description of the Parameter
   *@param  Type  Description of the Parameter
   */
  public ConstantBias( EvaluationSet set, int Type ) {
    super( set );
    mType = Type;
    mfreq = new float[mMaxItemID];
    computeAnnaWeight();
    if ( mType > 0 )
      computeVarianceMatrix();
  }


  /**
   *  Return an array that contains predictions for the ratings of the given
   *  user.
   *
   *@param  u  a set of one-dimensional ratings
   *@return    an array containing predictions
   */
  public float[] completeUser( TIntFloatHashMap u ) {
    float average = UtilMath.average( u );
    float[] answer = new float[mMaxItemID];
    for ( int k = 0; k < mfreq.length; ++k )
      mfreq[k] = 0.0f;
    if ( mType == 3 )
      for ( int k = 0; k < mMaxItemID; ++k ) {
        int best = -1;
        float var = 0;
        float value = 0;
        TIntFloatIterator j = u.iterator();
        while ( j.hasNext() ) {
          j.advance();
          if ( ( best < 0 ) || ( var > mVariance[k][j.key()] ) ) {
            best = j.key();
            value = j.value();
            var = mVariance[k][j.key()];
          }
        }
        answer[k] = mAnnaWeight[k][best] + value;
      }

    if ( mType == 0 ) {
      TIntFloatIterator j = u.iterator();
      while ( j.hasNext() ) {
        j.advance();
        for ( int k = 0; k < mMaxItemID; ++k ) {
          if ( k == j.key() )
            continue;
          mfreq[k] += 1;
          answer[k] += ( mAnnaWeight[k][j.key()] + j.value() );
        }
      }
    }
    if ( mType == 1 ) {
      TIntFloatIterator j = u.iterator();
      while ( j.hasNext() ) {
        j.advance();
        for ( int k = 0; k < mMaxItemID; ++k ) {
          if ( k == j.key() )
            continue;
          mfreq[k] += 1 / (float) Math.sqrt( mVariance[k][j.key()] );
          answer[k] += ( mAnnaWeight[k][j.key()] + j.value() ) / (float) Math.sqrt( mVariance[k][j.key()] );
        }
      }

    }
    if ( mType == 2 ) {
      TIntFloatIterator j = u.iterator();
      while ( j.hasNext() ) {
        j.advance();
        for ( int k = 0; k < mMaxItemID; ++k ) {
          if ( k == j.key() )
            continue;
          mfreq[k] += 1 / mVariance[k][j.key()];
          answer[k] += ( mAnnaWeight[k][j.key()] + j.value() ) / mVariance[k][j.key()];
        }
      }

    }
    if ( mType == 4 ) {
      TIntFloatIterator j = u.iterator();
      while ( j.hasNext() ) {
        j.advance();
        for ( int k = 0; k < mMaxItemID; ++k ) {
          if ( k == j.key() )
            continue;
          mfreq[k] += mFrequency[k][j.key()];
          answer[k] += ( mAnnaWeight[k][j.key()] + j.value() ) * mFrequency[k][j.key()];
        }
      }

    }
    //int[] freq = new int[mMaxItemID];
    if(false) {
    TIntFloatIterator j = u.iterator();
    while ( j.hasNext() ) {
      j.advance();
      for ( int k = 0; k < mMaxItemID; ++k ) {
        if ( k == j.key() )
          continue;
        if ( mType == 0 ) {
          if( mFrequency[k][j.key()] > 0 ) {
            mfreq[k] += 1;
            answer[k] += ( mAnnaWeight[k][j.key()] + j.value() );
          }
        }
        else if ( mType == 1 ) {
          mfreq[k] += 1 / (float) Math.sqrt( mVariance[k][j.key()] );
          answer[k] += ( mAnnaWeight[k][j.key()] + j.value() ) / (float) Math.sqrt( mVariance[k][j.key()] );
        }
        else if ( mType == 2 ) {
          mfreq[k] += 1 / mVariance[k][j.key()];
          answer[k] += ( mAnnaWeight[k][j.key()] + j.value() ) / mVariance[k][j.key()];
        }
        else if ( mType == 4 ) {
          mfreq[k] += mFrequency[k][j.key()];
          answer[k] += ( mAnnaWeight[k][j.key()] + j.value() ) * mFrequency[k][j.key()];
        }
      }
    }
    }
    for ( int k = 0; k < mMaxItemID; ++k )
      if ( mfreq[k] > 0 )
        answer[k] /= mfreq[k];
      else
        answer[k] = average;
      //  answer[k] += average;

    return answer;
  }


  /**
   *  Description of the Method
   */
  private void computeVarianceMatrix() {
    mVariance = new float[mMaxItemID][mMaxItemID];
    //short [] Frequency = new short[mMaxItemID][mMaxItemID];
    TIntObjectIterator t = mSet.iterator();
    while ( t.hasNext() ) {
      t.advance();
      TIntFloatHashMap CurrentEvaluation = (TIntFloatHashMap) t.value();
      TIntFloatIterator k = CurrentEvaluation.iterator();
      while ( k.hasNext() ) {
        k.advance();
        TIntFloatIterator j = CurrentEvaluation.iterator();
        while ( j.hasNext() ) {
          j.advance();
          if ( j.key() == k.key() )
            continue;
          mVariance[k.key()][j.key()] += UtilMath.sqr( j.value() + mAnnaWeight[k.key()][j.key()] - k.value() );
        }
      }
    }
    for ( int k = 0; k < mMaxItemID; ++k )
      for ( int l = 0; l < mMaxItemID; ++l )
        if ( mFrequency[k][l] > 0 )
          mVariance[k][l] /= mFrequency[k][l];
  }


  /**
   *  Gets the weight attribute of the ConstantBias object
   *
   *@return    The weight value
   */
  public float[][] getWeight() {
    return mAnnaWeight;
  }


  /**
   *  As the name implies, it computes Anna's weight. This method is called only
   *  once. Anna here is Anna Maclachlan, researcher and colleague at NRC.
   */
  private void computeAnnaWeight() {
    mAnnaWeight = new float[mMaxItemID][mMaxItemID];
    mFrequency = new short[mMaxItemID][mMaxItemID];
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
          ++mFrequency[l.key()][k.key()];
          mAnnaWeight[l.key()][k.key()] += l.value() - k.value();
        }
      }
    }
    //System.out.println("freq[0][...]");
    //UtilMath.print(Frequency[0]);
    for ( int k = 0; k < mMaxItemID; ++k )
      for ( int l = 0; l < mMaxItemID; ++l )
        if ( mFrequency[k][l] > 0 )
          mAnnaWeight[k][l] /= mFrequency[k][l];
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
    return "ConstantBias" + mType;
  }

}

