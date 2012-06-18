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

import java.util.*;
import cofi.algorithms.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.util.*;
import cofi.data.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

/**
 *  The generic STI O(1) scheme of order N.
 *
 *
 *  $Id: STINonPersonalizedNsteps.java,v 1.4 2004/04/22 14:55:52 lemire Exp $
 *  $Date: 2004/04/22 14:55:52 $
 *  $Author: lemire $
 *  $Revision: 1.4 $
 *  $Log: STINonPersonalizedNsteps.java,v $
 *  Revision 1.4  2004/04/22 14:55:52  lemire
 *  The compileit script now runs under cygwin
 *
 *  Revision 1.3  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.2  2003/10/28 01:43:08  lemired
 *  Lots of refactoring.
 *
 *  Revision 1.1  2003/10/27 17:21:15  lemired
 *  Putting some order
 *
 *  Revision 1.18  2003/08/22 13:38:23  howsen
 *  *** empty log message ***
 *
 *  Revision 1.17  2003/08/21 18:04:29  lemired
 *  Added toString method plus added necessary activation.jar for convenience.
 *
 *  Revision 1.16  2003/08/08 03:23:22  lemired
 *  addedUser/removedUser was broken in most implementation. I fixed that now.
 *
 *  Revision 1.15  2003/08/07 15:31:01  lemired
 *   This should fix the problem reported by Marcel.
 *
 *  Revision 1.14  2003/08/07 13:16:06  lemired
 *  More javadoc improvments.
 *
 *  Revision 1.13  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author     Daniel Lemire
 *@since      December 2002
 */
