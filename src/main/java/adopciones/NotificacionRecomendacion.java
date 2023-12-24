package adopciones;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class NotificacionRecomendacion {

  @Id
  @GeneratedValue
  private Long id;

  private String enviadoA;

  private String asunto;

  private String mensaje;

  private Date fechaEnvio;

  public NotificacionRecomendacion(){ }

  public NotificacionRecomendacion(String enviadoA, String asunto, String mensaje){
    this.enviadoA = enviadoA;
    this.asunto = asunto;
    this.mensaje = mensaje;
    this.fechaEnvio = new Date();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEnviadoA() {
    return enviadoA;
  }

  public void setEnviadoA(String enviadoA) {
    this.enviadoA = enviadoA;
  }

  public String getAsunto() {
    return asunto;
  }

  public void setAsunto(String asunto) {
    this.asunto = asunto;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public Date getFechaEnvio() {
    return new Date(this.fechaEnvio.getTime());
  }
}
