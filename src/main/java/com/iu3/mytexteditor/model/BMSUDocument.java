/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iu3.mytexteditor.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 *
 * @author aarapov
 */
public class BMSUDocument {

    private final String path;
    private final String type;
    private InputStream stream;
    private String text;
    private List<String[]> tables;
    private Boolean isWorkTable;

    public BMSUDocument(String path) {
        tables = new ArrayList<>();

        if (path == null) {
            throw new NullPointerException();
        } else {
            this.path = path;
            String[] split = path.split("\\.");
            this.type = split[split.length - 1];
            try {
                this.stream = new FileInputStream(path);
                //get file type name aka doc docs etc.
                switch (this.type) {
                    case "doc":
                        HWPFDocument doc = new HWPFDocument(stream);
                        this.text = doc.getDocumentText();
                        this.isWorkTable = false;
                        break;
                    case "docx":
                        XWPFDocument docx = new XWPFDocument(stream);
                        this.text = new XWPFWordExtractor(docx).getText();
                        this.isWorkTable = false;
                        break;
                    case "txt":
                        this.text = new Scanner(new File(path)).useDelimiter("\\Z").next();
                        this.isWorkTable = false;
                        break;
                    case "xls":
                    case "xlsx":
                        Workbook wb = WorkbookFactory.create(stream);
                        Sheet sheet = wb.getSheetAt(0);
                        DataFormatter dataFormat = new DataFormatter();
                        FormulaEvaluator eval;
                        if (wb instanceof HSSFWorkbook) {
                            eval = new HSSFFormulaEvaluator((HSSFWorkbook) wb);
                        } else {
                            eval = new XSSFFormulaEvaluator((XSSFWorkbook) wb);
                        }
                        if (sheet != null) {
                            for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++) {
                                Row row = sheet.getRow(i);
                                if (row != null) {
                                    if (row.getLastCellNum() > 0) {
                                        String[] rowValue = new String[row.getLastCellNum()];
                                        for (int cellNum = 0; cellNum < rowValue.length; cellNum++) {
                                            Cell cell = row.getCell(cellNum);
                                            rowValue[cellNum] = dataFormat.formatCellValue(cell, eval);
                                        }
                                        tables.add(rowValue);
                                    } else {
                                        tables.add(new String[0]);
                                    }
                                }
                            }
                        }
                        this.isWorkTable = true;
                        break;
                    default:
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(BMSUDocument.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(BMSUDocument.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void setBold(int start, int end) throws IOException {
        
        switch (type) {
            case "docx":
                XWPFDocument docx = new XWPFDocument(stream);
                docx.getParagraphs().forEach(par->{
                    for (XWPFRun run : par.getRuns()){
                        //пробегаем по ранам, запоминая позиции
                        //когда текущий ран содержит позицию старта, 
                        //он удаляется, но создается два новых рана на месте старого: один с текстом до
                        //позиции, другой с текстом - после и до конца оригинального рана
                        //далее к тексту применяется форматирование и раны продолжают перебираться и увеличением позиции
                        // и применением форматирования
                        //когда дойдет до позиции конца, производится операция аналогичная позиции начала,
                        //только в обратном порядке
                    }
                });
                

        }
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public Boolean isWorkTable() {
        return isWorkTable;
    }

    public List<String[]> getTables() {
        return tables;
    }

}
