package com.jdupo.duc;

public class Procedure {

	public static void main(String[] args) {

		Data data = new Data();
		Policy policy = new Policy();		
		Request request = new Request();
		
		Usage usage = new Usage();
		Tracer tracer = new Tracer();
		Proof proof = new Proof();
		
		usage.setRequest(request);
		usage.setPolicy(policy);
		
		boolean result = usage.processRequest();

		if (result) {
			System.out.println("return data");
			data = usage.processData();
			tracer = usage.processTracer();
		} else {
			System.out.println("proof");
			proof = usage.processProof();
		}
		
		//System.out.print(data);

	}

}
