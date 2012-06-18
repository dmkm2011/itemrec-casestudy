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
package cofi.algorithms.util;

/**
 *  Some algorithms only converge some of the time
 * and if they don't converge within some fix
 * number of iterations, this exception is thrown.
 *
 *
 *  $Id: NoConvergenceException.java,v 1.2 2003/11/11 13:25:58 lemired Exp $
 *  $Date: 2003/11/11 13:25:58 $
 *  $Author: lemired $
 *  $Revision: 1.2 $
 *  $Log: NoConvergenceException.java,v $
 *  Revision 1.2  2003/11/11 13:25:58  lemired
 *  Added gpl headers
 *
 *  Revision 1.1  2003/10/27 17:21:15  lemired
 *  Putting some order
 *
 *  Revision 1.3  2003/08/22 13:38:23  howsen
 *  *** empty log message ***
 *
 *  Revision 1.2  2003/08/07 00:37:42  lemired
 *  Mostly, I updated the javadoc.
 *
 *
 *@author     Daniel Lemire
 *@since    August 6, 2003
 */
public class NoConvergenceException
   extends Exception
{
   /**
    *  Constructor for the NoConvergenceException object
    */
   public NoConvergenceException()
   {
      super();
   }
}
