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
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.logging.Level;



import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.LiteralComparator;
import spindle.core.dom.Mode;
import spindle.core.dom.ProvabilityLevel;
import spindle.core.dom.Rule;
import spindle.core.dom.RuleType;
import spindle.core.dom.Temporal;
import spindle.core.dom.TemporalStartComparator;
import spindle.core.dom.Theory;
import spindle.core.dom.TheoryType;
import spindle.sys.AppConst;
import spindle.sys.AppLogger;
import spindle.sys.AppModuleBase;
import spindle.sys.message.ErrorMessage;

public abstract class LiteralDashboardBase extends AppModuleBase implements TdlReasoningConstant,LiteralDashboardConstant {
//	private static final Temporal PERSISTENT_TEMPORAL = Temporal.PERSISTENT_TEMPORAL;

	private Theory theory;
	private Map<String, Set<String>> strongerModeSet;

//	private Set<Literal> allPlainLiteralsInTheory;
	private Map<ProvabilityLevel, Map<Literal,Set<Literal>>>basicLiterals;
	private Set<Literal>allBasicLiterals;

	private Map<ProvabilityLevel, Map<Literal, AbstractLiteral>> headLiterals;
	private Map<ProvabilityLevel, Map<Literal, AbstractLiteral>> bodyLiterals;
	
//	private Map<ProvabilityLevel, Map<Literal,Set<Literal>>>groundedLiterals;

	private Map<ProvabilityLevel, Set<Literal>> initialProvableLiterals;
	private Map<ProvabilityLevel, Set<Literal>> initialUnprovableLiterals;

	private Map<ProvabilityLevel, NavigableSet<Literal>> newConclusionsDerived;
	private Map<ProvabilityLevel, Map<Literal, NavigableMap<Temporal, ConclusionType>>> consolidatedConclusions;

	private Map<Literal, Set<Literal>> conflictLiterals;
	private Map<Literal, Map<ProvabilityLevel, Set<AbstractLiteral>>> conflictAbstractLiterals;

	public LiteralDashboardBase(Theory theory, AppLogger appLogger) throws LiteralDashboardException {
		if (null == theory) throw new LiteralDashboardException("Input theory is null");
		if (theory.isEmpty()) throw new LiteralDashboardException("Input theory is empty!");
		if (!TheoryType.TDL.equals(theory.getTheoryType())) throw new LiteralDashboardException("Inputed theory is not a TDL theory!");
		setAppLogger(appLogger);
		setTheory(theory);
		initialize();
	}
	
	private void addBasicLiterals(Map<ProvabilityLevel, Map<Literal,AbstractLiteral>>entries){
		for (Entry<ProvabilityLevel,Map<Literal,AbstractLiteral>>literalEntry:entries.entrySet()){
		allBasicLiterals.addAll(literalEntry.getValue().keySet());
		}
	}

	private void initialize() throws LiteralDashboardException {
		basicLiterals=new TreeMap<ProvabilityLevel, Map<Literal,Set<Literal>>>();
		allBasicLiterals=new TreeSet<Literal>();

		headLiterals = new TreeMap<ProvabilityLevel, Map<Literal, AbstractLiteral>>();
		bodyLiterals = new TreeMap<ProvabilityLevel, Map<Literal, AbstractLiteral>>();
		

		initialProvableLiterals = new TreeMap<ProvabilityLevel, Set<Literal>>();
		initialUnprovableLiterals = new TreeMap<ProvabilityLevel, Set<Literal>>();

		initialUnprovableLiterals.put(ProvabilityLevel.DEFINITE, new TreeSet<Literal>(theory.getAllLiteralsInRules()));
		initialUnprovableLiterals.put(ProvabilityLevel.DEFEASIBLE, new TreeSet<Literal>(theory.getAllLiteralsInRules()));

		newConclusionsDerived=new TreeMap<ProvabilityLevel,NavigableSet<Literal>>();
		consolidatedConclusions= new TreeMap<ProvabilityLevel, Map<Literal, NavigableMap<Temporal, ConclusionType>>>();
		
		conflictLiterals=new TreeMap<Literal,Set<Literal>>(LiteralComparator.getNoTemporalLiteralComparator());
		conflictAbstractLiterals=new TreeMap<Literal,Map<ProvabilityLevel,Set<AbstractLiteral>>>(LiteralComparator.getNoTemporalLiteralComparator());
//		conflictLiterals=new TreeMap<Literal,Set<Literal>>(new LiteralComparator(false));
//		conflictAbstractLiterals=new TreeMap<Literal,Map<ProvabilityLevel,Set<AbstractLiteral>>>(new LiteralComparator(false));

		for (Rule rule : theory.getFactsAndAllRules().values()) {
			RuleType ruleType = rule.getRuleType();
			ProvabilityLevel provability = ruleType.getProvabilityLevel();
			if (ProvabilityLevel.NONE.equals(provability)) continue;
			Collection<Literal> ruleHeadLiterals = rule.getHeadLiterals();
//System.out.println("head literals");
			addLiteralToSet(ruleHeadLiterals, provability, rule.getLabel(), headLiterals,true);
//System.out.println("body literals");
			addLiteralToSet(rule.getBodyLiterals(), provability, rule.getLabel(), bodyLiterals,false);
//			addLiteralToSet(bodyLiterals,rule.getLabel(),provability,rule.getBodyLiterals());

			Set<Literal> unprovableSet = initialUnprovableLiterals.get(provability);
			unprovableSet.removeAll(ruleHeadLiterals);

			if (rule.isEmptyBody()) {
				Set<Literal> provableSet = initialProvableLiterals.get(provability);
				if (null == provableSet) {
					provableSet = new TreeSet<Literal>();
					initialProvableLiterals.put(provability, provableSet);
				}
				provableSet.addAll(ruleHeadLiterals);
			}
		}
		System.out.println("loop ended!");
		
		addBasicLiterals(headLiterals);addBasicLiterals(bodyLiterals);

		// generateLiteralsProvableSegments();
		// logMessage(Level.INFO, 0, "initialize");
		// int n = 0;
		// for (Map<Literal, NavigableSet<Temporal>> entry : provableIntervals.values()) {
		// n += entry.size();
		// }
		// if (n == 0) throw new LiteralDashboardException("The inputed theory contains no provable intervals");
		//
		// logMessage(Level.INFO,0,"initialProvableLiterals.size="+initialProvableLiterals.size());
		// System.out.println("initialProvableLiterals.size="+initialProvableLiterals.size());
		for (Entry<ProvabilityLevel, Set<Literal>> provabilityEntry : initialProvableLiterals.entrySet()) {
			for (Literal literal : provabilityEntry.getValue()) {
				logMessage(Level.INFO, 0, "initialProvableLiteral", provabilityEntry.getKey(), literal);
			}
		}
		checkInitialUnprovableLiterals();
	}
	
	
	
