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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import spindle.core.dom.Conclusion;
import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.ProvabilityLevel;
import spindle.core.dom.Theory;
import spindle.engine.tdl2.impl.BasicConclusionInferencer;
import spindle.sys.AppLogger;

public class LiteralDashboard extends LiteralDashboardBase {
	private ConclusionInferencer conclusionInferencer;

	public LiteralDashboard(Theory theory) throws LiteralDashboardException {
		this(theory, null);
	}

	public LiteralDashboard(Theory theory, AppLogger appLogger) throws LiteralDashboardException {
		super(theory, appLogger);
		// setAppLogger(appLogger);
		logMessage(Level.INFO, 0, "============\nInput theory\n============\n" + theory.toString());
		logMessage(Level.INFO, 0, "==============\nDashboard info\n==============\n" + toString());

		StringBuilder sb = new StringBuilder();
		Map<ProvabilityLevel, Set<Literal>> initialUnprovableLiterals = getInitialUnprovableLiterals();
		for (Entry<ProvabilityLevel, Set<Literal>> provabilityEntry : initialUnprovableLiterals.entrySet()) {
			ProvabilityLevel provability = provabilityEntry.getKey();
			sb.append("\n");
			sb.append(provability);
			for (Literal literal : provabilityEntry.getValue()) {
				sb.append("\n").append(INDENTATOR).append(literal);
			}
		}
		if (sb.length() > 0) logMessage(Level.INFO, 0, "initial unprovable literals", sb.toString());
	}

	public boolean isProvable(Literal literal, ProvabilityLevel provability) throws LiteralDashboardException {
		return isProvable(literal,  provability,literal.getTemporal());
	}
	


//	private boolean isProvable(Literal literal, Temporal interval, ProvabilityLevel provability) throws LiteralDashboardException {
//		Temporal provableInterval = getProvableTemporalSegment(literal,  provability,interval);
//		return null == provableInterval ? false : provableInterval.contains(getTemporal(interval));
//	}

//	/**
//	 * Return the set of preceeding temporal segments that are unproved.
//	 * 
//	 * @param literal Literal to be checked.
//	 * @param provability Provability level.
//	 * @param includeSameStart Decide whether to include temporal segments with the same start.
//	 * @return <code>null</code> - if all temporal segments of the literal has been proved;</br> <code>empty</code> - if
//	 *         all preceeding segments are proved.
//	 * @throws LiteralDashboardException
//	 */
//	public NavigableSet<Temporal> getUnprovedPreceedingTemporalSegments(Literal literal, ProvabilityLevel provability,
//			boolean includeSameStart) throws LiteralDashboardException {
//		Temporal provableInterval = getProvableTemporalSegment(literal,  provability,literal.getTemporal());
//		if (null == provableInterval) return null;
//
//		NavigableSet<Temporal> unprovedIntervals = getUnprovedHeadIntervals(literal, provability);
//		if (null == unprovedIntervals) return null;
//
//		Temporal literalTemporal = getTemporal(literal);
//		NavigableSet<Temporal> preceedingSet = new TreeSet<Temporal>();
//		for (Temporal temporal : unprovedIntervals) {
//			if (provableInterval.startAfter(temporal)) continue;
//			if (temporal.startAfter(literalTemporal)) break;
//			if (literalTemporal.sameStart(temporal)) {
//				if (includeSameStart) preceedingSet.add(temporal);
//			} else {
//				preceedingSet.add(temporal);
//			}
//		}
//		return preceedingSet;
//	}

//	public NavigableSet<Temporal> getUnprovedSucceedingTemporalSegments(Literal literal, ProvabilityLevel provability,
//			boolean includeSameStart) throws LiteralDashboardException {
//		Temporal provableInterval = getProvableTemporalSegment(literal, provability, literal.getTemporal());
//		if (null == provableInterval) return null;
//
//		NavigableSet<Temporal> unprovedIntervals = getUnprovedHeadIntervals(literal, provability);
//		if (null == unprovedIntervals) return null;
//
//		Temporal literalTemporal = getTemporal(literal);
//		NavigableSet<Temporal> succeedingSet = new TreeSet<Temporal>();
//		for (Temporal temporal : unprovedIntervals) {
//			if (temporal.startBefore(literalTemporal)) continue;
//			if (temporal.getStartTime() >= provableInterval.getEndTime()) break;
//			if (literalTemporal.sameStart(temporal)) {
//				if (includeSameStart) succeedingSet.add(temporal);
//			} else {
//				succeedingSet.add(temporal);
//			}
//		}
//		return succeedingSet;
//	}
//	
	
@Override
	protected ConclusionInferencer getConclusionInferencer() throws LiteralDashboardException {
		try {
			if (null == conclusionInferencer) {
				conclusionInferencer = new BasicConclusionInferencer(this);
				conclusionInferencer.setAppLogger(logger);				
			}
			return conclusionInferencer;
		} catch (Exception e) {
			throw new LiteralDashboardException(e);
		}
	}

//	protected Set<Conclusion> generateNewBodyLiteralConclusions(Literal literal, ConclusionType conclusionType) throws LiteralDashboardException {
//		ProvabilityLevel provability=conclusionType.getProvabilityLevel();
//		boolean includeSameStart=conclusionType.isNegativeConclusion();
//
//		
//		// TODO add code for conclusion generation
//		//consolidateConclusion(literal, provability, getConclusionUpdater());
//		//Set<Conclusion> conclusionsGenerated = super.consolidateConclusion(literal, provability, getConclusionUpdater());
//		 Set<Conclusion> conclusionsGenerated=new TreeSet<Conclusion>();
//		Temporal literalTemporal = getTemporal(literal);
//		long temporalStart = literalTemporal.getStartTime();
//		long temporalEnd = literalTemporal.getEndTime();
//
//		NavigableSet<Temporal> unprovedSucceedingIntervals = getUnprovedSucceedingTemporalSegments(literal, provability, includeSameStart);
//		if (null != unprovedSucceedingIntervals) {
//			for (Temporal succeedingTemporal : unprovedSucceedingIntervals) {
//				if (temporalEnd > succeedingTemporal.getStartTime()) {
//					temporalEnd = succeedingTemporal.getStartTime();
//					break;
//				}
//			}
//
//			Temporal provableInterval=null;
//
//			logMessage(Level.INFO,2,"consolidateConclusion(" + literal + ").conclusionType="+conclusionType+" ("+conclusionType.isPositiveConclusion()+")");
//			if (conclusionType.isPositiveConclusion()){
//				provableInterval=new Temporal(temporalStart,temporalEnd);
//			} else{
//				logMessage(Level.INFO,2,"consolidateConclusion(" + literal + ").start,end="+temporalStart+","+temporalEnd);
//				if (temporalStart!=temporalEnd){
//					provableInterval=new Temporal(temporalStart,temporalEnd);
//					
//					NavigableSet<Temporal>unprovableSet=getUnprovedBodyIntervals(literal,provability);
//					logMessage(Level.INFO,2,"consolidateConclusion(" + literal + "," + provability + ").unprovableSet="+unprovableSet);
//					for (Temporal temporal:unprovableSet){
//						logMessage(Level.INFO,2,"verifying temporal(" + temporal + ")="+temporal);
//						if (temporal.overlap(provableInterval)){
//						Literal newL=literal.cloneWithTemporal(temporal);
//						conclusionsGenerated.add(new Conclusion(conclusionType,newL));
//						}
//					}
//				}
//			}
//			logMessage(Level.INFO,2,"consolidateConclusion(" + literal + "," + provability + ").provableInterval="+provableInterval);
//		}
//		logMessage(Level.INFO, 1, "consolidateConclusion.Conclusions generated", conclusionsGenerated);
//		return conclusionsGenerated;
//	}

