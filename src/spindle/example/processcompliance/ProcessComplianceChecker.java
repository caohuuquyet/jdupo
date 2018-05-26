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
package spindle.example.processcompliance;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import spindle.Reasoner;
import spindle.core.ReasonerException;
import spindle.core.dom.Conclusion;
import spindle.core.dom.ConclusionType;
import spindle.core.dom.DomUtilities;
import spindle.core.dom.Literal;
import spindle.core.dom.Theory;
import spindle.core.dom.TheoryException;
import spindle.io.IOManager;
import spindle.io.ParserException;
import spindle.io.parser.DflTheoryParser2;
import spindle.sys.AppLogger;

public class ProcessComplianceChecker {
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

	// SPINdle defeasible reasoner
	private Reasoner reasoner = null;

	// application knowledge base
	private Theory knowledgeBase = null;

	public ProcessComplianceChecker(File knowledgeBaseFile, AppLogger logger) throws ParserException, IOException {
		// create a SPINdle defeasible reasoner
		reasoner = new Reasoner();

		// load the application knowledge base (a defeasible theory)
		// from a file using the I/O Manager
		knowledgeBase = IOManager.getTheory(knowledgeBaseFile, logger);
	}

	public Map<Literal, Map<ConclusionType, Conclusion>> checkCompliance(Theory contextualInfo)
			throws TheoryException, ReasonerException {
		// duplicate a copy of the knowledge base
		Theory workingTheory = knowledgeBase.clone();

		// combine the contextual information with the knowledge base
		workingTheory.add("contextInfo", contextualInfo);

		// load theory to the reasoner
		reasoner.loadTheory(workingTheory);

		// generate the conclusions with theory transformations
		return reasoner.generateConclusionsWithTransformations();
	}

	public Map<ConclusionType, Conclusion> getLiteralConclusion(String literalName, boolean isNegation,
			Map<Literal, Map<ConclusionType, Conclusion>> conclusions) {
		// generate the literal on the fly
		Literal literal = DomUtilities.getLiteral(literalName, isNegation);

		// retrieve the result from the conclusions set
		return conclusions.get(literal);
	}

	/**
	 * @param args
	 */

	private static void myTask(String namef) {
		System.out.println("Running");

		try {
			// defeasible theory saved in file
			File theoryFile = null;
			long startTime, endTime;
			// theory used to store the original theory
			Theory origTheory = null;

			// Defeasible theory that is generated on the fly using the DFL or
			// XML
			// syntax.
			// Then the new defeasible logic theory can be retrieved using the
			// static method in the associated parsers:
			// spindle.io.parser.DflTheoryParser, and
			// spindle.io.parser.XmlTheoryParser
			String[] newRulesStr = new String[] { "" };

			Theory newTheoryRules = null;
			try {
				newTheoryRules = generateTheory(newRulesStr, null);
			} catch (ParserException e1) {
				e1.printStackTrace();
			}

			Reasoner reasoner = new Reasoner();

			theoryFile = new File(namef);

			origTheory = IOManager.getTheory(theoryFile, null);
			// or load the theory directly to the reasoner
			// and retrieve the theory using the Reasoner.getTheory() method
			// reasoner.loadTheory(theoryFile);
			// origTheory = reasoner.getTheory();

			// use the Theory.clone() method to duplicate a copy of the
			// theory.
			// Doing this can prevent any modification of the original
			// theory
			// by the reasoner or other process.
			Theory workingTheory = origTheory.clone();

			// Theories can be combined using the Theory.add(Theory theory)
			// method
			// to form a new theory
			// Here we combined the new rules with the working theory (but
			// not
			// the origTheory)
			// if (null != newTheoryRules) workingTheory.add("newRule",
			// newTheoryRules);

			/*
			 * System.out.println("=== original theory ===");
			 * System.out.println(origTheory); System.out.println(
			 * "=== new rules ==="); System.out.println(newTheoryRules);
			 * System.out.println("=== woring theory ===");
			 * System.out.println(workingTheory);
			 */

			// load the working (combined) theory into the reasoner
			reasoner.loadTheory(workingTheory);

			// transform the theory to regular form and
			// remove all defeater(s) and superiority(ies)

			reasoner.transformTheoryToRegularForm();
			reasoner.removeDefeater();
			reasoner.removeSuperiority();

			// compute and retrieve the conclusions
			startTime = System.currentTimeMillis();

			Map<Literal, Map<ConclusionType, Conclusion>> conclusions = reasoner.getConclusions();
			System.out.println("\nConclusions\n===========");
			endTime = System.currentTimeMillis();

			System.out.print("performance of " + namef + ":" + (endTime - startTime));
		} catch (ReasonerException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void main(String[] args) {

		int i = 0;

		String namef = "./samples/pl";
		while (i < 400) {
			namef = namef + i + ".dfl";

			final String nn = namef;
			final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
			executorService.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					myTask(nn);
				}
			}, 0, 5, TimeUnit.SECONDS);

			/*
			 * for (Entry<Literal, Map<ConclusionType, Conclusion>> entry :
			 * conclusions .entrySet()) { System.out.println(entry.getKey() +
			 * ":" + entry.getValue().keySet()); }
			 */

			// or get the conclusions as a list
			/*
			 * List<Conclusion> conclusionList = reasoner
			 * .getConclusionsAsList(); System.out .println(
			 * "\nConclusions as list\n==================="); for (Conclusion
			 * conclusion : conclusionList) { System.out.println(conclusion); }
			 */

			i = i + 10;
			namef = "./samples/pl";

		}

	}
}
