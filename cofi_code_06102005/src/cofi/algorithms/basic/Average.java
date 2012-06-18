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
import gnu.trove.map.hash.TIntFloatHashMap;

/**
 *  Most basis CFS. $Id: Average.java,v 1.4 2003/11/11 15:54:24 lemired Exp $
 *  $Date: 2003/11/11 15:54:24 $ $Author: lemired $ $Revision: 1.4 $ $Log:
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
public class Average
     extends CollaborativeFilteringSystem {

  /**
   *  Constructor for the Average object
   *
   *@param  set  The EvaluationSet you want to work on
   */
  public Average( EvaluationSet set ) {
    super( set );
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
    int[] indices = u.keys();
    if ( indices.length == 0 )
      return new float[mMaxItemID];
    float average = UtilMath.average( u.values() );
    // else compute the average
    float[] complete = new float[mMaxItemID];
    for ( int k = 0; k < mMaxItemID; ++k )
      complete[k] = average;

    return complete;
  }


  /**
   *  Updates the buffer of the algorithm when a user enters a new rating. For
   *  average, this does nothing.
   *
   *@param  u  User as it were before changes
   */
  /*
   *  public void updateUser( TIntFloatHashMap u, int itemNum, float newVal ) {
   *  }
   */
  /**
   *  This must called after you remove a user
   *
   *@param  u  the evaluation
   */
  public void removedUser( TIntFloatHashMap u ) {
    // nothing to do!!!
  }


  /**
   *  This must called after you add a user
   *
   *@param  u  the evaluation
   */
  public void addedUser( TIntFloatHashMap u ) {
    // nothing to do!!!
  }


  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public String toString() {
    return "Average";
  }
}

