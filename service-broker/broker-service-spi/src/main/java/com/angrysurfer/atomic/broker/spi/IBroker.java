package com.angrysurfer.atomic.broker.spi;

import com.angrysurfer.atomic.broker.api.ServiceRequest;
import com.angrysurfer.atomic.broker.api.ServiceResponse;

public interface IBroker {
    <T> ServiceResponse<T> submit(ServiceRequest request);
}
