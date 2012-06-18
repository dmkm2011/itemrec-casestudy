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
import cofi.data.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

/**
 *  The simple "per item average" O(1) scheme. $Id: PerItemAverage.java,v 1.3
 *  2003/11/11 13:25:58 lemired Exp $ $Date: 2003/11/11 15:54:24 $ $Author:
 *  lemired $ $Revision: 1.4 $ $Log: PerItemAverage.java,v $
 *  lemired $ $Revision: 1.4 $ Revision 1.4  2003/11/11 15:54:24  lemired
 *  lemired $ $Revision: 1.4 $ Updated script some more
 *  lemired $ $Revision: 1.4 $ Revision 1.3
 *  2003/11/11 13:25:58 lemired Added gpl headers Revision 1.2 2003/10/28
 *  01:43:08 lemired Lots of refactoring. Revision 1.1 2003/10/27 17:21:15
 *  lemired Putting some order Revision 1.16 2003/08/22 13:38:23 howsen ***
 *  empty log message *** Revision 1.15 2003/08/21 18:04:29 lemired Added
 *  toString method plus added necessary activation.jar for convenience.
 *  Revision 1.14 2003/08/12 11:52:11 lemired Added more regression testing.
 *  Revision 1.13 2003/08/08 13:10:01 ballm Partially fixed the add rating page
 *  - "s were in the wrong place Revision 1.12 2003/08/08 03:23:22 lemired
 *  addedUser/removedUser was broken in most implementation. I fixed that now.
 *  Revision 1.11 2003/08/07 15:31:01 lemired This should fix the problem
 *  reported by Marcel. Revision 1.10 2003/08/07 13:16:05 lemired More javadoc
 *  improvments. Revision 1.9 2003/08/07 00:37:42 lemired Mostly, I updated the
 *  javadoc.
 *
 *@author     Daniel Lemire
 *@created    November 11, 2003
 *@since      December 2002
 */
public class PerItemAverage
     extends CollaborativeFilteringSystem {

  /**
   *  Description of the Field
   */
  public float[] mPerItemAverage;
  /**
   *  Description of the Field
   */
  public int[] mPerItemAverageFrequency;


  /**
   *  Constructor for the PerItemAverage object
   *
   *@param  set  the training set
   */
  public PerItemAverage( EvaluationSet set ) {
    super( set );
    computePerItemAverage();
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
    return mPerItemAverage;
  }


  /**
   *  As the name implies, it computes the per item average. This method is
   *  called only once.
   */
  private void computePerItemAverage() {
    mPerItemAverageFrequency = new int[mMaxItemID];
    mPerItemAverage = new float[mMaxItemID];
    TIntObjectIterator t = mSet.iterator();
    int TotalNumber = 0;
    while ( t.hasNext() ) {
      t.advance();
      TIntFloatIterator uiter = ( (TIntFloatHashMap) t.value() ).iterator();
      while ( uiter.hasNext() ) {
        uiter.advance();
        ++mPerItemAverageFrequency[uiter.key()];
        mPerItemAverage[uiter.key()] += uiter.value();
      }
    }
    for ( int k = 0; k < mPerItemAverage.length; ++k )
      if ( mPerItemAverageFrequency[k] > 0 )
        mPerItemAverage[k] /= (float) mPerItemAverageFrequency[k];

  }


  /**
   *  This must called after you remove a user
   *
   *@param  u  evaluation which was removed
   */
  public void removedUser( TIntFloatHashMap u ) {
    TIntFloatIterator uiter = u.iterator();
    while ( uiter.hasNext() ) {
      uiter.advance();
      if ( mPerItemAverageFrequency[uiter.key()] <= 0 )
        throw new CollaborativeFilteringException(
            "You are trying to remove a user with rating on item " +
            uiter.key() + " whereas there is no such user" );
      mPerItemAverage[uiter.key()]
           -= uiter.value() / (float) mPerItemAverageFrequency[uiter.key()];
      --mPerItemAverageFrequency[uiter.key()];
      if ( mPerItemAverageFrequency[uiter.key()] > 0 )
        mPerItemAverage[uiter.key()] *=
            ( (float) mPerItemAverageFrequency[uiter.key()] + 1 ) /
            ( (float) mPerItemAverageFrequency[uiter.key()] );

    }
  }


  /**
   *  This must called after you add a user
   *
   *@param  u  evaluation which was added
   */
  public void addedUser( TIntFloatHashMap u ) {
    TIntFloatIterator uiter = u.iterator();
    while ( uiter.hasNext() ) {
      uiter.advance();
      //System.out.println("Adduser Key: " + uiter.key());
      mPerItemAverage[uiter.key()] *=
          ( (float) mPerItemAverageFrequency[uiter.key()] ) /
          ( (float) mPerItemAverageFrequency[uiter.key()] + 1 );
      ++mPerItemAverageFrequency[uiter.key()];
      mPerItemAverage[uiter.key()]
           += uiter.value() / mPerItemAverageFrequency[uiter.key()];
    }
  }


  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public String toString() {
    return "PerItemAverage";
  }

}

