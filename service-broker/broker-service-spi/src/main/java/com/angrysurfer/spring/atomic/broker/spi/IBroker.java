package com.angrysurfer.spring.atomic.broker.spi;

import com.angrysurfer.spring.atomic.broker.api.ServiceRequest;
import com.angrysurfer.spring.atomic.broker.api.ServiceResponse;

public interface IBroker {
    <T> ServiceResponse<T> submit(ServiceRequest request);
}
