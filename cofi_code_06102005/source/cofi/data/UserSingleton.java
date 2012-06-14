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

import java.util.*;

/**
 *
 * This gives us static access to the users as an in-memory database
 * and this classe handles the addedUser calls to the algorithms package.
 *
 *@author     Nancy Howse
 *@since    August 3, 2003
 *@version    1.0
 */

public class UserSingleton implements Runnable
{

   private LinkedHash list = null;
   //private int count;// bad design!
   private static UserSingleton instance = null;
   static private Thread mDestroyThread;
   public static int mDelay = 60 * 60 * 1000; //hard coded, tries to destroy every hour

   /**
    *  Constructor for the UserSingleton object
    */
   private UserSingleton()
   {
      list = ConnectionSingleton.getInstance().loadUsers();
      CFSSingleton.destroy();
      mDestroyThread = new Thread(this);
      mDestroyThread.start();
//		count = ConnectionSingleton.getInstance().getCount();//NO!
   }

   /**
    *  Description of the Method
    *
    *@param  name  Description of the Parameter
    *@param  user  Description of the Parameter
    */
   public void put(String name, User user)
   {
      if (user == null)
      {
   throw new RacofiDataException("NULL USER");
      }
      instance.list.put(name, user);
      ConnectionSingleton.getInstance().updateTimeStamp(user.getUserID());
      CFSSingleton.getInstance().addedUser(user);
   }

   /**
    *  Description of the Method
    *
    *@param  name  Description of the Parameter
    *@param  pass  Description of the Parameter
    */
   public void put(String name, String pass)
   {
      if (!list.containsKey(name))
      {
   //new user to be added to db as well: HOW do you know that???
   // we need to check, dl
   CFSSingleton cfss = CFSSingleton.getInstance();
   User user = null;
   if (!ConnectionSingleton.getInstance().checkForUser(name))
   {
      int userID = ConnectionSingleton.getInstance().put(name, pass);
      user = new User(name, pass, userID);
      instance.list.put(name, user);
      //count++; //why do you need to count them?
   }
   else
   {
      user = ConnectionSingleton.getInstance().getUser(name);
      list.put(name, user);
      ConnectionSingleton.getInstance().updateTimeStamp(user.getUserID());
   }
   if (user != null)
   {
      cfss.addedUser(user);

   }
      }
      else
      {
   // user already in list, just put it on top!
   list.remove(name);
   put(name, pass);
      }
      //else
      //throw new RacofiDataException("No such user "+name);//do something bad
   }

   /**
    *  Description of the Method
    *
    *@param  name  Description of the Parameter
    *@return       Description of the Return Value
    */
   public User get(String name)
   {
      return (User) instance.list.get(name);
   }

   /**
    *  Gets the fromDatabase attribute of the UserSingleton object
    *
    *@param  name  Description of the Parameter
    *@return       The fromDatabase value
    */
   public User getFromDatabase(String name)
   {
      User user = ConnectionSingleton.getInstance().getUser(name);
      ConnectionSingleton.getInstance().updateTimeStamp(user.getUserID());
      return user;
   }

   /**
    *  Description of the Method
    *
    *@param  name  Description of the Parameter
    *@return       Description of the Return Value
    */
   public boolean containsKey(String name)
   {
      return instance.list.containsKey(name);
   }

   /**
    *  Description of the Method
    *
    *@param  name  Description of the Parameter
    *@return       Description of the Return Value
    */
   public int lastItem(String name)
   {
      try
      {
   return ( (User) list.get(name)).lastItem();
      }
      catch (NullPointerException e)
      {
   System.err.println(e.getMessage());
      }
      return -1;
   }

   /**
    *  Gets the size attribute of the UserSingleton object
    *
    *@return    The size value
    */
   public int getSize()
   {
      return list.size(); //count;
   }

   /**
    *  Gets the list attribute of the UserSingleton object
    *
    *@return    The list value
    */
   public LinkedHash getList()
   {
      return list;
   }

   /**
    *  Sets the list attribute of the UserSingleton object
    *
    *@param  map  The new list value
    */
   public void setList(LinkedHash map)
   {
      list = map;
      CFSSingleton.destroy();
      //update connectionsingeton to recognise this? // I don't see why!
   }

   /**
    *  Gets the set attribute of the UserSingleton object
    *
    *@return    The set value
    */
   public Set getSet()
   {
      Set aSet = list.entrySet();
      return aSet;
   }

   /**
    * check for users in memory
    *
    *@param  name  Description of the Parameter
    *@param  pass  Description of the Parameter
    *@return       Description of the Return Value
    */
   public boolean checkPass(String name, String pass)
   {
      if (list.containsKey(name))
      {
   if ( ( (User) list.get(name)).getPass().equals(pass))
   {
      return true;
   }
      }
      // interesting... so, if the user is not loaded
      // in memory, then you refuse to go search in the
      // database???? BAD!
      /*if ( ConnectionSingleton.getInstance().checkForUser( name ) ) {
       // don't load in memory just yet, wait to see if
       // proper password!
       User user = ConnectionSingleton.getInstance().getUser( name );
       if ( ( (User) list.get( name ) ).getPass().equals( pass ) )
  return true;
   }*/
      //Should only check for users in memory

      return false;
   }

   /**
    *  Description of the Method
    *
    *@param  name      Description of the Parameter
    *@param  dbRemove  Description of the Parameter
    */
   public void removeUser(String name, boolean dbRemove)
   {
      if (dbRemove)
      {
   User user = (User) list.get(name);
   ConnectionSingleton.getInstance().removeUser(user);
      }
      list.remove(name);
   }

   /**
    *  Gets the instance attribute of the UserSingleton class
    *
    *@return    The instance value
    */
   public static synchronized UserSingleton getInstance()
   {
      if (instance == null)
      {
   instance = new UserSingleton();
      }
      return instance;
   }


   public static synchronized void destroy()
   {
       instance = null;
       CFSSingleton.destroy();
       ConnectionSingleton.destroy();
       ItemSingleton.destroy();
      mDestroyThread.interrupt();
      mDestroyThread = null;
   }
   
   public static synchronized void reset()
   {
       instance = null;
       CFSSingleton.destroy();
       ConnectionSingleton.destroy();
       ItemSingleton.destroy();
   }
   
   public void run() {
     Thread me = Thread.currentThread();
     while(me == mDestroyThread) {
       try {
         mDestroyThread.sleep(mDelay);
       } catch(Exception e) {}
       reset();
     }
   }
}
