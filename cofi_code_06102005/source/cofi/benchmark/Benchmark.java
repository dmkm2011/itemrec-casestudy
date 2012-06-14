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
package cofi.benchmark;

import gnu.trove.*;
import java.io.*;
import java.util.*;
import cofi.algorithms.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.stin.*;
import cofi.algorithms.memorybased.*;
import cofi.algorithms.linear.*;
import cofi.algorithms.jester.*;
import cofi.algorithms.util.*;
import cofi.algorithms.composition.*;
import cofi.data.*;

/**
 *  A class used for the benchmarking/performance eval. of CFS.
 *
 * $Id: Benchmark.java,v 1.18 2004/04/22 17:06:43 lemire Exp $
 * $Date: 2004/04/22 17:06:43 $
 * $Author: lemire $
 * $Revision: 1.18 $
 * $Log: Benchmark.java,v $
 * Revision 1.18  2004/04/22 17:06:43  lemire
 * Getting ready to test over movielens
 *
 * Revision 1.17  2004/04/06 18:33:42  lemire
 * I've added Itembased
 *
 * Revision 1.16  2003/12/14 14:41:53  lemired
 * fixed the new scheme parameter and added adjustscale__pearson
 *
 * Revision 1.15  2003/12/12 14:41:12  lemired
 * factory for more tunable benchmarks
 *
 * Revision 1.14  2003/12/09 13:15:43  lemired
 * Now, can allow any number of min ratings per user
 *
 * Revision 1.13  2003/12/08 15:31:56  lemired
 * Optimization
 *
 * Revision 1.12  2003/12/08 15:13:27  lemired
 * Added an option to constant bias
 *
 * Revision 1.11  2003/12/07 23:21:23  lemired
 * More fiddling
 *
 * Revision 1.10  2003/12/07 21:55:40  lemired
 * More hacking
 *
 * Revision 1.9  2003/12/05 21:47:08  lemired
 * I think I finally got rule of 3 right
 *
 * Revision 1.8  2003/12/05 14:47:22  lemired
 * I think I made benchmark less crazy
 *
 * Revision 1.7  2003/12/04 03:49:23  lemired
 * Some hacking after my discussions with ANna
 *
 * Revision 1.6  2003/11/24 16:33:07  lemired
 * Added ruleof3 scheme as a comparison
 *
 * Revision 1.5  2003/11/24 16:26:29  lemired
 * Removed data package again
 *
 * Revision 1.4  2003/11/17 18:43:07  lemired
 * Added new BiRuleOf3
 *
 * Revision 1.3  2003/11/12 17:39:19  lemired
 * Made rule of 5 a standard scheme
 *
 * Revision 1.2  2003/11/12 16:20:48  lemired
 * There was a small bug in BiNPN
 *
 * Revision 1.1  2003/11/11 13:25:58  lemired
 * Added gpl headers
 *
 * Revision 1.22  2003/11/09 23:48:34  lemired
 * progres with Anna
 *
 * Revision 1.21  2003/11/05 17:26:46  lemired
 * Put gamma to 0.02
 *
 * Revision 1.20  2003/11/03 23:41:57  lemired
 * Latest changes: should almost conclude paper with Anna.
 *
 * Revision 1.19  2003/10/31 00:47:06  lemired
 * Still got a bug in TIOptimalWeight... it should be better than average... arghh!
 *
 * Revision 1.18  2003/10/28 01:43:08  lemired
 * Lots of refactoring.
 *
 * Revision 1.17  2003/10/07 13:28:32  lemired
 * Did some tweaking...
 *
 * Revision 1.16  2003/09/29 14:37:33  lemired
 * Fixed the optimalweight scheme.
 *
 * Revision 1.15  2003/09/25 23:18:27  lemired
 * Added Anna.
 *
 * Revision 1.14  2003/09/24 14:58:40  lemired
 * Worked hard on eigenmatch.
 *
 * Revision 1.13  2003/09/22 18:47:59  lemired
 * Got first-order exactness right!
 *
 * Revision 1.12  2003/09/18 12:41:42  lemired
 * Still doing lots of boring research.
 *
 * Revision 1.11  2003/08/28 16:41:51  lemired
 * Added Harold's bibtex changes, added a howto and my recent changes to algos.
 *
 * Revision 1.10  2003/08/21 20:46:30  lemired
 * Finally got STIOptimalWeight right!
 *
 * Revision 1.9  2003/08/21 18:49:38  lemired
 * It should now compile nicely.
 *
 * Revision 1.8  2003/08/21 18:04:29  lemired
 * Added toString method plus added necessary activation.jar for convenience.
 *
 * Revision 1.7  2003/08/19 23:13:57  lemired
 * More work on OptimalWeight. Don't think it works well afterall.
 *
 * Revision 1.6  2003/08/19 17:51:21  lemired
 * I've been improving OptimalWeight.
 *
 * Revision 1.5  2003/08/12 11:52:11  lemired
 * Added more regression testing.
 *
 * Revision 1.4  2003/08/07 00:37:42  lemired
 * Mostly, I updated the javadoc.
 *
 *
 *@author       Daniel Lemire, Ph.D.
 *@since     September 2002
 */
