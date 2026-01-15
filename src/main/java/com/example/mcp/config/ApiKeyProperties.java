package com.example.mcp.config;

import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.Versioned;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.Map;

@Component
public class ApiKeyProperties {

    private final VaultOperations vaultOperations;
    private String apiKey;

    public ApiKeyProperties(VaultOperations vaultOperations) {
        this.vaultOperations = vaultOperations;
    }

    @PostConstruct
    public void init() {
        try {
            VaultVersionedKeyValueOperations kvOps = vaultOperations.opsForVersionedKeyValue("secret");

            // Read from secret/mcp (application-name path)
            // Vault path: secret/mcp
            // Key: api-key
            Versioned<Map<String, Object>> secret = kvOps.get("mcp");
            if (secret != null && secret.getData() != null) {
                Object apiKeyValue = secret.getData().get("api-key");
                if (apiKeyValue != null) {
                    this.apiKey = apiKeyValue.toString();
                }
            }
        } catch (Exception e) {
            // Log error but don't fail startup
        }
    }

    public String getValue() {
        return apiKey;
    }
}
