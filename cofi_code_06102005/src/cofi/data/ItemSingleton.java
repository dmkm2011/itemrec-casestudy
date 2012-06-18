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

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 *   A singleton class which manages the Item objects.</p> <p>
 *
 *
 *@author     Nancy Howse
 *@created    August 3, 2003
 *@since      July 12, 2003
 *@version    1.0
 */

public class ItemSingleton {

  private static ItemSingleton instance = null;
  private TIntObjectHashMap itemList = null;
  private TIntIntHashMap mapping = null;
  /**
   *  ItemSingleton constructor.
   */
  private ItemSingleton() {
    itemList = ConnectionSingleton.getInstance().loadItems();
    mapping = new TIntIntHashMap();
    TIntObjectIterator it = itemList.iterator();
    while(it.hasNext())
    {
      it.advance();
      int key = it.key();
      int itemID = ((Item)it.value()).getItemID();
      mapping.put(itemID, key);
    }
  }

  /**
   *  Removes specified item from the item list.
   *
       *@param  lastListItem   Duplicated last item left over in item list after list
   *      has been shifted down.
   *@param  removedItemID  itemID of removed item.
   */
  public void remove(int lastListItem, int removedItemID) {
    ConnectionSingleton.getInstance().removeItem(removedItemID);
    itemList.remove(lastListItem);
    itemList = ConnectionSingleton.getInstance().loadItems();

    //rebuild mapping from itemID to array index
    mapping = new TIntIntHashMap();
    TIntObjectIterator it = itemList.iterator();
    while(it.hasNext())
    {
      it.advance();
      int key = it.key();
      int itemID = ((Item)it.value()).getItemID();
      mapping.put(itemID, key);
    }
  }

  public void put(String artist, String album, String url, String price,
      String label, String trackNum, String releaseDate) {
    int count = itemList.size();
    ConnectionSingleton.getInstance().addItem(artist, album, url, price, label,
                trackNum, releaseDate);
    Item item = new Item(artist, album, url, label, releaseDate,
       Integer.parseInt(trackNum), Float.parseFloat(price),
       count);
    instance.itemList.put(count, item);
    mapping.put(item.getItemID(), count);
  }

  /**
   *  Retrieves the Item corresponding to the specified key.
   *
   *@param  num  Description of the Parameter
   *@return      the requested Item.
   */
  public Item get(int num) {
    if (!itemList.containsKey(num)) {
      throw new RacofiDataException("No such item ... num =  " + num);
    }
    return (Item) itemList.get(num);
  }

  /**
   *  Accessor method for itemList.
   *
   *@return    the TIntObjectHashMap itemList.
   */
  public TIntObjectHashMap getList() {
    return itemList;
  }

  /**
   *  Accessor method for count.
   *
   *@return    the integer number of items in the HashMap.
   */
  public int getCount() {
    return itemList.size();
  }

  /**
   *  Synchronized static method which returns the existing ItemSingleton
   *  instance if it is not null. If it is null, the ItemSingleton constructor is
   *  called.
   *
   *@return    the synchronized, static ItemSingleton object.
   */
  public static synchronized ItemSingleton getInstance() {
    if (instance == null) {
      instance = new ItemSingleton();
    }
    return instance;
  }

  /**
   *  This returns a hashmap which gives us a itemid -> key map
   *  This is useful to map items to a stacked array index.
   *  It is also much faster than the previous findID implementation!
   *
   *@return    Description of the Return Value
   */

  public TIntIntHashMap getMapping()
  {
    return mapping;
  }

  /**
   *  This returns a hashmap which gives us a itemid -> key map
   *  This is useful to map items to a stacked array index.
   *  It is also much faster than the previous findID implementation!
   *
   *@return    Description of the Return Value
   */

  public TIntIntHashMap computeItemIDToArrayId()
  {
    return getMapping();
  }

  /*
   *  A faster implementation than findID.
   */

  public static void destroy() {
    instance = null;
  }

}
