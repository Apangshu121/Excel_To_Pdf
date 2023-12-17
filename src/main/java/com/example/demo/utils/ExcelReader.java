package com.example.demo.utils;
import java.io.File;
import java.io.FileInputStream;

import com.example.demo.models.Interview;
import org.apache.poi.ss.usermodel.DateUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
   static List<Interview> al = new ArrayList<>();

    public static List<Interview> readExcelFile() {
        // Try block to check for exceptions
        try {

            // Reading file from local directory
            FileInputStream file = new FileInputStream(
                    new File("src/main/java/resources/Accolite Interview Data - Q4 2023 - Grad Program November 2023.xlsx"));

            // Create Workbook instance holding reference to
            // .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            // Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            // Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            // Till there is an element condition holds true
            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();

                if(row.getRowNum()==0)
                    continue;

                // For each row, iterate through all the
                // columns
                Iterator<Cell> cellIterator
                        = row.cellIterator();

                Interview i=new Interview();

                while (cellIterator.hasNext()) {

                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();

                    switch (columnIndex) {
                        case 0:
                            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                                // System.out.print(cell.getDateCellValue()+"\t");
                                i.setDate(cell.getDateCellValue());
                               // System.out.println("1");
                            } else {
                                continue;
                            }
                            break;
                        case 2:
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                                i.setTeam(cell.getStringCellValue());
                            else
                                continue;
                            break;
                        case 3:
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                                i.setPanelName(cell.getStringCellValue());
                            else
                                continue;
                            break;
                        case 4:
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                                i.setRound(cell.getStringCellValue());
                            else
                                continue;
                            break;
                        case 5:
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                                i.setSkill(cell.getStringCellValue());
                            else
                                continue;
                            break;
                        case 6:
                            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                                // System.out.print(cell.getDateCellValue()+"\t");
                                i.setTime(cell.getDateCellValue());
                            } else {
                                continue;
                            }
                            break;
                        case 7:
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                                i.setCandidateCurrentLoc(cell.getStringCellValue());
                            else
                                continue;
                            break;
                        case 8:
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                                i.setPreferredLocation(cell.getStringCellValue());
                            else
                                continue;
                            break;
                        case 9:
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING)
                                i.setCandidateName(cell.getStringCellValue());
                            else
                                continue;
                            break;
                    }
                }
                al.add(i);
            }

            al = al.stream().filter(interview -> interview.getDate()!=null && interview.getTeam()!=null && interview.getPanelName()!=null && interview.getRound()!=null && interview.getSkill()!=null && interview.getTime()!=null && interview.getCandidateCurrentLoc()!=null && interview.getPreferredLocation()!=null && interview.getCandidateName()!=null).collect(Collectors.toCollection(ArrayList::new));

            file.close();
        }

        // Catch block to handle exceptions
        catch (Exception e) {

            // Display the exception along with line number
            // using printStackTrace() method
            e.printStackTrace();
        }
        return al;
    }
}