	public void terminate() {
		logMessage(Level.INFO, 0, toString());
		resetAppLogger();
	}
	

	
//	private void addLiteralToSet(	Map<ProvabilityLevel, Map<Literal, AbstractLiteral>> entries ,String ruleLabel, ProvabilityLevel provability, 
//			Literal... literals
//		) throws LiteralDashboardException, AbstractLiteralException {
//		if (null==literals||literals.length==0)return;
//		for (Literal literal : literals) {
//			AbstractLiteral abstractLiteral=getAbstractLiteral(literal,provability,entries,true);
//			abstractLiteral.addLiteral(literal, ruleLabel);
////			addLiteralToSet(literal, provability, ruleLabel, entries);
//		}
//	}

	private void addLiteralToSet(Collection<Literal> literals, ProvabilityLevel provability, String ruleLabel,
			Map<ProvabilityLevel, Map<Literal, AbstractLiteral>> entries,boolean addBasicLiteral) throws LiteralDashboardException {
		if (null == literals || literals.size() == 0) return;
		try{			
		for (Literal literal : literals) {
//			System.out.println(INDENTATOR+literal);
			AbstractLiteral abstractLiteral = getAbstractLiteral(literal, provability, entries, true);
			abstractLiteral.addLiteral(literal, ruleLabel);
//			addLiteralToSet(literal, provability, ruleLabel, entries);
//			if (literal.hasPredicatesGrounded()) {
//				Map<Literal,Set<Literal>>literalSet=groundedLiterals.get(provability);
//				if (null==literalSet){
//					literalSet=new TreeMap<Literal,Set<Literal>>(LiteralComparator.getNoTemporalNoPredicatesLiteralComparator());
//					groundedLiterals.put(provability, literalSet);
//				}
//				Set<Literal>groundedLiteralsSet=literalSet.get(literal);
//				if (null==groundedLiteralsSet){
//					groundedLiteralsSet=new TreeSet<Literal>();
//					literalSet.put(literal.getPlainLiteralClone(), groundedLiteralsSet);
//				}
//				groundedLiteralsSet.add(literal);
//			}
			if (addBasicLiteral) addBasicLiteral(literal,provability);
		}
		}catch (Exception e){
			throw new LiteralDashboardException(e);
		}
	}



//	private void addLiteralToSet(Literal literal, ProvabilityLevel provability, String ruleLabel,
//			Map<ProvabilityLevel, Map<Literal, AbstractLiteral>> entries) throws LiteralDashboardException {
//		AbstractLiteral abstractLiteral = getAbstractLiteral(literal, provability, entries, true);
//		try {
//			abstractLiteral.addLiteral(literal, ruleLabel);
//		} catch (Exception e) {
//			throw new LiteralDashboardException(e);
//		}
//	}

	private AbstractLiteral getAbstractLiteral(Literal literal,ProvabilityLevel provability,
			Map<ProvabilityLevel, Map<Literal, AbstractLiteral>> entries,  boolean addNew) {
		if (null == entries||null==literal) return null;
//		System.out.printf("        getAbstractLiteral(%s)\n",literal.toString());
		Map<Literal, AbstractLiteral> literalEntries = entries.get(provability);
		if (null == literalEntries) {
			if (!addNew) return null;
			literalEntries = new TreeMap<Literal, AbstractLiteral>(LiteralComparator.getNoTemporalNoGroundedPredicatesLiteralComparator());
//			literalEntries = new TreeMap<Literal, AbstractLiteral>(LiteralComparator.getNoTemporalLiteralComparator());
//			literalEntries = new TreeMap<Literal, AbstractLiteral>(new LiteralComparator(false));
			entries.put(provability, literalEntries);
		}

		//return getAbstractLiteral(literal,literalEntries,addNew);
		
		AbstractLiteral abstractLiteral = literalEntries.get(literal);
		if (null == abstractLiteral) {
			if (!addNew) return null;
			abstractLiteral = new AbstractLiteral(literal);
//			literalEntries.put(literal.cloneWithNoTemporal(), abstractLiteral);
//			literalEntries.put(literal.getPlainLiteralClone(), abstractLiteral);
			literalEntries.put(abstractLiteral.getLiteral(), abstractLiteral);
		}

		return abstractLiteral;
	}
	

	
//	private AbstractLiteral getAbstractLiteral(Literal literal, Map<Literal,AbstractLiteral>literalEntries,boolean addNew){
//		AbstractLiteral abstractLiteral=literalEntries.get(literal);
//		if (null==abstractLiteral){
//			if (!addNew)return null;
//			abstractLiteral=new AbstractLiteral(literal);
//			literalEntries.put(literal.cloneWithNoTemporal(), abstractLiteral);
//		}
//		return abstractLiteral;
//	}

