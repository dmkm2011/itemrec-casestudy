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
import cofi.algorithms.util.*;
import gnu.trove.*;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntFloatProcedure;
import gnu.trove.procedure.TIntObjectProcedure;

import java.io.*;

/**
 *  We define an evaluation to be a set of unidimensional ratings on a set of items from a single user. This class
 *  models a set of such evaluations.
 *
 *@author     Daniel Lemire
 *@since      September 4th 2002
 */
public class EvaluationSet extends TIntObjectHashMap implements Serializable, Cloneable {
  int mSize = -1;
  int mMaxItemID = -1;
  int mMinNumberOfRatings;
  /**
   *  How many users can we load by default
   */
  public static int DEFAULT_MAX_USER = 1000000;

  final static TObjectFunction mSubstractAverageFunction =
    new TObjectFunction() {
      public Object execute( Object a ) {
        TIntFloatHashMap user = (TIntFloatHashMap) a;
        UtilMath.subtractAverage( user );
        return user;
      }
    };



  /**
   *  Constructor for the EvaluationSet object
   */
  public EvaluationSet() { }


  /**
   *  Constructor for the EvaluationSet object
   *
   *@param  file                custom binary file
   *@param  MinNumberOfRatings  minimum number of ratings an evaluation can have
   *@exception  IOException     Description of the Exception
   */
  public EvaluationSet( File file, int MinNumberOfRatings ) throws IOException {
    mMinNumberOfRatings = MinNumberOfRatings;
    BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
    try {
      read( bis );
    } finally {
      bis.close();
    }
  }


  /**
   *  Constructor for the EvaluationSet object
   *
   *@param  file                Custom Binary file containing the data
   *@param  dryrun              whether to actually build the object or not
   *@param  MinNumberOfRatings  Minimum number of ratings an evaluation can have
   *@exception  IOException     if could not read file succesfully
   */
  public EvaluationSet( File file, boolean dryrun, int MinNumberOfRatings ) throws IOException {
    mMinNumberOfRatings = MinNumberOfRatings;
    BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
    try {
      read( bis, dryrun );
    } finally {
      bis.close();
    }
  }


  /**
   *  Constructor for the EvaluationSet object
   *
   *@param  file                Binary file containing the data
   *@param  MaxUser             How many users can your read? (max)
   *@param  MinNumberOfRatings  Description of the Parameter
   *@exception  IOException     if could not read file succesfully
   */
  public EvaluationSet( File file, int MaxUser, int MinNumberOfRatings ) throws IOException {
    mMinNumberOfRatings = MinNumberOfRatings;
    BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
    read( bis, MaxUser, 0, false );
    bis.close();
  }


  /**
   *  Constructor for the EvaluationSet object
   *
   *@param  file                Binary file containing the data
   *@param  MaxUser             How many users can your read? (max)
   *@param  skip                Description of the Parameter
   *@param  MinNumberOfRatings  Description of the Parameter
   *@exception  IOException     if could not read file succesfully
   */
  public EvaluationSet( File file, int MaxUser, int skip, int MinNumberOfRatings ) throws IOException {
    mMinNumberOfRatings = MinNumberOfRatings;
    BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
    read( bis, MaxUser, skip, false );
    bis.close();
  }


  /**
   *  Constructor for the EvaluationSet object
   *
   *@param  file                Binary file containing the data
   *@param  MaxUser             How many users can your read? (max)
   *@param  skip                Description of the Parameter
   *@param  dryrun              Description of the Parameter
   *@param  MinNumberOfRatings  Description of the Parameter
   *@exception  IOException     if could not read file succesfully
   */
  public EvaluationSet( File file, int MaxUser, int skip, boolean dryrun, int MinNumberOfRatings ) throws IOException {
    mMinNumberOfRatings = MinNumberOfRatings;
    BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
    read( bis, MaxUser, skip, dryrun );
    bis.close();
  }


  /**
   *  Constructor for the EvaluationSet object
   *
   *@param  file                Binary file containing the data
   *@param  MaxUser             How many users can your read? (max)
   *@param  dryrun              Description of the Parameter
   *@param  MinNumberOfRatings  Description of the Parameter
   *@exception  IOException     if could not read file succesfully
   */
  public EvaluationSet( File file, int MaxUser, boolean dryrun, int MinNumberOfRatings ) throws IOException {
    mMinNumberOfRatings = MinNumberOfRatings;
    BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
    read( bis, MaxUser, 0, dryrun );
    bis.close();
  }


