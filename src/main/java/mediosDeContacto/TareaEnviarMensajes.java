package mediosDeContacto;

import adopciones.NotificacionRecomendacion;
import org.uqbarproject.jpa.java8.extras.PerThreadEntityManagers;
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import usuarios.Contacto;

import java.util.List;

public class TareaEnviarMensajes {

  public void enviarMensajesPorVariosMediosA(List<Contacto> contactos, List<MedioDeContacto> mediosDeContacto, String asunto, String mensaje) {
    mediosDeContacto.forEach(medio -> {
      MedioDeContacto medioDeContacto;
        medioDeContacto = (MedioDeContacto) ServiceLocator.getServiceLocator().get(medio.getClass());
      contactos.forEach(contacto -> {
        medioDeContacto.comunicarleA(contacto, asunto, mensaje);
      });
    });
  }

  public void enviarMailATodosLosContactos(List<Contacto> contactos, String asunto, String mensaje) {
    MailerGmail mailer = (MailerGmail) ServiceLocator.getServiceLocator().get(MailerGmail.class);

    contactos.stream().forEach(contacto -> {

      // Guardo notificacion en base de datos para saber si se corre el cron bien.
      // Realmente habria que guardarlo si se envio bien el mail o guardar por que fallo el envio del mail.
      NotificacionRecomendacion notificacion = new NotificacionRecomendacion(contacto.getEmail(), asunto, mensaje);

      PerThreadEntityManagers.getEntityManager().getTransaction().begin();
      PerThreadEntityManagers.getEntityManager().persist(notificacion);
      PerThreadEntityManagers.getEntityManager().getTransaction().commit();

      mailer.enviarMail(new Mail(contacto.getEmail(), asunto, mensaje));



    });
  }

  public void enviarSMSATodosLosContactos(List<Contacto> contactos, String mensaje) {
    MensajeriaTwilio twilio = (MensajeriaTwilio) ServiceLocator.getServiceLocator().get(MensajeriaTwilio.class);

    contactos.stream().forEach(contacto -> twilio.enviarMensaje(contacto.getTelefono(),mensaje));
  }
}
