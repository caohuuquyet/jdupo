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
package spindle.engine.tdl2.impl;


import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeSet;

import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.ProvabilityLevel;
import spindle.core.dom.Temporal;
import spindle.engine.tdl2.AbstractLiteral;
import spindle.engine.tdl2.AbstractLiteralException;
import spindle.engine.tdl2.LiteralDashboard;
import spindle.engine.tdl2.ConclusionInferencer;
import spindle.engine.tdl2.ConclusionInferencerException;

public class BasicConclusionInferencer extends ConclusionInferencer {

	public BasicConclusionInferencer(LiteralDashboard dashboard) throws ConclusionInferencerException {
		super(dashboard);		
	}
	


//	public  Map<Literal, NavigableMap<Temporal, ConclusionType>>  updateConclusions(Literal literal,			
//			 Map<Literal, NavigableSet<Temporal>> unprovedHeadLiterals,
//			 Map<Literal, NavigableSet<Temporal>> unprovedBodyLiterals, //
//			Map<Literal, NavigableMap<Temporal, ConclusionType>> conclusions) {
//		// TODO update conclusion
//		// TODO should also include conclusions due to conflict modal operators
//		logMessage(Level.INFO, 1, getClass().getCanonicalName()+ ".updateCocnlusions", literal,conclusions.get(literal));
//		
//		Map<Literal,NavigableMap<Temporal, ConclusionType>>  conclusionsUpdated= new TreeMap<Literal,NavigableMap<Temporal, ConclusionType>>(new LiteralComparator(
//				false));
//		
////		NavigableMap<Temporal, ConclusionType> conclusionSet = getConclusionSet(literal, provability, conclusions);
////		
////		// do nothing if there is no or only 1 conclusion available in the conclusion set
////		if (null==conclusionSet||conclusionSet.size() < 2) return;
////
////		NavigableMap<Temporal, ConclusionType> updatedConclusions = new TreeMap<Temporal, ConclusionType>(new TemporalComparator());
////
////		Temporal preceedingTemporal = null;
////		ConclusionType preceedingConclusionType = null;
////
////		for (Entry<Temporal, ConclusionType> entry : conclusionSet.entrySet()) {
////			if (null == preceedingTemporal) {
////				preceedingTemporal = entry.getKey();
////				preceedingConclusionType = entry.getValue();
////			} else {
////				Temporal currTemporal = entry.getKey();
////				ConclusionType currConclusionType = entry.getValue();
////				if (preceedingTemporal.overlap(entry.getKey())) {
////					if (isSameSide(preceedingConclusionType,currConclusionType)){
////						if (currTemporal.endAfter(preceedingTemporal))preceedingTemporal.setEndTime(currTemporal.getEndTime());
////					} else{
////						if (preceedingTemporal.sameStart(currTemporal)){
////							preceedingConclusionType=ProvabilityLevel.getPositiveConclusionType(preceedingConclusionType.getProvabilityLevel());
////						} else{
////
////						}
////						
////						preceedingTemporal.setEndTime(currTemporal.getStartTime());
////						updatedConclusions.put(preceedingTemporal,preceedingConclusionType);
////
////						preceedingTemporal=currTemporal;
////						preceedingConclusionType=currConclusionType;
////						
////					}
////				} else {
////					updatedConclusions.put(preceedingTemporal,preceedingConclusionType);
////					preceedingTemporal=currTemporal;
////					preceedingConclusionType=currConclusionType;
////				}
////			}
////
////		}
////		
////		if (updatedConclusions.size()==0){System.out.println("no updated conclusions are found!");}else{
////for (Entry<Temporal,ConclusionType>entry:updatedConclusions.entrySet()){
////	System.out.println("updatedConclusion:"+entry.getKey()+":"+entry.getValue());
////}}
//
//	///	conclusionSet.clear();
//	//	conclusionSet.putAll(updatedConclusions);
//		// return conclusionSet;
//return conclusionsUpdated;
//	}

	
	
	
	

	@Override
	protected Map<Literal,NavigableMap<Temporal,ConclusionType>> consolidateConflictConclusions(AbstractLiteral literal, ProvabilityLevel provability, Map<ProvabilityLevel, Set< AbstractLiteral>> conflictLiterals)
			throws ConclusionInferencerException {
		Set<Temporal>temporals=new TreeSet<Temporal>();
		try {
			
			switch(provability){
			case DEFEASIBLE:
temporals.addAll(				getConsolidatedConclusionsTemporal(conflictLiterals.get(ProvabilityLevel.DEFEASIBLE)));
			case DEFINITE:
				temporals.addAll(				getConsolidatedConclusionsTemporal(conflictLiterals.get(ProvabilityLevel.DEFINITE)));
				break;
				default:
			}

		} catch (AbstractLiteralException e) {
			throw new ConclusionInferencerException(e);
		}
		
return null;
	}

//	private NavigableMap<Temporal, ConclusionType> getConclusionSet(Literal literal, ProvabilityLevel provability, //
//			Map<Literal, NavigableMap<Temporal, ConclusionType>> conclusions) {
//		Map<Literal, NavigableMap<Temporal, ConclusionType>> provabilitySet = conclusions.get(provability);
	//	if (null==provabilitySet)return null;
		//return provabilitySet.get(literal);
//	}

//	private void updateConclusion(Literal literal, ProvabilityLevel provability) {
//
//	}

}
