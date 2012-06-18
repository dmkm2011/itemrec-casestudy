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

import junit.framework.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.stin.*;
import cofi.algorithms.linear.*;
import cofi.algorithms.memorybased.*;

/**
 * Run this method to test the collaborative filtering
 * algorithms using JUnit.
 */
public class CollaborativeFilteringTestSuite
{

   public static Test suite()
   {
      TestSuite suite = new TestSuite();
      suite.addTest(new PerItemAverageTests("Test PerItemAverage algorithm"));
      suite.addTest(new STITests("Test STI algorithms"));
      suite.addTest(new NaNTests("Test algorithms for NaN outputs"));
      suite.addTest(new MemoryBasedTests("Test memory-based algos"));
      suite.addTest(new NormalizationIndependenceTests(
   "Testing normalization independence"));
      suite.addTest(new EigenMatchTests("eigenmatch"));
      suite.addTest(new RuleOf5Tests("anna"));
      return suite;
   }

   public static void main(String args[])
   {
      junit.textui.TestRunner.run(suite());
   }
}
