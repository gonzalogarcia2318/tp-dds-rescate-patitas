package imagenes.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import imagenes.UploadImageService;
import imagenes.exceptions.NoSePudoSubirUnaImagenException;

import java.io.File;

public class UploadImageServiceAWS implements UploadImageService {

  public void uploadFile(String localFilename, String bucketFileKey) {
    String bucketName = "mascotas-bucket";

    ProcessBuilder processBuilder = new ProcessBuilder();
    String awsAccessKey = processBuilder.environment().get("AWS_ACCESS_KEY");
    String awsSecretKey = processBuilder.environment().get("AWS_SECRET_KEY");


    try {
      BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
      AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
          .withRegion(Regions.SA_EAST_1)
          .withCredentials(new AWSStaticCredentialsProvider(credentials))
          .build();

      // Upload a file as a new object with ContentType and title specified.
      PutObjectRequest request = new PutObjectRequest(bucketName, bucketFileKey, new File("./fotos-mascotas/" + localFilename));
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType("image/png");
      request.setMetadata(metadata);
      s3Client.putObject(request);
    } catch (Exception e) {
      // The call was transmitted successfully, but Amazon S3 couldn't process
      // it, so it returned an error response.
      e.printStackTrace();
      throw new NoSePudoSubirUnaImagenException("Hubo un error al subir la imagen.");
    }
  }


}