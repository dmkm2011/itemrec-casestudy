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

package cofi.algorithms.composition;

import cofi.algorithms.*;
import cofi.algorithms.util.*;
import cofi.data.*;
import gnu.trove.*;

/**
 *  Most basis CFS. $Id: BiasFrom.java,v 1.1 2003/12/04 03:53:32 lemired Exp $
 *  $Date: 2003/12/04 03:53:32 $ $Author: lemired $ $Revision: 1.1 $ $Log:
 *  Average.java,v $ Revision 1.3 2003/11/11 13:25:58 lemired Added gpl headers
 *  Revision 1.2 2003/10/28 01:43:08 lemired Lots of refactoring. Revision 1.1
 *  2003/10/27 17:21:15 lemired Putting some order Revision 1.8 2003/08/22
 *  13:38:22 howsen *** empty log message *** Revision 1.7 2003/08/21 18:04:29
 *  lemired Added toString method plus added necessary activation.jar for
 *  convenience. Revision 1.6 2003/08/07 00:37:42 lemired Mostly, I updated the
 *  javadoc.
 *
 *@author     Daniel Lemire
 *@created    November 11, 2003
 *@since      December 2002
 */
public class BiasFrom
     extends CollaborativeFilteringSystem {
       
   CollaborativeFilteringSystem mCFS;
   float[] mBias;

  /**
   *  Constructor for the Average object
   *
   *@param  set  The EvaluationSet you want to work on
   */
  public BiasFrom( CollaborativeFilteringSystem cfs ) {
    super( cfs.getTrainingSet());
    mCFS = cfs;
    measureBias();
  }
  
  private void measureBias() {
    int[] Frequency = new int[mMaxItemID];
    mBias = new float[mMaxItemID];
    TIntObjectIterator t = mSet.iterator();
    int TotalNumber = 0;
    while ( t.hasNext() ) {
      t.advance();
      TIntFloatHashMap u = (TIntFloatHashMap) t.value();
      float[] predict = mCFS.completeUser(u);
      TIntFloatIterator uiter = u.iterator();
      while ( uiter.hasNext() ) {
        uiter.advance();
        ++Frequency[uiter.key()];
        mBias[uiter.key()] += (uiter.value() - predict[uiter.key()]);
      }
    }
    for ( int k = 0; k < mBias.length; ++k )
      if ( Frequency[k] > 0 )
        mBias[k] /= (float) Frequency[k];
  }


  /**
   *  Return an array that contains predictions for the ratings of the given
   *  user.
   *@param  u  a set of one-dimensional ratings
   *@return    an array containing predictions
   */
  public float[] completeUser( TIntFloatHashMap u ) {
    float[] complete = mCFS.completeUser(u);
    for ( int k = 0; k < mMaxItemID; ++k )
      complete[k] += mBias[k];
    return complete;
  }



  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public String toString() {
    return "BiasFrom__"+mCFS.toString();
  }
}

