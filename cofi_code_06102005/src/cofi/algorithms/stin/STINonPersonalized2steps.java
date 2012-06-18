/**
* (c) National Research Council of Canada, 2002-2003 by Daniel Lemire, Ph.D.
* Email lemire at ondelette dot com for support and details.
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
package cofi.algorithms.stin;

import cofi.data.*;
import cofi.algorithms.util.*;
import cofi.algorithms.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;
import java.io.*;
import java.util.*;


/**
 *  The second order O(1) STI CFS (possibly best scheme). 
 *
 * 
 *  $Id: STINonPersonalized2steps.java,v 1.3 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $ 
 *  $Author: lemired $ 
 *  $Revision: 1.3 $ 
 *  $Log: STINonPersonalized2steps.java,v $
 *  Revision 1.3  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.2  2003/10/28 01:43:08  lemired
 *  Lots of refactoring.
 *
 *  Revision 1.1  2003/10/27 17:21:15  lemired
 *  Putting some order
 *
 *  Revision 1.24  2003/09/24 14:58:40  lemired
 *  Worked hard on eigenmatch.
 *
 *  Revision 1.23  2003/08/29 13:04:19  lemired
 *  Fixed again this class. Nancy did something to the class I did not understand. Reverted back to prior format.
 *
 *  Revision 1.20  2003/08/21 18:04:29  lemired
 *  Added toString method plus added necessary activation.jar for convenience.
 *
 *  Revision 1.19  2003/08/12 11:52:11  lemired
 *  Added more regression testing.
 *
 *  Revision 1.18  2003/08/08 03:23:22  lemired
 *  addedUser/removedUser was broken in most implementation. I fixed that now.
 *
 *  Revision 1.17  2003/08/07 15:31:01  lemired
 *   This should fix the problem reported by Marcel.
 *
 *  Revision 1.16  2003/08/07 13:16:05  lemired
 *  More javadoc improvments.
 *
 *  Revision 1.15  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author     Daniel Lemire
 *@since      December 2002
 */
public class STINonPersonalized2steps extends STINonPersonalized {

  double[] mItemAverageSecondOrder;
  int[] mItemFrequencySecondOrder;
  boolean mInfForSecondOrder = true;// whether to "cheat" and use linf instead of lp for second order

  /**
   *  Constructor for the STINonPersonalized2steps object
   *
   *@param  set  the training set
   *@param  p    which lp norm to user (p=2 is good)
   */
  public STINonPersonalized2steps( EvaluationSet set, float p ) {
    super( set, p );
    computeSecondOrderItemAverage();
  }

  /**
   *  Constructor for the STINonPersonalized2steps object
   *
   *@param  InfForSecondOrder  whether to use linf for second order (default is yes)
   *@param  set  the training set
   *@param  p    which lp norm to user (p=2 is good)
   */
  public STINonPersonalized2steps( EvaluationSet set, float p , boolean InfForSecondOrder) {
    super( set, p );
    mInfForSecondOrder = InfForSecondOrder;
    computeSecondOrderItemAverage();
  }


  /**
   *  Computer higher order averages
   */
  protected void computeSecondOrderItemAverage() {
    mItemFrequencySecondOrder = new int[mMaxItemID];
    mItemAverageSecondOrder = new double[mMaxItemID];
    TIntObjectIterator t = mSet.iterator();
    int TotalNumber = 0;
    while ( t.hasNext() ) {
      t.advance();
      ++TotalNumber;
      TIntFloatHashMap RunningU = new TIntFloatHashMap( (TIntFloatHashMap) t.value() );
      float average = UtilMath.average( RunningU );
      float[] completed = super.completeUser( RunningU , false);
      float linf;
      if(mInfForSecondOrder)
        linf =  UtilMath.linfdiff( RunningU, completed);
      else 
        linf =  UtilMath.lpdiff( RunningU, completed, mP );
      linf = UtilMath.lpnorm( RunningU, average, mP );
      if ( linf > UtilMath.epsilon ) {
        TIntFloatIterator uiter = RunningU.iterator();
        while ( uiter.hasNext() ) {
          uiter.advance();
          ++mItemFrequencySecondOrder[uiter.key()];
          mItemAverageSecondOrder[uiter.key()] += ( uiter.value() - completed[uiter.key()] ) / linf;
        }
      }
    }
    for ( int k = 0; k < mItemAverageSecondOrder.length; ++k )
      if ( mItemFrequencySecondOrder[k] > 0 )
        mItemAverageSecondOrder[k] /= mItemFrequencySecondOrder[k];
      else
        mItemAverageSecondOrder[k] = 0.0f;
  }


  public void addedUser( TIntFloatHashMap u ) {
    super.addedUser( u );
    float average = UtilMath.average( u );
    float[] completed = super.completeUser( u ,false);
    float newlinf ;//=UtilMath.lpdiff( u, completed, mP );// UtilMath.linfdiff( u, completed );
    if(mInfForSecondOrder)
        newlinf =  UtilMath.linfdiff( u, completed );
    else 
        newlinf =  UtilMath.lpdiff( u, completed, mP );
    if ( newlinf > UtilMath.epsilon ) {
      TIntFloatIterator uCloneiter = u.iterator();
      while ( uCloneiter.hasNext() ) {
        uCloneiter.advance();
        mItemAverageSecondOrder[uCloneiter.key()] *= ((float) mItemFrequencySecondOrder[uCloneiter.key()] ) / ((float) mItemFrequencySecondOrder[uCloneiter.key()] + 1 );
        ++mItemFrequencySecondOrder[uCloneiter.key()];
//				if(mItemFrequencySecondOrder[uCloneiter.key()]== 0)
//					throw new CollaborativeFilteringException(" zero...+++ ");
        mItemAverageSecondOrder[uCloneiter.key()]
             += ( uCloneiter.value() - completed[uCloneiter.key()] )
             / ( newlinf * mItemFrequencySecondOrder[uCloneiter.key()] );

      }
    }
  }

