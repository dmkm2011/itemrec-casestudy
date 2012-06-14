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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  
 *
 *  Extends LinkedHashMap data structure to specify the maximum
 *  entries to be contained in the hashmap and it also calls 
 * removedUser when items are removed.
 *
 *
 *@author     Nancy Howse
 *@created    August 3, 2003
 *@version    1.0
 */

public class LinkedHash extends LinkedHashMap {

  private  int MAX_ENTRIES = 5000;// no need for final, could change, except for the fact it is private!



  /**
   *  Constructor for the LinkedHash object
   */
  public LinkedHash() {
    super();
  }


  /**
   *  Description of the Method
   *
   *@param  eldest  Description of the Parameter
   *@return         Description of the Return Value
   */
  protected boolean removeEldestEntry( Map.Entry eldest ) {
    return size() > MAX_ENTRIES;
  }


  /**
   *  Description of the Method
   *
   *@param  o  Description of the Parameter
   *@return    Description of the Return Value
   */
  public Object remove( Object o ) {
    CFSSingleton cfss = CFSSingleton.getInstance();
    User user = (User) super.remove( o );
    if ( user != null )
      cfss.removedUser( user );
    return user;
  }


  /**
   *  Gets the max attribute of the LinkedHash object
   *
   *@return    The max value
   */
  /*public int getMax() {
    return MAX_ENTRIES;
  }*/

  public int getMaxSize() {
    return MAX_ENTRIES ;
  }
}
