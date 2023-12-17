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

	private static void createAPdf()
	{
		try{
			document = new Document(PageSize.A4);
			String filename = "queries.pdf";
			writer = PdfWriter.getInstance(document,new FileOutputStream(filename));
		}catch (DocumentException e) {
			e.printStackTrace();
		}catch (FileNotFoundException e) {
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
		JFreeChart[] charts = new JFreeChart[]{
				createChart("Top 3 panels for the month of Oct and Nov", panels.get(0).getPanelName(),panels.get(0).getNoOfInterviews(), panels.get(1).getPanelName(), panels.get(1).getNoOfInterviews(), panels.get(2).getPanelName(), panels.get(2).getNoOfInterviews()),
				createChart("Top 3 skills for the months of Oct and Nov", skills.get(0), Integer.parseInt(freq.get(0)),skills.get(1), Integer.parseInt(freq.get(1)), skills.get(2), Integer.parseInt(freq.get(2))),
				createChart("Top 3 skills for which interviews were conducted", skills1.get(0), Integer.parseInt(freq1.get(0)),skills1.get(1), Integer.parseInt(freq1.get(1)), skills1.get(2), Integer.parseInt(freq1.get(2)))
		};

		// Calculate the height for each chart
		float chartHeight = PageSize.A4.getHeight()/ charts.length;

		//Draw each chart on a new line
		for(int i=0;i<charts.length;i++)
		{
			//Create a Pdf template with the size of the chart
			PdfContentByte contentByte = writer.getDirectContent();
			PdfTemplate template = contentByte.createTemplate(PageSize.A4.getWidth(),chartHeight);
			Graphics2D graphics2D = template.createGraphics(PageSize.A4.getWidth(), chartHeight);

			// Draw the charts on the template
			charts[i].draw(graphics2D,new Rectangle2D.Double(0,0,PageSize.A4.getWidth(), chartHeight));

			//Close the graphics object
			graphics2D.dispose();

			// Add the template to the documentat the correcct position
			contentByte.addTemplate(template,0,PageSize.A4.getHeight() - chartHeight*(i+1));
		}
		document.close();

	}
	private static JFreeChart createChart(String title, String product1, int value1, String product2, int value2, String product3, int value3) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue(product1, value1);
		dataset.setValue(product2, value2);
		dataset.setValue(product3, value3);

		JFreeChart pieChart = ChartFactory.createPieChart(
				title,
				dataset,
				true,
				true,
				false);

		PiePlot plot = (PiePlot) pieChart.getPlot();
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1} ({2})"));

		return pieChart;
	}
}
