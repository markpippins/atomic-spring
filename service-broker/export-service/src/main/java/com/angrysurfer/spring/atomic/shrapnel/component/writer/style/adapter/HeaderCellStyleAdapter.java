package com.angrysurfer.spring.atomic.shrapnel.component.writer.style.adapter;

import com.angrysurfer.spring.atomic.shrapnel.PropertyConfig;
import com.angrysurfer.spring.atomic.shrapnel.component.writer.style.FontSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeaderCellStyleAdapter extends CellStyleAdapter {

    private static final String FONT_SIZE = "font.size";

    private static final String MARGIN = "cell.margin";

    private static final String PADDING = "cell.padding";

    static int DEFAULT_FONT_SIZE = 9;

    static int DEFAULT_MARGIN = 2;

    static int DEFAULT_PADDING = 2;

    public HeaderCellStyleAdapter() {
        PropertyConfig propertyConfig = PropertyConfig.getInstance();
        setFontSize(propertyConfig.containsKey(FONT_SIZE) ? Integer.parseInt(propertyConfig.getProperty(FONT_SIZE).toString()) : DEFAULT_FONT_SIZE);
        setMargin(propertyConfig.containsKey(MARGIN) ? Integer.parseInt(propertyConfig.getProperty(MARGIN).toString()) : DEFAULT_MARGIN);
        setPadding(propertyConfig.containsKey(PADDING) ? Integer.parseInt(propertyConfig.getProperty(PADDING).toString()) : DEFAULT_PADDING);
        setFont(FontSource.getFont());
    }
}