  /**
   *  Return a copy
   *
   *@return    copy
   *@author    Daniel Lemire
   */
  public synchronized Object clone() {
    EvaluationSet myclone;
	try {
		myclone = (EvaluationSet) super.clone();
		myclone.mSize = mSize;
	    myclone.mMaxItemID = mMaxItemID;
	    if ( myclone._values == null )
	      myclone._values = new Object[_values.length];
	    for ( int k = 0; k < super._values.length; ++k ) {
	      final TIntFloatHashMap currentelement = (TIntFloatHashMap) super._values[k];
	      if ( currentelement != null )
	        myclone._values[k] = new TIntFloatHashMap(currentelement);
	    }
	    return myclone;
	} catch (CloneNotSupportedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
  }


  /**
   *  This finds the max item ID dynamically. This is necessary to define what a
   *  complete user is.
   *
   *@return    The maxItemID value
   */
  private int computeMaxItemID() {
    /*
     *  TIntObjectIterator iter = iterator();
     *  int maxKey = 0;
     *  while ( iter.hasNext() ) {
     *  iter.advance();
     *  TIntFloatHashMap user = (TIntFloatHashMap) iter.value();
     *  TIntFloatIterator itemidIterator = user.iterator();
     *  while ( itemidIterator.hasNext() ) {
     *  itemidIterator.advance();
     *  if ( itemidIterator.key() > maxKey )
     *  maxKey = itemidIterator.key();
     *  }
     *  }
     */
    int maxKey = ItemSingleton.getInstance().getCount();
    return maxKey;
  }
  
  /**
  * This method should try to estimate the MaxItemID 
  * <b>without</b> any call to ItemSingleton.
  */
  public int computeApparentMaxItemID() {
       TIntObjectIterator iter = iterator();
       int maxKey = 0;
       while ( iter.hasNext() ) {
         iter.advance();
         TIntFloatHashMap user = (TIntFloatHashMap) iter.value();
         TIntFloatIterator itemidIterator = user.iterator();
         while ( itemidIterator.hasNext() ) {
            itemidIterator.advance();
            if ( itemidIterator.key() > maxKey )
              maxKey = itemidIterator.key();
         }
       }
       return maxKey + 1;
  }
  


  /**
   *  This finds the max item ID dynamically. This is necessary to define what a
   *  complete user is.
   * If you can't use directly setMaxItemID( int i )
   * because you don't know MaXItemId, and you don't want
   * a call to a database you don't have, use
   * setMaxItemId(computeApparentMaxItemId());
   *
   *@return    The maxItemID value
   */
  public int getMaxItemID() {
    if ( mMaxItemID != -1 )
      return mMaxItemID;
    return computeMaxItemID();
  }


  /**
   *  Sets the maxItemID attribute of the EvaluationSet object
   *
   *@param  i  The new maxItemID value
   */
  public void setMaxItemID( int i ) {
    mMaxItemID = i;
  }


  /**
   *  Gets the number of users
   *
   *@return    The number of users (can be zero)
   */
  public int getNumberOfUsers() {
    return size();
  }



  /**
   *  Add a rating
   *
   *@param  user  user id
   *@param  item  item id
   *@param  vote  ranked attibuted
   *@return       the previous value of the rating, or null (how) if this is a new
   *      rating
   */
  public float add( int user, int item, float vote ) {
    if ( !containsKey( user ) )
      put( user, new TIntFloatHashMap() );
    TIntFloatHashMap userinfo = (TIntFloatHashMap) get( user );
    return userinfo.put( item, vote );
  }


  /**
   *  Description of the Method
   *
   *@param  key   Description of the Parameter
   *@param  user  Description of the Parameter
   */
  public void add( int key, TIntFloatHashMap user ) {
    put( key, user );
  }


  /**
   *  Add a rating
   *
   *@param  r  Ranking object to be added
   *@return    the previous value of the rating, or null if this is a new rating
   */
  public float add( Rating r ) {
    return add( r.mUser, r.mItem, r.mVote );
  }


  /**
   *  This should be in the constructor, but for now, just make a separate call.
   *  You have to call this right after your constructor (each time).
   */
  public void subtractAverages() {
    transformValues( mSubstractAverageFunction );
  }


  /**
   *  Display on screen the content of the object
   */
  public void print() {
    forEachEntry(
      new TIntObjectProcedure() {
        public boolean execute( int a, Object b ) {
          // ID of user
          TIntFloatHashMap user = (TIntFloatHashMap) b;
          EvaluationSet.printUser( user );
          return true;
        }
      } );

  }


  /**
   *  Description of the Method
   *
   *@param  is      Description of the Parameter
   *@param  dryrun  Description of the Parameter
   */
  public void read( InputStream is, boolean dryrun ) {
    read( is, DEFAULT_MAX_USER, 0, dryrun );
  }


  /**
   *  Description of the Method
   *
   *@param  is  Description of the Parameter
   */
  public void read( InputStream is ) {
    read( is, DEFAULT_MAX_USER, 0, false );
  }


  /**
   *  Read this object from input stream (object will reset itself before reading
   *  data)
   *
   *@param  is       Input stream where to read
   *@param  MaxUser  if could not read object
   *@param  skip     Description of the Parameter
   *@param  dryrun   Description of the Parameter
   */
  public void read( InputStream is, int MaxUser, int skip, boolean dryrun ) {
    int userid;
    int number;
    int itemid;
    int NumberOfUsers = 0;
    int NumberOfVotes = 0;
    float vote;
    clear();
    // make sure we reinitialize
    DataInputStream dis = new DataInputStream( is );
    try {
      int version = dis.readInt();
      // should check that it is 1!
      if ( dis.readInt() != 1234 )
        System.err.println( "[Error] Magic Number not recognized. File is corrupted." );

      while ( skip > 0 ) {
        userid = dis.readInt();
        number = dis.readInt();
        //TIntFloatHashMap user = new TIntFloatHashMap();
        for ( int k = 0; k < number; ++k ) {
          itemid = dis.readInt() - 1;// by convention, when we save them they go from 1-n, now we pack them!
          vote = dis.readFloat();
          //user.put(itemid, vote);
        }
        if ( number >= mMinNumberOfRatings )
          --skip;

      }
      while ( NumberOfUsers < MaxUser ) {
        userid = dis.readInt();
        number = dis.readInt();
        TIntFloatHashMap user = new TIntFloatHashMap();
        for ( int k = 0; k < number; ++k ) {
          itemid = dis.readInt() - 1; // by convention, they are store as 1...n, so we must subtract 1 to pack them!
          vote = dis.readFloat();
          user.put( itemid, vote );
          ++NumberOfVotes;
        }
        if ( number >= mMinNumberOfRatings ) {
          put( userid, user );
          NumberOfUsers++;
        }
      }
    } catch ( EOFException eofe ) {
      System.out.println( "[debug] eof" );
      // this is expected
      //return;
    } catch ( IOException ioe ) {
      // unexpected
      ioe.printStackTrace();
    }
    if ( dryrun )
      System.out.println( "[dryrun] Read " + NumberOfUsers + " users with an average of " + ( NumberOfVotes / (float) NumberOfUsers ) + " per user" );
  }


  /**
   *  Read this object from input stream (object will reset itself before reading
   *  data)
   *
   *@param  is             Input stream where to read
   *@param  MaxRating      Description of the Parameter
   *@param  RatingsToSkip  Description of the Parameter
   */
  public void readByRating( InputStream is, int MaxRating, int RatingsToSkip ) {
    int userid;
    int number;
    int itemid;
    float vote;
    clear();
    // make sure we reinitialize
    DataInputStream dis = new DataInputStream( is );
    try {
      int version = dis.readInt();
      // should check that it is 1!
      if ( dis.readInt() != 1234 )
        System.err.println( "[Error] Magic Number not recognized. File is corrupted." );

      while ( RatingsToSkip > 0 ) {
        userid = dis.readInt();
        number = dis.readInt();
        //System.out.println("number = "+number);
        for ( int k = 0; k < number; ++k ) {
          itemid = dis.readInt() - 1;// must subtract 1 to pack ids
          vote = dis.readFloat();
        }
        if ( number >= mMinNumberOfRatings )
          RatingsToSkip -= number;
        //	System.out.println("skipped "+number);


      }
      while ( MaxRating > 0 ) {
        userid = dis.readInt();
        number = dis.readInt();
        TIntFloatHashMap user = new TIntFloatHashMap();
        for ( int k = 0; k < number; ++k ) {
          itemid = dis.readInt() - 1;// must subtract 1 to pack ids
          vote = dis.readFloat();
          user.put( itemid, vote );
        }
        if ( number >= mMinNumberOfRatings ) {
          put( userid, user );
          MaxRating -= number;
        }
      }
    } catch ( EOFException eofe ) {
      System.out.println( "[debug] eof" );
      // this is expected
      //return;
    } catch ( IOException ioe ) {
      // unexpected
      ioe.printStackTrace();
    }
    //if(dryrun) System.out.println("[dryrun] Read "+NumberOfUsers+" users with an average of "+(NumberOfVotes/ (float)NumberOfUsers)+" per user");
  }


  /**
   *  Write this object to a binary file
   *
   *@param  os               stream where to write
   *@exception  IOException  if could not write object
   */
  public void write( OutputStream os ) throws IOException {
    final DataOutputStream dos = new DataOutputStream( os );
    dos.writeInt( 1 );
    // version number
    dos.writeInt( 1234 );
    //magic number
    forEachEntry(
      new TIntObjectProcedure() {
        public boolean execute( int a, Object b ) {
          try {
            dos.writeInt( a );
            // ID of user
            TIntFloatHashMap user = (TIntFloatHashMap) b;
            dos.writeInt( user.keys().length );
            // number of items this user rated
            user.forEachEntry(
              new TIntFloatProcedure() {
                public boolean execute( int a, float b ) {
                  try {
                    dos.writeInt( a );
                    dos.writeFloat( b );
                    return true;
                  } catch ( IOException ioe ) {
                    ioe.printStackTrace();
                    return false;
                  }
                }
              } );
            return true;
          } catch ( IOException ioe ) {
            ioe.printStackTrace();
            return false;
          }
        }
      } );
  }


  /**
   *  Description of the Method
   *
   *@param  file                Description of the Parameter
   *@param  MaxRating           Description of the Parameter
   *@param  RatingsToSkip       Description of the Parameter
   *@param  MinNumberOfRatings  Description of the Parameter
   *@return                     Description of the Return Value
   *@exception  IOException     Description of the Exception
   */
  public static EvaluationSet readRatings( File file, int MaxRating, int RatingsToSkip, int MinNumberOfRatings ) throws IOException {
    EvaluationSet ans = new EvaluationSet();
    ans.mMinNumberOfRatings = MinNumberOfRatings;
    BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
    try {
      ans.readByRating( bis, MaxRating, RatingsToSkip );
    } finally {
      bis.close();
    }
    return ans;
  }


  /**
   *  The main program for the EvaluationSet class
   *
   *@param  arg              The command line arguments
   *@exception  IOException  if could not read a file
   */
  public static void main( String[] arg ) throws IOException {
    String defaultdb = System.getProperty( "user.home" ) + "/CFData/vote.bin";
    int maxuser = 2000;
    if ( arg.length > 0 )
      try {
        maxuser = Integer.parseInt( arg[0] );
      } catch ( NumberFormatException nfe ) {
        nfe.printStackTrace();
      }

    int skip = 16000;
    if ( arg.length > 1 )
      try {
        skip = Integer.parseInt( arg[1] );
      } catch ( NumberFormatException nfe ) {
        nfe.printStackTrace();
      }

    System.out.println( "Loading up to " + maxuser + " users and skipping " + skip );
    EvaluationSet uset = new EvaluationSet( new File( defaultdb ), maxuser, skip, true, 2 );
    //int[] userids = uset.keys();
    System.out.println( "Loaded " + uset.size() + " users!" );
    TIntObjectIterator iter = uset.iterator();
    int maxID = uset.getMaxItemID();
    while ( iter.hasNext() ) {
      System.out.println( "UserID = " + iter.key() );
      UtilMath.print( (TIntFloatHashMap) iter.value(), maxID );
    }
  }



  /**
   *  Compute the average of a user restricted to another
   *
   *@param  user        the user
   *@param  constraint  Description of the Parameter
   *@return             the average
   */
  public static float average( TIntFloatHashMap user, TIntFloatHashMap constraint ) {
    int[] ids = user.keys();
    float aver = 0.0f;
    int number = 0;
    for ( int k = 0; k < ids.length; ++k )
      if ( constraint.contains( ids[k] ) ) {
        aver += user.get( ids[k] );
        number++;
      }
    if ( number > 0 )
      return aver / number;
    return 0.0f;
  }


  /**
   *  Display the info known about a given user
   *
   *@param  user  The user we want to display
   */
  public static void printUser( TIntFloatHashMap user ) {
    System.out.print( "[" );
    // number of items this user rated
    user.forEachEntry(
      new TIntFloatProcedure() {
        public boolean execute( int a, float b ) {
          System.out.print( " t[" + a + "]=" + b );
          return true;
        }
      } );
    System.out.println( " ]" );
  }
}

