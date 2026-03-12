package com.angrysurfer.spring.atomic.shrapnel;

import java.util.Map;

import com.angrysurfer.spring.atomic.shrapnel.component.writer.ExcelDataWriter;
import com.angrysurfer.spring.atomic.shrapnel.component.writer.PdfDataWriter;
import com.angrysurfer.spring.atomic.shrapnel.field.IFields;
import com.angrysurfer.spring.atomic.shrapnel.filter.IDataFilter;
import com.itextpdf.kernel.geom.PageSize;

public interface IExport {

    void addFilter(Map<String, Object> filterCriteria);

    void addFilter(IDataFilter filter);

    IFields getFields();

    String getName();

    ExcelDataWriter getExcelRowWriter();

    PdfDataWriter getPdfRowWriter();

    PageSize getPdfPageSize();

    void init();
}
