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

/**
 *  These runtime exceptions are thrown when a bug is found.
 *
 *
 *  $Id: CollaborativeFilteringException.java,v 1.2 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $
 *  $Author: lemired $
 *  $Revision: 1.2 $
 *  $Log: CollaborativeFilteringException.java,v $
 *  Revision 1.2  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.1  2003/10/27 17:21:15  lemired
 *  Putting some order
 *
 *  Revision 1.4  2003/08/22 13:38:23  howsen
 *  *** empty log message ***
 *
 *  Revision 1.3  2003/08/07 13:16:05  lemired
 *  More javadoc improvments.
 *
 *  Revision 1.2  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author     Daniel Lemire
 *@since   August 6, 2003
 */
public class CollaborativeFilteringException
   extends RuntimeException
{

   String message;

   /**
    *  Constructor for the CollaborativeFilteringException object
    */
   public CollaborativeFilteringException()
   {
      super();
      message = "Exception in Collaborative Filtering (COFI) library";
   }

   /**
    *  Constructor for the CollaborativeFilteringException object
    *
    *@param  err  a custom error message
    */
   public CollaborativeFilteringException(String err)
   {
      super(err);
      message = err;
   }

   /**
    *  Gets the error attribute of the CollaborativeFilteringException object
    *
    *@return    The error value
    */
   public String getError()
   {
      return message;
   }
}
