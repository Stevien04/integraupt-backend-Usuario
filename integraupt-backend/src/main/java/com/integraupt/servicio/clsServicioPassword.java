package com.integraupt.servicio;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Servicio encargado de encriptar y desencriptar contraseñas de usuarios.
 */
@Service
public class clsServicioPassword {

    private static final Logger LOGGER = LoggerFactory.getLogger(clsServicioPassword.class);
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private final String secretKeyConfig;
    private final String ivConfig;
    private SecretKeySpec secretKeySpec;
    private IvParameterSpec ivParameterSpec;
    private final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();

    public clsServicioPassword(
            @Value("${app.security.password-secret:IntegraUPTSecretKey!}") String secretKeyConfig,
            @Value("${app.security.password-iv:IntegraUPTInitVect}") String ivConfig) {
        this.secretKeyConfig = secretKeyConfig;
        this.ivConfig = ivConfig;
    }

    @PostConstruct
    void init() {
        this.secretKeySpec = new SecretKeySpec(normalizarClave(secretKeyConfig), "AES");
        this.ivParameterSpec = new IvParameterSpec(normalizarClave(ivConfig));
    }

    public String encriptar(String valorPlano) {
        if (!StringUtils.hasText(valorPlano)) {
            return valorPlano;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(valorPlano.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo encriptar la contraseña", e);
        }
    }

    public String desencriptar(String valorEncriptado) {
        if (!StringUtils.hasText(valorEncriptado)) {
            return valorEncriptado;
        }
        if (esHashBCrypt(valorEncriptado)) {
            return valorEncriptado;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(valorEncriptado);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            LOGGER.debug("El valor proporcionado no está en Base64, se devolverá sin cambios");
            return valorEncriptado;
        } catch (GeneralSecurityException e) {
            LOGGER.error("Error al desencriptar la contraseña", e);
            throw new IllegalStateException("No se pudo desencriptar la contraseña", e);
        }
    }

    public boolean matches(String passwordPlano, String valorAlmacenado) {
        if (!StringUtils.hasText(passwordPlano) || !StringUtils.hasText(valorAlmacenado)) {
            return false;
        }
        if (esHashBCrypt(valorAlmacenado)) {
            return bcryptEncoder.matches(passwordPlano, valorAlmacenado);
        }
        String desencriptada = desencriptar(valorAlmacenado);
        return passwordPlano.equals(desencriptada);
    }

    private boolean esHashBCrypt(String valor) {
        return valor != null && (valor.startsWith("$2a$") || valor.startsWith("$2b$") || valor.startsWith("$2y$"));
    }

    private byte[] normalizarClave(String valor) {
        byte[] bytes = valor != null
                ? valor.getBytes(StandardCharsets.UTF_8)
                : new byte[0];
        byte[] clave = new byte[16];
        int longitud = Math.min(bytes.length, 16);
        System.arraycopy(bytes, 0, clave, 0, longitud);
        if (longitud < 16) {
            for (int i = longitud; i < 16; i++) {
                clave[i] = '0';
            }
        }
        return clave;
    }
}