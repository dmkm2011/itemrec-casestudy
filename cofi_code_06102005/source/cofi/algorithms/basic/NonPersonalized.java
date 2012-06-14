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
package cofi.algorithms.basic;

import cofi.algorithms.*;
import cofi.algorithms.util.*;
import cofi.data.*;
import gnu.trove.*;

/**
 *  The biais from mean O(1) scheme. For research and non commercial purposes.
 *  $Id: NonPersonalized.java,v 1.4 2003/11/11 15:54:24 lemired Exp $ $Date:
 *  2003/11/11 13:25:58 $ $Author: lemired $ $Revision: 1.4 $ $Log:
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
public class NonPersonalized
     extends CollaborativeFilteringSystem {

  /**
   *  Per item bias from mean
   */
  protected float[] mItemAverageWithoutMean;
  /**
   *  Frequency of each item in terms of ratings
   */
  protected int[] mItemFrequency;


  /**
   *  Constructor for the NonPersonalized object
   *
   *@param  set  the training set
   */
  public NonPersonalized( EvaluationSet set ) {
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
    mItemFrequency = new int[mMaxItemID];
    mItemAverageWithoutMean = new float[mMaxItemID];
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
        mItemFrequency[uiter.key()] += 1;
        mItemAverageWithoutMean[uiter.key()]
             += ( uiter.value() - average );
      }
    }
    for ( int k = 0; k < mItemAverageWithoutMean.length; ++k )
      if ( mItemFrequency[k] > 0 )
        mItemAverageWithoutMean[k] /= mItemFrequency[k];
      else
        mItemAverageWithoutMean[k] = 0.0f;
  }

  /**
   *  This must called after you remove a user
   *
   *@param  u  the evaluation which was removed
   */
  public void removedUser( TIntFloatHashMap u ) {
    float average = UtilMath.average( u );
     {
      TIntFloatIterator uiter = u.iterator();
      while ( uiter.hasNext() ) {
        uiter.advance();
        if ( mItemFrequency[uiter.key()] == 0 )
          throw new CollaborativeFilteringException(
              " You are trying to remove a user who rated item " +
              uiter.key() + " yet there are no such users." );
        mItemAverageWithoutMean[uiter.key()]
             -= ( uiter.value() - average ) / (float) mItemFrequency[uiter.key()];
        --mItemFrequency[uiter.key()];
        if ( mItemFrequency[uiter.key()] > 0 )
          mItemAverageWithoutMean[uiter.key()] *=
              ( (float) mItemFrequency[uiter.key()] + 1 ) /
              ( (float) mItemFrequency[uiter.key()] );

      }
    }
  }


  /**
   *  This must called after you add a user
   *
   *@param  u  the evaluation which was added
   */
  public void addedUser( TIntFloatHashMap u ) {
    float average = UtilMath.average( u );
    TIntFloatIterator uiter = u.iterator();
    while ( uiter.hasNext() ) {
      uiter.advance();
      mItemAverageWithoutMean[uiter.key()] *=
          ( (float) mItemFrequency[uiter.key()] ) /
          ( (float) mItemFrequency[uiter.key()] + 1 );
      ++mItemFrequency[uiter.key()];
      mItemAverageWithoutMean[uiter.key()]
           += ( uiter.value() - average ) / mItemFrequency[uiter.key()];
    }
  }


  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public String toString() {
    return "NonPersonalized";
  }
}

