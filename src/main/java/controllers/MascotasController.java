package controllers;

import caracteristicas.CaracteristicaMascota;
import imagenes.exceptions.NoSePudoSubirUnaImagenException;
import mascotas.Mascota;
import mascotas.MascotaBuilder;
import mascotas.Sexo;
import mascotas.TipoMascota;
import repositorios.RepositorioCaracteristicas;
import repositorios.RepositorioMascotas;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import usuarios.Duenio;
import utils.FotosHelper;

import javax.servlet.MultipartConfigElement;
import java.util.*;

public class MascotasController {

  private FotosHelper fotosHelper = new FotosHelper();

  public ModelAndView indexQR(Request req, Response res) {
    Map<String, Object> model = new HashMap<>();
    model.put("id", req.params(":id"));
    return ModelAndViewBuilder.create(model, "mascota-qr.hbs", req);
  }

  public ModelAndView indexCrear(Request req, Response res) {
    Map<String, Object> model = new HashMap<>();
    model.put("caracteristicas", RepositorioCaracteristicas.getRepositorio().getCaracteristicas());
    model.put("registrada", false);
    return ModelAndViewBuilder.create(model, "mascota.hbs", req);
  }

  public ModelAndView crear(Request req, Response res) {
    Duenio duenio = req.session().attribute("usuario_logueado");

    // Para obtener las fotos y los campos ya que el form se envia encodeado como multipart
    req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

    Mascota mascota = new MascotaBuilder()
        .setDuenio(duenio)
        .setTipo(TipoMascota.valueOf(req.queryParams("tipoMascota")))
        .setNombre(req.queryParams("nombre"))
        .setApodo(req.queryParams("apodo"))
        .setEdad(Integer.parseInt(req.queryParams("edad")))
        .setSexo(Sexo.valueOf(req.queryParams("sexo")))
        .setDescripcion(req.queryParams("descripcion"))
        .setCaracteristicas(this.getCaracteristicasFromRequest(req))
        .setFotos(Collections.emptyList())
        .build();

    Map<String, Object> model = new HashMap<>();
    model.put("caracteristicas", RepositorioCaracteristicas.getRepositorio().getCaracteristicas());

    try {
      mascota.setFotos(this.fotosHelper.getFotosFromRequest(req, "mascotas"));
    } catch (NoSePudoSubirUnaImagenException e) {
      model.put("mensajeError", e.getMessage());
      return ModelAndViewBuilder.create(model, "mascota.hbs", req);
    }

    duenio.agregarMascota(mascota);

    model.put("registrada", true);
    model.put("id", mascota.getId());

    return ModelAndViewBuilder.create(model, "mascota.hbs", req);
  }

  public ModelAndView index(Request req, Response res) {
    Duenio duenio = req.session().attribute("usuario_logueado");
    List<Mascota> mascotas = RepositorioMascotas.getInstance().getMascotasPorDuenio(duenio);
    Map<String, Object> model = new HashMap<>();
    model.put("mascotas", mascotas);
    return ModelAndViewBuilder.create(model, "mascotas.hbs", req);
  }


  private List<CaracteristicaMascota> getCaracteristicasFromRequest(Request req) {
    List<CaracteristicaMascota> caracteristicasMascota = new ArrayList<>();
    RepositorioCaracteristicas.getRepositorio().getCaracteristicas().forEach(caracteristica -> {
      String valorCaracteristica = req.queryParams("caracteristicas[" + caracteristica.getNombre() + "]");
      if (valorCaracteristica != null && !valorCaracteristica.isEmpty()) {
        caracteristicasMascota.add(new CaracteristicaMascota(caracteristica.getNombre(), valorCaracteristica));
      }
    });
    return caracteristicasMascota;
  }

}
