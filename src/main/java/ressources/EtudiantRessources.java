package ressources;

import entities.Etudiant;
import entities.EtudiantList;
import entities.Option;
import metiers.EtudiantBusiness;
import metiers.OptionBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("etudiants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EtudiantRessources {

    private final EtudiantBusiness etudiantBusiness = new EtudiantBusiness();
    private final OptionBusiness optionBusiness = new OptionBusiness();

    // 1) POST /etudiants
    @POST
    public Response addEtudiant(Etudiant etudiant) {
        if (etudiant == null || etudiant.getOption() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (etudiantBusiness.getEtudiantByIdentifiant(etudiant.getIdentifiant()) != null) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        // addEtudiant retourne false si l'option n'existe pas
        if (etudiantBusiness.addEtudiant(etudiant)) {
            return Response.status(Response.Status.CREATED).entity(etudiant).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // 2) GET /etudiants
    @GET
    public Response getAllEtudiants() {
        List<Etudiant> liste = etudiantBusiness.getAllEtudiants();
        return Response.ok(liste).build();
    }

    // 6) GET /etudiants/option?codeOption=1   (réponse XML)
    // Le chemin littéral "option" est prioritaire sur le modèle {identifiant}
    @GET
    @Path("option")
    @Produces(MediaType.APPLICATION_XML)
    public Response getEtudiantsByOption(@QueryParam("codeOption") int codeOption) {
        Option option = optionBusiness.getOptionByCode(codeOption);
        if (option == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Etudiant> liste = etudiantBusiness.getEtudiantsByOption(option);
        return Response.ok(new EtudiantList(liste)).build();
    }

    // 3) GET /etudiants/{identifiant}
    @GET
    @Path("{identifiant}")
    public Response getEtudiantByIdentifiant(@PathParam("identifiant") String identifiant) {
        Etudiant etudiant = etudiantBusiness.getEtudiantByIdentifiant(identifiant);
        if (etudiant == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(etudiant).build();
    }

    // 5) PUT /etudiants/{identifiant}  (l'identifiant du chemin fait foi)
    @PUT
    @Path("{identifiant}")
    public Response updateEtudiant(@PathParam("identifiant") String identifiant, Etudiant etudiant) {
        if (etudiant == null || etudiant.getOption() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Option option = optionBusiness.getOptionByCode(etudiant.getOption().getCodeOption());
        if (option == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        etudiant.setIdentifiant(identifiant);
        etudiant.setOption(option);
        if (etudiantBusiness.updateEtudiant(identifiant, etudiant)) {
            return Response.ok(etudiant).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // 4) DELETE /etudiants/{identifiant}
    @DELETE
    @Path("{identifiant}")
    public Response deleteEtudiant(@PathParam("identifiant") String identifiant) {
        if (etudiantBusiness.deleteEtudiant(identifiant)) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
