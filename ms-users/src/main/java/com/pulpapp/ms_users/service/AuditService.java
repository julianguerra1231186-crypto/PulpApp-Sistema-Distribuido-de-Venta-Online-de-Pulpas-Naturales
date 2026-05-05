package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.entity.AuditLog;
import com.pulpapp.ms_users.repository.AuditLogRepository;
import com.pulpapp.ms_users.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    public List<Map<String, Object>> findAll() {
        return repository.findByTenantIdOrderByCreatedAtDesc(resolveTenantId()).stream().map(a -> Map.<String, Object>of(
                "id", a.getId(), "userName", a.getUserName() != null ? a.getUserName() : "Sistema",
                "action", a.getAction(), "module", a.getModule(),
                "description", a.getDescription(), "createdAt", a.getCreatedAt().toString()
        )).toList();
    }

    public void log(String userName, String action, String module, String description) {
        AuditLog entry = new AuditLog();
        entry.setTenantId(resolveTenantId());
        entry.setUserName(userName);
        entry.setAction(action);
        entry.setModule(module);
        entry.setDescription(description);
        repository.save(entry);
    }

    private Long resolveTenantId() {
        Long t = TenantContext.getTenantId();
        return t != null ? t : defaultTenantId;
    }
}
