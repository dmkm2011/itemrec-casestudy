/**
 *  (c) National Research Council of Canada, 2004 by Daniel Lemire, Ph.D.
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
 *  This is an implementation of the Anna collaborative filtering method. 
 *  $Id: ItemBased.java,v 1.6 2004/04/07 01:35:43 lemired Exp $
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ $Log: ItemBased.java,v $
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ Revision 1.6  2004/04/07 01:35:43  lemired
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ Should be ok now
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ Revision 1.5  2004/04/07 00:38:06  lemired
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ Implemented itembased thingy
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ Revision 1.4  2004/04/06 20:51:07  lemire
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ Trying to fix it!
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ Revision 1.3  2004/04/06 20:45:20  lemire
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ Latest hack
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ Revision 1.2  2004/04/06 18:48:27  lemire
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ Fixing bugs in item based
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ Revision 1.1  2004/04/06 18:33:42  lemire
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $ I've added Itembased
 *  $Date: 2004/04/07 01:35:43 $ $Author: lemired $ $Revision: 1.6 $
 *
 *@author     Daniel Lemire
 *@created    April 6th 2004
 *@since      April 6th 2004
 */
public class ItemBased extends CollaborativeFilteringSystem {
  float[][] mProductsWA;
  float[][] mSquares1WA;
  float[][] mProducts;
  float[][] mSquares1;
  float[][] mSum1;
  short[][] mCount;
  
  
  public ItemBased( EvaluationSet set) {
    super( set );
    computeCorrelations();
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
    float[] weight = new float[mMaxItemID];
    TIntFloatIterator j = u.iterator();
    while ( j.hasNext() ) {
      j.advance();
      int jkey = j.key();
      for ( int k = 0; k < mMaxItemID; ++k ) {
	  short mCountkjkey = mCount[k][j.key()];
          if( k == jkey) continue;
	  if( mCountkjkey == 0) continue;
          float productofsquares = mSquares1WA[k][jkey]* mSquares1WA[jkey][k] ; 
	  if(productofsquares <= 0.00001f) 
		  continue;
	  float correlation = mProductsWA[k][jkey] / (float) Math.sqrt(productofsquares);
          float alpha = 1.0f, beta = 0.0f;
          float denom = mCountkjkey * mSquares1[jkey][k] - mSum1[jkey][k] * mSum1[jkey][k];
          if(denom >= 0.0001f) {
 	    alpha = (mProducts[k][jkey] * mCountkjkey - mSum1[k][jkey] * mSum1[jkey][k]) 
                    / denom;
	    beta = (mSquares1[jkey][k] * mSum1[k][jkey] - mSum1[jkey][k] * mProducts[k][jkey]) 
                    / denom;
 	  }
	  answer[k] += Math.abs(correlation) * ( alpha * (j.value()) + beta);
          weight[k] += Math.abs(correlation);
       }
    }
    for(int k = 0; k < mMaxItemID; ++k) {
//          System.out.println(k +" "+ answer[k]+" " + weight[k]);
	    if(weight[k] > 0) answer[k] /= weight[k];
    }
    return answer;
  }
  
/*                         [   i xx - x    i xx - x   ]
(C33) invert(m).[xy,y];

                           [   i xy         x y    ]
                           [ --------- - --------- ]
                           [         2           2 ]
                           [ i xx - x    i xx - x  ]
(D33)                      [                       ]
                           [   xx y        x xy    ]
                           [ --------- - --------- ]
                           [         2           2 ]
                           [ i xx - x    i xx - x  ]
(C34) ratsimp(invert(m).[xy,y]);

                               [   x y - i xy ]
                               [ - ---------- ]
                               [           2  ]
                               [   i xx - x   ]
(D34)                          [              ]
                               [ xx y - x xy  ]
                               [ -----------  ]
                               [          2   ]
                               [  i xx - x    ]
(C35) m
(C35) ;

                                   [ xx  x ]
(D35)                              [       ]
                                   [ x   i ]
*/


  /**
   *  Compute the correlations accross different items using the Ajusted
   *  Cosine Similarity.
   *
   */
  private void computeCorrelations() {
    mProducts = new float[mMaxItemID][mMaxItemID];
    mSquares1 = new float[mMaxItemID][mMaxItemID];
    mProductsWA = new float[mMaxItemID][mMaxItemID];
    mSquares1WA = new float[mMaxItemID][mMaxItemID];
    mSum1 = new float[mMaxItemID][mMaxItemID];
    mCount = new short[mMaxItemID][mMaxItemID];
    TIntObjectIterator t = mSet.iterator();
    while ( t.hasNext() ) {
      t.advance();
      TIntFloatHashMap CurrentEvaluation = (TIntFloatHashMap) t.value();
      float average = UtilMath.average(CurrentEvaluation);
      TIntFloatIterator k = CurrentEvaluation.iterator();
      while ( k.hasNext() ) {
        k.advance();
        TIntFloatIterator l = CurrentEvaluation.iterator();
        while ( l.hasNext() ) {
          l.advance();
          mSquares1WA[l.key()][k.key()] += (l.value() - average) * (l.value() - average);
	  mProductsWA[l.key()][k.key()] += (l.value() - average) * (k.value() - average);
          mSquares1[l.key()][k.key()] += (l.value()) * (l.value());
	  mProducts[l.key()][k.key()] += (l.value()) * (k.value());
	  mCount[l.key()][k.key()] += 1;
	  mSum1[l.key()][k.key()] += l.value();
        }
      }
    }
  }

  public String toString() {
    return "ItemBased";
  }
  
}



