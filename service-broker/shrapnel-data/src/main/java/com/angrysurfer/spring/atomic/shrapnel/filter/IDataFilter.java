package com.angrysurfer.spring.atomic.shrapnel.filter;

import com.angrysurfer.spring.atomic.shrapnel.writer.IDataWriter;
import com.angrysurfer.spring.atomic.shrapnel.property.IPropertyAccessor;

public interface IDataFilter {
    boolean allows(Object item, IDataWriter writer, IPropertyAccessor accessor);
}
