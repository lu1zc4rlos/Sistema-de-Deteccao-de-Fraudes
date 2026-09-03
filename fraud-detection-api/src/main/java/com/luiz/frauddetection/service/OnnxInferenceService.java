package com.luiz.frauddetection.service;

import ai.onnxruntime.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Collections;

@Service
public class OnnxInferenceService {

    private OrtEnvironment env;
    private OrtSession session;

    @PostConstruct
    public void init() {
        try {
            this.env = OrtEnvironment.getEnvironment();

            // Usando o localizador nativo do Spring (resolve problemas de classpath em testes)
            ClassPathResource resource = new ClassPathResource("models/fraud_model.onnx");

            if (!resource.exists()) {
                throw new IllegalStateException("Modelo ONNX não encontrado no classpath: " + resource.getPath());
            }

            try (InputStream modelStream = resource.getInputStream()) {
                byte[] modelBytes = modelStream.readAllBytes();
                this.session = env.createSession(modelBytes, new OrtSession.SessionOptions());
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar o modelo ONNX no Spring Boot", e);
        }
    }

    public float predictFraudProbability(float[] features) throws OrtException {
        long[] shape = new long[]{1, features.length};
        OnnxTensor tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(features), shape);

        try (OrtSession.Result results = session.run(Collections.singletonMap("float_input", tensor))) {
            float[][] probabilities = (float[][]) results.get(1).getValue();
            return probabilities[0][1];
        }
    }

    @PreDestroy
    public void close() {
        try {
            if (session != null) session.close();
            if (env != null) env.close();
        } catch (OrtException ignored) {
        }
    }
}