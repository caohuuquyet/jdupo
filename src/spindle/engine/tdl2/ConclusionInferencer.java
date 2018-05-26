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
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.Mode;
import spindle.core.dom.ProvabilityLevel;
import spindle.core.dom.Temporal;
import spindle.core.dom.Theory;
import spindle.sys.AppModuleBase;
import spindle.sys.message.ErrorMessage;

public abstract class ConclusionInferencer extends AppModuleBase {

	protected LiteralDashboard dashboard;
	protected Theory theory;
	protected Map<String, Set<String>> strongerModeSet = null;

	public ConclusionInferencer(LiteralDashboard dashboard) throws ConclusionInferencerException {
		super();
		setLiteralDashboard(dashboard);
	}

	protected boolean hasStrongerMode(Literal literal, Literal conflictLiteral) {
		Mode m1 = literal.getMode();
		Mode m2 = conflictLiteral.getMode();

		String m1Name = null == m1 ? "" : m1.getName();
		String m2Name = null == m2 ? "" : m2.getName();

		if (m1Name.equals(m2Name)) return false;

		Set<String> strongerMode = strongerModeSet.get(m1Name);
		if (null == strongerMode) return false;

		return strongerMode.contains(m2Name);
	}

	public Map<Literal, NavigableMap<Temporal, ConclusionType>> updateConclusions(AbstractLiteral literal, ProvabilityLevel provability,
			Map<ProvabilityLevel, AbstractLiteral> abstractLiterals, //
			Map<ProvabilityLevel, Set<AbstractLiteral>> conflictAbstractLiterals) throws ConclusionInferencerException {
		
//		consolidatedConclusions.putAll(literalConsolidatedConclusions);
//		literalTemporalSegments.addAll(literalConsolidatedConclusions.keySet());
//
//		switch (provability) {
//		case DEFEASIBLE:
//			Set< AbstractLiteral> defeasibleConflictLiterals = conflictAbstractLiterals.get(ProvabilityLevel.DEFEASIBLE);
//			if (null != defeasibleConflictLiterals) {
//				for (AbstractLiteral abstractConflictLiteral:defeasibleConflictLiterals){
//					derivedLiterals.remove(abstractConflictLiteral.getLiteral());
//					literalConsolidatedConclusions = abstractConflictLiteral.getConsolidatedConclusions();
//					literalTemporalSegments.addAll(literalConsolidatedConclusions.keySet());
//				}
//			}
//		case DEFINITE:
//			Set< AbstractLiteral> definiteConflictLiterals = conflictAbstractLiterals.get(ProvabilityLevel.DEFINITE);
//			if (null != definiteConflictLiterals) {
//				for (AbstractLiteral abstractConflictLiteral:definiteConflictLiterals){
//					derivedLiterals.remove(abstractConflictLiteral.getLiteral());
//					literalConsolidatedConclusions = abstractConflictLiteral.getConsolidatedConclusions();
//					literalTemporalSegments.addAll(literalConsolidatedConclusions.keySet());
//				}
//			}
//			break;
//		default:
//			if (PRINT_DETAILS) System.out.printf("generateConsolidatedConclusions.skip provability level:%s\n", provability);
//		}
		
		try {
			if (conflictAbstractLiterals.size() == 0) {
				Map<Literal, NavigableMap<Temporal, ConclusionType>> consolidatedConclusions = new TreeMap<Literal, NavigableMap<Temporal, ConclusionType>>();
				consolidatedConclusions.put(literal.getLiteral(),
						new TreeMap<Temporal, ConclusionType>(literal.getConsolidatedConclusions()));
				return consolidatedConclusions;
			} else {
				return consolidateConflictConclusions(literal, provability, conflictAbstractLiterals);
			}
		} catch (Exception e) {
			throw new ConclusionInferencerException(e);
		}
	}

	protected Set<Temporal> getConsolidatedConclusionsTemporal(Collection<AbstractLiteral> literals) throws AbstractLiteralException {
		
		
		
		Set<Temporal> temporals = new TreeSet<Temporal>();
		for (AbstractLiteral literal : literals) {
			NavigableMap<Temporal, ConclusionType> consolidatedConclusions = literal.getConsolidatedConclusions();
			temporals.addAll(consolidatedConclusions.keySet());
		}
		return temporals;
	}

	// ==================================================
	// getters and setters
	// ==================================================
	protected void setLiteralDashboard(LiteralDashboard dashboard) throws ConclusionInferencerException {
		if (null == dashboard) throw new ConclusionInferencerException(ErrorMessage.LITERAL_DASHBOARD_NULL);
		this.dashboard = dashboard;
		setTheory(this.dashboard.getTheory());
	}

	protected void setTheory(Theory theory) throws ConclusionInferencerException {
		if (null == theory) throw new ConclusionInferencerException(ErrorMessage.THEORY_NULL_THEORY);
		this.theory = theory;
		setStrongerModeSet(this.theory.getStrongerModeSet());
	}

	protected Set<Literal> getConflictLiterals(Literal literal) {
		return theory.getConflictLiterals(literal);
	}

	protected Set<String> getStrongerMode(String modeName) {
		return null == strongerModeSet || null == modeName ? null : strongerModeSet.get(modeName.trim().toUpperCase());
	}

	public void setStrongerModeSet(Map<String, Set<String>> strongerModeSet) {
		this.strongerModeSet = strongerModeSet;
	}

	// ==================================================
	// abstract methods
	// ==================================================
	protected abstract Map<Literal, NavigableMap<Temporal, ConclusionType>> consolidateConflictConclusions(AbstractLiteral literal,
			ProvabilityLevel provability, Map<ProvabilityLevel, Set<AbstractLiteral>> conflictAbstractLiterals)
			throws ConclusionInferencerException;

}
