package com.inventory.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Servicio de almacenamiento de archivos para plantillas de reportes.
 * Gestiona operaciones de lectura/escritura/eliminación en el directorio configurado.
 */
@Service
public class ReportStorageService {

    @Value("${reports.storage.path:./reports-storage}")
    private String storagePath;

    private Path getStorageDir() throws IOException {
        Path dir = Paths.get(storagePath).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        return dir;
    }

    /**
     * Guarda un archivo en el almacenamiento.
     *
     * @param content  bytes del archivo
     * @param filename nombre del archivo (solo nombre, sin ruta)
     * @return nombre del archivo guardado
     */
    public String saveFile(byte[] content, String filename) throws IOException {
        validateFilename(filename);
        Path destination = getStorageDir().resolve(filename).normalize();
        // Prevenir path traversal
        if (!destination.startsWith(getStorageDir())) {
            throw new SecurityException("Nombre de archivo inválido: " + filename);
        }
        Files.write(destination, content);
        return filename;
    }

    /**
     * Lee un archivo del almacenamiento.
     */
    public byte[] readFile(String filename) throws IOException {
        validateFilename(filename);
        Path filePath = getStorageDir().resolve(filename).normalize();
        if (!filePath.startsWith(getStorageDir())) {
            throw new SecurityException("Acceso inválido: " + filename);
        }
        if (!Files.exists(filePath)) {
            throw new IOException("Archivo no encontrado: " + filename);
        }
        return Files.readAllBytes(filePath);
    }

    /**
     * Elimina un archivo del almacenamiento.
     */
    public void deleteFile(String filename) throws IOException {
        if (filename == null || filename.isBlank()) return;
        Path filePath = getStorageDir().resolve(filename).normalize();
        if (!filePath.startsWith(getStorageDir())) {
            throw new SecurityException("Ruta inválida: " + filename);
        }
        Files.deleteIfExists(filePath);
    }

    /**
     * Verifica si un archivo existe.
     */
    public boolean fileExists(String filename) {
        try {
            if (filename == null || filename.isBlank()) return false;
            Path filePath = getStorageDir().resolve(filename).normalize();
            return Files.exists(filePath);
        } catch (IOException e) {
            return false;
        }
    }

    /** Previene nombres de archivo con rutas relativas o caracteres peligrosos. */
    private void validateFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío");
        }
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new SecurityException("Nombre de archivo inválido: " + filename);
        }
    }
}
