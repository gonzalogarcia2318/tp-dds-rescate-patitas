package controllers;

import imagenes.exceptions.NoSePudoSubirUnaImagenException;
import mapa.Ubicacion;
import mascotas.Mascota;
import mascotasEncontradas.MascotaEncontrada;
import mascotasEncontradas.MascotaEncontradaConChapita;
import mascotasEncontradas.PublicacionMascotaEncontrada;
import repositorios.RepositorioMascotas;
import repositorios.RepositorioMascotasEncontradas;
import rescatistas.Rescatista;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import usuarios.Contacto;
import usuarios.TipoDocumento;
import utils.FotosHelper;


import javax.servlet.MultipartConfigElement;
import java.time.LocalDate;
import java.util.*;

public class MascotaEncontradaController {

  private FotosHelper fotosHelper = new FotosHelper();

  public MascotaEncontrada mascotaEncontrada(Request request, List<String> fotos) {
    // Datos del rescatista
    String nombreRescatista = request.queryParams("nombre-rescatista");
    String apellidoRescatista = request.queryParams("apellido-rescatista");
    TipoDocumento tipoDocumento = TipoDocumento.valueOf(request.queryParams("tipo-documento"));
    int numeroDocumento = Integer.parseInt(request.queryParams("numero-documento"));

    // Datos de contacto
    String nombreContacto = request.queryParams("nombre-contacto");
    String apellidoContacto = request.queryParams("apellido-contacto");
    String telefonoContacto = request.queryParams("telefono");
    String emailContacto = request.queryParams("email");

    Contacto contacto = new Contacto(nombreContacto, apellidoContacto, telefonoContacto, emailContacto);
    List<Contacto> listaContactos = new ArrayList<>();
    listaContactos.add(contacto);

    Rescatista rescatista = new Rescatista(nombreRescatista, apellidoRescatista, tipoDocumento, numeroDocumento, listaContactos);

    // Datos de la mascota encontrada
    String descripcion = request.queryParams("descripcion");
    //String imagen = request.queryParams("imagen-mascota");

    Ubicacion ubicacion = new Ubicacion();
    ubicacion.setDireccion(request.queryParams("direccion"));
    ubicacion.setLatitud(Double.parseDouble(request.queryParams("latitud")));
    ubicacion.setLongitud(Double.parseDouble(request.queryParams("longitud")));

    return new MascotaEncontrada(fotos, descripcion,  ubicacion, LocalDate.now(), rescatista);
  }

  public ModelAndView encontradaMascotaSinChapita(Request request, Response response) {
    return ModelAndViewBuilder.create(null, "mascota-encontrada.hbs", request);
  }

  public ModelAndView encontradaMascotaConChapita(Request request, Response response) {

    Long id = Long.valueOf(request.params("id"));
    boolean estaRegistradoEnElSistema = RepositorioMascotas.getInstance().existeLaMascotaConID(id);

    if(estaRegistradoEnElSistema) {
      Map<String, Object> model = new HashMap<>();
      model.put("id", id);
      return ModelAndViewBuilder.create(model, "mascota-encontrada.hbs", request);
    } else {

      return ModelAndViewBuilder.create(null, "encontrada_not_found.hbs", request);
    }

  }

  public ModelAndView crearPublicacion(Request request, Response response) {
    // Para obtener las fotos y los campos ya que el form se envia encodeado como multipart
    request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

    MascotaEncontrada mascotaEncontrada;
    try {
      mascotaEncontrada = mascotaEncontrada(request, this.fotosHelper.getFotosFromRequest(request, "encontradas"));
    } catch (NoSePudoSubirUnaImagenException e) {
      Map<String, Object> model = new HashMap<>();
      model.put("mensajeError", e.getMessage());
      return ModelAndViewBuilder.create(model, "mascota-encontrada.hbs", request);
    }
    // Creo la publicacion de mascota encontrada
    PublicacionMascotaEncontrada publicacion = new PublicacionMascotaEncontrada(mascotaEncontrada, false);

    RepositorioMascotasEncontradas.getRepositorioMascotas().agregarPublicacion(publicacion);

    Map<String, Object> model = new HashMap<>();
    model.put("registrada", true);
    return ModelAndViewBuilder.create(model, "mascota-encontrada.hbs", request);
  }

  public ModelAndView crearMascotaConChapita(Request request, Response response) {
    // Para obtener las fotos y los campos ya que el form se envia encodeado como multipart
    request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));


    MascotaEncontrada mascotaEncontrada;

    try {
      mascotaEncontrada = mascotaEncontrada(request, this.fotosHelper.getFotosFromRequest(request, "encontradas"));
    } catch (NoSePudoSubirUnaImagenException e) {
      Map<String, Object> model = new HashMap<>();
      model.put("mensajeError", e.getMessage());
      return ModelAndViewBuilder.create(model, "mascota-encontrada.hbs", request);
    }

    Mascota mascota = RepositorioMascotas.getInstance().getMascotaById(Long.valueOf(request.params("id")));

    // Creo mascota encontrada con chapita (tiene duenio)

    MascotaEncontradaConChapita mascotaEncontradaConChapita = new MascotaEncontradaConChapita(mascotaEncontrada, mascota);
    RepositorioMascotasEncontradas.getRepositorioMascotas().agregarMascotaEncontrada(mascotaEncontradaConChapita);

    Map<String, Object> model = new HashMap<>();
    model.put("registrada", true);
    return ModelAndViewBuilder.create(model, "mascota-encontrada.hbs", request);
  }

}
