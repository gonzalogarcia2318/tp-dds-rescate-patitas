package adopciones.notificador;

import mediosDeContacto.MailerGmail;
import mediosDeContacto.ServiceLocator;
import repositorios.RepositorioAsociaciones;
import usuarios.Asociacion;

import java.util.List;

public class NotificadorDeAdopciones {

  public static void main(String[] args) {
    System.out.println("Ejecutando Notificador de Adopciones");
    ServiceLocator.getServiceLocator().registrar(MailerGmail.class, new MailerGmail());
    notificar();
  }

  public static void notificar(){
    // Para pensar a futuro: guardar quienes recibieron notificaciones y cuando, y controlar que no se vuelva a enviar lo mismo
    RepositorioAsociaciones.getRepositorio().getAsociaciones().forEach(Asociacion::recomendarMascotasParaAdoptar);
  }

}