	protected boolean removeAbstractLiteral(Literal literal, ProvabilityLevel provability,
			Map<ProvabilityLevel, Map<Literal, AbstractLiteral>> entries) {
		if (null == entries) return false;
		Map<Literal, AbstractLiteral> literalEntries = entries.get(provability);
		if (null == literalEntries) return false;
		try {
			return null != literalEntries.remove(literal);
		} finally {
			if (literalEntries.size() == 0) entries.remove(provability);
		}
	}
	
	protected Map<ProvabilityLevel, Set<AbstractLiteral>> getConflictAbstractLiterals(Literal literal,
			Map<ProvabilityLevel, Map<Literal, AbstractLiteral>> entries) {
		Map<ProvabilityLevel, Set<AbstractLiteral>> conflictAbstractLiteralsEntry = this.conflictAbstractLiterals.get(literal);
		if (null != conflictAbstractLiteralsEntry) return conflictAbstractLiteralsEntry;

		conflictAbstractLiteralsEntry = new TreeMap<ProvabilityLevel, Set<AbstractLiteral>>();
		try {
			for (ProvabilityLevel provability : ProvabilityLevel.values()) {
				switch (provability) {
				case DEFINITE:
				case DEFEASIBLE:
					Set<AbstractLiteral> conflictAbstractLiterals = new TreeSet<AbstractLiteral>();
					for (Literal conflictLiteral : getConflictLiterals(literal)) {
						AbstractLiteral abstractLiteral = getAbstractLiteral(conflictLiteral, provability, entries, false);
						if (null != abstractLiteral) conflictAbstractLiterals.add(abstractLiteral);
					}
					if (conflictAbstractLiterals.size() > 0) conflictAbstractLiteralsEntry.put(provability, conflictAbstractLiterals);
					break;
				default:
					// skip other provability values
				}
			}
			return conflictAbstractLiteralsEntry;
		} finally {
			this.conflictAbstractLiterals.put(literal, conflictAbstractLiteralsEntry);
		}
	}
	protected void addBasicLiteral(Literal literal, ProvabilityLevel provability) {
		Map<Literal, Set<Literal>> literalEntry = basicLiterals.get(provability);
		if (null == literalEntry) {
			literalEntry = new TreeMap<Literal, Set<Literal>>(LiteralComparator.getBasicLiteralComparator());
			basicLiterals.put(provability, literalEntry);
		}

		Set<Literal> literalSet = literalEntry.get(literal);
		if (null == literalSet) {
			literalSet = new TreeSet<Literal>();
			literalEntry.put(literal.getBasicLiteral(), literalSet);
		}
		literalSet.add(literal);
	}
	protected Set<Literal> getConflictLiterals(Literal literal) {
		Set<Literal> conflictLiterals = this.conflictLiterals.get(literal);
		if (null != conflictLiterals) return conflictLiterals;
		try {
			conflictLiterals = theory.getConflictLiterals(literal);
			return conflictLiterals;
		} finally {
			if (null != conflictLiterals) this.conflictLiterals.put(literal.cloneWithNoTemporal(), conflictLiterals);
		}
	}
	
	public boolean isConflict(Literal l1, Literal l2) {
		Set<Literal> cl1 = getConflictLiterals(l1);
		return cl1.contains(l2.cloneWithNoTemporal());
	}


	
	protected NavigableMap<Temporal, ConclusionType> getConclusionSet(Literal literal, ProvabilityLevel provability, //
			Map<ProvabilityLevel, Map<Literal, NavigableMap<Temporal, ConclusionType>>> entries, //
			boolean addNew) {
		if (null == entries) return null;

		Map<Literal, NavigableMap<Temporal, ConclusionType>> literalEntries = entries.get(provability);
//		System.out.println("literal=" + literal + ",literalEntries=" + literalEntries);
		if (null == literalEntries) {
			if (!addNew) return null;
			literalEntries = new TreeMap<Literal, NavigableMap<Temporal, ConclusionType>>(LiteralComparator.getNoTemporalLiteralComparator());
//			literalEntries = new TreeMap<Literal, NavigableMap<Temporal, ConclusionType>>(new LiteralComparator(false));
			entries.put(provability, literalEntries);
		}

		NavigableMap<Temporal, ConclusionType> temporalEntries = literalEntries.get(literal);
//		System.out.println("literal=" + literal + ",temporalEntries=" + temporalEntries);
		if (null == temporalEntries) {
			if (!addNew) return null;
			temporalEntries =new TreeMap<Temporal, ConclusionType>(new TemporalStartComparator());				
			literalEntries.put(literal.cloneWithNoTemporal(), temporalEntries);
		}

		return temporalEntries;
	}

