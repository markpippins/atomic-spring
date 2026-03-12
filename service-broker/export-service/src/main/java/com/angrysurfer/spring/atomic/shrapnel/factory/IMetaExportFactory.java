package com.angrysurfer.spring.atomic.shrapnel.factory;

import java.util.List;

import com.angrysurfer.spring.atomic.shrapnel.service.Request;

public interface IMetaExportFactory {

    boolean hasFactory(Request request);

    IExportFactory newInstance(Request request);

    List< String > getAvailableExports();
}
