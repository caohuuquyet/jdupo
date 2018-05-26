/**
 * jDUPO 1.1
 */
package com.jdupo;

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
import spindle.tools.explanation.InferenceLogger;

/**
 * steps: 
 * (1) duplicate the original theory into a new working theory, 
 * (2) add the new rules to the working theory, 
 * (3) compute the consequence of the working theory, and 
 * (4) retrieve the conclusions from the reasoner.
 * 
 */
public class jDUPO {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// defeasible theory saved in file
		File theoryFile = new File("./samples/dupo.dfl");
		System.out.println(theoryFile.getAbsolutePath());
		// theory used to store the original theory
		Theory origTheory = null;

		// Defeasible theory that is generated on the fly using the DFL or XML
		// syntax.
		// Then the new defeasible logic theory can be retrieved using the
		// static method in the associated parsers:
		// spindle.io.parser.DflTheoryParser, and
		// spindle.io.parser.XmlTheoryParser
		
		//todo
		//grenerate data
		//policy size: 1 - 100
		//trust enforcement time in seconds
		
		
		String[] newRulesStr = new String[] { "CO,[P]SpatialScope(street),[P]TemporalScope(hourly),[P]AbstractionScope(detail) =>[O] CreateRequest" };

		Theory newTheoryRules = null;
		try {
			newTheoryRules = generateTheory(newRulesStr,
					Conf.getLogger(theoryFile.getName()));
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

			// Theories can be combined using the Theory.add(Theory theory)
			// method
			// to form a new theory
			// Here we combined the new rules with the working theory (but not
			// the origTheory)
			if (null != newTheoryRules)
				workingTheory.add("newRule", newTheoryRules);

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
			Map<Literal, Map<ConclusionType, Conclusion>> conclusions = reasoner
					.getConclusions();

			// or, instead of executing the transformations and generate the
			// conclusions one-by-one,
			// user can simply execute generateConclusionsWithTransformations()
			// method
			// in the reasoner class to compute the conclusions, as shown below:
			// reasoner.generateConclusionsWithTransformations();

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
				System.out.println(conclusion.getLiteral().getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// test

		if (Conf.isLogInferenceProcess()) {
			InferenceLogger inferenceLogger = reasoner.getInferenceLogger();
			if (null != inferenceLogger) {
				System.out.println("=== Inference Logger - start ===\n"
						+ inferenceLogger
						+ "\n=== Inference Logger -  end  ===");
			}
		}
	}
	
	protected static Theory generateTheory(String[] theory, AppLogger logger)
			throws ParserException {
		StringBuilder sb = new StringBuilder();
		for (String rule : theory) {
			sb.append(rule).append("\n");
		}
		return generateTheory(sb.toString(), logger);
	}

	protected static Theory generateTheory(String theory, AppLogger logger)
			throws ParserException {
		return DflTheoryParser2.getTheory(theory, logger);
	}

}
