/**
 *  (c) National Research Council of Canada, 2002-2003 by Daniel Lemire, Ph.D.
 *  Email lemire at ondelette dot com for support and details.
 */
/**
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation (version 2). This program is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details. You should have received a copy of
 *  the GNU General Public License along with this program; if not, write to the
 *  Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 *  02111-1307, USA.
 */
package cofi.algorithms.basic;

import cofi.data.*;
import gnu.trove.*;
import junit.framework.*;

/**
 *  Description of the Class
 *
 *@author     lemired
 *@created    November 11, 2003
 */
public class PerItemAverageTests
     extends TestCase {

  PerItemAverage pia;
  PerItemAverage piaLarge;
  PerItemAverage piaSmall;


  /**
   *  Constructor for the PerItemAverageTests object
   *
   *@param  name  Description of the Parameter
   */
  public PerItemAverageTests( String name ) {
    super( name );
  }


  /**
   *  The JUnit setup method
   *
   *@exception  Exception  Description of the Exception
   */
  public void setUp() throws Exception {
    pia = new PerItemAverage( getEvaluationSet( 5000, 601, 5 ) );
    piaLarge = new PerItemAverage( getEvaluationSet( 10000, 2000, 21 ) );
    piaSmall = new PerItemAverage( getEvaluationSet( 12, 250, 13 ) );
  }


  /**
   *  The teardown method for JUnit
   *
   *@exception  Exception  Description of the Exception
   */
  public void tearDown() throws Exception {
    pia = null;
    piaLarge = null;
    piaSmall = null;
  }


  /**
   *  Description of the Method
   */
  public void runTest() {
    testAverage();
    testUpdate();
  }


  /**
   *  A unit test for JUnit
   */
  public void testAverage() {
     {
      float[] ans = pia.completeUser( new TIntFloatHashMap() );
      assertTrue( ans.length == 601 );
      for ( int k = 0; k < pia.getNumberOfItems(); ++k )
        assertEquals( (int) Math.round( ans[k] ), k );

    }
     {
      float[] ans = piaLarge.completeUser( new TIntFloatHashMap() );
      assertTrue( ans.length == 2000 );
      for ( int k = 0; k < piaLarge.getNumberOfItems(); ++k )
        assertEquals( (int) Math.round( ans[k] ), k );

    }
     {
      float[] ans = piaSmall.completeUser( new TIntFloatHashMap() );
      assertTrue( ans.length == 250 );
      for ( int k = 0; k < piaSmall.getNumberOfItems(); ++k )
        assertEquals( (int) Math.round( ans[k] ), k );

    }
  }


  /**
   *  A unit test for JUnit
   */
  public void testUpdate() {
     {
      EvaluationSet es = pia.getTrainingSet();
      int firstindex = es.size() / 2;
      TIntFloatHashMap ratings = (TIntFloatHashMap) es.get( es.size() / 2 );
      es.remove( firstindex );
      pia.removedUser( ratings );
      testAverage();
      TIntFloatHashMap ratings2 = (TIntFloatHashMap) es.get( 0 );
      es.remove( 0 );
      pia.removedUser( ratings2 );
      testAverage();
      es.put( firstindex, ratings );
      pia.addedUser( ratings );
      testAverage();
      es.put( 0, ratings2 );
      pia.addedUser( ratings2 );
      testAverage();
    }
     {
      EvaluationSet es = piaLarge.getTrainingSet();
      int firstindex = es.size() / 2;
      TIntFloatHashMap ratings = (TIntFloatHashMap) es.get( es.size() / 2 );
      es.remove( firstindex );
      piaLarge.removedUser( ratings );
      testAverage();
      TIntFloatHashMap ratings2 = (TIntFloatHashMap) es.get( 0 );
      es.remove( 0 );
      piaLarge.removedUser( ratings2 );
      testAverage();
      es.put( firstindex, ratings );
      piaLarge.addedUser( ratings );
      testAverage();
      es.put( 0, ratings2 );
      piaLarge.addedUser( ratings2 );
      testAverage();
    }
     {
      EvaluationSet es = piaSmall.getTrainingSet();
      int firstindex = es.size() / 2;
      TIntFloatHashMap ratings = (TIntFloatHashMap) es.get( es.size() / 2 );
      es.remove( firstindex );
      piaSmall.removedUser( ratings );
      testAverage();
      TIntFloatHashMap ratings2 = (TIntFloatHashMap) es.get( 0 );
      es.remove( 0 );
      piaSmall.removedUser( ratings2 );
      testAverage();
      es.put( firstindex, ratings );
      piaSmall.addedUser( ratings );
      testAverage();
      es.put( 0, ratings2 );
      piaSmall.addedUser( ratings2 );
      testAverage();
    }
  }


  /**
   *  Gets the evaluationSet attribute of the PerItemAverageTests class
   *
   *@param  NumberOfUsers  Description of the Parameter
   *@param  NumberOfItems  Description of the Parameter
   *@param  DensityFactor  Description of the Parameter
   *@return                The evaluationSet value
   */
  public static EvaluationSet getEvaluationSet( int NumberOfUsers,
      int NumberOfItems,
      int DensityFactor ) {
    EvaluationSet es = new EvaluationSet();
    for ( int i = 0; i < NumberOfItems; ++i ) {
      int begin = ( DensityFactor * i ) % NumberOfUsers;
      int end = ( DensityFactor * ( i + 2 ) ) % NumberOfUsers;
      for ( int u = begin; u < end; ++u )
        es.add( u, i, i );
     //always rate item i with value i
      es.add( 0, i, i );
      // make sure item is rated
      es.add( 2, i, i );
      // make sure item is rated
      es.add( 13, i, i );
      // make sure item is rated
    }
    es.setMaxItemID( NumberOfItems );
    return es;
  }
}

