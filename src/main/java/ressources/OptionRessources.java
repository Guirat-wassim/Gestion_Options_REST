package ressources;

import entities.Option;
import metiers.OptionBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("options")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OptionRessources {

    private final OptionBusiness optionBusiness = new OptionBusiness();

    // 1) POST /options
    @POST
    public Response addOption(Option option) {
        if (option == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (optionBusiness.getOptionByCode(option.getCodeOption()) != null) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        optionBusiness.addOption(option);
        return Response.status(Response.Status.CREATED).entity(option).build();
    }

    // 2) GET /options  et  3) GET /options?domaine=Mathématiques
    @GET
    public Response getOptions(@QueryParam("domaine") String domaine) {
        List<Option> liste = (domaine == null || domaine.trim().isEmpty())
                ? optionBusiness.getListeOptions()
                : optionBusiness.getOptionsByDomaine(domaine);
        return Response.ok(liste).build();
    }

    // 6) GET /options/{code}
    @GET
    @Path("{code}")
    public Response getOptionByCode(@PathParam("code") int code) {
        Option option = optionBusiness.getOptionByCode(code);
        if (option == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(option).build();
    }

    // 5) PUT /options/{code}  (le code du chemin fait foi)
    @PUT
    @Path("{code}")
    public Response updateOption(@PathParam("code") int code, Option option) {
        if (option == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        option.setCodeOption(code);
        if (optionBusiness.updateOption(code, option)) {
            return Response.ok(option).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // 4) DELETE /options/{code}
    @DELETE
    @Path("{code}")
    public Response deleteOption(@PathParam("code") int code) {
        if (optionBusiness.deleteOption(code)) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
