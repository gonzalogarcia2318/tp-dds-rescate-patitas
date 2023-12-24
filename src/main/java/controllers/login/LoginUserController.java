package controllers.login;

import controllers.LoginController;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import usuarios.Duenio;

import java.util.Map;

public class LoginUserController extends LoginController {
  @Override
  public ModelAndView index(Request request, Response response) {
    return new ModelAndView(null,"login.hbs");
  }

  @Override
  public Object checkUser(String username, String password) {
    Duenio usuarioEncontrado = repo.checkUser(username);
    if(usuarioEncontrado != null)
      if(usuarioEncontrado.passwordCoincide(password))
        return usuarioEncontrado;
    return null;
  }

  @Override
  public void setearSesion(Request request, Object usuarioEncontrado) {
    request.session().attribute("usuario_logueado", usuarioEncontrado);
    request.session().removeAttribute("admin_logueado");
  }

  @Override
  public ModelAndView indexError(Map<String, Object> model) {
    model.put("mensajeError", "Usuario o contraseña incorrectos");
    return new ModelAndView(model, "login.hbs");
  }

  @Override
  public void redireccionarTrasLoguearse(Response response) {
    response.redirect("/home");
  }
}
