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

import cofi.data.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

/**
 *  The biais from mean O(1) scheme. For research and non commercial purposes.
 *  $Id: ReverseNonPersonalized.java,v 1.1 2003/12/04 03:49:23 lemired Exp $ $Date:
 *  2003/11/11 13:25:58 $ $Author: lemired $ $Revision: 1.1 $ $Log:
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
public class ReverseNonPersonalized
     extends PerItemAverage {




  /**
   *  Constructor for the NonPersonalized object
   *
   *@param  set  the training set
   */
  public ReverseNonPersonalized( EvaluationSet set ) {
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
    TIntFloatIterator uiter = u.iterator();
    float sum = 0.0f;
    while (uiter.hasNext()) {
      uiter.advance();
      sum += uiter.value() - mPerItemAverage[uiter.key()];
    }
    float averagedeviation = sum / u.size();
    float[] answer = new float[mMaxItemID];
    for ( int k = 0; k < mMaxItemID; ++k )
      answer[k] = mPerItemAverage[k] + averagedeviation;
    return answer;
  }



  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public String toString() {
    return "ReverseNonPersonalized";
  }
}

