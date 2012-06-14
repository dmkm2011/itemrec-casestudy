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

import java.sql.*;
import java.util.*;

import gnu.trove.*;
import html.*;

/**
 *
 *   Manages Connection to mySQL database (JDBC). </p>
 *
 *@author     Nancy Howse & Marcel Ball
 *@since      July 11, 2003
 */

public class ConnectionSingleton
{

   private Connection con = null;
   private static ConnectionSingleton instance = null;

   /**
    *  ConnectionSingleton constructor.
    */
   private ConnectionSingleton()
   {
      try
      {
   Class.forName(HTML.SQL_DRIVER).newInstance();
   con = java.sql.DriverManager.getConnection(HTML.getDatabase());
      }
      catch (ClassNotFoundException e)
      {
   System.err.println("[ConnectionSingleton] ClassNotFoundException: " +
          e.getMessage());
   e.printStackTrace();
   System.err.println("If you are using this code could without a database, please use EvaluationSet with setMaxItemID");
   throw new RacofiDataException(e.getMessage());
      }
      catch (IllegalAccessException e)
      {
   System.err.println("[ConnectionSingleton] IllegalAccessException: " +
          e.getMessage());
   e.printStackTrace();
   throw new RacofiDataException(e.getMessage());
      }
      catch (InstantiationException e)
      {
   System.err.println("[ConnectionSingleton] InstantiationException: " +
          e.getMessage());
   e.printStackTrace();
   throw new RacofiDataException(e.getMessage());
      }
      catch (java.sql.SQLException e)
      {
   
   System.err.println("[ConnectionSingleton] SQLException: " +
          e.getMessage());
   e.printStackTrace();
   throw new RacofiDataException(e.getMessage());
      }
      catch (Exception e)
      {
   System.err.println("[ConnectionSingleton] Exception: " + e.getMessage());
   throw new RacofiDataException(e.getMessage());
      }
      catch (Error e)
      {
   System.err.println("[ConnectionSingleton] Error: " + e.getMessage());
   throw e;
      }
   }

