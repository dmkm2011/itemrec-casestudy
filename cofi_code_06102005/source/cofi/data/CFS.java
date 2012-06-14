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

import cofi.algorithms.*;
import cofi.algorithms.stin.*;
import cofi.algorithms.memorybased.*;
import cofi.algorithms.basic.*;
import cofi.data.*;
import gnu.trove.TIntFloatHashMap;


/**
 *
 *  Description: Applies each CollaborativeFilteringSystem algorithm to the
 *  given EvaluationSet.</p> <p>
 *
 *@author     Nancy Howse
 *@created    August 3, 2003
 *@since      July 12, 2003
 *@version    1.0
 */

public class CFS {

  private EvaluationSet aSet;
  private CollaborativeFilteringSystem[] systems;
  private final String[] algorithms
       = {"STI Non-Personalized 2 Step", "STI Pearson", "Average",
      "Non-Personalized", "Per Item Average"};
  private final static int STI_NON_PER_2S = 0;
  private final static int STI_PEARSON = 1;
  private final static int AVERAGE = 2;
  private final static int NON_PER = 3;
  private final static int PER_ITEM_AV = 4;


  /**
   *  CFS Constructor.
   *
   *@param  type  Description of the Parameter
   */
  public CFS( char type ) {
    systems = new CollaborativeFilteringSystem[algorithms.length];
    EvaluationSet aSet = EvaluationSetSingleton.getInstance().getSet( type );
    System.out.println(aSet.size());
    systems[0] = new STINonPersonalized2steps( aSet, 2.0f );
    systems[1] = new STIPearson( aSet, 2.0f );
    systems[2] = new Average( aSet );
    systems[3] = new NonPersonalized( aSet );
    systems[4] = new PerItemAverage( aSet );
  }


  /**
   *  Predicts ratings for the given user.
   *
   *@param  u  Description of the Parameter
   *@param  i  Description of the Parameter
   *@return    a two dimensional array containing the resulting predictions for
   *      all of the CollaborativeFilteringSystem algorithms.
   */
  /*
   *  public float[][] completeUser( TIntFloatHashMap u ) {
   *  float[][] completedAlgorithms = new float[systems.length][];
   *  for ( int i = 0; i < completedAlgorithms.length; i++ )
   *  completedAlgorithms[i] = systems[i].completeUser( u );
   *  return completedAlgorithms;
   *  }
   */

  /**
   *  Predicts ratings for the given user, using a specified algorithm.
   *
   *@param  u  Description of the Parameter
   *@param  i  Description of the Parameter
   *@return    an array containing the resulting predictions for the specified
   *      algorithm.
   */
  public float[] completeUser( TIntFloatHashMap u, int i ) {
    return systems[i].completeUser( u );
  }


  /**
   *  Description of the Method
   *
   *@param  u    Description of the Parameter
   *@param  i    Description of the Parameter
   *@param  min  Description of the Parameter
   *@param  max  Description of the Parameter
   *@return      Description of the Return Value
   */
  public float[] completeUser( TIntFloatHashMap u, int i, int min, int max ) {
    return systems[i].completeUser( u, min, max );
  }


  /**
   *  Updates a user's predicted ratings without recreating any objects, or
   *  redoing all calculations.
   *
   *@param  u        Description of the Parameter
   *@param  itemNum  Description of the Parameter
   *@param  rating   Description of the Parameter
   */
  /*public void updateUser( TIntFloatHashMap u, int itemNum, float rating ) {
    for ( int i = 0; i < systems.length; i++ )
        systems[i].updateUser( u, itemNum, rating );
  }*/


  /**
   *  Description of the Method
   *
   *@param  u  Description of the Parameter
   */
  public void addedUser( TIntFloatHashMap u ) {
    for ( int i = 0; i < systems.length; i++ )
      systems[i].addedUser( u );
  }


  /**
   *  Description of the Method
   *
   *@param  u  Description of the Parameter
   */
  public void removedUser( TIntFloatHashMap u ) {
    for ( int i = 0; i < systems.length; i++ )
      systems[i].removedUser( u );
  }


  /**
   *  Accessor method for list of algorithms.
   *
   *@return    a String array of the names of the algorithms.
   */
  public String[] getAlgos() {
    return algorithms;
  }


  /**
   *  Gets the per_Item_Av attribute of the CFS class
   *
   *@return    The per_Item_Av value
   */
  public static int getPer_Item_Av() {
    return PER_ITEM_AV;
  }

}

