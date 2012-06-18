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

import java.io.*;
import gnu.trove.*;
import gnu.trove.map.hash.TIntFloatHashMap;
import cofi.data.*;

/**
 *  A class to parse the Jester database.
 *
 * For research and non commercial purposes.
 *
 *
 *
 *@author       Daniel Lemire, Ph.D.
 *@since      September 2002
 *@author	National Research Council of Canada
 */
public final class JesterVoteParser {

  /**
   *  What is the number of ratings we can read (max)
   */
  public static int MAX_DEFAULT_NUMBER_OF_RATINGS = 999999999;
/*
  public static void deleteme(String[] arg) throws IOException {
    EvaluationSet us = JesterVoteParser.parseLine(new File(arg[0]),',', 17071);
  }
  */
  
  /**
   *  The main program for the EachMovieVoteParser class
   *
   *@param  arg  The command line arguments
   */
  public static void main(String[] arg) {
    try {
      long before;
      long after;
      before = System.currentTimeMillis();
      EvaluationSet us = JesterVoteParser.parse(new File(arg[0]));
      after = System.currentTimeMillis();
      System.out.println("Parsed " + us.getNumberOfUsers() + " in " + ((after - before) / 1024.0) + " s.");
      // next we see if we need to serialize it!
      if (arg.length > 1) {
        System.out.println("Serializing to " + arg[1]);
        before = System.currentTimeMillis();
        OutputStream ostream = new BufferedOutputStream(new FileOutputStream(arg[1]));
        us.write(ostream);
        ostream.flush();
        ostream.close();
        after = System.currentTimeMillis();
        System.out.println("It took " + ((after - before) / 1024.0) + " s.");
        System.out.println("Testing file integrity.");
        before = System.currentTimeMillis();
        InputStream istream = new BufferedInputStream(new FileInputStream(arg[1]));
        us.read(istream);
        istream.close();
        after = System.currentTimeMillis();
        System.out.println("Parsed " + us.getNumberOfUsers() + " in " + ((after - before) / 1024.0) + " s.");
      }
    } catch (IOException fnfe) {
      fnfe.printStackTrace();
    }
  }


  /**
   *  Read a EvaluationSet object from a Vote.txt EachMovie database
   *
   *@param  file                       Text file containing the data (Vote.txt)
   *@param  delimiter                  The delimiter should be chosen to be '\t'
   *@param  MaxNumber                  How many ratings you want to read (max)
   *@return                            A EvaluationSet object
   *@exception  FileNotFoundException  if the file cannot be found
   */
  public static EvaluationSet parse(File file, char delimiter, int MaxNumber) throws FileNotFoundException {
    EvaluationSet us = new EvaluationSet();
    BufferedReader in = new BufferedReader(new FileReader(file));
    String line;
    Rating r = new Rating();
    int NumberRead = 0;
    try {
      while (((line = in.readLine()) != null) && (NumberRead < MaxNumber)) {				
        TIntFloatHashMap user = parseJester(line, delimiter, false);
        ++NumberRead;
        us.add(NumberRead,user);
        
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
      // nothing else you can do? Just abort for now!
      System.err.println("[Error] could parse the file");
    }
    return us;
  }
  
  /**
  */
  public static EvaluationSet parseLine(File file, char delimiter, int LineNumber) throws FileNotFoundException {
    EvaluationSet us = new EvaluationSet();
    BufferedReader in = new BufferedReader(new FileReader(file));
    String line;
    Rating r = new Rating();
    int NumberRead = 0;
    try {
      while (((line = in.readLine()) != null) && (NumberRead < LineNumber - 1)) {				
        ++NumberRead;				
      }
      TIntFloatHashMap user = parseJester(line, delimiter, true);
      us.add(0,user);
    } catch (IOException ioe) {
      ioe.printStackTrace();
      // nothing else you can do? Just abort for now!
      System.err.println("[Error] could parse the file");
    }
    return us;
  }	

  /**
  */	
  public static TIntFloatHashMap parseJester(String str, char delimiter, boolean debug) {
    TIntFloatHashMap user = new TIntFloatHashMap(); 
    int i = 0;
    int newi;
    int stored = 0;
    String substring;
    while ((stored < 101) && (i != -1) && (i < str.length())) {
      newi = str.indexOf(delimiter, i);
      //System.out.println("i = "+i);
      //System.out.println("newi = "+newi);
      if (newi == -1) {
        substring = str.substring(i);
      } else {
        substring = str.substring(i, newi);
      }
      if(stored == 0 ) {
        // do nothing?
      } else {
        float rating = Float.parseFloat(substring);
        if(debug) System.out.println(stored+" "+substring);
        if(Math.abs(rating) <= 20.0f)	{
          user.put(stored, rating);
        } else {
          if(rating != 99.0f) System.out.println("[Warning] Don't know what to do with "+rating);
        }
      }
      ++stored;
      i = newi + 1;
    }
    return user;
  }


  /**
   *  Read a EvaluationSet object from a Vote.txt EachMovie database
   *
   *@param  file                       Text file containing the data (Vote.txt)
   *@return                            A EvaluationSet object
   *@exception  FileNotFoundException  if the file cannot be found
   */
  public static EvaluationSet parse(File file) throws FileNotFoundException {
    return parse(file, ',', 999999999);
  }


}