public class Benchmark {

  static boolean Fast = false;
  static int factor = 1;// hardcoded cheat factor
  //
  //final static short STEEPEST = 0;
  //final static short DELTA = 1;
  //final static short DUMP = 2;
  final static short JESTER = 3;
  final static short EACHMOVIE = 4;
  final static short MOVIELENS = 5;
  //final static short INFO = 4;
  final static int INFINITE = 99999999;



  public static void benchmark( CollaborativeFilteringSystem cfs, EvaluationSet origuset, int RatingsToTest, float minvalue, float maxvalue ) {
    System.out.println( "[waiting] Completing..." );
    TIntObjectIterator iter = origuset.iterator();
    int NumberTested = 0;
    float nmaeAllBut1 = 0.0f;
    float[] nmaeAllBut1s = new float[cfs.getNumberOfItems()];
    float nmaeAllBut1_2 = 0.0f;
    float[] nmaeAllBut1s_2 = new float[cfs.getNumberOfItems()];
    int TotalAllBut1 = 0;
    int[] TotalAllBut1s = new int[cfs.getNumberOfItems()];
    float match_quality = 0.0f;
    while ( iter.hasNext() ) {
      ++NumberTested;
      if ( ( RatingsToTest > 0 ) && ( NumberTested > RatingsToTest ) )
        break;
      iter.advance();
      TIntFloatHashMap User = (TIntFloatHashMap) iter.value();
      float[] c = cfs.completeUser( User , minvalue, maxvalue);
      match_quality += UtilMath.l2diff(User,c) / origuset.size();
      int[] itemids = User.keys();
      int number = 0;
      for ( int itemidindex = 0; itemidindex < itemids.length; itemidindex += factor ) {
        final int RemovalIndex = itemids[itemidindex];
        if( cfs instanceof JesterClassical ) {
          int[] index = ((JesterClassical) cfs).getStandard();
          boolean found = false;
          for (int k = 0; k < index.length; ++k)
            if(index[k] == RemovalIndex) found = true;
          if(found) continue;
        }
        ++TotalAllBut1;
        ++TotalAllBut1s[RemovalIndex];
        TIntFloatHashMap HackedUser = (TIntFloatHashMap) User.clone();
        final float RemovedValue = HackedUser.remove( RemovalIndex );
        float[] complete = cfs.completeUser( HackedUser,minvalue, maxvalue );
        float err = (float) Math.abs( complete[RemovalIndex] - RemovedValue );
        float err2 = ( complete[RemovalIndex] - RemovedValue ) * ( complete[RemovalIndex] - RemovedValue );
        nmaeAllBut1 += err;
        nmaeAllBut1s[RemovalIndex] += err;
        nmaeAllBut1_2 += err2;
        nmaeAllBut1s_2[RemovalIndex] += err2;

        ++number;
      }
      //nmaeAllBut1 += currentNMAEAllBut1;			
    }
    for (int k = 0; k < nmaeAllBut1s.length; ++k) {
      if(TotalAllBut1s[k] > 0)
        nmaeAllBut1s[k] /= TotalAllBut1s[k]; 
    }
    for (int k = 0; k < nmaeAllBut1s_2.length; ++k) {
      if(TotalAllBut1s[k] > 0)
        nmaeAllBut1s_2[k] /= TotalAllBut1s[k]; 
    }
    UtilMath.print( nmaeAllBut1s,10);
    float min = nmaeAllBut1s[0]; int minpos = 0;
    float max = nmaeAllBut1s[0]; int maxpos = 0;
    for (int k = 0; k < nmaeAllBut1s.length; ++k) {
      if( (nmaeAllBut1s[k] < min) && (TotalAllBut1s[k]>0) ) {
        min = nmaeAllBut1s[k];
        minpos = k;
      }
      if( (nmaeAllBut1s[k] > max) && (TotalAllBut1s[k]>0) ) {
        max = nmaeAllBut1s[k];
        maxpos = k;
      }
    }
    //System.out.println("[info] min allbut1 = "+min+" at "+minpos);
    //System.out.println("[info] max allbut1 = "+max+" at "+maxpos);
    float average = UtilMath.average(nmaeAllBut1s);
    float stddev = (float) Math.sqrt(UtilMath.variance(nmaeAllBut1s, average));
    //System.out.println( "[info] average allbut1 "+average + " stddev = "+stddev+ "(warning: doesn't apply to eigentaste)");
    System.out.println( "[info] AllBut1  NMAE = " + (  nmaeAllBut1 / TotalAllBut1 ) );
    System.out.println( "[info]  NMAE_2 = " + Math.sqrt(  nmaeAllBut1 / TotalAllBut1 ) );
    System.out.println( "[info] match_quality = "+match_quality);
  }
  public static void printDoc() {
	  System.out.println("I use this class the following way:");
	  System.out.println("  java -Xmx768m -classpath ~/CVS/racofi/cofi/classes cofi.benchmark.Benchmark");
	  System.out.println(" Then, I append the following options:");
	  System.out.println("   -maxvalue : max value of a rating");
	  System.out.println("   -minvalue : min value of a rating");
	  System.out.println("   -minratingsperuser : reject users that don't have at least this number of ratings, default to 20");
	  System.out.println("   -input: path to the rating file, must be a special binary file");
  	  System.out.println("        just used one of the cofi.parsers tool to convert data to binary.");

	  System.out.println("   -max : is a misnomer, it is the minimal number of ratings");
	  System.out.println("        needed in the training data set");
	  System.out.println("   -skip : number of ratings to skip before entering ");
	  System.out.println("        training data (ratings are read in sequence)");
	  System.out.println("   -test : minimal number of ratings in test data");
	  System.out.println("   -testskip : how many ratings to skip before entering in");
	  System.out.println("        the test data");
	  System.out.println("   -justinfo : don't actual benchmark, just load data sets");		  
	  System.out.println("   -scheme followed by coma-separated names of algorithms,");
	  System.out.println("         can be the name of the class such as cofi.algorithms.Average,");
	  System.out.println("         or some defined in the"); 
	  System.out.println("           cofi.algorithms.CollaborativeFilteringSystemFactory");
	  System.out.println("         class.");
   }
   /**
   *  The main program 
   *
   *@param  arg              The command line arguments
   *@exception  IOException  Description of the Exception
   */
  public static void main( String[] arg ) throws IOException {
    long before, after;
    System.out.println( "(c) 2002-2004, NRC by Daniel Lemire, Ph.D." );
    System.out.println();
    if(arg.length == 0) {
	    printDoc();
	    return;
    }
    String DataFileName = System.getProperty( "user.home" ) + "/CFData/vote.bin";
    float minvalue = 0.0f;
    float maxvalue = 1.0f;
    int maxratings = INFINITE;
    int skip = 0;
    int test = -1;
    int testskip = 0;
    boolean testweight = false;
    boolean optimal = false;
    short currentmethod = EACHMOVIE;
    int minratingsperuser = 20;
    boolean justinfo = false;
    String[] names = null;
    for ( int k = 0; k < arg.length; ++k ) {
      if ( arg[k].equals( "-max" ) ) {
        try {
          maxratings = Integer.parseInt( arg[k + 1] );
          ++k;
        } catch ( Exception e ) {
          System.out.println( "[Error] couldn't parse " + arg[k + 1] + " as an integer." );
        }
        continue;
      }
      if ( arg[k].equals( "-maxvalue" ) ) {
        try {
          maxvalue = Float.parseFloat( arg[k + 1] );
          ++k;
        } catch ( Exception e ) {
          System.out.println( "[Error] couldn't parse " + arg[k + 1] + " as a float." );
        }
        continue;
      } 
      if ( arg[k].equals( "-scheme" ) ) {
        try {
          names = arg[k + 1].split(",");
          ++k;
        } catch ( Exception e ) {
          System.out.println( "[Error] couldn't parse " + arg[k + 1] + " as a list of schemes." );
        }
        continue;
      } 
      if ( arg[k].equals( "-minratingsperuser" ) ) {
        try {
          minratingsperuser = Integer.parseInt( arg[k + 1] );
          ++k;
        } catch ( Exception e ) {
          System.out.println( "[Error] couldn't parse " + arg[k + 1] + " as a float." );
        }
        continue;
      } 
      if ( arg[k].equals( "-minvalue" ) ) {
        try {
          minvalue = Float.parseFloat( arg[k + 1] );
          ++k;
        } catch ( Exception e ) {
          System.out.println( "[Error] couldn't parse " + arg[k + 1] + " as a float." );
        }
        continue;
      } 
 
      if ( arg[k].equals( "-input" ) ) {
        DataFileName = arg[++k];
        continue;
      }
      if ( arg[k].equals( "-skip" ) ) {
        try {
          skip = Integer.parseInt( arg[k + 1] );
          ++k;
        } catch ( Exception e ) {
          System.out.println( "[Error] couldn't parse " + arg[k + 1] + " as an integer." );
        }
        continue;
      }
      if ( arg[k].equals( "-test" ) ) {
        try {
          test = Integer.parseInt( arg[k + 1] );
          ++k;
        } catch ( Exception e ) {
          System.out.println( "[Error] couldn't parse " + arg[k + 1] + " as an integer." );
        }
        continue;
      }
      if ( arg[k].equals( "-testskip" ) ) {
        try {
          testskip = Integer.parseInt( arg[k + 1] );
          ++k;
        } catch ( Exception e ) {
          System.out.println( "[Error] couldn't parse " + arg[k + 1] + " as an integer." );
        }
        continue;
      }
      if ( arg[k].equals( "-factor" ) ) {
        try {
          factor = Integer.parseInt( arg[k + 1] );
          ++k;
        } catch ( Exception e ) {
          System.out.println( "[Error] couldn't parse " + arg[k + 1] + " as an integer." );
        }
        continue;
      }
      if ( arg[k].equals( "-fast" ) ) {
        Fast = true;
        continue;
      }
      if ( arg[k].equals( "-testweight" ) ) {
        testweight = true;
        continue;
      }
      if ( arg[k].equals( "-optimal" ) ) {
        optimal = true;
        continue;
      }
      if ( arg[k].equals( "-justinfo" ) ) {
        justinfo = true;
        continue;
      }

      if ( arg[k].equals( "-method" ) ) {
	if (arg[k+1].equals("eachmovie") ) {
		currentmethod = EACHMOVIE;
	} else if (arg[k+1].equals("movielens")) {
		currentmethod = MOVIELENS;
	}
        else if ( arg[k + 1].equals( "jester" ) ) {
          currentmethod = JESTER;
          System.out.println( "[info] Switching to jest method" );
        }
        else System.out.println("Unknown method "+arg[k+1]);
        ++k;
        continue;
      }
      System.out.println("Unrecognized option : "+arg[k]);
    }
    if ( test == -1 ) test = maxratings;
/*    if ( currentmethod == INFO ) {
      System.out.println("[info] Reading file "+DataFileName);
      EvaluationSet origuset = EvaluationSet.readRatings( new File( DataFileName ), maxratings, 0, minratingsperuser );
      System.out.println("[info] Number of users with at least "+minratingsperuser+" ratings = "+origuset.getNumberOfUsers() );
      int NumberOfRatings = 0;
      TIntObjectIterator i = origuset.iterator();
      while(i.hasNext()) {
        i.advance();
        NumberOfRatings += ((TIntFloatHashMap) i.value()).size();
      }
      System.out.println("[info] Number of ratings in users with at least "+minratingsperuser+" ratings = "+NumberOfRatings );
      System.out.println("[info] Ratings per users with at least "+minratingsperuser+" ratings = "+NumberOfRatings / (float) origuset.getNumberOfUsers() );
      System.out.println("[info] Detected MaxItemID = "+origuset.getMaxItemID());
      return;
    }*/
    System.out.println( "[info] maxratings = " + maxratings + " skip = " + skip + " users to test against = " + test + " skip test = " + testskip +" factor = "+factor +" fast = "+Fast+ " testweight = "+testweight);
    //
    System.out.println( "[waiting] Parsing..." );
    //
    EvaluationSet origuset = EvaluationSet.readRatings( new File( DataFileName ), maxratings, skip, minratingsperuser );
    EvaluationSet testset = EvaluationSet.readRatings( new File( DataFileName ), test, testskip, minratingsperuser );
    int origMaxItemID = origuset.computeApparentMaxItemID();
    int testMaxItemID = testset.computeApparentMaxItemID();
    System.out.println( "[info] origset size = " + origuset.size() + " max id=" + origMaxItemID );
    System.out.println( "[info] testset size = " + testset.size() + " max id=" + testMaxItemID );
    int MaxItemID = origMaxItemID > testMaxItemID ? origMaxItemID : testMaxItemID;
    origuset.setMaxItemID(MaxItemID);
    testset.setMaxItemID(MaxItemID);
    System.out.println("[info] Setting max item id to : "+MaxItemID);
    System.out.println( "[info] Loaded " + origuset.size() + " users" );
    //
    Vector CFSVector = null;
    if (names == null)
      CFSVector = getPredefined(origuset,currentmethod,testweight,Fast,optimal);
    else 
      CFSVector = CollaborativeFilteringSystemFactory.getCFS(origuset,names); 
    int RatingsToTest = test;
    if(!justinfo) {
      for (Enumeration e = CFSVector.elements() ; e.hasMoreElements() ;) {
        CollaborativeFilteringSystem cfs = (CollaborativeFilteringSystem) e.nextElement();
        System.out.println( "[name] "+cfs.toString());
        benchmark( cfs, testset, RatingsToTest,minvalue, maxvalue );
      }
    }
  }

