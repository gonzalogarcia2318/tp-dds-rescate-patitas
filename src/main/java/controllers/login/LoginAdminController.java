package controllers.login;

import controllers.LoginController;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import usuarios.Admin;

import java.util.Map;

public class LoginAdminController extends LoginController {
  @Override
  public ModelAndView index(Request request, Response response) {
    return new ModelAndView(null,"login-admin.hbs");
  }

  @Override
  public Object checkUser(String username, String password) {
    Admin usuarioEncontrado = repo.checkAdmin(username);
    if(usuarioEncontrado != null)
      if(usuarioEncontrado.passwordCoincide(password))
        return usuarioEncontrado;
    return null;
  }

  @Override
  public void setearSesion(Request request, Object usuario) {
    request.session().attribute("admin_logueado", usuario);
    request.session().removeAttribute("usuario_logueado");
  }

  @Override
  public ModelAndView indexError(Map<String, Object> model) {
    model.put("mensajeError", "Usuario o contraseña incorrectos");
    return new ModelAndView(model, "login-admin.hbs");
  }

  @Override
  public void redireccionarTrasLoguearse(Response response) {
    response.redirect("/caracteristicas");
  }
}
