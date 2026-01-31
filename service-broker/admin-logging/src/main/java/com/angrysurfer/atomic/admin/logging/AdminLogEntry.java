package com.angrysurfer.atomic.admin.logging;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "admin_log_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLogEntry {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "server_id")
    private String serverId;

    @Column(name = "server_port")
    private Integer serverPort;

    @Column(name = "server_config")
    private String serverConfig;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "service")
    private String service;

    @Column(name = "operation")
    private String operation;

    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams; // JSON or serialized form of parameters

    @Column(name = "success_status")
    private Boolean successStatus;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "request_id")
    private String requestId;
}