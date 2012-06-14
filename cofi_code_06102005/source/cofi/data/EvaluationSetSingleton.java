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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/**
 *  This class provides static access to an evaluation set.
 *
 *@author     Nancy Howse
 *@since      July 12, 2003
 *@version    1.0
 */

public class EvaluationSetSingleton {

  private HashMap list = null;
  private EvaluationSet totalSet = null;
  private EvaluationSet lyricsSet = null;
  private EvaluationSet musicSet = null;
  private EvaluationSet originalitySet = null;
  private EvaluationSet performanceSet = null;

  /**
   *  Description of the Field
   */
  public static EvaluationSetSingleton instance = null;


  /**
   *  Constructor for the EvaluationSetSingleton object
   */
  public EvaluationSetSingleton() {
    list = UserSingleton.getInstance().getList();
    System.out.println("Size in ESS: " + list.size());
    totalSet = new EvaluationSet();
    lyricsSet = new EvaluationSet();
    musicSet = new EvaluationSet();
    originalitySet = new EvaluationSet();
    performanceSet = new EvaluationSet();
    createSet();
  }


  /**
   *  Description of the Method
   */
  private void createSet() {
    Set aSet = UserSingleton.getInstance().getSet();
    String aName;
    Iterator iter = aSet.iterator();
    while ( iter.hasNext() ) {
      aName = iter.next().toString().split( "=" )[0];
      totalSet.add( aName.hashCode(),
          ( (User) list.get( aName ) ).getRatingsList( 't' ) );
      lyricsSet.add( aName.hashCode(),
          ( (User) list.get( aName ) ).getRatingsList( 'l' ) );
      musicSet.add( aName.hashCode(),
          ( (User) list.get( aName ) ).getRatingsList( 'm' ) );
      originalitySet.add( aName.hashCode(),
          ( (User) list.get( aName ) ).getRatingsList( 'o' ) );
      performanceSet.add( aName.hashCode(),
          ( (User) list.get( aName ) ).getRatingsList( 'p' ) );
    }
  }


  /**
   *  Gets the set attribute of the EvaluationSetSingleton object
   *
   *@param  type  Description of the Parameter
   *@return       The set value
   */
  public EvaluationSet getSet( char type ) {
    switch ( type ) {
            case 'l':
              return lyricsSet;
            case 'm':
              return musicSet;
            case 'o':
              return originalitySet;
            case 'p':
              return performanceSet;
    }
    return instance.totalSet;
  }


  /**
   *  Gets the instance attribute of the EvaluationSetSingleton class
   *
   *@return    The instance value
   */
  public static synchronized EvaluationSetSingleton getInstance() {
    if ( instance == null )
      instance = new EvaluationSetSingleton();
    return instance;
  }

  // who would take care of getting rid of the memory usage?
  public static void destroy() {
    instance = null;
  }
}

