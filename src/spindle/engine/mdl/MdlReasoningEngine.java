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
package spindle.engine.mdl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import spindle.core.dom.Conclusion;
import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.Mode;
import spindle.core.dom.Rule;
import spindle.core.dom.RuleType;
import spindle.engine.ReasoningEngineException;
import spindle.engine.sdl.SdlReasoningEngine;
import spindle.sys.AppConst;
import spindle.tools.explanation.RuleInferenceStatus;

/**
 * MDL Reasoning Engine.
 * 
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory
 * @since version 1.0.0
 * @version Last modified 2012.08.20
 */
public class MdlReasoningEngine extends SdlReasoningEngine {
	protected Map<String, Set<String>> strongerModeSet = null;

	public MdlReasoningEngine() {
		super();
	}

	@Override
	protected void initialize() throws ReasoningEngineException {
		strongerModeSet = theory.getStrongerModeSet();
		super.initialize();
	}

	protected Set<Literal> getConflictLiteralListWithoutOperatorChange(final Literal literal) {
		Set<Literal> conflictLiteralList = new TreeSet<Literal>();

		conflictLiteralList.add(literal.getComplementClone());

		Mode literalMode = literal.getMode();
		if (!"".equals(literalMode.getName())) {
			Literal conflictLiteral = literal.clone();
			conflictLiteral.setMode(literalMode.getComplementClone());
			conflictLiteralList.add(conflictLiteral);
		}
		return conflictLiteralList;
	}

	// remove ambiguity caused by complementary literal
	protected void removeComplementLiteralAmbiguity(int i) {
		logMessage(Level.FINE, 1, "=== removeComplementLiteralAmbiguity - start ===");
		List<Conclusion> ambiguousConclusionToRemove = new ArrayList<Conclusion>();
		List<Conclusion> recordsToRemove = new ArrayList<Conclusion>();
		for (Entry<Conclusion, Set<String>> entry : ambiguousConclusions[i].entrySet()) {
			Conclusion conclusion = entry.getKey();
			Set<String> ruleLabels = entry.getValue();

			Literal literal = conclusion.getLiteral();
			Set<Literal> conflictLiterals = getConflictLiteralListWithoutOperatorChange(literal);
			switch (conclusion.getConclusionType()) {
			case DEFINITE_PROVABLE:
				logMessage(Level.FINER, 2, "removeComplementLiteralAmbiguity, check literal (definite): ", literal);
				if (!containsUnprovedRuleInTheory(conflictLiterals, RuleType.STRICT)) {
					if (isAmbiguousConclusionExist(conflictLiterals, ConclusionType.DEFINITE_PROVABLE)) {
						ambiguousConclusionToRemove.add(conclusion);
						recordsToRemove.add(new Conclusion(ConclusionType.DEFINITE_PROVABLE, literal));
						if (isLogInferenceProcess)
							getInferenceLogger().updateRuleInferenceStatus(ruleLabels, RuleType.STRICT,
									ConclusionType.DEFINITE_NOT_PROVABLE, literal, RuleInferenceStatus.DEFEATED);
						newLiteralFind_definiteNotProvable(literal, true);
					}
				}
				break;
			case DEFEASIBLY_PROVABLE:
				logMessage(Level.FINER, 2, "removeComplementLiteralAmbiguity, check literal (defeasible): ", literal);
				if (!containsUnprovedRuleInTheory(conflictLiterals, RuleType.DEFEASIBLE)) {
					if (isAmbiguousConclusionExist(conflictLiterals, ConclusionType.DEFEASIBLY_PROVABLE)) {
						ambiguousConclusionToRemove.add(conclusion);
						recordsToRemove.add(new Conclusion(ConclusionType.DEFEASIBLY_PROVABLE, literal));
						if (isLogInferenceProcess)
							getInferenceLogger().updateRuleInferenceStatus(ruleLabels, RuleType.DEFEASIBLE,
									ConclusionType.DEFEASIBLY_NOT_PROVABLE, literal, RuleInferenceStatus.DEFEATED);
						newLiteralFind_defeasiblyNotProvable(literal, true);
					}
				}
				break;
			default:
			}
		}

		for (Conclusion conclusion : ambiguousConclusionToRemove) {
			ambiguousConclusions[i].remove(conclusion);
		}
		for (Conclusion record : recordsToRemove) {
			removeRecord(record);
		}
		logMessage(Level.FINE, 1, "=== removeComplementLiteralAmbiguity -  end  ===");
	}

