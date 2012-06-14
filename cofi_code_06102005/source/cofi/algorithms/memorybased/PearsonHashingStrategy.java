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
package cofi.algorithms.memorybased;

import gnu.trove.*;

/**
 *  This hashing strategy insures that collisions are resolved against an equality defined
 * by the equality of hash codes.
 *
 *
 *  $Id: PearsonHashingStrategy.java,v 1.2 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $
 *  $Author: lemired $
 *  $Revision: 1.2 $
 *  $Log: PearsonHashingStrategy.java,v $
 *  Revision 1.2  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.1  2003/10/27 17:21:15  lemired
 *  Putting some order
 *
 *  Revision 1.5  2003/08/22 13:38:23  howsen
 *  *** empty log message ***
 *
 *  Revision 1.4  2003/08/08 03:23:22  lemired
 *  addedUser/removedUser was broken in most implementation. I fixed that now.
 *
 *  Revision 1.3  2003/08/07 13:16:05  lemired
 *  More javadoc improvments.
 *
 *  Revision 1.2  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author     Marcel Ball and Daniel Lemire
 *@since    August 6, 2003
 */
public class PearsonHashingStrategy
   implements TObjectHashingStrategy
{
   /**
    *  Constructor for the PearsonHashingStrategy object
    */
   public PearsonHashingStrategy()
   {}

   /**
    *  Compute the hash code of an object
    *
    *@param  o  the object
    *@return    the hash code
    */
   public int computeHashCode(Object o)
   {
      return o.hashCode();
   }

   /**
    *  test whether two objects are equal based on their hash codes
    *
    *@param  o   first object
    *@param  o2  second object
    *@return     whether they are equal
    */
   public boolean equals(Object o, Object o2)
   {
      return (o.hashCode() == o2.hashCode());
   }

}