  static Vector getPredefined(EvaluationSet origuset, int currentmethod, boolean testweight, boolean Fast, boolean optimal) {
    Vector CFSVector = new Vector();
    if(testweight) {
      if ( ! Fast ) {					
        /* CFSVector.add(new OptimalWeight(new RuleOf5( origuset) , 1.0f )); */
        
      }
      //int seven_percent = (int)Math.round(0.06*MaxItemID);
      //int number_of_vectors = seven_percent > 30 ? 30 : seven_percent;
      //CFSVector.add(new EigenMatch( origuset,1,false,false));
      //CFSVector.add(new EigenMatch( origuset,1,true,false));
      //CFSVector.add(new EigenMatch( origuset,1,false,true));
      //CFSVector.add(new EigenMatch( origuset,1,true,true));
      if(optimal) {
          CFSVector.add(new TIOptimalConstantWeight( origuset ));
          CFSVector.add(new TIOptimalWeight( origuset ));
          CFSVector.add(new OptimalConstantWeight( origuset));
      }
      /* if(true) {
        OptimalWeight ow = new OptimalWeight(anna, 0.1f);
        CFSVector.add(ow);
      } */
      //CFSVector.add(new EigenMatch( origuset,2));
      //CFSVector.add(new EigenMatch( origuset,5));
    }
    if ( ! Fast ) {
      CFSVector.add(new Pearson( origuset ));
      CFSVector.add(new STIPearson( origuset, 2.0f ));
      CFSVector.add(new MeanSTIPearson( origuset, 2.0f ));
    }
    //CFSVector.add(new SimpleBiConstantBias( origuset));
    //CFSVector.add(new AdjustScale(new SimpleBiConstantBias( origuset)));
    CFSVector.add(new ItemBased( origuset));
    CFSVector.add(new ConstantBias( origuset,4));
    CFSVector.add(new AdjustScale(new ConstantBias( origuset,4)));
    CFSVector.add(new BiConstantBias( origuset));
    CFSVector.add(new AdjustScale(new BiConstantBias( origuset)));
    //CFSVector.add(new ConstantBias( origuset,3));
    CFSVector.add(new ConstantBias( origuset,0));
    CFSVector.add(new AdjustScale(new ConstantBias( origuset,0)));
    
    
    if(false) CFSVector.add(new FittedConstantBias( origuset));
    if(false) {
      CFSVector.add(new PairwiseRegression( origuset,0,false));
      CFSVector.add(new PairwiseRegression( origuset,0, true));
      CFSVector.add(new PairwiseRegression( origuset,1, false));
      CFSVector.add(new PairwiseRegression( origuset,1, true));
      CFSVector.add(new PairwiseRegression( origuset,2,false));
      CFSVector.add(new PairwiseRegression( origuset,2,true));
      CFSVector.add(new PairwiseRegression( origuset,3,false));
      CFSVector.add(new PairwiseRegression( origuset,3,true));
    }
    if(false) {
      CFSVector.add(new AdjustScale(new OptiBiRuleOf3( origuset)));
      CFSVector.add(new OptiBiRuleOf3( origuset));
      CFSVector.add(new OptiRuleOf3( origuset));
    }

    
    //CFSVector.add(new ConstantBias( origuset,1));
    //CFSVector.add(new ConstantBias( origuset,2));

    
    //
    CFSVector.add(new NonPersonalized( origuset ));
    CFSVector.add(new AdjustScale(new NonPersonalized( origuset )));
    CFSVector.add(new ReverseNonPersonalized( origuset ));    
    //CFSVector.add(new BiasFrom(new BiRuleOf3( origuset)));
    if(false) {
      CFSVector.add(new BiasFrom(new BiRuleOf3( origuset)));
      CFSVector.add(new ReverseBiasFrom(new BiRuleOf3( origuset)));
    }
    if( false) {
      CFSVector.add(new AdjustScale(new BiRuleOf3( origuset)));
      CFSVector.add(new BiRuleOf3( origuset));
      CFSVector.add(new RuleOf3( origuset));
    }

    //CFSVector.add(new BiasFrom(new BiConstantBias( origuset)));
    if(false) {
      CFSVector.add(new BiasFrom(new BiConstantBias( origuset)));
      CFSVector.add(new ReverseBiasFrom(new BiConstantBias( origuset)));
    }
    

 
//    CFSVector.add(new BiNonPersonalized( origuset ));
    CFSVector.add(new Average(  origuset));
    CFSVector.add(new PerItemAverage( origuset ));
    
    CFSVector.add(new STINonPersonalized( origuset, 2.0f ));
    if(false) {
      CFSVector.add(new BiRuleOf5( origuset));
      CFSVector.add(new RuleOf5( origuset));
    }
    if(false) {
      CFSVector.add(new MeanSTINonPersonalized( origuset, 2.0f ));
      CFSVector.add(new MeanSTINonPersonalized2steps( origuset, 2.0f ));
    }
    CFSVector.add(new STINonPersonalized2steps( origuset, 2.0f ));
    if(false) {
      CFSVector.add(new STINonPersonalizedNsteps( origuset, 2.0f, 1 ));
      CFSVector.add(new STINonPersonalizedNsteps( origuset, 2.0f, 2 ));
      CFSVector.add(new STINonPersonalizedNsteps( origuset, 2.0f, 3 ));
    }

    //
    //
    if ( currentmethod == JESTER ) {
        CFSVector.add(new JesterClassical(origuset, false));// true == new normalization
        CFSVector.add(new JesterSTI(origuset));
    }
    return CFSVector;
  }
  
}
//CollaborativeFilteringSystemFactory
