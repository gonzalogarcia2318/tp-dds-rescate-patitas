package imagenes;

public interface UploadImageService {

  void uploadFile(String localFilename, String bucketFileKey);
}
