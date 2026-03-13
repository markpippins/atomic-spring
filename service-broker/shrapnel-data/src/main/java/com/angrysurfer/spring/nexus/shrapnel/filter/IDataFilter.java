package com.angrysurfer.spring.nexus.shrapnel.filter;

import com.angrysurfer.spring.nexus.shrapnel.property.IPropertyAccessor;
import com.angrysurfer.spring.nexus.shrapnel.writer.IDataWriter;

public interface IDataFilter {
    boolean allows(Object item, IDataWriter writer, IPropertyAccessor accessor);
}
