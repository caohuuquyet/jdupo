package com.jdupo.dao;

import java.io.File;
import java.util.List;

import spindle.Reasoner;
import spindle.core.dom.Conclusion;
import spindle.core.dom.Theory;
import spindle.io.IOManager;

import com.app.utils.TextUtilities;

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
public class TrustEnforcement {

	public static void main(String[] args) {
		// TODO 3
		// Policy Query
		// Translate the Policy from RDF to the SPINdle syntax.
		// Enforcement
		try {
			long startTime, endTime;
			File theoryFile = new File("./samples/pl04.dfl");
			Theory theory = IOManager.getTheory(theoryFile, null);
			Reasoner reasoner = new Reasoner();
			reasoner.loadTheory(theory);
			startTime = System.currentTimeMillis();
			reasoner.getConclusions();
			endTime = System.currentTimeMillis();

				
			System.out.print("per" + (endTime - startTime));

		} catch (Exception e) {
			//
			System.out.print("err");
		}

	}

}
