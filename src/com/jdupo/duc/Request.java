package com.jdupo.duc;

import spindle.core.dom.Theory;
import spindle.io.ParserException;
import spindle.io.parser.DflTheoryParser2;
import spindle.sys.AppLogger;
import spindle.sys.Conf;

public class Request {

	Theory theoryRequest = null;

	public Request() {
		//String[] requestRules = new String[] {"CO(X),[P]SpatialScope(X,zone),[P]TemporalScope(X,weekly),[P]AggregateScope(X,statistic) =>[O] ConsumerRequest(X)" };

		String[] requestRules = new String[] {"CO(X), [P]SpatialScope(X,street),[P]TemporalScope(X,hourly), [P]AggregateScope(X,detail) =>[O] ConsumerRequest(X)" };

		
		try {
			theoryRequest = generateTheory(requestRules, Conf.getLogger(""));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected static Theory generateTheory(String[] theory, AppLogger logger) throws ParserException {
		StringBuilder sb = new StringBuilder();
		for (String rule : theory) {
			sb.append(rule).append("\n");
		}
		return generateTheory(sb.toString(), logger);
	}

	protected static Theory generateTheory(String theory, AppLogger logger) throws ParserException {
		return DflTheoryParser2.getTheory(theory, logger);
	}

	public Theory getTheoryRequest() {
		return theoryRequest;
	}

	public void setTheoryRequest(Theory tr) {
		this.theoryRequest = tr;
	}

}
