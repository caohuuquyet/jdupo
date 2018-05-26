/**
 * SPINdle examples (version 2.2.4)
 * Copyright (C) 2009-2014 NICTA Ltd.
 *
 * This file is part of SPINdle examples project.
 * 
 * SPINdle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SPINdle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SPINdle.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory 
 */
package spindle.example.loadtheory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import spindle.Reasoner;
import spindle.core.ReasonerException;
import spindle.core.dom.Conclusion;
import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;

public class TheoryInStr {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// assume the defeasible theory is generated on the fly using
		// the DFL syntax
		String[] theoryStr = new String[] { "a->b", "b=>c", ">>a" };

		Reasoner reasoner = new Reasoner();

		try {
			// load the theory into the reasoner
			reasoner.loadTheory(theoryStr);
			

			// transform the theory to regular form and
			// remove all defeater(s) and superiority(ies)
			reasoner.transformTheoryToRegularForm();
			reasoner.removeDefeater();
			reasoner.removeSuperiority();
			

			// compute and retrieve the conclusions
			Map<Literal, Map<ConclusionType, Conclusion>> conclusions = reasoner
					.getConclusions();
			System.out.println("\nConclusions\n===========");
			for (Entry<Literal, Map<ConclusionType, Conclusion>> entry : conclusions
					.entrySet()) {
				System.out.println(entry.getKey() + ":"
						+ entry.getValue().keySet());
			}

			// or get the conclusions as a list
			List<Conclusion> conclusionList = reasoner.getConclusionsAsList();
			System.out.println("\nConclusions as list\n===================");
			for (Conclusion conclusion : conclusionList) {
				System.out.println(conclusion);
			}
		} catch (ReasonerException e) {
			e.printStackTrace();
		}
	}

}
