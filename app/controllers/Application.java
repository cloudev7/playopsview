package controllers;

import play.*;
import play.mvc.*;
import views.html.*;


// WS specific imports
import play.libs.WS;
import play.mvc.Result;
import static play.libs.F.Function;
import static play.libs.F.Promise;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

public class Application extends Controller {


    final static String hostname = "arg-prod-app01";

    //http://opsview-arg.localdomain/rest/login
    final static String endpoint_login = "http://opsview-arg.localdomain/rest/login";

    //http://opsview-arg.localdomain/rest/status/performancemetric/?order=hostname&order=metricname&metricname=cpu_idle&hostname=arg-prod-app01
    final static String endpoint_cpuIdle = "http://opsview-arg.localdomain/rest/status/performancemetric";

    static String token = "";

    final static JsonNode authJSON = Json.newObject()
            .put("username", "support")
            .put("password", "FreeB1rd");

    /*public static Result index() {
        return ok(index.render("Your new application is ready."));
    }*/

    public static Promise<Result> index() {
        final Promise<Result> loginPromiseResult = WS.url(endpoint_login)
	    .setContentType("application/json")
	    .post(authJSON)
	    .map(
               new Function<WS.Response, Result>() {
                  public Result apply(WS.Response response) {
                    token = response.asJson().findPath("token").asText();
                    //return ok("response: " + token);
                    return ok("response: " + response.asJson());
                  }
               }
            );
	if ( token != null ) {
	    Promise<Result> cpuIdlePrmoiseResult = getIdleCPU();
            return cpuIdlePrmoiseResult;
	}
	else{
	
            return loginPromiseResult;
	}
            

    }

    public static Promise<Result> getIdleCPU() {
        final Promise<Result> cpuIdlePrmoiseResult = WS.url(endpoint_cpuIdle)
	    .setContentType("application/json")
	    .setHeader("X-Opsview-Username", "support")
	    .setHeader("X-Opsview-Token", token)
            .setQueryParameter("order", "hostname")
            .setQueryParameter("order", "metricname")
            .setQueryParameter("metricname", "cpu_idle")
            .setQueryParameter("hostname", hostname)
	    .get()
	    .map(
               new Function<WS.Response, Result>() {
                  public Result apply(WS.Response response) {
                    return ok("response: " + response.asJson());
                  }
               }
            );

        return cpuIdlePrmoiseResult;
    }
}