	// public List<Conclusion>generateInitialConclusions(){
	// List<Conclusion>conclusions=new Vector<Conclusion>();
	// for (Entry<Literal,SortedSet<Temporal>>entry:bodyLiterals.entrySet()){
	// Literal literal=entry.getKey();
	// for (Temporal temporal:entry.getValue()){
	// if (!isProvable(literal,temporal)) {
	// Literal nLiteral=literal.clone();
	// nLiteral.setTemporal(temporal);
	// conclusions.add(new Conclusions(ConclsuionType.))
	// }
	// }
	// }
	// return conclusions;
	// }

	public Set<Conclusion> addConclusionDerived(Conclusion conclusion,String ruleLabel) throws LiteralDashboardException {
		// System.out.println("Update conclusion: " + conclusion);
		Literal literal = conclusion.getLiteral();
		ConclusionType conclusionType = conclusion.getConclusionType();
		//ProvabilityLevel provability = conclusionType.getProvabilityLevel();

//		boolean isHeadLiteral = isHeadLiteral(literal, conclusionType);
	//	logMessage(Level.INFO, 0, "[" + literal + ","+conclusionType+"], isHeadLiteral=" + isHeadLiteral);
		//if (!isHeadLiteral)return null;
		
//		boolean hasHeadTemporalRemoved = removeUnprovedHeadLiteral(literal, provability);
	//	logMessage(Level.INFO, 0, "[" + literal + "], hasHeadTemporalRemoved=" + hasHeadTemporalRemoved);
		
		
	if(	super.addConclusionDerived(literal,conclusionType,ruleLabel)){
		ProvabilityLevel provability=conclusionType.getProvabilityLevel();
		
	//	removeUnprovedHeadLiteral(literal, provability);	
		
		// TODO consolidate conclusions accordingly
	//	consolidateConclusion(literal,provability);
	}

//		// if no item has been removed from the unproved set,
//		// then the literal is either not appear in the rule head
//		// or has already been proved, which require no further operations.
//		//if (!hasHeadTemporalRemoved) return null;
//
//		NavigableMap<Temporal, ConclusionType> conclusionSet = getConclusionSet(literal, provability, true);
//
//		conclusionSet.put(getTemporal(literal), conclusionType);
//
//		// TODO add code
//		Set<Conclusion> conclusionsGenerated = null;
//		if (hasHeadTemporalRemoved) {
//			logMessage(Level.INFO, 1, "head literal updated - update conclusions from rule body");
//			consolidateConclusion(literal, provability, getConclusionUpdater());
//			conclusionsGenerated = generateNewBodyLiteralConclusions(literal,conclusionType);
//		}
//		// getConclusionUpdater().updateConclusions(unprovedBodyLiterals);
//
//		// Set<Conclusion> conclusionsGenerated = new TreeSet<Conclusion>();
//
//		return conclusionsGenerated;
		return null;
	}

}
