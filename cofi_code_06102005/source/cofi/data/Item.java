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

import java.io.*;
import java.util.*;

/**
 *  Metadata for an item object.
 *
 *@author     Nancy Howse
 *@since      July 12, 2003
 *@version    1.0
 */

public class Item {
  private String artist, album, url, label;
  private String releaseDate;
  private int numberOfTracks;
  private float priceInCents;
  int itemID;


  /**
   *  Constructor for the Item object
   */
  public Item()
        { }


  /**
   *  Constructor for the Item object
   *
   *@param  anArtist  artist name
   *@param  anAlbum   album name
   *@param  itemURL    URL for item
   *@param  IDnum     Item key in SQL database
   */
  public Item( String anArtist, String anAlbum, String itemURL, String Alabel, String AreleaseDate, int AnumberOfTracks, float ApriceInCents, int IDnum ) {
    artist = anArtist;
    album = anAlbum;
    url = itemURL;
    itemID = IDnum;
    label = Alabel;
    releaseDate = AreleaseDate;
    numberOfTracks = AnumberOfTracks;
    priceInCents = ApriceInCents;
  }
//String label, Date releaseDate, int numberOfTracks, float priceInCents
  public String getLabel() {
      return label;
  }
  public void setLabel(String l ) {
    label = l;
  }
  public String getReleaseDate() {
    return releaseDate;
  }
  public void setReleaseDate(String d) {
    releaseDate = d;
  }
  public int getNumberOfTracks() {
    return numberOfTracks;
  }
  public void setNumberOfTracks(int t) {
    numberOfTracks = t;
  }
  public float getPriceInCents() {
    return priceInCents;
  }
  public void setPriceInCents(int p) {
    priceInCents = p;
  }
  /**
   *  Gets the artist attribute of the Item object
   *
   *@return    The artist value
   */
  public String getArtist() {
    return artist;
  }


  /**
   *  Gets the album attribute of the Item object
   *
   *@return    The album value
   */
  public String getAlbum() {
    return album;
  }


  /**
   *  Gets the URL attribute of the Item object
   *
   *@return    The URL value
   */
  public String getURL() {
    return url;
  }


  /**
   *  Returns the itemID attribute of the Item object
   *
   *@return    int itemID value
   */
  public int getItemID() {
    return itemID;
  }


  /**
   *  Sets the artist attribute of the Item object
   *
   *@param  anArtist  The new artist value
   */
  public void setArtist( String anArtist ) {
    artist = anArtist;
  }


  /**
   *  Sets the album attribute of the Item object
   *
   *@param  anAlbum  The new album value
   */
  public void setAlbum( String anAlbum ) {
    album = anAlbum;
  }


  /**
   *  Sets the URL attribute of the Item object
   *
   *@param  itemURL  The new url value
   */
  public void setURL( String itemURL ) {
    url = itemURL;
  }


  /**
   *  Sets the itemID attribute of the Item object
   *
   *@param  IDnum  The new itemID value
   */
  public void setItemID( int IDnum ) {
    itemID = IDnum;
  }
}