	protected boolean removeLiteralConclusionSet(Literal literal, ProvabilityLevel provability, Temporal temporal,
			Map<ProvabilityLevel, Map<Literal, NavigableMap<Temporal, ConclusionType>>> entries) {
		if (null == entries) return false;
		Map<Literal, NavigableMap<Temporal, ConclusionType>> literalEntries = entries.get(provability);
		if (null == literalEntries) return false;

		NavigableMap<Temporal, ConclusionType> temporalEntries = null;
		try {
			if (null == temporal) {
				return null != literalEntries.remove(literal);
			} else {
				temporalEntries = literalEntries.get(literal);
				return null == temporalEntries ? false : (null != temporalEntries.remove(temporal));
			}
		} finally {
			if (null != temporalEntries && temporalEntries.size() == 0) literalEntries.remove(literal);
			if (literalEntries.size() == 0) entries.remove(provability);
		}
	}

	private void checkInitialUnprovableLiterals() throws LiteralDashboardException {
		logMessage(Level.INFO, 0, "checkUnprovableLiteral");
		Map<ProvabilityLevel, Set<Literal>> unprovableLiteralsSet = new TreeMap<ProvabilityLevel, Set<Literal>>(initialUnprovableLiterals);
		initialUnprovableLiterals.clear();

		int allLiteralsCount = theory.getAllLiteralsInRules().size();

		for (Entry<ProvabilityLevel, Set<Literal>> provabilityEntry : unprovableLiteralsSet.entrySet()) {
			ProvabilityLevel provability = provabilityEntry.getKey();
			Set<Literal> unprovableLiterals = new TreeSet<Literal>();

			Set<Literal> unprovableLiteralsChecked = provabilityEntry.getValue();
			if (unprovableLiteralsChecked.size() == allLiteralsCount) {
				unprovableLiterals.addAll(unprovableLiteralsChecked);
			} else {
				for (Literal literal : unprovableLiteralsChecked) {
					if (!isProvable(literal, provability, getTemporal(literal))) unprovableLiterals.add(literal);
				}
			}
			initialUnprovableLiterals.put(provability, unprovableLiterals);
			logMessage(Level.INFO, 0, "initialUnprovableLiterals", provability, unprovableLiterals);
			System.out.println(provability + ":" + unprovableLiterals);
		}

		// for (ProvabilityLevel provability : ProvabilityLevel.values()) {
		// switch (provability) {
		// case DEFINITE:
		// case DEFEASIBLE:
		// ConclusionType ct=ProvabilityLevel.getNegativeConclusionType(provability);
		// Set<Literal> literals = new TreeSet<Literal>(theory.getAllLiteralsInRules());
		// Map<Literal, NavigableSet<Temporal>> literalsSet = allHeadLiterals.get(provability);
		// Set<Literal> unprovableLiterals = new TreeSet<Literal>();
		// try {
		// if (null == literalsSet) {
		// unprovableLiterals.addAll(literals);
		// } else {
		// literals.removeAll(literalsSet.keySet());
		// for (Literal literal : literals) {
		// if (!isProvable(literal, provability, getTemporal(literal))) unprovableLiterals.add(literal);
		// }
		// }
		// initialUnprovableLiterals.put(provability, unprovableLiterals);
		// logMessage(Level.INFO, 0, "checkUnprovableLiteral", provability, unprovableLiterals);
		// System.out.println(provability + ":" + unprovableLiterals);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// break;
		// default:
		// }
		// }
	}

	protected boolean isProvable(Literal literal, ProvabilityLevel provability, Temporal interval) throws LiteralDashboardException {
		AbstractLiteral abstractLiteral = getAbstractLiteral(literal, provability, headLiterals, false);
		// AbstractLiteralSet literalSet=getLiteralSet(literal,allHeadLiteralsInTheory.get(provability),false);
		if (null == abstractLiteral) return false;
		// Temporal provableTemporalSegment= literalSet.getProvableTemporalSegment(literal.getTemporal());
		// System.out.println(literal+".provableTemporalSegment="+provableTemporalSegment);
		// return provableTemporalSegment;

		// Temporal provableInterval = getProvableTemporalSegment(literal, provability, interval);
		// Temporal provableTemporalSegment=literalSet.getProvableTemporalSegment(literal.getTemporal());
		// return null != provableTemporalSegment;
		try {
			return abstractLiteral.isProvable(literal);
		} catch (Exception e) {
			throw new LiteralDashboardException(e);
		}
		// return null!=abstractLiteral.getProvableTemporalSegment(literal.getTemporal());
		// return null == provableInterval ? false : provableInterval.contains(getTemporal(interval));
	}

	public boolean isHeadLiteral(Literal literal, ProvabilityLevel provability) {
		AbstractLiteral abstractLiteral = getAbstractLiteral(literal, provability, headLiterals, false);
//		return null==abstractLiteral ? false : abstractLiteral.containsTemporalsToProve(getTemporal(literal));
		return null == abstractLiteral ? false : abstractLiteral.getAllTemporalsToProve().contains(getTemporal(literal));
	}

	public boolean isBodyLiteral(Literal literal, ProvabilityLevel provability) {
		AbstractLiteral abstractLiteral = getAbstractLiteral(literal, provability, bodyLiterals, false);
//		return null==abstractLiteral ? false : abstractLiteral.containsTemporalsToProve(getTemporal(literal));
		return null == abstractLiteral ? false : abstractLiteral.getAllTemporalsToProve().contains(getTemporal(literal));
	}

	
	public boolean containsLiteralInRuleHead(Literal literal, ProvabilityLevel provability) {
		return containsLiteral(literal,provability,headLiterals);
	}
	
