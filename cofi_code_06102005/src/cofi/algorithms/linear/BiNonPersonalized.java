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
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

/**
 *  The biais from mean O(1) scheme. For research and non commercial purposes.
 *  $Id: BiNonPersonalized.java,v 1.3 2003/11/12 17:36:20 lemired Exp $ $Date:
 *  2003/11/11 13:25:58 $ $Author: lemired $ $Revision: 1.3 $ $Log:
 *  NonPersonalized.java,v $ Revision 1.3 2003/11/11 13:25:58 lemired Added gpl
 *  headers Revision 1.2 2003/10/28 01:43:08 lemired Lots of refactoring.
 *  Revision 1.1 2003/10/27 17:21:15 lemired Putting some order Revision 1.13
 *  2003/08/22 13:38:23 howsen *** empty log message *** Revision 1.12
 *  2003/08/21 18:04:29 lemired Added toString method plus added necessary
 *  activation.jar for convenience. Revision 1.11 2003/08/08 03:23:22 lemired
 *  addedUser/removedUser was broken in most implementation. I fixed that now.
 *  Revision 1.10 2003/08/07 13:16:05 lemired More javadoc improvments. Revision
 *  1.9 2003/08/07 00:37:42 lemired Mostly, I updated the javadoc.
 *
 *@author     Daniel Lemire
 *@created    November 11, 2003
 *@since      December 2002
 */
public class BiNonPersonalized
     extends CollaborativeFilteringSystem {

  /**
   *  Per item bias from mean
   */
  protected float[] mItemAverageWithoutMean;
  /**
   *  Frequency of each item in terms of ratings
   */
  protected int[] mItemFrequencyLow;
  protected int[] mItemFrequencyHigh;
  protected float[] mItemAverageWithoutMeanLow;
  protected float[] mItemAverageWithoutMeanHigh;


  /**
   *  Constructor for the NonPersonalized object
   *
   *@param  set  the training set
   */
  public BiNonPersonalized( EvaluationSet set ) {
    super( set );
    computeItemAverage();
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
    float average = UtilMath.average( u );
    float[] answer = new float[mMaxItemID];
    for ( int k = 0; k < mMaxItemID; ++k )
      answer[k] = mItemAverageWithoutMean[k] + average;
    return answer;
  }


  /**
   *  Compute the average for each item
   */
  protected void computeItemAverage() {
    //mItemFrequency = new int[mMaxItemID];
    mItemAverageWithoutMean = new float[mMaxItemID];
    mItemFrequencyHigh = new int[mMaxItemID];
    mItemAverageWithoutMeanHigh = new float[mMaxItemID];
    mItemFrequencyLow = new int[mMaxItemID];
    mItemAverageWithoutMeanLow = new float[mMaxItemID];
    TIntObjectIterator t = mSet.iterator();
    int TotalNumber = 0;
    while ( t.hasNext() ) {
      t.advance();
      ++TotalNumber;
      TIntFloatHashMap RunningU = (TIntFloatHashMap) t.value();
      float average = UtilMath.average( RunningU );
      TIntFloatIterator uiter = ( RunningU ).iterator();
      while ( uiter.hasNext() ) {
        uiter.advance();
        if(uiter.value() > average) {
          mItemFrequencyHigh[uiter.key()] += 1;
          mItemAverageWithoutMeanHigh[uiter.key()]
             += ( uiter.value() - average );
        } else if(uiter.value() < average) {
          mItemFrequencyLow[uiter.key()] += 1;
          mItemAverageWithoutMeanLow[uiter.key()]
             += ( uiter.value() - average );
        }
      }
    }
    for ( int k = 0; k < mItemAverageWithoutMean.length; ++k ) {
      if ( mItemFrequencyHigh[k] > 0 )
        mItemAverageWithoutMeanHigh[k] /= mItemFrequencyHigh[k];
      if ( mItemFrequencyLow[k] > 0 )
        mItemAverageWithoutMeanLow[k] /= mItemFrequencyLow[k];
      if ( -mItemAverageWithoutMeanLow[k] < mItemAverageWithoutMeanHigh[k] ) {
        mItemAverageWithoutMean[k] = mItemAverageWithoutMeanHigh[k];
      } else {
        mItemAverageWithoutMean[k] = mItemAverageWithoutMeanLow[k]; 
      }
      if( mItemFrequencyHigh[k] > mItemFrequencyLow[k]) 
        mItemAverageWithoutMean[k] = mItemAverageWithoutMeanHigh[k];
      else if ( mItemFrequencyHigh[k] == mItemFrequencyLow[k])
        mItemAverageWithoutMean[k] = 0.0f;
      else 
        mItemAverageWithoutMean[k] = mItemAverageWithoutMeanLow[k];
    }
  }

  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public String toString() {
    return "BiNonPersonalized";
  }
}

