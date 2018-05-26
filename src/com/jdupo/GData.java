/**
 * jDUPO 1.1
 */
package com.jdupo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


/**
 * steps:
 * 
 */
public class GData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			// create an print writer for writing to a file
			int f = 150;
			int n = 10;
			PrintStream out = null;

			for (int i = 1; i <= f; i++) {

				out = new PrintStream(new FileOutputStream("./samples/dataset/ds" + i + ".dfl"));
				System.setOut(out);
				
				out.println(">> CO(X)");
				out.println(">> DO(X)");
				out.println(">> MA(X)");

				// facts
				for (int j = 1; j <= n*i; j++) {
					out.println(">> CO" + i + "_" + j + "(X)");
					out.println(">> DO" + i + "_" + j + "(X)");
					out.println(">> MA" + i + "_" + j + "(X)");
				}

				// rules
				out.println("rd1: DO(X) =>[P] TemporalScope(X,any)");
				out.println("rd2: DO(X) =>[P] SpatialScope(X,any)");
				out.println("rd3: DO(X) =>[P] AggregateScope(X,any)");
				out.println("rd4: DO(X) =>[P] PurposeScope(X,any)");

				out.println("rm1: MA(X) =>[P] SpatialScope(X,street)");
				out.println("rm3: MA(X) =>[P] TemporalScope(X,hourly)");
				out.println("rm5: MA(X) =>[P] AggregateScope(X,average)");

				out.println("rc1: CO(X) =>[P] SpatialScope(X,zone)");
				out.println("rc3: CO(X) =>[P] TemporalScope(X,weekly)");
				out.println("rc5: CO(X) =>[P] AggregateScope(X,statistic)");
				
				for (int j = 1; j <= n*i; j++) {
					out.println("rd1" + i + "_" + j + ": DO" + i + "_" + j + "(X) =>[P] TemporalScope(X,any)");
					out.println("rd2" + i + "_" + j + ": DO" + i + "_" + j + "(X) =>[P] SpatialScope(X,any)");
					out.println("rd3" + i + "_" + j + ": DO" + i + "_" + j + "(X) =>[P] AggregateScope(X,any)");
					out.println("rd4" + i + "_" + j + ": DO" + i + "_" + j + "(X) =>[P] PurposeScope(X,any)");

					out.println("rm1" + i + "_" + j + ": MA" + i + "_" + j + "(X) =>[P] SpatialScope(X,street)");
					out.println("rm3" + i + "_" + j + ": MA" + i + "_" + j + "(X) =>[P] TemporalScope(X,hourly)");
					out.println("rm5" + i + "_" + j + ": MA" + i + "_" + j + "(X) =>[P] AggregateScope(X,average)");
					
				}
				// request
				out.println(
						"request: MA(X),[P]SpatialScope(X,street),[P]TemporalScope(X,hourly),[P]AggregateScope(X,average) =>[O] ConsumerRequest(X)");

			}//end of file i

			
			// close the file (VERY IMPORTANT!)
			out.close();
			
		} catch (IOException e1) {
			System.out.println("Error during reading/writing");
		}

	}

}
