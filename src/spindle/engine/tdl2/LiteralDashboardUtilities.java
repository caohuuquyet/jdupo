/**
 * SPINdle (version 2.2.4)
 * Copyright (C) 2009-2014 NICTA Ltd.
 *
 * This file is part of SPINdle project.
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
package spindle.engine.tdl2;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;

import com.app.utils.TextUtilities;

import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.ProvabilityLevel;
import spindle.core.dom.Temporal;
import spindle.sys.IOConstant;

public class LiteralDashboardUtilities implements IOConstant{



	public static String generateObjectsString(Collection<? extends Object> objects, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : objects) {
			sb.append(delimiter).append(obj.toString());
		}
		return sb.toString();
	}

	public static String generateAbstractLiteralInfo(AbstractLiteral abstractLiteral, String provability) {
		if (null == abstractLiteral) return "";
		String ind = TextUtilities.repeatStringPattern(" ", provability.length() + 2);
		StringBuilder sb = new StringBuilder();
		sb.append("\n").append(INDENTATOR).append(provability).append(": provable intervals:")
				.append(generateObjectsString(abstractLiteral.getProvableTemporalSegments(), " "));
		sb.append("\n").append(INDENTATOR).append(ind).append("temporals to prove:")
				.append(generateObjectsString(abstractLiteral.getAllTemporalsToProve(), " "));
		return sb.toString();
	}

	public static String generateAbstractLiteralsString(Map<ProvabilityLevel, Map<Literal, AbstractLiteral>> entries, String label,
			Set<Literal> allLiteralsInTheory) {
		StringBuilder sb = new StringBuilder();

		if (null != label && !"".equals(label.trim())) sb.append(label.trim());

		Map<Literal, AbstractLiteral> definiteLiteralSet = entries.get(ProvabilityLevel.DEFINITE);
		Map<Literal, AbstractLiteral> defeasibleLiteralSet = entries.get(ProvabilityLevel.DEFEASIBLE);

		for (Literal literal : allLiteralsInTheory) {
			AbstractLiteral definiteLiteral = null == definiteLiteralSet ? null : definiteLiteralSet.get(literal);
			AbstractLiteral defeasibleLiteral = null == defeasibleLiteralSet ? null : defeasibleLiteralSet.get(literal);
			if (null == definiteLiteral && null == defeasibleLiteral) continue;
			sb.append("\n").append(literal.toString()).append(":");
			sb.append(generateAbstractLiteralInfo(definiteLiteral, "definite"));
			sb.append(generateAbstractLiteralInfo(defeasibleLiteral, "defeasible"));
		}

		return sb.toString();

	}

	private static String generateConclusionInfo(NavigableMap<Temporal, ConclusionType> entries, String label) {
		if (null == entries) return "";
		StringBuilder sb = new StringBuilder();
		sb.append("\n").append(INDENTATOR).append(label).append(":");
		for (Entry<Temporal, ConclusionType> entry : entries.entrySet()) {
			sb.append("\n").append(INDENTATOR).append(INDENTATOR);
			sb.append(entry.getKey()).append(": ").append(entry.getValue());
		}
		return sb.toString();
	}

	public static String generateConclusionString(Map<ProvabilityLevel, Map<Literal, NavigableMap<Temporal, ConclusionType>>> entries,
			String label, Set<Literal> allLiteralsInTheory) {
		StringBuilder sb = new StringBuilder();

		if (null != label && !"".equals(label.trim())) sb.append(label.trim());
		Map<Literal, NavigableMap<Temporal, ConclusionType>> definiteSet = entries.get(ProvabilityLevel.DEFINITE);
		Map<Literal, NavigableMap<Temporal, ConclusionType>> defeasibleSet = entries.get(ProvabilityLevel.DEFEASIBLE);

		for (Literal literal : allLiteralsInTheory) {
			NavigableMap<Temporal, ConclusionType> definiteTemporals = null == definiteSet ? null : definiteSet.get(literal);
			NavigableMap<Temporal, ConclusionType> defeasibleTemporals = null == defeasibleSet ? null : defeasibleSet.get(literal);

			if (null == definiteTemporals && null == defeasibleTemporals) continue;
			sb.append("\n").append(literal.toString()).append(":");
			sb.append(generateConclusionInfo(definiteTemporals, "definite"));
			sb.append(generateConclusionInfo(defeasibleTemporals, "defeasible"));
		}

		return sb.toString();
	}

	// public static String generateDashboardString(
	// Map<ProvabilityLevel, Map<Literal, NavigableSet<Temporal>>> headLiterals, //
	// Map<ProvabilityLevel, Map<Literal, NavigableSet<Temporal>>> provableIntervals,
	// Map<ProvabilityLevel, Map<Literal, NavigableSet<Temporal>>> unprovedHeadLiterals,
	// Map<ProvabilityLevel, Map<Literal, NavigableSet<Temporal>>> unprovedBodyLiterals,
	// Map<ProvabilityLevel, Set<Literal>> initialProvableLiterals, //
	// Map<ProvabilityLevel, Set<Literal>> initialUnprovableLiterals, //
	// Map<ProvabilityLevel, Map<Literal, NavigableMap<Temporal, ConclusionType>>> conclusionsDerived,
	// Map<ProvabilityLevel, Map<Literal, NavigableMap<Temporal, ConclusionType>>> conclusionsPending,
	// Map<ProvabilityLevel, Map<Literal, NavigableMap<Temporal, ConclusionType>>> consolidatedConclusions) {
	// try {
	// StringBuilder sb = new StringBuilder();
	//
	// generateLiteralSetString(headLiterals, LABEL_ALL_HEAD_LITERALS, sb);
	//
	// generateLiteralSetString(provableIntervals, LABEL_PROVABLE_INTERVALS, sb);
	// generateLiteralSetString(unprovedHeadLiterals, LABEL_UNPROVED_HEAD_LITERALS, sb);
	// generateLiteralSetString(unprovedBodyLiterals, LABEL_UNPROVED_BODY_LITERALS, sb);
	//
	// generateLiteralSetString(initialProvableLiterals, LABEL_INITIAL_PROVABLE_LITERALS, sb);
	// generateLiteralSetString(initialUnprovableLiterals, LABEL_INITIAL_UNPROVABLE_LITERALS, sb);
	//
	// generateConclusionSetString(conclusionsDerived, LABEL_CONCLUSIONS_DERIVED, sb);
	// generateConclusionSetString(conclusionsPending, LABEL_CONCLUSIONS_PENDING, sb);
	// generateConclusionSetString(consolidatedConclusions, LABEL_CONCLUSIONS_CONSOLIDATED, sb);
	//
	// return sb.toString();
	// } catch (LiteralDashboardUtilitiesException e) {
	// return TextUtilities.getExceptionMessage(e);
	// }
	// }
	//
	// @SuppressWarnings("unchecked")
	// private static void generateLiteralSetString(Map<ProvabilityLevel, ? extends Object> literalSet, String label,
	// StringBuilder sb)
	// throws LiteralDashboardUtilitiesException {
	// if (literalSet.size() == 0) return;
	//
	// Map<Literal, String> strs = new TreeMap<Literal, String>();
	// String ss = "";
	// for (Entry<ProvabilityLevel, ? extends Object> provabilityEntry : literalSet.entrySet()) {
	// ProvabilityLevel provability = provabilityEntry.getKey();
	//
	// Object entryValue = provabilityEntry.getValue();
	// if (entryValue instanceof Collection) {
	// String s = generateItemString((Collection<? extends Object>) entryValue, provability.toString(), "\n" +
	// INDENTATOR);
	// if (!"".equals(s)) ss += ("\n" + s);
	// } else if (entryValue instanceof Map) {
	// Map<Literal, NavigableSet<Temporal>> literalsSet = (Map<Literal, NavigableSet<Temporal>>) entryValue;
	// if (literalsSet.size() == 0) continue;
	// for (Entry<Literal, NavigableSet<Temporal>> literalEntry : literalsSet.entrySet()) {
	// Literal literal = literalEntry.getKey();
	// String temporalStr = generateItemString(literalEntry.getValue(), "", " ");
	//
	// String s = strs.get(literal);
	// if (null == s) s = "";
	// s += ("\n" + INDENTATOR + provability + ":" + temporalStr);
	// strs.put(literal, s);
	// }
	// } else {
	// throw new LiteralDashboardUtilitiesException("Undefined map value type:" +
	// entryValue.getClass().getCanonicalName());
	// }
	// }
	// addString(label, strs, sb);
	// if (!"".equals(ss)) sb.append("\n").append(label).append(ss);
	// }
	//
	// private static void generateConclusionSetString(
	// Map<ProvabilityLevel, Map<Literal, NavigableMap<Temporal, ConclusionType>>> conclusionsSet, //
	// String label, StringBuilder sb) {
	// if (conclusionsSet.size() == 0) return;
	//
	// Map<Literal, String> strs = new TreeMap<Literal, String>();
	// for (Entry<ProvabilityLevel, Map<Literal, NavigableMap<Temporal, ConclusionType>>> provabilityEntry :
	// conclusionsSet.entrySet()) {
	// for (Entry<Literal, NavigableMap<Temporal, ConclusionType>> literalEntry :
	// provabilityEntry.getValue().entrySet()) {
	// Literal literal = literalEntry.getKey();
	// for (Entry<Temporal, ConclusionType> temporalEntry : literalEntry.getValue().entrySet()) {
	// String s = strs.get(literal);
	// if (null == s) s = "";
	// s += ("\n" + INDENTATOR + temporalEntry.getKey() + ": " + temporalEntry.getValue());
	// strs.put(literal, s);
	// }
	// }
	// }
	// addString(label, strs, sb);
	// }
	//
	// private static String generateItemString(Collection<? extends Object> objs, String label, String delimiter) {
	// if (objs.size() == 0) return "";
	// StringBuilder sb = new StringBuilder();
	// sb.append(label);
	// for (Object o : objs) {
	// sb.append(delimiter).append(o.toString());
	// }
	// return sb.toString();
	// }
	//
	// private static void addString(String label, Map<Literal, String> strs, StringBuilder sb) {
	// if (strs.size() == 0) return;
	// if (sb.length() > 0) sb.append("\n");
	// sb.append(label);
	// for (Entry<Literal, String> entry : strs.entrySet()) {
	// sb.append("\n").append(entry.getKey()).append(entry.getValue());
	// }
	// }
}