/**
 * jDUPO 1.0
 */
package spindle.core.dom;

import spindle.io.ParserException;
import spindle.io.outputter.DflTheoryConst;
import spindle.sys.message.ErrorMessage;

/**
 * Enumerate on the types of rule.
 * 
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory
 * @version Last modified 2011.07.27
 * @since version 1.0.0
 */
public enum RuleType {
	LITERAL_VARIABLE_SET("Literal variable/boolean function", "set", ProvabilityLevel.NONE), //
	FACT("Fact", ">>", ProvabilityLevel.NONE), //
	STRICT("Strict rule", "->", ProvabilityLevel.DEFINITE), //
	DEFEASIBLE("Defeasible rule", "=>", ProvabilityLevel.DEFEASIBLE), //
	DEFEATER("Defeater", "~>", ProvabilityLevel.NONE), //
	SUPERIORITY("Superiority relation", ">", ProvabilityLevel.NONE), //
	INFERIORITY("Inferiority relation", "<", ProvabilityLevel.NONE), //
	MODE_CONVERSION("Mode conversion", "==", ProvabilityLevel.NONE), //
	MODE_CONFLICT("Mode conflict", "!=", ProvabilityLevel.NONE), //
	MODE_EXCLUSION("Mode exclusion", "<>", ProvabilityLevel.NONE),//
	REQUEST("Consumer's Request", "=>", ProvabilityLevel.DEFEASIBLE);

	private final String label;
	private final String symbol;
	private final ProvabilityLevel provabilityLevel;

	RuleType(String _label, String _symbol, ProvabilityLevel _provabilityLevel) {
		label = _label.trim();
		symbol = _symbol.trim();
		provabilityLevel = _provabilityLevel;
	}

	public String getLabel() {
		return label;
	}

	public String getSymbol() {
		return symbol;
	}

	public ProvabilityLevel getProvabilityLevel() {
		return provabilityLevel;
	}

	/**
	 * @param str rule string
	 * @return rule type associated with the rule string
	 * @throws ParserException If no rule type associated with the string can found
	 */
	public static RuleType getRuleType(String str) throws ParserException {
		for (RuleType ruleType : RuleType.values()) {
			switch (ruleType) {
			case LITERAL_VARIABLE_SET:
				if (str.startsWith(ruleType.symbol)) return ruleType;
				break;
			default:
				// skip all characters before the rule label
				int loc = str.indexOf(DflTheoryConst.RULE_LABEL_SEPARATOR);
				if (loc < 0) loc = 0;
				// return ruleType if the rule symbol appears in the rule body
				if (str.indexOf(ruleType.symbol, loc) >= 0) return ruleType;
			}
		}
		throw new ParserException(ErrorMessage.RULE_UNRECOGNIZED_RULE_TYPE, new Object[] { str });
	}
}
