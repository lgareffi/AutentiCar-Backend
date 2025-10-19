package app.service;

import org.springframework.web.multipart.MultipartFile;

public interface IDNIUsuarioService {
    void subirFrente(MultipartFile file);

    void subirDorso(MultipartFile file);

    String getFrenteUrl(Long usuarioIdSolicitado);

    String getDorsoUrl(Long usuarioIdSolicitado);

    void enviar();

    void validar(Long usuarioId);

    void rechazar(Long usuarioId);
    
}
