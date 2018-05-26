package com.jdupo.duc;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import spindle.Reasoner;
import spindle.core.dom.Conclusion;
import spindle.core.dom.ConclusionType;
import spindle.core.dom.Literal;
import spindle.core.dom.Theory;
import spindle.io.IOManager;

public class Usage {

	private Data data;
	private Policy policy;
	private Request request;
	private Proof proof;
	private Tracer tracer;

	public void setRequest(Request req) {
		this.request = req;

	}

	public Request getRequest() {
		return request;

	}

	public void setPolicy(Policy pol) {
		this.policy = pol;

	}

	public Policy getPolicy() {
		return policy;

	}

	public Proof processProof() {
		return proof;
	}

	public Tracer processTracer() {
		return tracer;
	}

	public Data processData() {
		return data;
	}

	public boolean processRequest() {
		boolean result = false;
		Reasoner reasoner = new Reasoner();
		try {
			Theory origTheory = policy.getTheoryPolicy();
			Theory workingTheory = origTheory.clone();

			if (null != request.getTheoryRequest())
				workingTheory.add("req", request.getTheoryRequest());

			reasoner.loadTheory(workingTheory);

			reasoner.transformTheoryToRegularForm();
			reasoner.removeDefeater();
			reasoner.removeSuperiority();
			
			List<Conclusion> conclusionList = reasoner.getConclusionsAsList();
			System.out.println("\nConclusions as list\n===================");
			for (Conclusion conclusion : conclusionList) {

				if (conclusion.getLiteral().getName().equalsIgnoreCase("ConsumerRequest")) {

					if (conclusion.getConclusionType().isPositiveConclusion()) {
						result = true;
					}					

					System.out.println(conclusion.toString());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
