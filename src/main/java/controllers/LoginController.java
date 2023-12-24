package controllers;

import repositorios.RepositorioUsuarios;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import usuarios.Admin;
import usuarios.Duenio;

import java.util.HashMap;
import java.util.Map;

public abstract class LoginController {

  protected RepositorioUsuarios repo = new RepositorioUsuarios();

  public abstract ModelAndView index(Request request, Response response);

  public abstract Object checkUser(String username, String password);

  public abstract void setearSesion(Request req, Object usuario);

  public abstract ModelAndView indexError(Map<String, Object> model);

  public abstract void redireccionarTrasLoguearse(Response response);

  public ModelAndView login(Request request, Response response) {
    String usuario = request.queryParams("Username");
    String password = request.queryParams("Password");

    Object usuarioEncontrado = checkUser(usuario, password);

    Map<String, Object> model = new HashMap<>();
    model.put("username", usuario);

    if (usuarioEncontrado != null) {
        request.session(true);
        setearSesion(request, usuarioEncontrado);
        request.session().maxInactiveInterval(3600);
        redireccionarTrasLoguearse(response);
      } else {
        return indexError(model);
      }

    return null;
  }

  public ModelAndView removeSession(Request request, Response response) {

    request.session().removeAttribute("usuario_logueado");
    request.session().removeAttribute("admin_logueado");
    response.redirect("/home");

    return null;
  }

}