	public boolean containsLiteralInRuleBody(Literal literal, ProvabilityLevel provability){
		return containsLiteral(literal,provability,bodyLiterals);
	}
	
	protected boolean containsLiteral(Literal literal,ProvabilityLevel provability,Map<ProvabilityLevel,Map<Literal,AbstractLiteral>>entries){
		Map<Literal, AbstractLiteral> literalEntries = entries.get(provability);
		if (null == literalEntries) return false;
		return literalEntries.containsKey(literal);				
	}
	
	// protected NavigableMap<Temporal, ConclusionType> getConclusionIntervalsSet(Literal literal, ProvabilityLevel
	// provability,
	// Map<ProvabilityLevel, Map<Literal, NavigableMap<Temporal, ConclusionType>>> conclusionsSet, boolean addNew) {
	// Map<Literal, NavigableMap<Temporal, ConclusionType>> provabilitySet = conclusionsSet.get(provability);
	// if (null == provabilitySet) {
	// if (addNew) {
	// provabilitySet = new TreeMap<Literal, NavigableMap<Temporal, ConclusionType>>(new LiteralComparator(false));
	// conclusionsSet.put(provability, provabilitySet);
	// } else {
	// return null;
	// }
	// }
	// NavigableMap<Temporal, ConclusionType> conclusionSet = provabilitySet.get(literal);
	// if (null == conclusionSet) {
	// if (addNew) {
	// conclusionSet = new TreeMap<Temporal, ConclusionType>(new TemporalComparator());
	// provabilitySet.put(literal.cloneWithNoTemporal(), conclusionSet);
	// }
	// }
	// return conclusionSet;
	// }

	protected boolean addConclusionDerived(Literal literal, ConclusionType conclusionType, String ruleLabel)
			throws LiteralDashboardException {
		AbstractLiteral abstractLiteral = getAbstractLiteral(literal, conclusionType.getProvabilityLevel(), headLiterals, false);
		if (null == abstractLiteral) return false;
		try {
			boolean isConclusionUpdated = abstractLiteral.addConclusionDerived(literal, conclusionType, ruleLabel);
			logMessage(Level.INFO, 1, "addConclusionDerived.isConclusionUpdated("+literal+")", isConclusionUpdated);
			if (!isConclusionUpdated)return isConclusionUpdated;
//			if (isConclusionUpdated){
//				Temporal literalTemporal=getTemporal(literal);
				ProvabilityLevel provability=conclusionType.getProvabilityLevel();
//				NavigableMap<Temporal,ConclusionType>	conclusionSet1=getConclusionSet(literal,provability,conclusionsDerived,true);
//				logMessage(Level.INFO, 1, "addConclusionDerived.isConclusionUpdated("+literal+")1", conclusionsDerived.toString());
//				logMessage(Level.INFO, 1, "addConclusionDerived.isConclusionUpdated("+literal+")1", conclusionSet1.toString());
//				conclusionSet1.put(literalTemporal, conclusionType);
//				logMessage(Level.INFO, 1, "addConclusionDerived.isConclusionUpdated("+literal+")2", conclusionSet1.toString());

				//NavigableMap<Temporal,ConclusionType>	conclusionSet2=getConclusionSet(literal,provability,newConclusionsDerived,true);
				//logMessage(Level.INFO, 1, "addConclusionDerived.isConclusionUpdated("+literal+")3", conclusionSet2.toString());
				//conclusionSet2.put(literalTemporal, conclusionType);
				//logMessage(Level.INFO, 1, "addConclusionDerived.isConclusionUpdated("+literal+")4", conclusionSet2.toString());
				NavigableSet<Literal>derivedLiterals=newConclusionsDerived.get(provability);
				if (null==derivedLiterals){
					derivedLiterals=new TreeSet<Literal>(LiteralComparator.getNoTemporalLiteralComparator());
//					derivedLiterals=new TreeSet<Literal>(new LiteralComparator(false));
					newConclusionsDerived.put(provability, derivedLiterals);
				}
				derivedLiterals.add(literal.cloneWithNoTemporal());
	//		}
//				return true;
			return isConclusionUpdated;
		} catch (Exception e) {
			throw new LiteralDashboardException(e);
		}
	}

	public ConclusionType getConclusionDerived(Literal literal, ProvabilityLevel provability) throws LiteralDashboardException {
		AbstractLiteral abstractLiteral = getAbstractLiteral(literal, provability, headLiterals, false);
		try {
			return abstractLiteral.getConclusionDerived(literal.getTemporal());
		} catch (Exception e) {
			throw new LiteralDashboardException(e);
		}
	}
	
	
	public NavigableMap<Temporal,ConclusionType> getConsolidatedConclusions(Literal literal,ProvabilityLevel provability) throws LiteralDashboardException{
		generateConsolidatedConclusions(literal,provability);
		return getConclusionSet(literal,provability,consolidatedConclusions,false);
	}
	
	
	protected void updateConsolidatedConclusions(Literal literal, ProvabilityLevel provability,
			NavigableMap<Temporal, ConclusionType> consolidatedConclusions) {
		NavigableMap<Temporal, ConclusionType> consolidatedConclusionSet = getConclusionSet(literal, provability,
				this.consolidatedConclusions, true);
		consolidatedConclusionSet.clear();
		consolidatedConclusionSet.putAll(consolidatedConclusions);
	}