	@Override
	protected void updateAmbiguousConclusions(int i) {
		if (ambiguousConclusions[i].size() == 0) return;
		logMessage(Level.FINE, 0, "MdlReasoningEngine.updateAmbiguousConclusions - start, i=", i);
		if (!AppConst.isDeploy) printEngineStatus("updateAmbiguousConclusions-before");

		List<Conclusion> ambiguousConclusionToRemove = new ArrayList<Conclusion>();
		List<Conclusion> recordsToRemove = new ArrayList<Conclusion>();

		// remove ambiguity caused by complementary literals
		removeComplementLiteralAmbiguity(i);

		// remove ambiguity based on modal operator strength
		for (Entry<Conclusion, Set<String>> entry : ambiguousConclusions[i].entrySet()) {
			Conclusion conclusion = entry.getKey();
			Set<String> ruleLabels = entry.getValue();

			Literal literal = conclusion.getLiteral();
			Set<Literal> conflictLiterals = getConflictLiterals(literal);
			boolean conflictLiteralInSccGroup = false;
			for (Literal conflictLiteral : conflictLiterals) {
				if (null != getSccGroup(conflictLiteral)) conflictLiteralInSccGroup = true;
			}

			switch (conclusion.getConclusionType()) {
			case DEFINITE_PROVABLE:
				logMessage(Level.FINER, 1, "updateAmbiguousConclusion [MDL], check literal (definite): ", literal);
				if (!containsUnprovedRuleInTheory(conflictLiterals, RuleType.STRICT)) {
					boolean chk2 = isRecordExist(conflictLiterals, ConclusionType.DEFINITE_PROVABLE);
					if (chk2) {
						ambiguousConclusionToRemove.add(conclusion);

						int conflictLiteralExistCount = 0;
						int strongModeCount = 0;
						for (Literal conflictLiteral : conflictLiterals) {
							if (isRecordExist(conflictLiteral, ConclusionType.DEFINITE_PROVABLE)) {
								conflictLiteralExistCount++;
								if (hasStrongerMode(literal, conflictLiteral)) {
									logMessage(Level.FINEST, 2, null, literal, " hasStrongerMode: ", conflictLiteral);
									strongModeCount++;
								}
							}
						}

						// only conclusion with strongest modal operator is concluded
						if (strongModeCount == conflictLiteralExistCount) {
							// addPendingConclusion(new Conclusion(ConclusionType.DEFINITE_PROVABLE, literal));
							// ambiguousConclusionToRemove.add(conclusion);
							if (isLogInferenceProcess)
								getInferenceLogger().updateRuleInferenceStatus(ruleLabels, conclusion, RuleInferenceStatus.APPICABLE);
							newLiteralFind_definiteProvable(literal, true);
						} else {
							// recordsToRemove.add(new Conclusion(ConclusionType.DEFINITE_PROVABLE, literal));
							recordsToRemove.add(conclusion);
							// addPendingConclusion(new Conclusion(ConclusionType.DEFINITE_NOT_PROVABLE, literal));
							// ambiguousConclusionToRemove.add(conclusion);
							if (isLogInferenceProcess)
								getInferenceLogger().updateRuleInferenceStatus(ruleLabels, RuleType.STRICT,
										ConclusionType.DEFINITE_NOT_PROVABLE, literal, RuleInferenceStatus.DEFEATED);
							newLiteralFind_definiteNotProvable(literal, true);
						}
					} else {
						// addPendingConclusion(new Conclusion(ConclusionType.DEFINITE_PROVABLE, literal));
						ambiguousConclusionToRemove.add(conclusion);
						if (isLogInferenceProcess)
							getInferenceLogger().updateRuleInferenceStatus(ruleLabels, conclusion, RuleInferenceStatus.APPICABLE);
						newLiteralFind_definiteProvable(literal, true);
					}
				}
				break;
			case DEFEASIBLY_PROVABLE:
				logMessage(Level.FINER, 1, "updateAmbiguousConclusion [MDL], check literal (defeasible): ", literal);
				Set<Rule> rulesWithLiteralAsHead = theory.getRulesWithHead(literal);
				boolean keepLiteralInAmbiguousSet = false;
				for (Rule r : rulesWithLiteralAsHead) {
					for (Literal bodyLiteral : r.getBodyLiterals()) {
						if (bodyLiteral.isPlaceHolder()) keepLiteralInAmbiguousSet = true;
					}
				}
				boolean dchk2 = isRecordExist(conflictLiterals, ConclusionType.DEFEASIBLY_PROVABLE);

				if (keepLiteralInAmbiguousSet) {
					// defer the removal of literal until all rules generated
					// by the superiority removal process have been evaluated
					logMessage(Level.FINER, 2, "keep literal in ambiguous set temporary");
				} else if (!containsUnprovedRuleInTheory(conflictLiterals, RuleType.DEFEASIBLE)) {
					ambiguousConclusionToRemove.add(conclusion);
					if (dchk2) {
						int conflictLiteralExistCount = 0;
						int strongModeCount = 0;
						for (Literal conflictLiteral : conflictLiterals) {
							if (isRecordExist(conflictLiteral, ConclusionType.DEFEASIBLY_PROVABLE)) {
								conflictLiteralExistCount++;
								if (hasStrongerMode(literal, conflictLiteral)) {
									logMessage(Level.FINEST, 2, null, literal, " hasStrongerMode: ", conflictLiteral);
									strongModeCount++;
								}
							}
						}

						// only conclusion with strongest modal operator is concluded
						if (strongModeCount == conflictLiteralExistCount) {
							// addPendingConclusion(new Conclusion(ConclusionType.DEFEASIBLY_PROVABLE, literal));
							// ambiguousConclusionToRemove.add(conclusion);
							if (isLogInferenceProcess)
								getInferenceLogger().updateRuleInferenceStatus(ruleLabels, conclusion, RuleInferenceStatus.APPICABLE);
							newLiteralFind_defeasiblyProvable(literal, true);
						} else {
							recordsToRemove.add(new Conclusion(ConclusionType.DEFEASIBLY_PROVABLE, literal));
							// ambiguousConclusionToRemove.add(conclusion);
							// Conclusion negConclusion = new Conclusion(ConclusionType.DEFEASIBLY_NOT_PROVABLE,
							// literal);
							// addPendingConclusion(negConclusion);
							if (isLogInferenceProcess)
								getInferenceLogger().updateRuleInferenceStatus(ruleLabels, RuleType.DEFEASIBLE,
										ConclusionType.DEFEASIBLY_NOT_PROVABLE, literal, RuleInferenceStatus.DEFEATED);
							newLiteralFind_defeasiblyNotProvable(literal, true);
						}
					} else {
						// addPendingConclusion(new Conclusion(ConclusionType.DEFEASIBLY_PROVABLE, literal));
						// ambiguousConclusionToRemove.add(conclusion);
						if (isLogInferenceProcess)
							getInferenceLogger().updateRuleInferenceStatus(ruleLabels, conclusion, RuleInferenceStatus.APPICABLE);
						newLiteralFind_defeasiblyProvable(literal, true);
					}
				} else if (conflictLiteralInSccGroup && dchk2) {
					ambiguousConclusionToRemove.add(conclusion);
					recordsToRemove.add(conclusion);
					if (isLogInferenceProcess)
						getInferenceLogger().updateRuleInferenceStatus(ruleLabels, conclusion, RuleInferenceStatus.DISCARDED);
					newLiteralFind_defeasiblyNotProvable(literal, true);
				}
				break;
			default:
			}
		}

		for (Conclusion conclusion : ambiguousConclusionToRemove) {
			ambiguousConclusions[i].remove(conclusion);
		}
		for (Conclusion record : recordsToRemove) {
			removeRecord(record);
		}

		if (!AppConst.isDeploy) printEngineStatus("updateAmbiguousConclusions-after");
		logMessage(Level.FINE, 0, "MdlReasoningEngine.updateAmbiguousConclusions - end, i=", i);
	}

	private boolean hasStrongerMode(Literal literal, Literal conflictLiteral) {
		Mode m1 = literal.getMode();
		Mode m2 = conflictLiteral.getMode();
		if (m1.equals(m2)) return false;
		if (m1.getName().equals(m2.getName()) && m1.isNegation() != m2.isNegation()) return false;
		if (!strongerModeSet.containsKey(m1.getName())) return false;
		Set<String> modeList = strongerModeSet.get(m1.getName());
		String m2ModeName = m2.getName();
		for (String s : modeList) {
			if (s.equals(m2ModeName)) { return true; }
		}
		return false;
	}
}
