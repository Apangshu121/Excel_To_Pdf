package com.example.demo.utils;

import com.example.demo.models.Panel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DbQueries{

    public static void teamWithMaximumInterviews(Document document)
    {
        String team = null;
        int count = 0;
        try(Connection conn = DbConnection.getInstance().getConnection())
        {
            String sql = "SELECT team,COUNT(*) AS count FROM interviews "+
                         "WHERE dateOfInterview LIKE 'Oct%' OR dateOfInterview LIKE 'Nov%' "+
                         "GROUP BY team "+
                         "ORDER BY count DESC "+
                         "LIMIT 1";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                team = rs.getString("team");
                count = rs.getInt("count");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        String s = "1.Team with the most interviews in the month of Oct and Nov is: "+team+"("+count+" interviews)";

        try {
            document.open();
            Paragraph para = new Paragraph(s);
            document.add(para);
            document.add(new Paragraph(new Chunk("\n")));
            document.add(new Paragraph(new Chunk("\n")));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    public static void teamWithMinimumInterviews(Document document)
    {
        String team = null;
        int count = 0;
        try(Connection conn = DbConnection.getInstance().getConnection())
        {
            String sql = "SELECT team,COUNT(*) AS count FROM interviews "+
                         "WHERE dateOfInterview LIKE 'Oct%' OR dateOfInterview LIKE 'Nov%' "+
                         "GROUP BY team "+
                         "ORDER BY count "+
                         "LIMIT 1";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                team = rs.getString("team");
                count = rs.getInt("count");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        String s = "2.Team with the least interviews in the month of Oct and Nov is: "+team+"("+count+" interviews)";

        try {
            document.open();
            Paragraph para = new Paragraph(s);
            document.add(para);
            document.add(new Paragraph(new Chunk("\n")));
            document.add(new Paragraph(new Chunk("\n")));

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static List<Panel> top3Panels(Document document)
    {
        List<Panel> top3Panels;
        Map<String,Integer> panels = new HashMap<>();

        try(Connection conn = DbConnection.getInstance().getConnection())
        {
            String sql = "SELECT panelName,COUNT(*) AS count FROM interviews "+
                    "WHERE dateOfInterview LIKE 'Oct%' OR dateOfInterview LIKE 'Nov%' "+
                    "GROUP BY panelName";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                panels.put(rs.getString("panelName"), panels.getOrDefault(rs.getString("panelName"),0)+ rs.getInt("count"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        top3Panels=panels.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue().reversed()).limit(3).map(entry->new Panel(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        System.out.println();

        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(50);
        PdfPCell c1 = new PdfPCell(new Phrase("Panels"));
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase("No. of Interviews"));
        table.addCell(c2);
        table.setHeaderRows(3);

        for (Panel p:top3Panels)
        {
            table.addCell(p.getPanelName());
            table.addCell(Integer.toString(p.getNoOfInterviews()));
        }

        try{
            document.add(new Paragraph("3.Top 3 panels during Oct-Nov 2023:"));
            document.add(new Paragraph(new Chunk("\n")));
            document.add(table);
            document.add(new Paragraph(new Chunk("\n")));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        return top3Panels;
    }

    public static List<List<String>> top3Skills(Document document)
    {
        List<String> skills = new ArrayList<>();
        List<String> freq = new ArrayList<>();
        try(Connection conn = DbConnection.getInstance().getConnection())
        {
            String createViewSql = "CREATE OR REPLACE VIEW top3Skills AS("+
                                   "SELECT skill,COUNT(*) as count "+
                                   "from interviews "+
                                   "group by skill)";

            PreparedStatement createViewStmt = conn.prepareStatement(createViewSql);
            createViewStmt.executeUpdate();

            String querySql = "SELECT * FROM top3Skills ORDER BY COUNT DESC LIMIT 3";
            PreparedStatement querySqlStmt = conn.prepareStatement(querySql);
            ResultSet rs = querySqlStmt.executeQuery();

            while(rs.next()){
                skills.add(rs.getString("skill"));
                freq.add(Integer.toString(rs.getInt("count")));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(50);
        PdfPCell c1 = new PdfPCell(new Phrase("Skills"));
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase("Frequency"));
        table.addCell(c2);
        table.setHeaderRows(3);

        for (int i=0;i<skills.size();i++)
        {
            table.addCell(skills.get(i));
            table.addCell(freq.get(i));
        }

        try{
            document.add(new Paragraph("4.Top 3 skills during Oct-Nov 2023:"));
            document.add(new Paragraph(new Chunk("\n")));
            document.add(table);
            document.add(new Paragraph(new Chunk("\n")));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        List<List<String>> res = new ArrayList<>();
        res.add(skills);
        res.add(freq);

        return res;
    }

    public static List<List<String>> top3SkillsDuringPeakTime(Document document)
    {
        List<String> skills = new ArrayList<>();
        List<String> freq = new ArrayList<>();
        try(Connection conn = DbConnection.getInstance().getConnection())
        {
            String sql = "SELECT skill, COUNT(*) as count " +
                         "from interviews "+
                         "WHERE STR_TO_DATE(time,'%H:%i:%s') BETWEEN STR_TO_DATE('09:00:00','%H:%i:%s') AND STR_TO_DATE('17:00:00','%H:%i:%s') "+
                         "GROUP BY skill "+
                         "ORDER BY count DESC "+
                         "LIMIT 3";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();


            while(rs.next()){
                skills.add(rs.getString("skill"));
                freq.add(Integer.toString(rs.getInt("count")));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(50);
        PdfPCell c1 = new PdfPCell(new Phrase("Skills"));
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase("Frequency"));
        table.addCell(c2);
        table.setHeaderRows(3);

        for (int i=0;i<skills.size();i++)
        {
            table.addCell(skills.get(i));
            table.addCell(freq.get(i));
        }

        try{
            document.add(new Paragraph("5.Top 3 skills during Peak Time:"));
            document.add(new Paragraph(new Chunk("\n")));
            document.add(table);
            document.add(new Paragraph(new Chunk("\n")));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        List<List<String>> res = new ArrayList<>();
        res.add(skills);
        res.add(freq);

        return res;
    }
}
