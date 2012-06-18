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
package cofi.algorithms;

import cofi.data.*;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

/**
 *  Base class for all CFS.
 *
 *
 *  $Id: CollaborativeFilteringSystem.java,v 1.3 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $
 *  $Author: lemired $
 *  $Revision: 1.3 $
 *  $Log: CollaborativeFilteringSystem.java,v $
 *  Revision 1.3  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.2  2003/10/28 01:43:08  lemired
 *  Lots of refactoring.
 *
 *  Revision 1.1  2003/10/27 17:21:15  lemired
 *  Putting some order
 *
 *  Revision 1.11  2003/08/22 13:38:23  howsen
 *  *** empty log message ***
 *
 *  Revision 1.10  2003/08/21 18:04:29  lemired
 *  Added toString method plus added necessary activation.jar for convenience.
 *
 *  Revision 1.9  2003/08/19 17:51:21  lemired
 *  I've been improving OptimalWeight.
 *
 *  Revision 1.8  2003/08/08 03:23:22  lemired
 *  addedUser/removedUser was broken in most implementation. I fixed that now.
 *
 *  Revision 1.7  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author     Daniel Lemire
 *@since      December 2002
 */
public abstract class CollaborativeFilteringSystem
{
   /**
    *  This is the evaluation set where all CollaborativeFilteringSystems
    * go train on.
    */
   protected EvaluationSet mSet;

   /**
    * Default behavior: to fall back or not. Not supported by all classes.
    */
   boolean mFallBack = true;

   /*
    * We buffer the number of items. For this reason and others,
    * if you change the number of items, you must
    * recreate these objects.
    */
   protected int mMaxItemID = -1;

   /**
    *  Constructor for the CollaborativeFilteringSystem object
    *
    *@param  set  the training set
    */
   public CollaborativeFilteringSystem(EvaluationSet set)
   {
      mSet = set;
      mMaxItemID = mSet.getMaxItemID();
   }

   /**
    *  Gets the number of distinct items in the evaluation set
    *
    *@return    The number of items
    */
   public int getNumberOfItems()
   {
      return mMaxItemID;
   }

   /**
    *  Get the training set. WARNING: you shouldn't modify it
    * without calling respective addedUser and removedUser!
    *
    *@return    The training set
    */
   public EvaluationSet getTrainingSet()
   {
      return mSet;
   }

   /**
    *  Give out predicted ratings based on the provided (incomplete) evaluation
    *
    * This method should never change u.
    *
    *@param  u  an evaluation to be extended
    *@return    a prediction
    */
   public abstract float[] completeUser(TIntFloatHashMap u);

   /**
    *  Return an array that contains predictions for the ratings of the given
    *  user. Note that predictions over already rated items don't have to agree
    *  with the provided ratings. What this particular call does it to remove
    *  outliers (values outside the min, max range), but it doesn't do anything
    *  beyond the normal completeUser method.
    *
    * This method should never change u.
    *
    * This method also does some better error reporting.
    *
    *@param  u    a set of one-dimensional ratings
    *@param  max  Max. rating
    *@param  min  Min. rating
    *@return      an array containing predictions, all between min and max
     a */
   public float[] completeUser(TIntFloatHashMap u, final float min,
             final float max)
   {
      float[] completed = null;
      try
      {
   completed = completeUser(u);
      }
      catch (ArrayIndexOutOfBoundsException aio)
      {
   StringBuffer sb = new StringBuffer("Following User is crashing me :");
   TIntFloatIterator iter = u.iterator();
   while (iter.hasNext())
   {
      iter.advance();
      sb.append("u[");
      sb.append(iter.key());
      sb.append("]=");
      sb.append(iter.value());
      sb.append(" ");
   }
   sb.append(" MaxItemID = ");
   sb.append(mMaxItemID);
   throw aio /*CollaborativeFilteringException( sb.toString() )*/;
      }
      for (int k = 0; k < completed.length; ++k)
      {
   if (completed[k] < min)
   {
      completed[k] = min;
   }
   else if (completed[k] > max)
   {
      completed[k] = max;
   }
      }
      return completed;
   }

   /**
    *  This must called after you remove a user
    *
    *@param  u                                    evaluation to be removed
    */
   public void removedUser(TIntFloatHashMap u)
   {
      throw new CollaborativeFilteringException("Method not implemented yet!");
   }

   /**
    *  This must called after you add a user
    *
    *@param  u                                    Evaluation to be added
    */
   public void addedUser(TIntFloatHashMap u)
   {
      throw new CollaborativeFilteringException("Method not implemented yet!");
   }

   /**
    * This methods allow you to disable totally the fallback mechanism.
    * This is not supported by all classes.
    */
   public void setFallBack(boolean fallback)
   {
      mFallBack = fallback;
   }
   
   
   public boolean getFallBack()
   {
      return mFallBack;
   }

   public String toString()
   {
      return "CollaborativeFilteringSystem";
   }
}
