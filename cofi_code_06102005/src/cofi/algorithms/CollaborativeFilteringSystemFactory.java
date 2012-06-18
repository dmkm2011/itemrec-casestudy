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

import cofi.data.*;
import gnu.trove.*;
import cofi.algorithms.basic.*;
import cofi.algorithms.stin.*;
import cofi.algorithms.memorybased.*;
import cofi.algorithms.linear.*;
import cofi.algorithms.jester.*;
import cofi.algorithms.util.*;
import cofi.algorithms.composition.*;
import java.util.*;

/**
* not quite a factory pattern, but close
*/
public class CollaborativeFilteringSystemFactory {
 
	// this should not be done by hand, should call the
	// class by its name and then use a Class.forName call
  public static CollaborativeFilteringSystem getCFS(EvaluationSet origuset, String name) {
    if(name.equalsIgnoreCase("TIOptimalConstantWeight"))
      return new TIOptimalConstantWeight( origuset );
    if(name.equalsIgnoreCase("TIOptimalWeight"))
      return new TIOptimalWeight( origuset );
    if(name.equalsIgnoreCase("TIOptimalWeight"))
      return new OptimalConstantWeight( origuset);  
    if(name.equalsIgnoreCase("Pearson"))
      return new Pearson( origuset );
    if(name.equalsIgnoreCase("AdjustScale__Pearson"))
      return new AdjustScale(new Pearson( origuset ));
    if(name.equalsIgnoreCase("STIPearson"))
      return new STIPearson( origuset, 2.0f );
    if(name.equalsIgnoreCase("MeanSTIPearson"))
      return new MeanSTIPearson( origuset, 2.0f );
    if(name.equalsIgnoreCase("ConstantBias4"))
      return new ConstantBias( origuset,4);
    if(name.equalsIgnoreCase("AdjustScale__ConstantBias4"))
      return new AdjustScale(new ConstantBias( origuset,4));
    if(name.equalsIgnoreCase("BiConstantBias"))
      return new BiConstantBias( origuset);
    if(name.equalsIgnoreCase("AdjustScale__BiConstantBias"))
      return new AdjustScale(new BiConstantBias( origuset));
    if(name.equalsIgnoreCase("ConstantBias0"))
      return new ConstantBias( origuset,0);
    if(name.equalsIgnoreCase("AdjustScale__ConstantBias0"))
      return new AdjustScale(new ConstantBias( origuset,0));
    if(name.equalsIgnoreCase("NonPersonalized"))
      return new NonPersonalized( origuset );
    if(name.equalsIgnoreCase("AdjustScale__NonPersonalized"))
      return new AdjustScale(new NonPersonalized( origuset ));
    if(name.equalsIgnoreCase("ReverseNonPersonalized"))
      return new ReverseNonPersonalized( origuset );    
    if(name.equalsIgnoreCase("Average"))
      return new Average(  origuset);
    if(name.equalsIgnoreCase("PerItemAverage"))
      return new PerItemAverage( origuset );
    if(name.equalsIgnoreCase("STINonPersonalized"))
      return new STINonPersonalized( origuset, 2.0f );
    if(name.equalsIgnoreCase("STINonPersonalized2steps"))
      return new STINonPersonalized2steps( origuset, 2.0f );
    try { 
	    Class myclass = Class.forName(name);
	    Class[] paramclasses = new Class[1];
	    paramclasses[0] = origuset.getClass();
	    Object[] param = new Object[1];
	    param[0] = origuset;
	    return (CollaborativeFilteringSystem) myclass.getConstructor(paramclasses).newInstance(param);
    } catch(Exception cnfe) {cnfe.printStackTrace();}
    throw new CollaborativeFilteringException(" Not such scheme: "+name);
  }
  
  
  public static Vector getCFS(EvaluationSet set, Vector names) {
    Vector answer = new Vector();
    for (Enumeration e = names.elements() ; e.hasMoreElements() ;) {
      String name = (String) e.nextElement();
      answer.add(getCFS(set,name));
    }
    return answer;
  }  

  public static Vector getCFS(EvaluationSet set, String[] names) {
    Vector answer = new Vector();
    for(int k = 0; k < names.length; ++k) {
      answer.add(getCFS(set,names[k]));
    }
    return answer;
  }  

  
}
