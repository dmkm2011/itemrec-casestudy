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
public class SimpleBiConstantBias extends BiConstantBias {

  public SimpleBiConstantBias( EvaluationSet set) {
    super( set );
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
      if( j.value() > average ) {
        for ( int k = 0; k < mMaxItemID; ++k ) {
            if( k == j.key()) continue;
            answer[k] += mHigh[k][j.key()] * mFrequencyHigh[k][j.key()];
            freq[k] += mFrequencyHigh[k][j.key()];
        }
      } else if (j.value() < average) {
        for ( int k = 0; k < mMaxItemID; ++k ) {
            if( k == j.key()) continue;
            answer[k] += mLow[k][j.key()]  * mFrequencyLow[k][j.key()];
            freq[k] += mFrequencyLow[k][j.key()];
        }
      }
    }
    for ( int k = 0; k < mMaxItemID; ++k ) {
      answer[k] += average;
      if(freq[k] > 0 ) answer[k] /= freq[k];
    }
    return answer;
  }
  
  public String toString() {
    return "SimpleBiConstantBias";
  }
  
}
