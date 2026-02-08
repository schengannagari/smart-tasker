package com.sc.smarttasker.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class SecretManagerService {
    private final SecretsManagerClient client;

    public SecretManagerService(final SecretsManagerClient client) {
        this.client = client;
    }

    public String getSecret(String secretName) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        return client.getSecretValue(request).secretString();
    }

    public Map<String, String> getSecretsAsMap(String secretName) {
        String secret = getSecret(secretName);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(secret, new TypeReference<Map<String, String>>() {});
    }
}
