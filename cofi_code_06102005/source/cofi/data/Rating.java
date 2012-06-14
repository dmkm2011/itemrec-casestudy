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
package cofi.data;


/**
 *  An object to model a given rating.
 *
 *@author     Daniel Lemire
 *@author     National Research Council of Canada
 *@created    August 3, 2003
 *@since      4 septembre 2002
 */
public class Rating {
  /**
   *  These are user id and item id respectively
   */
  public int mUser, mItem;
  /**
   *  The value of the vote
   */
  public float mVote;


  /**
   *  Constructor for the Rating object
   */
  public Rating() { }

}

