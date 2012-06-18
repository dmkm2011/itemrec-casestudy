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
package cofi.algorithms.memorybased;

import cofi.algorithms.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.util.*;
import cofi.data.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

/**
 *  The memory-based Pearson scheme with case amplification. $Id: Pearson.java,v
 *  1.2 2003/10/28 01:43:08 lemired Exp $ $Date: 2003/11/11 13:25:58 $ $Author:
 *  lemired $ $Revision: 1.4 $ $Log: Pearson.java,v $
 *  lemired $ $Revision: 1.4 $ Revision 1.4  2003/11/11 13:25:58  lemired
 *  lemired $ $Revision: 1.4 $ Added gpl headers
 *  lemired $ $Revision: 1.4 $
 *  lemired $ $Revision: 1.4 $ Revision 1.3  2003/11/09 23:48:34  lemired
 *  lemired $ $Revision: 1.4 $ progres with Anna
 *  lemired $ $Revision: 1.4 $ Revision 1.2 2003/10/28
 *  01:43:08 lemired Lots of refactoring. Revision 1.1 2003/10/27 17:21:15
 *  lemired Putting some order Revision 1.11 2003/08/22 13:38:23 howsen ***
 *  empty log message *** Revision 1.10 2003/08/21 18:04:29 lemired Added
 *  toString method plus added necessary activation.jar for convenience.
 *  Revision 1.9 2003/08/12 12:27:24 lemired Fixed last problems with unit
 *  testing. Revision 1.8 2003/08/12 11:52:11 lemired Added more regression
 *  testing. Revision 1.7 2003/08/07 13:16:05 lemired More javadoc improvments.
 *  Revision 1.6 2003/08/07 00:37:42 lemired Mostly, I updated the javadoc.
 *
 *@author     Daniel Lemire
 *@created    November 6, 2003
 *@since      December 2002
 */
