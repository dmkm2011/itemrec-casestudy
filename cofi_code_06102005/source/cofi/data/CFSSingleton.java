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

import gnu.trove.TIntFloatHashMap;


/**
 *  <p>
 *
 *  Provides static access to the CFS object.
 *
 *
 *@author     Nancy Howse
 *@since      July 12, 2003
 *@version    1.0
 */

public class CFSSingleton {

  private static CFSSingleton instance = null;
  private CFS totalCFS = null;// belongs to the instance
  private CFS lyricsCFS = null;// belongs to the instance
  private CFS musicCFS = null;// belongs to the instance
  private CFS originalityCFS = null;//belongs to the instance
  private CFS performanceCFS = null;// belongs to the instance


  /**
   *  Constructor for the CFSSingleton object
   */
  public CFSSingleton() {
    totalCFS = new CFS( 't' );
    lyricsCFS = new CFS( 'l' );
    musicCFS = new CFS( 'm' );
    originalityCFS = new CFS( 'o' );
    performanceCFS = new CFS( 'p' );
  }


  /**
   *  Gets the instance attribute of the CFSSingleton class
   *
   *@return    The instance value
   */
  public static synchronized CFSSingleton getInstance() {
    if ( instance == null )
      instance = new CFSSingleton();
    return instance;
  }


  /**
   *  Description of the Method
   *
   *@param  u     Description of the Parameter
   *@param  type  Description of the Parameter
   *@param  i     Description of the Parameter
   *@return       Description of the Return Value
   */
  public float[] completeUser( TIntFloatHashMap u, char type, int i ) {
    float completed[] = {};
    switch ( type ) {
            case 't':
              completed = totalCFS.completeUser( u, i );
              break;
            case 'l':
              completed = lyricsCFS.completeUser( u, i );
              break;
            case 'm':
              completed = musicCFS.completeUser( u, i );
              break;
            case 'o':
              completed = originalityCFS.completeUser( u, i );
              break;
            case 'p':
              completed = performanceCFS.completeUser( u, i );
              break;
    }
    return completed;
  }


  /**
   *  Description of the Method
   *
   *@param  u     Description of the Parameter
   *@param  type  Description of the Parameter
   *@param  i     Description of the Parameter
   *@param  min   Description of the Parameter
   *@param  max   Description of the Parameter
   *@return       Description of the Return Value
   */
  public float[] completeUser( TIntFloatHashMap u, char type, int i, int min, int max ) {
    float completed[] = {};
    switch ( type ) {
            case 't':
              completed = totalCFS.completeUser( u, i, min, max );
              break;
            case 'l':
              completed = lyricsCFS.completeUser( u, i, min, max );
              break;
            case 'm':
              completed = musicCFS.completeUser( u, i, min, max );
              break;
            case 'o':
              completed = originalityCFS.completeUser( u, i, min, max );
              break;
            case 'p':
              completed = performanceCFS.completeUser( u, i, min, max );
              break;
    }
    return completed;
  }


  /**
   *  Description of the Method
   *
   *@param  u  Description of the Parameter
   */
  public void removedUser( User u ) {
    totalCFS.removedUser( u.getRatingsList( 't' ) );
    lyricsCFS.removedUser( u.getRatingsList( 'l' ) );
    musicCFS.removedUser( u.getRatingsList( 'm' ) );
    originalityCFS.removedUser( u.getRatingsList( 'o' ) );
    performanceCFS.removedUser( u.getRatingsList( 'p' ) );
  }


  /**
   *  Description of the Method
   *
   *@param  u  Description of the Parameter
   */
  public void addedUser( User u ) {
    totalCFS.addedUser( u.getRatingsList( 't' ) );
    lyricsCFS.addedUser( u.getRatingsList( 'l' ) );
    musicCFS.addedUser( u.getRatingsList( 'm' ) );
    originalityCFS.addedUser( u.getRatingsList( 'o' ) );
    performanceCFS.addedUser( u.getRatingsList( 'p' ) );
  }


  /**
   *  Description of the Method
   *
   *@param  u        Description of the Parameter
   *@param  type     Description of the Parameter
   *@param  itemNum  Description of the Parameter
   *@param  rating   Description of the Parameter
   */
  /*public void updateUser( TIntFloatHashMap u, char type, int itemNum, float rating ) {
    switch ( type ) {
            case 't':
              totalCFS.updateUser( u, itemNum, rating );
              break;
            case 'l':
              lyricsCFS.updateUser( u, itemNum, rating );
              break;
            case 'm':
              musicCFS.updateUser( u, itemNum, rating );
              break;
            case 'o':
              originalityCFS.updateUser( u, itemNum, rating );
              break;
            case 'p':
              performanceCFS.updateUser( u, itemNum, rating );
              break;
    }
  }
*/

  /**
   *  Description of the Method
   *
   *@param  user     Description of the Parameter
   *@param  itemNum  Description of the Parameter
   *@param  ratings  Description of the Parameter
   */
  /*public void updateUser( User user, int itemNum, int[] ratings ) {
    if ( ratings.length == 5 ) {
      totalCFS.updateUser( user.getRatingsList( 't' ), itemNum, ratings[0] );
      lyricsCFS.updateUser( user.getRatingsList( 'l' ), itemNum, ratings[1] );
      musicCFS.updateUser( user.getRatingsList( 'm' ), itemNum, ratings[2] );
      originalityCFS.updateUser( user.getRatingsList( 'o' ), itemNum, ratings[3] );
      performanceCFS.updateUser( user.getRatingsList( 'p' ), itemNum, ratings[4] );
    }
    else
      ;
    //Do something bad
  }
*/

  /**
   *  Free the instance, also calls destroy on EvaluationSetSingleton.
   */
  public static void destroy() {
    EvaluationSetSingleton.destroy();
    instance = null;
  }


  /**
   *  Gets the algos attribute of the CFSSingleton class
   *
   *@return    The algos value
   */
  public String[] getAlgos() {
    return totalCFS.getAlgos();
  }
}