	// private void addConsolidatedConclusions(Literal literal, ProvabilityLevel provability, Temporal interval,
	// ConclusionType conclusionType) {
	// NavigableMap<Temporal, ConclusionType> conclusions = getConclusionSet(literal, provability,
	// consolidatedConclusions, true);
	// Temporal floorTemporal = conclusions.floorKey(interval);
	// conclusions.put(interval, conclusionType);
	// }
	//
	// private void verifyConsolidatedConclusions(NavigableMap<Temporal, ConclusionType> conclusions) throws
	// LiteralDashboardException {
	// Temporal lastTemporal = null, currTemporal;
	// ConclusionType lastConclusionType = null, currConclusionType;
	// for (Entry<Temporal, ConclusionType> entry : conclusions.entrySet()) {
	// currTemporal = entry.getKey();
	// currConclusionType = entry.getValue();
	// if (null != lastTemporal) {
	// if (lastTemporal.overlap(currTemporal)) {
	// if (currConclusionType.isConflictWith(lastConclusionType))
	// throw new LiteralDashboardException("Temporals of consolidated conclusions are overlapper!");
	// }
	// }
	// lastTemporal = currTemporal;
	// lastConclusionType = currConclusionType;
	// }
	// }

	protected void generateConsolidatedConclusions(Literal literal, ProvabilityLevel provability) throws LiteralDashboardException {
		if (newConclusionsDerived.size() == 0) return;
		System.out.printf("newConclusionsDerived.1=%s\n", newConclusionsDerived);

		NavigableSet<Literal> derivedLiterals = newConclusionsDerived.get(provability);
		if (null == derivedLiterals || !derivedLiterals.remove(literal)) return;

		// NavigableSet<Temporal> literalTemporalSegments = new TreeSet<Temporal>();

		AbstractLiteral abstractLiteral = getAbstractLiteral(literal, provability, headLiterals, false);
		AbstractLiteral abstractLiteralStict = ProvabilityLevel.DEFEASIBLE.equals(provability) ? getAbstractLiteral(literal,
				ProvabilityLevel.DEFINITE, headLiterals, false) : null;

		Map<ProvabilityLevel, Set<AbstractLiteral>> conflictAbstractLiterals = getConflictAbstractLiterals(literal, headLiterals);

		try {
			NavigableMap<Temporal, ConclusionType> literalConclusions = abstractLiteral.getConsolidatedConclusions();

			if (conflictAbstractLiterals.size() == 0) {
				updateConsolidatedConclusions(literal, provability, literalConclusions);
			} else {
				// Map<Literal, NavigableMap<Temporal, ConclusionType>> literalConclusionsSet =
				// getConclusionInferencer().updateConclusions(
				// abstractLiteral, provability, conflictAbstractLiterals);
				// if (null != literalConclusionsSet) {
				// for (Entry<Literal, NavigableMap<Temporal, ConclusionType>> entry : literalConclusionsSet.entrySet())
				// {
				// updateConsolidatedConclusions(entry.getKey(), provability, entry.getValue());
				// }
				// }
			}
		} catch (AbstractLiteralException e) {
			throw new LiteralDashboardException(e);
		}

		// try {
		// NavigableMap<Temporal, ConclusionType> literalConsolidatedConclusions =
		// abstractLiteral.getConsolidatedConclusions();
		//
		// NavigableMap<Temporal, ConclusionType> consolidatedConclusions = new TreeMap<Temporal, ConclusionType>();
		// // if (conflictAbstractLiterals.size() == 0) {
		// // addConsolidatedConclusions(literal, provability, literalConsolidatedConclusions);
		// // return;
		// // }
		//
		// consolidatedConclusions.putAll(literalConsolidatedConclusions);
		// literalTemporalSegments.addAll(literalConsolidatedConclusions.keySet());
		//
		// switch (provability) {
		// case DEFEASIBLE:
		// Set< AbstractLiteral> defeasibleConflictLiterals = conflictAbstractLiterals.get(ProvabilityLevel.DEFEASIBLE);
		// if (null != defeasibleConflictLiterals) {
		// for (AbstractLiteral abstractConflictLiteral:defeasibleConflictLiterals){
		// derivedLiterals.remove(abstractConflictLiteral.getLiteral());
		// literalConsolidatedConclusions = abstractConflictLiteral.getConsolidatedConclusions();
		// literalTemporalSegments.addAll(literalConsolidatedConclusions.keySet());
		// }
		// }
		// case DEFINITE:
		// Set< AbstractLiteral> definiteConflictLiterals = conflictAbstractLiterals.get(ProvabilityLevel.DEFINITE);
		// if (null != definiteConflictLiterals) {
		// for (AbstractLiteral abstractConflictLiteral:definiteConflictLiterals){
		// derivedLiterals.remove(abstractConflictLiteral.getLiteral());
		// literalConsolidatedConclusions = abstractConflictLiteral.getConsolidatedConclusions();
		// literalTemporalSegments.addAll(literalConsolidatedConclusions.keySet());
		// }
		// }
		// break;
		// default:
		// if (PRINT_DETAILS) System.out.printf("generateConsolidatedConclusions.skip provability level:%s\n",
		// provability);
		// }
		//
		// literalTemporalSegments= Temporal.getTemporalSegmentsFromSet(literalTemporalSegments);
		// System.out.printf("literalTemporalSegments=%s\n",literalTemporalSegments);
		// System.out.printf("newConclusionsDerived.2=%s\n",newConclusionsDerived);
		//
		// getConclusionInferencer().updateConclusions(abstractLiteral, provability, conflictAbstractLiterals);
		// } catch (AbstractLiteralException e) {
		// throw new LiteralDashboardException(e);
		// } catch (TemporalException e) {
		// throw new LiteralDashboardException(e);
		// } catch (ConclusionInferencerException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally {
		//
		// }
		// if (PRINT_DETAILS) {
		// for (AbstractLiteral conflictAbstractLiteral : provabilityEntry.values()) {
		// System.out.printf("conflictAbstractLiteral(%s)=%s\n", literal, conflictAbstractLiteral.getLiteral());
		// }
		// }
		// } else {
		// if (PRINT_DETAILS) System.out.printf("conflictAbstractLiteral(%s)=[]\n", literal);
		// }

		// NavigableSet<Literal> derivedConclusionLiterals = newConclusionsDerived.get(provability);
		// if (null == derivedConclusionLiterals) return;
		// if (!derivedConclusionLiterals.remove(literal)) return;
		//
		// AbstractLiteral abstractLiteral = getAbstractLiteral(literal, provability, headLiterals, false);
		//
		// Map<ProvabilityLevel, Map<Literal, AbstractLiteral>> conflictAbstractLiterals = null;
		// Map<Temporal, ConclusionType> consolidatedConclusions = null;
		//
		// NavigableSet<Temporal> temporalSegments = new TreeSet<Temporal>();
		//
		// try {
		// consolidatedConclusions = abstractLiteral.getConsolidatedConclusions();
		// temporalSegments.addAll(consolidatedConclusions.keySet());
		//
		// conflictAbstractLiterals = getConflictAbstractLiterals(literal, provability, headLiterals);
		// // if (conflictAbstractLiterals.size()==0)return;
		//
		// if (conflictAbstractLiterals.size() > 0) {
		// // for
		// //
		// (Entry<ProvabilityLevel,Map<Literal,AbstractLiteral>>provabilityEntry:conflictAbstractLiterals.entrySet()){
		// Map<Literal, AbstractLiteral> provabilityEntry = conflictAbstractLiterals.get(provability);
		// if (null != provabilityEntry) {
		// for (Entry<Literal, AbstractLiteral> entry : provabilityEntry.entrySet()) {
		// derivedConclusionLiterals.remove(entry.getKey());
		// AbstractLiteral conflictAbstractLiteral = entry.getValue();
		// consolidatedConclusions = conflictAbstractLiteral.getConsolidatedConclusions();
		// temporalSegments.addAll(consolidatedConclusions.keySet());
		// }
		// }
		//
		// // }
		//
		// // for (AbstractLiteral conflictAbstractLiteral:conflictAbstractLiterals){
		// // derivedConclusionLiterals.remove(conflictAbstractLiteral.getLiteral());
		// // consolidatedConclusions=conflictAbstractLiteral.getConsolidatedConclusions();
		// // temporalSegments.addAll(consolidatedConclusions.keySet());
		// // }
		// if (PRINT_DETAILS) {
		// for (AbstractLiteral conflictAbstractLiteral : provabilityEntry.values()) {
		// System.out.printf("conflictAbstractLiteral(%s)=%s\n", literal, conflictAbstractLiteral.getLiteral());
		// }
		// }
		// } else {
		// if (PRINT_DETAILS) System.out.printf("conflictAbstractLiteral(%s)=[]\n", literal);
		// }
		// // for (Literal conflictLiteral : conflictLiterals) {
		// // AbstractLiteral conflictAbstractLiteral = getAbstractLiteral(conflictLiteral, provability, headLiterals,
		// // false);
		// //
		// // if (null == conflictAbstractLiteral) continue;
		// // newConclusionsDerived.remove(conflictLiteral);
		// //
		// // // update the set of consolidated conclusions in the abstract literal
		// // concs= conflictAbstractLiteral.getConsolidatedConclusions();
		// // temporalSegments.addAll(concs.keySet());
		// // conflictAbstractLiterals.add(conflictAbstractLiteral);
		// // }
		// System.out.printf("temporalSegments=%s\n", temporalSegments);
		//
		// // if (conflictAbstractLiterals.size() == 0) return;
		// List<Temporal> segmentedTemporals = Temporal.getTemporalSegmentsFromSet(temporalSegments);
		// System.out.printf("segmented temporals=%s\n", segmentedTemporals);
		// for (Temporal temporal : segmentedTemporals) {
		// System.out.printf("==> consolidatedConclusion(%s)=%s,%s\n", temporal, abstractLiteral.isProvable(temporal),
		// abstractLiteral.getConsolidatedConclusion(temporal));
		// }
		// // } catch (AbstractLiteralException e) {
		// // throw new LiteralDashboardException(e);
		// // } catch (TemporalException e) {
		// // throw new LiteralDashboardException(e);
		// } finally {
		// if (derivedConclusionLiterals.size() == 0) newConclusionsDerived.remove(provability);
		// }

		// try{
		// // for (Entry<ProvabilityLevel,NavigableSet<Literal>>provabilityEntry:newConclusionsDerived.entrySet()){
		// // ProvabilityLevel provability=provabilityEntry.getKey();
		// // for (Literal literal:provabilityEntry.getValue()){
		// // AbstractLiteral abstractLiteral=getAbstractLiteral(literal,provability,headLiterals,false);
		// // if (null==abstractLiteral)throw new
		// LiteralDashboardException("abstract literal of ["+literal+"] is null");
		// //
		// //
		// Map<Literal,AbstractLiteral>conflictAbstractLiterals=getConflictAbstractLiterals(literal,provability,headLiterals);
		// //
		// NavigableMap<Temporal,ConclusionType>consolidatedConclusionSet=getConclusionSet(literal,provability,consolidatedConclusions,true);
		// // // ...
		// // if (conflictAbstractLiterals.size()==0){
		// // consolidatedConclusionSet.putAll(abstractLiteral.getConsolidatedConclusions());
		// // } else{
		// // // TODO generateConsolidatedConclusions
		// // }
		// // }
		// // }
		// newConclusionsDerived.clear();
		// }catch (AbstractLiteralException e){
		// throw new LiteralDashboardException (e);
		// }
	}
	
	
	