public class Pearson
     extends PerItemAverage {

  float mCaseAmplification = 2.5f;
  float[] mUserFrequency;
  int mTotalNumber;
  boolean mPearsonWithUserFrequency = false;
  boolean mPearsonWithCaseAmplification = true;


  /**
   *  Constructor for the Pearson object
   *
   *@param  set  The EvaluationSet you want to work on
   */
  public Pearson( EvaluationSet set ) {
    super( set );
    userFrequency();
  }


  /**
   *  Return an array that contains predictions for the ratings of the given
   *  user. Note that predictions over already rated items don't have to agree
   *  with the provided ratings. This algorithm takes time O(m) where m is the
   *  number of users.
   *
   *@param  u  a set of one-dimensional ratings
   *@return    an array containing predictions
   */
  public float[] completeUser( TIntFloatHashMap u ) {
    if ( u.size() == 0 )
      return new float[mMaxItemID];
    // mean
    float average = UtilMath.average( u );
    float[] complete = sumOfUsers( u );
    for ( int k = 0; k < mMaxItemID; ++k )
      complete[k] += average;

    return complete;
  }


  /**
   *  Compute weighted average
   *
   *@param  u  current evaluation
   *@return    weighted average
   */
  public float[] sumOfUsers( TIntFloatHashMap u ) {
    float[] complete = new float[mMaxItemID];
    // first we are going to record the weights corresponding to each user in the database
    int[] userids = mSet.keys();
    float[] weight = new float[userids.length];
    for ( int k = 0; k < userids.length; ++k )
      if ( !mPearsonWithUserFrequency )
        weight[k] = pearsonWithoutUserFrequency( (TIntFloatHashMap)
            mSet.get( userids[k] ), u );
      else
        weight[k] = pearsonWithUserFrequency( (TIntFloatHashMap) mSet.get(
            userids[k] ), u );
        // we may use case amplification if we want, just apply a given sign-preserving function to the weights
    if ( mPearsonWithCaseAmplification )
      caseAmplification( weight, mCaseAmplification );
      // for each item, we compute the absolute value of the weights used

    float[] ItemAmplitude = new float[mMaxItemID];
    for ( int k = 0; k < userids.length; ++k ) {
      final TIntFloatHashMap runningU = (TIntFloatHashMap) mSet.get( userids[
          k] );
      TIntFloatIterator iter = runningU.iterator();
      while ( iter.hasNext() ) {
        iter.advance();
        ItemAmplitude[iter.key()] += Math.abs( weight[k] );
      }
    }

    int CurrentItemIndex;
    for ( int k = 0; k < userids.length; ++k ) {
      final TIntFloatHashMap runningU = (TIntFloatHashMap) mSet.get( userids[
          k] );
      TIntFloatIterator iter = runningU.iterator();
      float av = UtilMath.average( runningU );
      while ( iter.hasNext() ) {
        iter.advance();
        if ( ItemAmplitude[iter.key()] > 0.0f )
          complete[iter.key()] += weight[k]
               / ItemAmplitude[iter.key()]
               * ( iter.value() - av );

      }
    }
    return complete;
  }


  /**
   *  Compute the Pearson correlation without taking into account the item
   *  frequency.
   *
   *@param  u1  first evaluation
   *@param  u2  second evaluation
   *@return     Pearson correlation
   */
  public float pearsonWithoutUserFrequency( TIntFloatHashMap u1,
      TIntFloatHashMap u2 ) {
    int[] ids1 = u1.keys();
    float av1 = UtilMath.average( u1 );
    float av2 = UtilMath.average( u2 );
    float product = 0.0f;
    float energy1 = 0.0f;
    float energy2 = 0.0f;
    int currentindex;
    float placeholder1 = 0.0f;
    float placeholder2 = 0.0f;
    int number = 0;
    for ( int k = 0; k < ids1.length; ++k ) {
      currentindex = ids1[k];
      if ( u2.containsKey( currentindex ) ) {
        number++;
        energy1 += ( placeholder1 = ( u1.get( currentindex ) - av1 ) ) *
            placeholder1;
        energy2 += ( placeholder2 = ( u2.get( currentindex ) - av2 ) ) *
            placeholder2;
        product += placeholder1 * placeholder2;
      }
    }
    if ( number < 2 )
      return 0.0f;
    if ( energy1 * energy2 > 0.0f )
      return product / (float) Math.sqrt( energy1 * energy2 );
    return 0.0f;
  }


  /**
   *  Compute the pearson correlation taking into account item frequency
   *
   *@param  u1  first evaluation
   *@param  u2  second evaluation
   *@return     Pearson correlation
   */
  public float pearsonWithUserFrequency( TIntFloatHashMap u1,
      TIntFloatHashMap u2 ) {
    // modified averages
    float av1 = UtilMath.average( u1, mUserFrequency );
    float av2 = UtilMath.average( u2, mUserFrequency );
    //
    int[] ids1 = u1.keys();
    float product = 0.0f;
    float energy1 = 0.0f;
    float energy2 = 0.0f;
    int currentindex;
    float placeholder1 = 0.0f;
    float placeholder2 = 0.0f;
    int number = 0;
    for ( int k = 0; k < ids1.length; ++k ) {
      currentindex = ids1[k];
      if ( u2.containsKey( currentindex ) ) {
        number++;
        energy1 +=
            ( placeholder1 = ( u1.get( currentindex ) * mUserFrequency[currentindex] -
            av1 ) ) * placeholder1;
        energy2 +=
            ( placeholder2 = ( u2.get( currentindex ) * mUserFrequency[currentindex] -
            av2 ) ) * placeholder2;
        product += placeholder1 * placeholder2;
      }
    }
    if ( number < 2 )
      return 0.0f;
    if ( energy1 * energy2 > 0.0f )
      return product / (float) Math.sqrt( energy1 * energy2 );
    return 0.0f;
  }


  /**
   *  Compute item frequency
   */
  protected void userFrequency() {
    mUserFrequency = new float[mMaxItemID];
    TIntObjectIterator t = mSet.iterator();
    mTotalNumber = 0;
    while ( t.hasNext() ) {
      t.advance();
      ++mTotalNumber;
      TIntFloatIterator uiter = ( (TIntFloatHashMap) t.value() ).iterator();
      while ( uiter.hasNext() ) {
        uiter.advance();
        if ( uiter.key() > mUserFrequency.length )
          System.err.println(
              "[Error] You have more item numbers than predicted " +
              ( uiter.key() ) + " > " + mUserFrequency.length );

        mUserFrequency[uiter.key()] += 1;
      }
    }
    for ( int k = 0; k < mUserFrequency.length; ++k )
      if ( mUserFrequency[k] > 0 )
        mUserFrequency[k] = (float) mUserFrequency[k] / mTotalNumber;

      else
        mUserFrequency[k] = 1.0f;

  }


  /*
   *  public void updateUser( TIntFloatHashMap u, int itemNum, float newVal ) {
   *  if ( !u.contains( itemNum ) ) {
   *  for ( int k = 0; k < mUserFrequency.length; ++k )
   *  mUserFrequency[k] *= (float) mTotalNumber / ( mTotalNumber );
   *  mUserFrequency[itemNum] += 1.0f / ( mTotalNumber );
   *  ++mTotalNumber;
   *  }
   *  }
   */
  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public String toString() {
    return "Pearson";
  }


  /**
   *  Apply case amplification on the weights
   *
   *@param  weights            the weights to be amplified
   *@param  CaseAmplification  Description of the Parameter
   */
  public static void caseAmplification( float[] weights,
      float CaseAmplification ) {
    for ( int k = 0; k < weights.length; ++k )
      weights[k] *= (float) Math.exp( ( CaseAmplification - 1 )
           * Math.log( Math.abs( weights[k] ) ) );

  }
}

