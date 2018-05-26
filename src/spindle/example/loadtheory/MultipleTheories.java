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

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import spindle.Reasoner;
import spindle.core.dom.Conclusion;
import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.Theory;
import spindle.io.IOManager;
import spindle.io.ParserException;
import spindle.io.parser.DflTheoryParser2;
import spindle.sys.AppLogger;
import spindle.sys.Conf;

/**
 * <pre>
 * This class demonstrated how to combine multiple (two or more) defeasible 
 * logic theories into one single theory and compute its consequence.
 * 
 * It is assumed that a knowledge base (described using DFL format) is already
 * stored in a file. User can use the reasoner to load the theory from the file, 
 * retrieve it to the application and store the theory as a knowledge base
 * (i.e. the origTheory in the code)
 * 
 * Then, new rules (which can be generated on the fly by other applications) can 
 * be added to the theory and consequence can be computed using the following 
 * steps: 
 * (1) duplicate the original theory into a new working theory, 
 * (2) add the new rules to the working theory, 
 * (3) compute the consequence of the working theory, and 
 * (4) retrieve the conclusions from the reasoner.
 * 
 * Step (1) is used to prevent modification of theory to the original theory. 
 * It is necessary if the original theory is going to be served as a knowledge 
 * base for all theory computation in the application.
 * </pre>
 * 
 * @author Brian Lam
 */
public class MultipleTheories {
	// private static class DummyLogger implements AppLogger {
	// private String FILENAME = "dummy_" + Utilities.getRandomString(20);
	// private Logger logger = null;
	//
	// @Override
	// public void onLogMessage(Level logLevel, String message, Object... objects) {
	// onLogMessage(logLevel, 0, message, objects);
	// }
	//
	// @Override
	// public void onLogMessage(Level logLevel, int level, String message, Object... objects) {
	// if (null == logger) logger = Conf.getLogger(FILENAME);
	// if (!logger.isLoggable(logLevel)) return;
	// StringBuilder sb = new StringBuilder();
	// for (int i = 0; i < level; i++) {
	// sb.append(AppConst.IDENTATOR);
	// }
	// if (null != message && !"".equals(message.trim())) sb.append(message.trim());
	// logger.log(Conf.getLogLevel(), sb.toString() + message, objects);
	// }
	// }

	protected static Theory generateTheory(String[] theory, AppLogger logger) throws ParserException {
		StringBuilder sb = new StringBuilder();
		for (String rule : theory) {
			sb.append(rule).append("\n");
		}
		return generateTheory(sb.toString(), logger);
	}

	protected static Theory generateTheory(String theory, AppLogger logger) throws ParserException {
		return DflTheoryParser2.getTheory(theory, logger);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// defeasible theory saved in file
		File theoryFile = new File("./samples/sdlTestTheory.dfl");
		// theory used to store the original theory
		Theory origTheory = null;

		// Defeasible theory that is generated on the fly using the DFL or XML syntax.
		// Then the new defeasible logic theory can be retrieved using the
		// static method in the associated parsers:
		// spindle.io.parser.DflTheoryParser, and
		// spindle.io.parser.XmlTheoryParser
		String[] newRulesStr = new String[] { "c->z", "z=>NewRuleSet1_Good" };

		Theory newTheoryRules = null;
		try {
			newTheoryRules = generateTheory(newRulesStr, Conf.getLogger(theoryFile.getName()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Reasoner reasoner = new Reasoner();
		try {
			// load defeasible theory from a file using spindle.io.IOManager
			origTheory = IOManager.getTheory(theoryFile, null);
			// or load the theory directly to the reasoner
			// and retrieve the theory using the Reasoner.getTheory() method
			// reasoner.loadTheory(theoryFile);
			// origTheory = reasoner.getTheory();

			// use the Theory.clone() method to duplicate a copy of the theory.
			// Doing this can prevent any modification of the original theory
			// by the reasoner or other process.
			Theory workingTheory = origTheory.clone();

			// Theories can be combined using the Theory.add(Theory theory) method
			// to form a new theory
			// Here we combined the new rules with the working theory (but not the origTheory)
			if (null != newTheoryRules) workingTheory.add("newRule", newTheoryRules);

			System.out.println("=== original theory ===");
			System.out.println(origTheory);
			System.out.println("=== new rules ===");
			System.out.println(newTheoryRules);
			System.out.println("=== woring theory ===");
			System.out.println(workingTheory);

			// load the working (combined) theory into the reasoner
			reasoner.loadTheory(workingTheory);

			// transform the theory to regular form and
			// remove all defeater(s) and superiority(ies)
			reasoner.transformTheoryToRegularForm();
			reasoner.removeDefeater();
			reasoner.removeSuperiority();
			
			// compute and retrieve the conclusions
			// compute and retrieve the conclusions
			Map<Literal, Map<ConclusionType, Conclusion>> conclusions = reasoner.getConclusions();
			
			// or, instead of executing the transformations and generate the conclusions one-by-one,
			// user can simply execute generateConclusionsWithTransformations() method
			// in the reasoner class to compute the conclusions, as shown below:
			// reasoner.generateConclusionsWithTransformations();
			
			System.out.println("\nConclusions\n===========");
			for (Entry<Literal, Map<ConclusionType, Conclusion>> entry : conclusions.entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue().keySet());
			}
			
			// or get the conclusions as a list
			List<Conclusion> conclusionList = reasoner.getConclusionsAsList();
			System.out.println("\nConclusions as list\n===================");
			for (Conclusion conclusion : conclusionList) {
				System.out.println(conclusion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
