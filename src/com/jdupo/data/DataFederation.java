package com.jdupo.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.app.utils.Converter;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import com.hp.hpl.jena.rdf.model.RDFNode;

import virtuoso.jena.driver.*;

public class DataFederation {

	public String queryData() {
		String responseData = "Processing Data";
		return responseData;

	}

	public String queryDataDB() {

		VirtGraph graph = new VirtGraph("http://localhost:8890/aarhus_parking.ttl#", "jdbc:virtuoso://localhost:1111",
				"dba", "dba");

		// parking place, monthly, sum
		String qr1 = "prefix tl: <http://purl.org/NET/c4dm/timeline.owl#>"
				+ "prefix sao: <http://iot.ee.surrey.ac.uk/citypulse/resources/ontologies/sao.ttl#>"
				+ "prefix ct: <http://www.insight-centre.org/citytraffic#>"
				+ "prefix ns1: <http://purl.oclc.org/NET/ssnx/ssn#>" + "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "SELECT ?latitude ?longitude ?time (sum(xsd:integer(?m)) as ?sum)" + "WHERE {"
				+ "?observation sao:value ?m ." + "?observation sao:time ?t ." + "?t tl:at ?time1. "
				+ " bind(MONTH(?time1) as ?time). " + "?observation ns1:featureOfInterest ?fi ."
				+ "?fi a sao:FeatureOfInterest ." + "?fi ct:hasFirstNode ?v ." + "?v ct:hasLatitude ?latitude ."
				+ "?v ct:hasLongitude ?longitude . " + "} " + "GROUP BY ?latitude ?longitude ?time "
				+ "ORDER BY ?latitude ?time ";

		// parking place, hourly, detail
		String qr2 = "prefix tl: <http://purl.org/NET/c4dm/timeline.owl#>"
				+ "prefix sao: <http://iot.ee.surrey.ac.uk/citypulse/resources/ontologies/sao.ttl#>"
				+ "prefix ct: <http://www.insight-centre.org/citytraffic#>"
				+ "prefix ns1: <http://purl.oclc.org/NET/ssnx/ssn#>" + "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"

				+ "SELECT ?latitude ?longitude ?time (xsd:integer(?m) as ?sum) " + "WHERE {"
				+ "?observation sao:value ?m ." + "?observation sao:time ?t ." + "?t tl:at ?time1. "
				+ "bind(xsd:dateTime(?time1) as ?time). " + "?observation ns1:featureOfInterest ?fi ."
				+ "?fi a sao:FeatureOfInterest ." + "?fi ct:hasFirstNode ?v ." + "?v ct:hasLatitude ?latitude ."
				+ "?v ct:hasLongitude ?longitude . " + "} " + "ORDER BY ?latitude ?time ";

		// parking place, daily, AVG
		String qr3 = "prefix tl: <http://purl.org/NET/c4dm/timeline.owl#>"
				+ "prefix sao: <http://iot.ee.surrey.ac.uk/citypulse/resources/ontologies/sao.ttl#>"
				+ "prefix ct: <http://www.insight-centre.org/citytraffic#>"
				+ "prefix ns1: <http://purl.oclc.org/NET/ssnx/ssn#>" + "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"

				+ "SELECT ?latitude ?longitude ?time (AVG(xsd:integer(?m)) as ?sum) " + "WHERE {"
				+ "?observation sao:value ?m ." + "?observation sao:time ?t ." + "?t tl:at ?time1. "
				+ "bind(year(?time1) month(?time1) day(?time1) hours(?time1) as ?time). "
				+ "?observation ns1:featureOfInterest ?fi ." + "?fi a sao:FeatureOfInterest ."
				+ "?fi ct:hasFirstNode ?v ." + "?v ct:hasLatitude ?latitude ." + "?v ct:hasLongitude ?longitude . "
				+ "} " + "GROUP BY ?latitude ?longitude ?time " + "ORDER BY ?latitude ?time ";

		String qr4 = "prefix tl: <http://purl.org/NET/c4dm/timeline.owl#>"
				+ "prefix sao: <http://iot.ee.surrey.ac.uk/citypulse/resources/ontologies/sao.ttl#>"
				+ "prefix ct: <http://www.insight-centre.org/citytraffic#>"
				+ "prefix ns1: <http://purl.oclc.org/NET/ssnx/ssn#>" + "prefix xsd: <http://www.w3.org/2001/XMLSchema#>"

				+ "SELECT ?latitude ?longitude ?time (AVG(xsd:integer(?m)) as ?sum) " + "WHERE {"
				+ "?observation sao:value ?m ." + "?observation sao:time ?t ." + "?t tl:at ?time1. "
				+ "bind(CONCAT(STR(year(?time1)), " + " '-', " + "  STR(month(?time1)), " + "   '-', "
				+ "  STR(day(?time1)), " + "':',  STR(hours(?time1))) as ?time). "
				+ "?observation ns1:featureOfInterest ?fi ." + "?fi a sao:FeatureOfInterest ."
				+ "?fi ct:hasFirstNode ?v ." + "?v ct:hasLatitude ?latitude ." + "?v ct:hasLongitude ?longitude . "
				+ "} " + "GROUP BY ?latitude ?longitude ?time " + "ORDER BY ?latitude ?time ";

		long startTime, endTime;
		String responseData = "";

		StringBuffer sb = new StringBuffer();

		startTime = System.currentTimeMillis();

		Query sparql = QueryFactory.create(qr4);

		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, graph);

		ResultSet results = vqe.execSelect();
		int count = 0;
		while (results.hasNext()) {
			QuerySolution result = results.nextSolution();
			count++;
			RDFNode graph_name = result.get("graph");
			RDFNode la = result.get("latitude");
			RDFNode lo = result.get("longitude");
			RDFNode ti = result.get("time");
			RDFNode su = result.get("sum");

			sb.append(graph_name + "_" + count + " { " + la + " " + lo + " " + ti + " " + su + " . }");
			// System.out.println(graph_name + " { " + la + " " + lo + " " + ti
			// + " " + su + " . }");
		}

		endTime = System.currentTimeMillis();

		// System.out.println("count/graph.getCount() = " + count + "/" +
		// graph.getCount());

		responseData = "The returned triples/Total = " + count + "/" + graph.getCount() + " : " + sb.toString();

		// System.out.println("time used=" + Converter.long2TimeString(endTime -
		// startTime));

		// closeconnection
		graph.close();

		return responseData;

	}

	public static void main(String[] args) {

		DataFederation df = new DataFederation();
		System.out.println(df.queryData());

	}

}
