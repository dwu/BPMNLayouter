package de.uni_hannover.se.BPMNLayouter2;

import java.io.File;
import java.util.HashMap;

import org.activiti.bpmn.model.BpmnModel;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.jdom2.Element;

import de.uni_hannover.se.BPMNLayouter2.layouter.SimpleGridLayouter;
import de.uni_hannover.se.BPMNLayouter2.util.Util;

public class App {

	private static boolean move = false;

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("i", true, "input file name");
		options.addOption("o", true, "output file name");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);
		if (!cmd.hasOption("i") || !cmd.hasOption("o")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("BpmnLayouter", options);
			System.exit(0);
		}

		layoutFile(cmd.getOptionValue("i"), cmd.getOptionValue("o"));
	}

	static void layoutFile(String infilename, String outfilename) throws Exception {
		String tempinfile = infilename + "copy";
		File file = new File(infilename);
		File copy = new File(tempinfile);
		FileUtils.copyFile(file, copy);

		HashMap<String, Element> extensionMap = Util.removeAndGetElementsFromXML(tempinfile, "extensionElements");
		// HashMap<String, Element> sedMap = Util.removeAndGetElementsFromXML(filePath +
		// "copy", "signalEventDefinition");

		BpmnModel model = Util.readBPMFile(copy);

		SimpleGridLayouter layouter = new SimpleGridLayouter(model);
		try {
			layouter.layoutModelToGrid(move);
		} catch (Exception e) {
			layouter = new SimpleGridLayouter(model);
			layouter.layoutModelToGrid(false);
		}
		layouter.applyGridToModel();

		Util.writeModel(model, outfilename);

		Util.addXMLElementsBackToFile(extensionMap, outfilename);
		// Util.addXMLElementsBackToFile(sedMap, name);

		copy.delete();
	}
}
