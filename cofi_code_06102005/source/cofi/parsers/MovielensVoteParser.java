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
package cofi.parsers;

import cofi.data.*;
import gnu.trove.*;
import java.io.*;

/**
 *  A class to parse the EachMovie database. Most EachMovie database-specific
 *  features should be in there. For research and non commercial purposes.
 *
 *@author       Daniel Lemire, Ph.D.
 *@since      April 2004
 *@author   Université du Québec
 */
public final class MovielensVoteParser {

  /**
   *  What is the number of ratings we can read (max)
   */
  public static int MAX_DEFAULT_NUMBER_OF_RATINGS = 999999999;


  /**
   *  The main program for the EachMovieVoteParser class
   *
   *@param  arg  The command line arguments
   */
  public static void main( String[] arg ) {
    try {
      long before;
      long after;
      before = System.currentTimeMillis();
      EvaluationSet us = MovielensVoteParser.parse( new File( arg[0] ) );
      after = System.currentTimeMillis();
      System.out.println( "Parsed " + us.getNumberOfUsers() + " in " + ( ( after - before ) / 1024.0 ) + " s." );
      // next we see if we need to serialize it!
      if ( arg.length > 1 ) {
        System.out.println( "Serializing to " + arg[1] );
        before = System.currentTimeMillis();
        OutputStream ostream = new BufferedOutputStream( new FileOutputStream( arg[1] ) );
        us.write( ostream );
        ostream.flush();
        ostream.close();
        after = System.currentTimeMillis();
        System.out.println( "It took " + ( ( after - before ) / 1024.0 ) + " s." );
        System.out.println( "Testing file integrity." );
        before = System.currentTimeMillis();
        InputStream istream = new BufferedInputStream( new FileInputStream( arg[1] ) );
        us.read( istream );
        istream.close();
        after = System.currentTimeMillis();
        System.out.println( "Parsed " + us.getNumberOfUsers() + " in " + ( ( after - before ) / 1024.0 ) + " s." );
      }
    } catch ( IOException fnfe ) {
      fnfe.printStackTrace();
    }
  }


  /**
   *  Read a EvaluationSet object from a ratings.dat Movielens database
   *
   *@param  file                       Text file containing the data (Vote.txt)
   *@return                            A EvaluationSet object
   *@exception  FileNotFoundException  if the file cannot be found
   */
  public static EvaluationSet parse( File file ) throws FileNotFoundException {
    return parse( file, "::", 999999999 );
  }


  /**
   *  Read a EvaluationSet object from a ratings.data Movielens database
   *
   *@param  file                       Text file containing the data (Vote.txt)
   *@param  delimiter                  The delimiter should be chosen to be '::'
   *@param  MaxNumber                  How many ratings you want to read (max)
   *@return                            A EvaluationSet object
   *@exception  FileNotFoundException  if the file cannot be found
   */
  public static EvaluationSet parse( File file, String delimiter, int MaxNumber ) throws FileNotFoundException {
    EvaluationSet us = new EvaluationSet();
    BufferedReader in = new BufferedReader( new FileReader( file ) );
    String line;
    Rating r = new Rating();
    int NumberRead = 0;
    try {
      while ( ( ( line = in.readLine() ) != null ) && ( NumberRead < MaxNumber ) ) {
        MovielensVoteParser.parseMovielens( r, line, delimiter );
        us.add( r );
        ++NumberRead;
      }
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      // nothing else you can do? Just abort for now!
      System.err.println( "[Error] could parse the file" );
    }
    return us;
  }


  /**
   *  This is EachMovie specific
   *
   *@param  str        A single line in the Vote.txt file
   *@param  delimiter  The delimiter used (\t is always used)
   *@param  r          Description of the Parameter
   */
  public static void parseMovielens( Rating r, String str, String delimiter ) {
    String[] dat = str.split(delimiter);
    if ( dat.length != 4 )
      System.err.println( "[Error] Parse Error on " + str);
    r.mUser = Integer.parseInt(dat[0]);
    r.mItem = Integer.parseInt(dat[1]);
    r.mVote = Integer.parseInt(dat[2]);
    // we don't care about dates
  }

}


