package utils;

import imagenes.aws.UploadImageServiceAWS;
import imagenes.exceptions.NoSePudoSubirUnaImagenException;
import spark.Request;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FotosHelper {

    public List<String> getFotosFromRequest(Request req, String mainKey) {
    List<String> rutasFotos = new ArrayList<>();

    File uploadDir = new File("fotos-mascotas");
    boolean dir = uploadDir.mkdir();

    System.out.println("Se creo el directorio fotos-mascotas: " + dir);

    try{
      List<Part> partsFotos = req.raw().getParts().stream()
          .filter(part -> part.getName().equals("foto"))
          .collect(Collectors.toList());

      for (Part part : partsFotos) {
        // createTempFile asegura que no se repita el nombre del archivo.
        Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
        String bucketFileKey = mainKey + "-" + UUID.randomUUID().toString();

        if (part != null) {
          try (InputStream input = part.getInputStream()) {
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
          }
        }

        // Hecho asi para pasar spotbugs.
        if (tempFile != null) {
          Path fileName = tempFile.getFileName();
          if (fileName != null) {
            UploadImageServiceAWS uploadImageService = new UploadImageServiceAWS();
            uploadImageService.uploadFile(fileName.toString(), bucketFileKey);
            rutasFotos.add(bucketFileKey);
          }

        }
      }
    } catch (IOException e) {
      throw new NoSePudoSubirUnaImagenException("Hubo un error al subir la imagen.");
    } catch (ServletException e){
      throw new NoSePudoSubirUnaImagenException("Hubo un error en la peticion.");
    }

    return rutasFotos;
  }
}
