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

import gnu.trove.*;

/**
 *  This object models a user in the sense that it includes
 * all ratings and metadata known about a user. 
 *
 *@author     Nancy Howse
 *@since      July 12, 2003
 *@version    2.0 - January 15th, 2003
 */

public class User {

  private int lastItem;
  private String name, pass;
  private boolean admin;
  private TIntFloatHashMap ratingsList, lyricsList, musicList,
      originalityList, performanceList;
  private int rateCount;
  private int userID;


  /**
   *  Constructor for the User object
   *
   *@param  aName  Description of the Parameter
   *@param  aPass  Description of the Parameter
   *@param  id     Description of the Parameter
   */
  public User( String aName, String aPass, int id ) {
    name = aName;
    pass = aPass;
    userID = id;
    admin = false;
    ratingsList = new TIntFloatHashMap();
    lyricsList = new TIntFloatHashMap();
    musicList = new TIntFloatHashMap();
    originalityList = new TIntFloatHashMap();
    performanceList = new TIntFloatHashMap();
    rateCount = 0;
    lastItem = -1;
  }


  /**
   *  Constructor for the User object
   *
   *@param  aName    Description of the Parameter
   *@param  aPass    Description of the Parameter
   *@param  id       Description of the Parameter
   *@param  isAdmin  Description of the Parameter
   */
  public User( String aName, String aPass, int id, boolean isAdmin ) {
    name = aName;
    pass = aPass;
    userID = id;
    admin = isAdmin;
    ratingsList = new TIntFloatHashMap();
    lyricsList = new TIntFloatHashMap();
    musicList = new TIntFloatHashMap();
    originalityList = new TIntFloatHashMap();
    performanceList = new TIntFloatHashMap();
    rateCount = 0;
    lastItem = -1;
  }


  /**
   *  Gets the name attribute of the User object
   *
   *@return    The name value
   */
  public String getName() {
    return name;
  }


  /**
   *  Sets the name attribute of the User object
   *
   *@param  aName  The new name value
   */
  public void setName( String aName ) {
    name = aName;
  }


  /**
   *  Gets the pass attribute of the User object
   *
   *@return    The pass value
   */
  public String getPass() {
    return pass;
  }


  /**
   *  Sets the pass attribute of the User object
   *
   *@param  aPass  The new pass value
   */
  public void setPass( String aPass ) {
    pass = aPass;
  }


  /**
   *  Gets the userID attribute of the User object
   *
   *@return    The userID value
   */
  public int getUserID() {
    return userID;
  }


  /**
   *  Sets the userID attribute of the User object
   *
   *@param  id  The new userID value
   */
  public void setUserID( int id ) {
    userID = id;
  }


  /**
   *  Gets the admin attribute of the User object
   *
   *@return    The admin value
   */
  public boolean isAdmin() {
    return admin;
  }


  /**
   *  Sets the admin attribute of the User object
   *
   *@param  isAdmin  The new admin value
   */
  public void setAdmin( boolean isAdmin ) {
    admin = isAdmin;
  }


  /**
   *  Sets the lastItem attribute of the User object
   *
   *@param  i  The new lastItem value
   */
  public void setLastItem( int i ) {
    lastItem = i;
  }


  /**
   *  Description of the Method
   *
   *@return    Description of the Return Value
   */
  public int lastItem() {
    return lastItem;
  }


  /**
   *  Gets the rating attribute of the User object
   *
   *@param  i     Description of the Parameter
   *@param  type  Description of the Parameter
   *@return       The rating value
   */
  public float getRating( int i, char type ) {
    switch ( type ) {
            case 'l':
              return lyricsList.get( i );
            case 'm':
              return musicList.get( i );
            case 'o':
              return originalityList.get( i );
            case 'p':
              return performanceList.get( i );
    }
    return ratingsList.get( i );
  }


  /**
   *  Sets the rating attribute of the User object
   *
   *@param  i       The new rating value
   *@param  rating  The new rating value
   */
  public void setRating( int i, float rating ) {
    ratingsList.put( i, rating );
    rateCount++;
  }


  /**
   *  Sets the rating attribute of the User object
   *
   *@param  i       The new rating value
   *@param  rating  The new rating value
   *@param  type    The new rating value
   */
  public void setRating( int i, float rating, char type ) {
    switch ( type ) {
            case 't':
              ratingsList.put( i, rating );
              break;
            case 'l':
              lyricsList.put( i, rating );
              break;
            case 'm':
              musicList.put( i, rating );
              break;
            case 'o':
              originalityList.put( i, rating );
              break;
            case 'p':
              performanceList.put( i, rating );
              break;
    }
  }


  /**
   *  Description of the Method
   *
   *@param  i  Description of the Parameter
   */
  public void removeRating( int i ) {
    ratingsList.remove( i );
  }


  /**
   *  Description of the Method
   *
   *@param  i          Description of the Parameter
   *@param  decrement  Description of the Parameter
   */
  public void removeRating( int i, boolean decrement ) {
    ratingsList.remove( i );
    if ( decrement )
      rateCount--;
  }


  /**
   *  Gets the rateCount attribute of the User object
   *
   *@return    The rateCount value
   */
  public int getRateCount() {
    return rateCount;
  }


  /**
   *  Gets the ratingsList attribute of the User object
   *
   *@param  type  Description of the Parameter
   *@return       The ratingsList value
   */
  public TIntFloatHashMap getRatingsList( char type ) {
    switch ( type ) {
            case 'l':
              return lyricsList;
            case 'm':
              return musicList;
            case 'o':
              return originalityList;
            case 'p':
              return performanceList;
    }
    return ratingsList;
  }
}