   /**
    *  Adds ratings to SQL database.
    *
    *@param  lastItem  integer key of item for which to add rating.
    *@param  userID    integer key for current user to update.
    *@param  ratings   integer array of ratings to add, one for each dimension.
       *@return           boolean true if rating is added, false if an exception is
    *      thrown.
    */
   public boolean addRating(int lastItem, int userID, int[] ratings)
   {
      int id = ItemSingleton.getInstance().get(lastItem).getItemID();
      Statement st2 = null;
      ResultSet rs1 = null;
      try
      {
   st2 = con.createStatement();
   rs1 = st2.executeQuery("SELECT ratingID FROM rating WHERE itemID=" +
        id +
        " AND userID=" + userID);
   if (rs1.next())
   {
      int ratingID = rs1.getInt(1);
      Statement st3 = null;
      try
      {
         st3 = con.createStatement();
         st3.execute("UPDATE rating SET subjective=" + ratings[0] +
         ", lyrics=" + ratings[1] + ", music=" + ratings[2] +
         ", originality=" + ratings[3] + ", performance=" +
         ratings[4] + " WHERE ratingID=" + ratingID);

         if (st3.getUpdateCount() != 1)
         {
      throw new RacofiDataException(
         " Was expected one update, got " + st3.getUpdateCount() +
         " for ratingID = " + ratingID);
         }
         //do something bad
      }
      finally
      {
         if (st3 != null)
         {
      st3.close();
         }
      }
   }
   else
   {
      Statement st1 = con.createStatement();
      st1.execute("INSERT INTO rating (itemID, userID, subjective, lyrics, music, originality, performance)" +
      "VALUES (" + id + "," + userID + "," + ratings[0] +
      "," + ratings[1] + "," + ratings[2] + "," + ratings[3] +
      "," + ratings[4] + ")");

      if (st1.getUpdateCount() != 1)
      {
         // does this even make sense?
         throw new RacofiDataException(" Was expected one insert, got " +
               st1.getUpdateCount() +
               " for ratingID = " + id);
      }

      st1.close();
   }
   return true;
      }
      catch (SQLException e)
      {
   //rm -fr /*
    if (rs1 != null)
    {
       try
       {
    rs1.close();
       }
       catch (SQLException e2)
       {
    e2.printStackTrace();
       }
    }
   ;
   if (st2 != null)
   {
      try
      {
         st2.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   return false;
      }
      finally
      {
   // always close, no matter what!!!
   if (rs1 != null)
   {
      try
      {
         rs1.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (st2 != null)
   {
      try
      {
         st2.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
      }
   }

   /**
    *  Adds a feature to the Item attribute of the ConnectionSingleton object
    *
    *@param  artist       The feature to be added to the Item attribute
    *@param  album        The feature to be added to the Item attribute
    *@param  asin         The feature to be added to the Item attribute
    *@param  price        The feature to be added to the Item attribute
    *@param  label        The feature to be added to the Item attribute
    *@param  trackNum     The feature to be added to the Item attribute
    *@param  releaseDate  The feature to be added to the Item attribute
    *@return              Description of the Return Value
    */
   public int addItem(String artist, String album, String asin, String price,
          String label, String trackNum, String releaseDate)
   {
      artist = makeStringSafe(artist);
      album = makeStringSafe(album);
      price = makeStringSafe(price);
      label = makeStringSafe(label);
      trackNum = makeStringSafe(trackNum);
      releaseDate = makeStringSafe(releaseDate);
      Hashtable query = new Hashtable();
      int itemID = -1;
      try
      {
   Statement st1 = con.createStatement();
   st1.execute(
      "INSERT INTO item (artist, album, url, price, label, trackNum, releaseDate)" +
      "VALUES ('" + artist + "','" + album + "','" + asin + "','" + price +
      "','" + label + "'," + trackNum + ",'" + releaseDate + "')");

   //do something bad
   if (st1.getUpdateCount() != 1)
   {
      throw new RacofiDataException(
         "Inserted many times this item in item table???");
   }
   ;
   //do something bad

   Statement state = con.createStatement();
   ResultSet rs = state.executeQuery(
      "SELECT itemID FROM item WHERE itemID = (SELECT max(itemID) FROM item)");
   rs.next();
   itemID = rs.getInt(1);
      }
      catch (SQLException e)
      {
   System.err.println("SQLException: " + e.getMessage());
    throw new RacofiDataException(e.getMessage());
      }
      return itemID;
   }
   
   public static String makeStringSafe(String s) {
     return (s.replace(';',' ')).replaceAll("'","\\'");
   }

   /**
    *  Adds a feature to the Item attribute of the ConnectionSingleton object
    *
    *@param  artist       The feature to be added to the Item attribute
    *@param  album        The feature to be added to the Item attribute
    *@param  url          The feature to be added to the Item attribute
    *@param  price        The feature to be added to the Item attribute
    *@param  label        The feature to be added to the Item attribute
    *@param  trackNum     The feature to be added to the Item attribute
    *@param  releaseDate  The feature to be added to the Item attribute
    *@return              Description of the Return Value
    */
   public void submitItem(String artist, String album, String url, String price,
        String label, String trackNum, String releaseDate)
   {
      artist = makeStringSafe(artist);
      album = makeStringSafe(album);
      url = makeStringSafe(url);
      price = makeStringSafe(price);
      label = makeStringSafe(label);
      trackNum = makeStringSafe(trackNum);
      releaseDate = makeStringSafe(releaseDate);
      Hashtable query = new Hashtable();
      if(artist.trim().length() != 0)
        query.put("artist","'"+artist+"'");
      if(album.trim().length() != 0)
        query.put("album", "'"+album+"'");
      if(url.trim().length() != 0)
        query.put("url","'"+url+"'");
      if(price.trim().length() != 0)
        query.put("price",price);
      if(label.trim().length() != 0)
        query.put("label","'"+label+"'");
      if(trackNum.trim().length() != 0)
        query.put("numberOfTracks",trackNum);
      if(releaseDate.trim().length() != 0)
        query.put("releaseDate","'"+releaseDate+"'");
      StringBuffer sb = new StringBuffer("INSERT INTO newItem (");
      Enumeration iter = query.keys();
      while(iter.hasMoreElements()) {
        sb.append((String)iter.nextElement());
        if(iter.hasMoreElements()) sb.append(",");
      }
      sb.append(") VALUES (");
      iter = query.keys();
      while(iter.hasMoreElements()) {
        sb.append(query.get(iter.nextElement()));
        if(iter.hasMoreElements()) sb.append(",");
      }
      sb.append(")");
      String queryString = sb.toString();
      try
      {
   Statement st1 = con.createStatement();
   st1.execute(queryString);
   //do something bad
   if (st1.getUpdateCount() != 1)
   {
      throw new RacofiDataException(
         "Inserted many times this item in item table??? Query was: "+queryString);
   }
   ;
   //do something bad
      }
      catch (SQLException e)
      {
        System.err.println("SQLException: " + e.getMessage()+" query = "+queryString);
        throw new RacofiDataException(queryString + " --> " + e.getMessage());
      }
   }

   /**
    *  Loads list of users from SQL database and returns them in a HashMap.
    *
    *@return    HashMap list of users.
    */
   public LinkedHash loadUsers()
   {
      LinkedHashMap BackwardList = new LinkedHashMap();
      LinkedHash CorrectOrderList = new LinkedHash();
      // need to pack the itemIDs
      TIntIntHashMap map = ItemSingleton.getInstance().computeItemIDToArrayId();
      Statement state = null;
      ResultSet rs = null;
      try
      {
   state = con.createStatement();
   rs = state.executeQuery(
      "SELECT BINARY userID, userName, password, admin FROM user ORDER BY timestamp DESC");
   while (rs.next())
   {
      int userID = rs.getInt(1);
      String userName = rs.getString(2);
      String password = rs.getString(3);
      boolean admin = (rs.getInt(4) == 1);
      Statement state2 = null;
      ResultSet rs2 = null;
      try
      {
         state2 = con.createStatement();
         rs2 = state2.executeQuery("SELECT itemID, subjective, lyrics, music, originality, performance FROM rating WHERE userID=" +
           userID);
         User user = new User(userName, password, userID, admin);
         int itemID, s, l, m, o, p, i = 0;
         while (rs2.next())
         {
      itemID = rs2.getInt(1);

      // i = ItemSingleton.getInstance().findID(itemID); // was too slow
      //				 if( i < 0 ) throw new RacofiDataException("Could not find item :"+itemID+" in the database, database corrupted?");
      if (!map.contains(itemID))
      {

         // it could happen that someone added or removed an item while this was being called
         throw new RacofiDataException("Could not find item :" +
      itemID +
      " in the database, database corrupted?");
      }
      i = map.get(itemID);
      // should return packed id...
      s = rs2.getInt(2);
      l = rs2.getInt(3);
      m = rs2.getInt(4);
      o = rs2.getInt(5);
      p = rs2.getInt(6);

      user.setRating(i, s);
      user.setRating(i, l, 'l');
      user.setRating(i, m, 'm');
      user.setRating(i, o, 'o');
      user.setRating(i, p, 'p');
      i++;
      //WWHHHHHHHHHHHHHHYYYYYYYYYYYYYYYYYYYYYYYYY??????????????????
         }
         BackwardList.put(userName, user); // where the check for the 5000 limit?
         if (BackwardList.size() >= CorrectOrderList.getMaxSize())
         {
      break;
         }
      }
      finally
      {
         if (rs2 != null)
         {
      rs2.close();
         }
         if (state2 != null)
         {
      state2.close();
         }
      }
   }
      }
      catch (SQLException e)
      {
   if (rs != null)
   {
      try
      {
         rs.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (state != null)
   {
      try
      {
         state.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   System.err.println(e.getMessage());
      }
      finally
      {
   if (rs != null)
   {
      try
      {
         rs.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (state != null)
   {
      try
      {
         state.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
      }
      /*
       * assume we had {c,b,a}, the iterator should return c,b,a
       * putting that back into a different list, we should get
       * the c is the first inserted, so the oldest... and so on...
       * This is a bit costly, but much less so than reading off the
       * ENTIRE database just so that it can be sorted in the right order.
       */
      Iterator iter = BackwardList.entrySet().iterator();
      System.out.println("Adding users: ");
      while (iter.hasNext())
      {
   Map.Entry entry = (Map.Entry) iter.next();
   System.out.println(entry.getValue());
   CorrectOrderList.put(entry.getKey(), entry.getValue());
   iter.remove(); // we try to use a constant amount of memory!
      }
      return CorrectOrderList;
   }

   /**
    *  Adds a new user to the SQL database.
    *
    *@param  name  String for user's name.
    *@param  pass  String for users's password.
    *@return       integer userID
    */
   public int put(String name, String pass)
   {
      name = makeStringSafe(name);
      pass = makeStringSafe(pass);
      int userID = -1;
      Statement st1 = null;
      //con.createStatement();
      Statement st2 = null;
      //con.createStatement();
      ResultSet rs2 = null;
      //st2.executeQuery("SELECT userID FROM user WHERE userName='" + name + "' AND password='" + pass + "'");
      try
      {
   st1 = con.createStatement();
   st1.execute(
      "INSERT INTO user (userName, password, registerDate) VALUES ('" +
      name + "', '" + pass + "',NOW())");
   st2 = con.createStatement();
   rs2 = st2.executeQuery(
      "SELECT userID FROM user WHERE BINARY userName='" + name +
      "' AND BINARY password='" + pass + "'");
   rs2.next();
   userID = rs2.getInt(1);
      }
      catch (SQLException e)
      {
   if (rs2 != null)
   {
      try
      {
         rs2.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (st2 != null)
   {
      try
      {
         st2.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (st1 != null)
   {
      try
      {
         st1.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   throw new RacofiDataException("SQLException in put : " +
               e.getMessage());
      }
      //do something bad
      finally
      {
   if (rs2 != null)
   {
      try
      {
         rs2.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (st2 != null)
   {
      try
      {
         st2.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (st1 != null)
   {
      try
      {
         st1.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
      }

      return userID;
   }

   /**
    *  Returns a list of items in the SQL database as a TIntObjectHashMap.
    *
    *@return    TIntObjectHashMap itemList
    */
   public TIntObjectHashMap loadItems()
   {
      TIntObjectHashMap itemList = new TIntObjectHashMap();
      Statement state = null;
      ResultSet rs = null;

      try
      {
   state = con.createStatement();
   rs = state.executeQuery("SELECT itemID, artist, album, url,label,releaseDate,numberOfTracks, price FROM item");
   while (rs.next())
   {
      int itemID = rs.getInt(1);
      String artist = rs.getString(2);
      String album = rs.getString(3);
      String asin = rs.getString(4);
      String label = rs.getString(5);
      String releaseDate = rs.getString(6);
      int numberOfTracks = rs.getInt(7);
      float price = (float) (rs.getDouble(8));
      Item item = new Item(artist, album, asin, label, releaseDate,
         numberOfTracks, price, itemID);
      itemList.put(itemList.size(), item);
   }
      }
      catch (SQLException e)
      {
   if (rs != null)
   {
      try
      {
         rs.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (state != null)
   {
      try
      {
         state.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   System.err.println("SQLException: " + e.getMessage());
   throw new RacofiDataException(e.getMessage());
      }
      finally
      {
   if (rs != null)
   {
      try
      {
         rs.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (state != null)
   {
      try
      {
         state.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
      }
      return itemList;
   }

   /**
    *  Removes item i from the SQL database.
    *
    *@param  i  integer key of item to be removed.
    *@return    boolean true if item is sucessfully removed, false otherwise.
    */

   public boolean removeItem(int i)
   {
      Statement stat = null;
      try
      {
   stat = con.createStatement();
   stat.execute("DELETE FROM item WHERE itemID=" + i);
   stat.close();
   stat = con.createStatement();
   stat.execute("DELETE FROM rating WHERE itemID=" + i);
   stat.close();
   return true;
      }
      catch (SQLException e)
      {
   if (stat != null)
   {
      try
      {
         stat.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   return false;
      }
      finally
      {
   if (stat != null)
   {
      try
      {
         stat.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
      }
   }

   /**
    *  Removes user from SQL database.
    *
    *@param  user  User to be removed from database.
       *@return       boolean true if user is successfully removed, false otherwise.
    */
   public boolean removeUser(User user)
   {
      Statement st1 = null;
      Statement st2 = null;
      Statement st3 = null;
      try
      {
   st1 = con.createStatement();
   st1.execute("DELETE FROM user WHERE userID=" + user.getUserID());
   st1.close();
   st2 = con.createStatement();
   st2.execute("DELETE FROM rating WHERE userID=" + user.getUserID());
   st2.close();
   st3 = con.createStatement();
   st3.execute("DELETE FROM comment WHERE userID=" + user.getUserID());
   st3.close();
   return true;
      }
      catch (SQLException e)
      {
   if (st1 != null)
   {
      try
      {
         st1.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (st2 != null)
   {
      try
      {
         st2.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (st3 != null)
   {
      try
      {
         st3.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   return false;
      }
      finally
      {
   if (st1 != null)
   {
      try
      {
         st1.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (st2 != null)
   {
      try
      {
         st2.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
   if (st3 != null)
   {
      try
      {
         st3.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   ;
      }
   }

   /**
    *  Check to see if a user is in the database
    *
    *@param  name  Name of the user
    *@return       Whether or not that user is present
    */
   public boolean checkForUser(String name)
   {
     name = makeStringSafe(name);
    //check mysql for user.
      boolean found = false;
      Statement st1 = null;
      //con.createStatement();
      ResultSet rs1 = null;
      //st1.executeQuery(
      try
      {
   st1 = con.createStatement();
   rs1 = st1.executeQuery(
      "SELECT userID FROM user WHERE BINARY userName='" + name + "'");
   found = rs1.next();
      }
      catch (SQLException e)
      {
   if (rs1 != null)
   {
      try
      {
         rs1.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   if (st1 != null)
   {
      try
      {
         st1.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   throw new RacofiDataException("Could not remove user " + name +
               " from database."); //do something bad
      }
      finally
      {
   if (rs1 != null)
   {
      try
      {
         rs1.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   if (st1 != null)
   {
      try
      {
         st1.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
      }
      return found;
   }

   /**
    *  Gets the user attribute of the ConnectionSingleton object
    *
    *@param  name  Description of the Parameter
    *@return       The user value
    */
   public User getUser(String name)
   {
      name = makeStringSafe(name);
      TIntIntHashMap map = ItemSingleton.getInstance().computeItemIDToArrayId();
      // need to pack the itemIDs
      User user = null;
      Statement state = null;
      ResultSet rs = null;
      try
      {
   state = con.createStatement();
   rs = state.executeQuery(
      "SELECT userID, userName, password, admin FROM user WHERE BINARY userName='" +
      name + "'");
   if (rs.next())
   {
      int userID = rs.getInt(1);
      String userName = rs.getString(2);
      String password = rs.getString(3);
      boolean admin = (rs.getInt(4) == 1);
      Statement state2 = null;
      ResultSet rs2 = null;
      try
      {
         state2 = con.createStatement();
         rs2 = state2.executeQuery("SELECT itemID, subjective, lyrics, music, originality, performance FROM rating WHERE userID=" +
           userID);
         user = new User(userName, password, userID, admin);
         int itemID, s, l, m, o, p, i = 0;
         while (rs2.next())
         {
      itemID = rs2.getInt(1);
      if (!map.contains(itemID))
      {
         throw new RacofiDataException("Could not retrieve itemID " +
      itemID +
      " from the database, database corrupted? ");
      }

      i = map.get(itemID);
      s = rs2.getInt(2);
      l = rs2.getInt(3);
      m = rs2.getInt(4);
      o = rs2.getInt(5);
      p = rs2.getInt(6);

      user.setRating(i, s);
      user.setRating(i, l, 'l');
      user.setRating(i, m, 'm');
      user.setRating(i, o, 'o');
      user.setRating(i, p, 'p');
      i++;
         }
      }
      finally
      {
         if (rs2 != null)
      rs2.close();
         if (state2 != null)
      state2.close();
      }
   }
   else
   {
      throw new RacofiDataException("User : " + name + " not found!");
   }

      }
      catch (SQLException e)
      {
   if (rs != null)
   {
      try
      {
         rs.close();
      }
      catch (SQLException e2)
      { e2.printStackTrace(); }
   }
   ;
   if (state != null)
   {
      try
      {
         state.close();
      }
      catch (SQLException e2)
      { e2.printStackTrace(); }
   }
   ;
   throw new RacofiDataException("SQL error " + e.getMessage() +
               " when querying user = " + name);
      }
      finally
      {
   if (rs != null)
   {
      try
      {
         rs.close();
      }
      catch (SQLException e2)
      { e2.printStackTrace(); }
   }
   if (state != null)
   {
      try
      {
         state.close();
      }
      catch (SQLException e2)
      { e2.printStackTrace(); }
   }
      }
      return user;
   }

   /**
    * Returns the email addresses of all administrators from the database.
    * @return addresses String with comma seperated email addresses
    */
   public String getAdmin()
   {
      Statement state = null;
      ResultSet rs = null;
      String addresses = "";

      try
      {
   state = con.createStatement();
   rs = state.executeQuery( "SELECT email FROM admins" );

   if (rs.next())
      addresses += rs.getString(1);
   while (rs.next())
   {
      addresses += ", " + rs.getString(1);
   }
  
      }
      catch( SQLException e )
      { System.err.println("[ConnectionSingleton] SQLException: " + e.getMessage()); }
      return addresses;
   }

   /**
    *  Go in the database and put the current time as the timestamp of the user
    *
    *@param  userID  user id
    */
   public void updateTimeStamp(int userID)
   {
      Statement st = null;
      try
      {
   st = con.createStatement();
   int i = st.executeUpdate(
      "UPDATE user SET timestamp=NOW() WHERE userID=" +
      userID);
   if (i > 1)
   {
      throw new RacofiDataException("Got " + i +
         " updates but was expecting 1 when querying timestamp for user " +
         userID);
   }
      }
      catch (SQLException e)
      {
   if (st != null)
   {
      try
      {
         st.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
   //do something bad
   throw new RacofiDataException("SQL error " + e.getMessage() +
               " when querying timestamp for user " +
               userID);
      }
      finally
      {
   if (st != null)
   {
      try
      {
         st.close();
      }
      catch (SQLException e2)
      {
         e2.printStackTrace();
      }
   }
      }

   }

   /**
    *  Static synchronized method which returns the singleton's instance.
    *
    *@return    ConnectionSingleton instance
    */
   public static synchronized ConnectionSingleton getInstance()
   {
      try
      {
   if ( (instance == null) || (instance.con.isClosed()))
   {
      instance = new ConnectionSingleton();
   }
      }
      catch (SQLException e)
      {
   // right, and who is going to tell us if it failed?
   e.printStackTrace(); //needed
   instance = new ConnectionSingleton();
      }
      return instance;
   }

   public static void destroy()
   {
      try
      {
   if (instance.con != null)
   {
      instance.con.close();
   }
      }
      catch (SQLException e)
      {
   e.printStackTrace();
      }
      instance = null;
   }
}