  /**
   *  This must called after you remove a user
   *
   *@param  u  evaluation which was removed
   */
  public void removedUser( TIntFloatHashMap u ) {
    float average = UtilMath.average( u );
    float[] completed = super.completeUser( u , false);
    float oldlinf;
      if(mInfForSecondOrder)
        oldlinf =  UtilMath.linfdiff( u, completed );
      else 
        oldlinf =  UtilMath.lpdiff( u, completed, mP );
    if ( oldlinf > UtilMath.epsilon ) {
      TIntFloatIterator uiter = u.iterator();
      while ( uiter.hasNext() ) {
        uiter.advance();
        if ( mItemFrequencySecondOrder[uiter.key()] == 0 )
          throw new CollaborativeFilteringException( " zero... " );
        mItemAverageSecondOrder[uiter.key()]
             -= ( uiter.value() - completed[uiter.key()] )
             / ( oldlinf * mItemFrequencySecondOrder[uiter.key()] );
        --mItemFrequencySecondOrder[uiter.key()];
        if ( mItemFrequencySecondOrder[uiter.key()] > 0 )
          mItemAverageSecondOrder[uiter.key()] *= ((float) mItemFrequencySecondOrder[uiter.key()] + 1 ) / ( (float)mItemFrequencySecondOrder[uiter.key()] );
      }
    }
    super.removedUser( u );
  }

  /**
   *  Return an array that contains predictions for the ratings of the given
   *  user. Note that predictions over already rated items don't have to agree
   *  with the provided ratings. This algorithm takes time O(1) with respect to
   *  the number of users.
   *
   * This implementation fallback on STINonPersonalized when
   * it fails (alpha near zero).
   *
   *@param  u  a set of one-dimensional ratings
   *@return    an array containing predictions
   */
  public float[] completeUser( TIntFloatHashMap u) {
    return completeUser(u, getFallBack());
  }
  
  /**
   *  Return an array that contains predictions for the ratings of the given
   *  user. Note that predictions over already rated items don't have to agree
   *  with the provided ratings. This algorithm takes time O(1) with respect to
   *  the number of users.
   *
   * This implementation fallback on STINonPersonalized when
   * it fails (alpha near zero).
   *
   *@param  u  a set of one-dimensional ratings
   *@return    an array containing predictions
   */
  public float[] completeUser( TIntFloatHashMap u , boolean fallback) {
    if ( u.size() < 3 && fallback)
      return super.completeUser( u, true );
    float[] completed = super.completeUser( u, false );
    TIntFloatHashMap RunningU = new TIntFloatHashMap(u);
    TIntFloatIterator uiter = ( RunningU ).iterator();
    while ( uiter.hasNext() ) {
      uiter.advance();
      uiter.setValue( uiter.value() - completed[uiter.key()] );
    }
    float averageIA = (float) UtilMath.average( mItemAverageSecondOrder, u );
    float averageIAold = UtilMath.average( mItemAverageWithoutMean, u );
    float enerIA = 0.0f;
    float productIA = 0.0f;
    TIntFloatIterator iter = RunningU.iterator();
    while ( iter.hasNext() ) {
      iter.advance();
      enerIA += ( mItemAverageWithoutMean[iter.key()] - averageIAold ) *
          ( mItemAverageWithoutMean[iter.key()] - averageIAold );
      productIA += ( mItemAverageWithoutMean[iter.key()] - averageIAold ) *
          ( mItemAverageSecondOrder[iter.key()] - averageIA );
    }
    float a = 0.0f;
    if ( enerIA > 0.0f )
      a = productIA / enerIA;
    float[] newmatch = new float[mMaxItemID];
    for ( int k = 0; k < mMaxItemID; ++k )
      newmatch[k] = (float) mItemAverageSecondOrder[k] - averageIA -
          a * ( mItemAverageWithoutMean[k] - averageIAold );
    iter = RunningU.iterator();
    float energy = 0.0f;
    float product = 0.0f;
    while ( iter.hasNext() ) {
      iter.advance();
      energy += newmatch[iter.key()] * newmatch[iter.key()];
      product += newmatch[iter.key()] * iter.value();
    }
    float alpha = 0.0f;
    if ( energy > 0.0f )
      alpha = product / energy;
    if ((Math.abs(product) < UtilMath.epsilon) && fallback)
      return super.completeUser(u, true);// fall back on 1 step
    float[] solution = new float[mMaxItemID];
    for ( int k = 0; k < mMaxItemID; ++k )
      solution[k] = completed[k] + (float) ( alpha * newmatch[k] );
    return solution;
  }
  
  public String toString() {
    return "STINonPersonalized2steps_mP="+mP+"_mInfForSecondOrder="+mInfForSecondOrder;
  }

}