	protected boolean hasStrongerMode(Literal l1, Literal l2) {
		return hasStrongerMode(l1.getMode(),l2.getMode());
	}
	
	protected boolean hasStrongerMode(Mode m1, Mode m2) {
		if (null==strongerModeSet)return false;
		
		String m1Name = null == m1 ? "" : m1.getName();
		String m2Name = null == m2 ? "" : m2.getName();

		if (m1Name.equals(m2Name)) return false;

		Set<String> strongerMode = strongerModeSet.get(m1Name);
		return null == strongerMode ? false : strongerMode.contains(m2Name);
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(LiteralDashboardUtilities.generateAbstractLiteralsString(headLiterals, LABEL_UNPROVED_HEAD_LITERALS, allBasicLiterals));
		sb.append("\n").append(
				LiteralDashboardUtilities.generateAbstractLiteralsString(bodyLiterals, LABEL_UNPROVED_BODY_LITERALS, allBasicLiterals));

//		if (groundedLiterals.size()>0){
//			sb.append("\n").append("-----------------\nGrounded literals\n-----------------");
//			for (Entry<ProvabilityLevel,Map<Literal,Set<Literal>>>provabilityEntry:groundedLiterals.entrySet()){
//				sb.append(NEW_LINE_INDENTATOR).append(provabilityEntry.getKey());
//				for (Entry<Literal,Set<Literal>>literalEntry:provabilityEntry.getValue().entrySet()){
//					sb.append(NEW_LINE_INDENTATOR).append(AppConst.INDENTATOR).append(literalEntry.toString());
//				}
//			}
//		}
		
		System.out.println(INDENTATOR);
		
		if (consolidatedConclusions.size()>0){
		sb.append("\n").append(	LiteralDashboardUtilities.generateConclusionString(consolidatedConclusions, LABEL_CONCLUSIONS_CONSOLIDATED, allBasicLiterals));
		}else{
		}
		
		
		if (basicLiterals.size()>0){
			sb.append("\n").append("-------------\nbasic literals\n-------------");
			for(Entry<ProvabilityLevel,Map<Literal,Set<Literal>>>literalEntry:basicLiterals.entrySet()){
				sb.append("\n").append(literalEntry.getKey());
				for (Entry<Literal,Set<Literal>>entry:literalEntry.getValue().entrySet()){
					sb.append(NEW_LINE_INDENTATOR).append(entry.getKey()).append("=").append(entry.getValue());
				}
			}
		}
		
		if (sb.length() > 0) sb.insert(0, "=========\nDashboard\n=========\n");

		return sb.toString();
	}

