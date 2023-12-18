package com.example.demo;

import com.example.demo.models.Interview;
import com.example.demo.models.Panel;
import com.example.demo.utils.DbOperations;
import com.example.demo.utils.DbQueries;
import com.example.demo.utils.ExcelReader;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

@SpringBootApplication
public class DemoApplication {
	static Document document;
	static PdfWriter writer;

	private static void createAPdf() {
		try {
			//PDF document with the A4 page size is created
			document = new Document(PageSize.A4);
			String filename = "queries.pdf";
			// PdfWriter instance for writing content to the PDF file.
			writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		// Reading from Excel file to Java Objects
		List<Interview> list = ExcelReader.readExcelFile();

		//inserting the Java Objects into database
		DbOperations.insertIntoDataBase(list);

		createAPdf();
		// Performing Queries
		// Query 1
		DbQueries.teamWithMaximumInterviews(document);

		//Query 2
		DbQueries.teamWithMinimumInterviews(document);

		//Query 3
		List<Panel> panels = DbQueries.top3Panels(document);

		//Query 4
		List<List<String>> top3Skills = DbQueries.top3Skills(document);

		//Query 5
		List<List<String>> top3SkillsDuringPeakTime = DbQueries.top3SkillsDuringPeakTime(document);

		//document.open();
		document.newPage();

		List<String> skills = top3Skills.get(0);
		List<String> freq = top3Skills.get(1);
		List<String> skills1 = top3SkillsDuringPeakTime.get(0);
		List<String> freq1 = top3SkillsDuringPeakTime.get(1);

		// Creating the charts
		// An array of JFreeChart objects is created.
		// Each chart is created by calling the createChart method with specific parameters.
		JFreeChart[] charts = new JFreeChart[]{
				createChart("Top 3 panels for the month of Oct and Nov", panels.get(0).getPanelName(), panels.get(0).getNoOfInterviews(), panels.get(1).getPanelName(), panels.get(1).getNoOfInterviews(), panels.get(2).getPanelName(), panels.get(2).getNoOfInterviews()),
				createChart("Top 3 skills for the months of Oct and Nov", skills.get(0), Integer.parseInt(freq.get(0)), skills.get(1), Integer.parseInt(freq.get(1)), skills.get(2), Integer.parseInt(freq.get(2))),
				createChart("Top 3 skills for which interviews were conducted", skills1.get(0), Integer.parseInt(freq1.get(0)), skills1.get(1), Integer.parseInt(freq1.get(1)), skills1.get(2), Integer.parseInt(freq1.get(2)))
		};

		//The code calculates the height available for each chart on the PDF page by dividing the total height of an A4 page by the number of charts.
		float chartHeight = PageSize.A4.getHeight() / charts.length;

		//The code iterates through each chart in the array.
		for (int i = 0; i < charts.length; i++) {
			//A PdfContentByte object is created from the PdfWriter object. This object allows you to write content to the PDF document.
			PdfContentByte contentByte = writer.getDirectContent();
			//A PdfTemplate object is created using the createTemplate method on the contentByte object. The size of the template is set to the width of an A4 page and a specified chart height.
			PdfTemplate template = contentByte.createTemplate(PageSize.A4.getWidth(), chartHeight);
			// It creates a Graphics2D object from the template and helps to draw shapes, text, and images onto the template.
			Graphics2D graphics2D = template.createGraphics(PageSize.A4.getWidth(), chartHeight);

			//The draw method is called on the current chart object, which draws the chart onto the Graphics2D object. The chart is drawn within a rectangle that has the same width as an A4 page and the specified chart height.

			charts[i].draw(graphics2D, new Rectangle2D.Double(0, 0, PageSize.A4.getWidth() / 2, chartHeight));

			//The Graphics2D object is disposed of to free up system resources.
			graphics2D.dispose();

			// The addTemplate method is called on the contentByte object to add the template (which now contains the drawn chart) to the PDF document.
			contentByte.addTemplate(template, 0, PageSize.A4.getHeight() - chartHeight * (i + 1));
		}
		document.close();

	}

	private static JFreeChart createChart(String title, String type1, int value1, String type2, int value2, String type3, int value3) {
		// It creates a DefaultPieDataset object, this object is used to store the data that will be displayed on the pie chart.
		DefaultPieDataset dataset = new DefaultPieDataset();
		// Adds the data to the pieChart.
		dataset.setValue(type1, value1);
		dataset.setValue(type2, value2);
		dataset.setValue(type3, value3);

		// creates the pieChart.
		JFreeChart pieChart = ChartFactory.createPieChart(
				title,
				dataset,
				true,
				true,
				false);

		// Retrieves the plot from the pieChart which is cast to PiePlot.
		PiePlot plot = (PiePlot) pieChart.getPlot();
		// We are setting the labels {0} represents type {1} represents the values and {2} repesents the percentage
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1} ({2})"));

		return pieChart;
	}
}