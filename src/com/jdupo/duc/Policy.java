package com.jdupo.duc;

import java.io.File;
import spindle.core.dom.Theory;
import spindle.io.IOManager;

public class Policy {
	
	Theory theoryPolicy;
	
	public Policy(){		
		// defeasible theory saved in file
		File theoryFile = new File("./samples/dupo.dfl");
		System.out.println(theoryFile.getAbsolutePath());
		
		try {
			// load defeasible theory from a file using spindle.io.IOManager
			theoryPolicy = IOManager.getTheory(theoryFile, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public Theory getTheoryPolicy(){		
		return theoryPolicy;
	}
	
	
	public void setTheoryPolicy(Theory tp){
		this.theoryPolicy = tp;
	}

}

//TODO: Translate the license from RDF to the SPINdle syntax

/*RDF license:
 :licCC-BY-NC-ND a cc:License;
 cc:requires cc:Attribution;
 cc:requires cc:Notice;
 cc:permits cc:Distribution;
 cc:permits cc:Reproduction;
 cc:prohibits cc:CommercialUse.

 ! SPINDle translation:
 r1: =>[O] Attribution
 r2: =>[O] Notice
 r3: =>[-O] -Distribution
 r4: =>[-O] -Reproduction
 r5: =>[O] -CommercialUse
 */