	// ==================================================
	// getters and setters
	// ==================================================
	public Theory getTheory() {
		return theory;
	}

	protected void setTheory(Theory theory) throws LiteralDashboardException {
		if (null == theory || theory.isEmpty()) throw new LiteralDashboardException("theory is null");
		this.theory = theory;
		this.strongerModeSet = this.theory.getStrongerModeSet();
	}

	protected Map<String, Set<String>> getStrongerModeSet() {
		return strongerModeSet;
	}

	protected Set<String> getStrongerMode(String modeName) {
		return (null == strongerModeSet || null == modeName) ? null : strongerModeSet.get(modeName.trim().toUpperCase());
	}

	protected static Temporal getTemporal(Literal literal) {
		return getTemporal(literal.getTemporal());
	}

	protected static Temporal getTemporal(Temporal temporal) {
		return null == temporal ? PERSISTENT_TEMPORAL : temporal;
	}

	public Map<ProvabilityLevel, Set<Literal>> getInitialUnprovableLiterals() {
		return initialUnprovableLiterals;
	}

	public AbstractLiteral getAbstractHeadLiteral(Literal literal, ProvabilityLevel provability) throws LiteralDashboardException {
		if (AppConst.isDeploy) throw new LiteralDashboardException(ErrorMessage.METHOD_NOT_SUPPORT_IN_DEPLOYMENT_MODE);
		return getAbstractLiteral(literal, provability, headLiterals, false);
	}

	// protected boolean isComplement(Literal l1,Literal l2){
	// return l1.isComplementTo(l2);
	// }

	// ==================================================
	// abstract methods
	// ==================================================
	protected abstract ConclusionInferencer getConclusionInferencer() throws LiteralDashboardException;
}
