package com.angrysurfer.spring.nexus.shrapnel.service;

import com.angrysurfer.spring.nexus.shrapnel.component.writer.ExcelDataWriter;
import com.angrysurfer.spring.nexus.shrapnel.component.writer.PdfDataWriter;
import com.angrysurfer.spring.nexus.shrapnel.field.IField;

import java.util.Collection;
import java.util.List;

public interface ILightweightExportsService {

    String writeCSVFile(Collection<Object> items, List<IField> columns, String filename);

    String writeExcelFile(Collection<Object> items, List<IField> columns, String sheetName, String filename);

    String writeExcelFile(Collection<Object> items, ExcelDataWriter writer, String sheetName, String filename);

    String writeTabularPdfFile(Collection<Object> items, List<IField> columns, String filename);

    String writeTabularPdfFile(Collection<Object> items, PdfDataWriter pdfRowWriter, String filename);
}