public class STINonPersonalizedNsteps
   extends PerItemAverage
{

   Vector mBasis = new Vector();
   Vector mFrequency = new Vector();
   int mN;
   float mP;

   /**
    *  Constructor for the STINonPersonalizedNsteps object
    *
    *@param  set  the training set
    *@param  p    which lp norm to user (p=2 is good)
    *@param  n    order of the scheme
    */
   public STINonPersonalizedNsteps(EvaluationSet set, float p, int n)
   {
      super(set);
      mP = p;
      mN = n;
      float[] ones = new float[mMaxItemID];
      for (int k = 0; k < mMaxItemID; ++k)
      {
   ones[k] = 1;
      }
      mBasis.add(ones);
      for (int k = 0; k < mN; ++k)
      {
   computeHigherOrderItemAverage();
      }
   }

   /**
    *  Compute higher order averages
    */
   protected void computeHigherOrderItemAverage()
   {
      int[] ItemFrequency = new int[mMaxItemID];
      float[] ItemAverageHigherOrder = new float[mMaxItemID];
      TIntObjectIterator t = mSet.iterator();
      int TotalNumber = 0;
      while (t.hasNext())
      {
   t.advance();
   ++TotalNumber;
   TIntFloatHashMap RunningU = new TIntFloatHashMap( (TIntFloatHashMap) t.value());
   float[] completed = completeUser(RunningU);
   float linf = UtilMath.lpdiff(RunningU, completed, mP); //UtilMath.linfdiff( RunningU, completed );
   if (linf > UtilMath.epsilon)
   {
      TIntFloatIterator uiter = RunningU.iterator();
      while (uiter.hasNext())
      {
         uiter.advance();
         ItemFrequency[uiter.key()] += 1;
         ItemAverageHigherOrder[uiter.key()] +=
      (uiter.value() - completed[uiter.key()]) / linf;
      }
   }
      }
      for (int k = 0; k < ItemAverageHigherOrder.length; ++k)
      {
   if (ItemFrequency[k] > 0)
   {
      ItemAverageHigherOrder[k] /= ItemFrequency[k];
   }
      }
      mBasis.add(ItemAverageHigherOrder);
      mFrequency.add(ItemFrequency);
   }

   /**
    *  Return an array that contains predictions for the ratings of the given
    *  user. Note that predictions over already rated items don't have to agree
       *  with the provided ratings. This algorithm takes time O(1) with respect to
    *  the number of users.
    *
    * This particular implementation doesn't fallback on PerItemAverage.
    *
    *@param  u  a set of one-dimensional ratings
    *@return    an array containing predictions
    */
   public float[] completeUser(TIntFloatHashMap u)
   {
      float[][] basis = new float[mBasis.size()][];
      int size = 0;
      Enumeration enumvar = mBasis.elements();
      while (enumvar.hasMoreElements())
      {
   float[] v = new float[mMaxItemID];
   System.arraycopy( (float[]) enumvar.nextElement(), 0, v, 0, mMaxItemID);
   for (int k = 0; k < size; ++k)
   {
      // could be faster if we normalized basis[k] each time
      float product = UtilMath.scalarProduct(v, basis[k], u);
      float ener = UtilMath.scalarProduct(basis[k], basis[k], u);
      if (ener > UtilMath.epsilon)
      {
         float alpha = product / ener;
         for (int i = 0; i < v.length; ++i)
         {
      v[i] -= alpha * basis[k][i];
         }
      }
   }
   basis[size++] = v;
      }
      float[] solution = new float[mMaxItemID];
      float[] coefficients = new float[size];
      for (int k = 0; k < size; ++k)
      {
   float product = UtilMath.scalarProduct(u, basis[k]);
   float ener = UtilMath.scalarProduct(basis[k], basis[k], u);
   if (ener > UtilMath.epsilon)
   {
      coefficients[k] = product / ener;
      for (int j = 0; j < mMaxItemID; ++j)
      {
         solution[j] += coefficients[k] * basis[k][j];
      }
   }
      }
      return solution;
   }

   /**
    *  Updates the buffer of the algorithm when a user enters a new rating.
    *
    *@param  u        User as it were before changes
    */
   //public void updateUser( TIntFloatHashMap u, int itemNum, float newVal ) {
   //super.updateUser( u, itemNum, newVal );
   // this is useless, but I do it to be clean
   // first remove it
   /*
    *  Vector mTemp = mBasis;
    *  mBasis = new Vector();
    *  mBasis.add( mTemp.get( 0 ) );
    *  / start it from scratch
    *  float[] oldlinf = new float[mN];
    *  for ( int k = 0; k < mN; ++k ) {
    *  int[] ItemFrequency = (int[]) mFrequency.get( k );
    *  float[] ItemAverageHigherOrder = (float[]) mTemp.get( k + 1 );
    *  float[] completed = completeUser( u );
    *  oldlinf[k] = UtilMath.linfdiff( u, completed );
    *  if ( oldlinf[k] > UtilMath.epsilon ) {
    *  TIntFloatIterator uiter = u.iterator();
    *  while ( uiter.hasNext() ) {
    *  uiter.advance();
    *  ItemAverageHigherOrder[uiter.key()] -= ( uiter.value() - completed[uiter.key()] )
    *  ( oldlinf[k] * ItemFrequency[uiter.key()] );
    *  }
    *  }
    *  mBasis.add( ItemAverageHigherOrder );
    *  }
    */
   //removedUser(u);
   // ok, now, the user has been removed, we need to modify it and add it back
   //TIntFloatHashMap uClone = (TIntFloatHashMap) u.clone();
   //uClone.put( itemNum, newVal );
   //addedUser(uClone);
   /*
    *  mTemp = mBasis;
    *  mBasis = new Vector();
    *  mBasis.add( mTemp.get( 0 ) );
    *  / start it from scratch
    *  for ( int k = 0; k < mN; ++k ) {
    *  int[] ItemFrequency = (int[]) mFrequency.get( k );
    *  float[] ItemAverageHigherOrder = (float[]) mTemp.get( k + 1 );
    *  float[] completed = completeUser( uClone );
    *  final float newlinf = UtilMath.linfdiff( uClone, completed );
    *  if ( newlinf > UtilMath.epsilon ) {
    *  if ( !u.contains( itemNum )  ) {
    *  ItemAverageHigherOrder[itemNum] *= ItemFrequency[itemNum];
    *  ++ItemFrequency[itemNum];
    *  ItemAverageHigherOrder[itemNum] /= ItemFrequency[itemNum];
    *  }
    *  if (oldlinf[k] <= UtilMath.epsilon)  {
    *  TIntFloatIterator uiter = u.iterator();
    *  while ( uiter.hasNext() ) {
    *  uiter.advance();
    *  ++mItemFrequency[uiter.key()] ;
    *  }
    *  }
    *  TIntFloatIterator uiter = uClone.iterator();
    *  while ( uiter.hasNext() ) {
    *  uiter.advance();
    *  ItemAverageHigherOrder[uiter.key()] += ( uiter.value() - completed[uiter.key()] )
    *  ( newlinf * ItemFrequency[uiter.key()] );
    *  }
    *  }
    *  mBasis.add( ItemAverageHigherOrder );
    *  }
    */
   //}

   /**
    *  This must called after you remove a user
    *
    *@param  u  evaluation which was removed
    */
   /*public void removedUser( TIntFloatHashMap u ) {
    // Need to process this in reverse order and that's not obvious!!!
    //Vector mTemp = mBasis;
    //mBasis = new Vector();
    //mBasis.add( mTemp.get( 0 ) );
    // start it from scratch
    float[] oldlinf = new float[mN];
    for ( int k = mN - 1; k < 0; ++k ) {
     int[] ItemFrequency = (int[]) mFrequency.get( k );
     float[] ItemAverageHigherOrder = (float[]) mTemp.get( k + 1 );
     float[] completed = completeUser( u );
     oldlinf[k] = UtilMath.lpdiff( u, completed, mP );//UtilMath.linfdiff( u, completed );
     if ( oldlinf[k] > UtilMath.epsilon ) {
      TIntFloatIterator uiter = u.iterator();
      while ( uiter.hasNext() ) {
       uiter.advance();
       ItemAverageHigherOrder[uiter.key()] -= ( uiter.value() - completed[uiter.key()] )
    / ( oldlinf[k] * ItemFrequency[uiter.key()] );
       --ItemFrequency[uiter.key()];
       if ( ItemFrequency[uiter.key()] > 0 )
  ItemAverageHigherOrder[uiter.key()] *= ( (float)ItemFrequency[uiter.key()] + 1 ) / ( (float)ItemFrequency[uiter.key()] );
      }
     }
     mBasis.add( ItemAverageHigherOrder );
    }
    super.removedUser( u );
     }*/

   /**
    *  This must called after you add a user
    *
    *@param  uClone  evaluation which was added
    */
   public void addedUser(TIntFloatHashMap uClone)
   {
      super.addedUser(uClone);
      Vector mTemp = mBasis;
      mBasis = new Vector();
      mBasis.add(mTemp.get(0));
      // start it from scratch
      for (int k = 0; k < mN; ++k)
      {
   int[] ItemFrequency = (int[]) mFrequency.get(k);
   float[] ItemAverageHigherOrder = (float[]) mTemp.get(k + 1);
   float[] completed = completeUser(uClone);
   final float newlinf = UtilMath.lpdiff(uClone, completed, mP); //UtilMath.linfdiff( uClone, completed );
   if (newlinf > UtilMath.epsilon)
   {
      TIntFloatIterator uiter = uClone.iterator();
      while (uiter.hasNext())
      {
         uiter.advance();
         ItemAverageHigherOrder[uiter.key()] *=
      ( (float) ItemFrequency[uiter.key()]) /
      ( (float) ItemFrequency[uiter.key()] + 1);
         ++ItemFrequency[uiter.key()];
         ItemAverageHigherOrder[uiter.key()] +=
      (uiter.value() - completed[uiter.key()])
      / (newlinf * ItemFrequency[uiter.key()]);

      }
   }
   mBasis.add(ItemAverageHigherOrder);
      }
   }

   public String toString()
   {
      return "STINonPersonalizedNsteps_mP=" + mP + "_mN=" + mN;
   }
}
