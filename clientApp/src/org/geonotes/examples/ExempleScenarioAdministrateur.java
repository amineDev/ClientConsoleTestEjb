package org.geonotes.examples;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.geonotes.entities.Administrateur;
import org.geonotes.entities.Categorie;
import org.geonotes.entities.Note;
import org.geonotes.entities.Parcour;
import org.geonotes.entities.Utilisateur;
import org.geonotes.exceptions.GeoNotesException;
import org.geonotes.interfaces.AdminServiceRemote;
import org.geonotes.interfaces.CategorieServiceRemote;
import org.geonotes.interfaces.NoteServiceRemote;
import org.geonotes.interfaces.ParcourServiceRemote;
import org.geonotes.interfaces.UtilisateurServiceRemote;

/**
 * 
 */

/**
 * @author Amine 
 * L'exemple montre le sc�nario que l'administrateur va effectuer
 */

public class ExempleScenarioAdministrateur {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


		InitialContext ctx;
		try {
			ctx = new InitialContext ();

			//R�cup�ration des EJB
			AdminServiceRemote adminService = (AdminServiceRemote) ctx.lookup("AdminServiceBean");
			ParcourServiceRemote parcourService = (ParcourServiceRemote) ctx.lookup("ParcourServiceBean");
			NoteServiceRemote noteService = (NoteServiceRemote) ctx.lookup("NoteServiceBean");
			CategorieServiceRemote categorieService = (CategorieServiceRemote) ctx.lookup("CategorieServiceBean");

			System.out.println("R�cup�ration des EJB a distance effectu�");



			//Cr�ation d'un admin
			Administrateur admin = null;
			admin = adminService.create(new Administrateur("aecd@gstxaidl.com", "TI", "passw", "AMINE"));

			System.out.println("Administrateur cr�� avec succ�s ID : " + admin.getId());

			//Cr�ation d'une cat�gorie

			Categorie categorie = categorieService.create(new Categorie("description","Touristique"));


			System.out.println("Cat�gorie cr�� avec succ�s ID : " + categorie.getId());

			//Cr�ation des Notes
			Note n1 = new Note("commentaire", new Date(),49.433333,4.4,"Caf�","Saint Etienne" ,categorie);
			Note n2 = new Note("commentaire", new Date(),45.433333,4.5,"Cin�ma","Saint Etienne", categorie);

			n1.setAdministrateur(admin);
			n2.setAdministrateur(admin);

			Note note1 =noteService.create(n1);
			Note note2 =noteService.create(n2);

			System.out.println("Les notes sont cr��s avec succ�s ");

			//Cr�ation d'un Parcours			
			Parcour parcour = new Parcour("commentaire", new Date(), 0.0, 0.0, "MonParcour", admin);
			HashSet<Note> notes = new HashSet<Note>();
			notes.add(note1);
			notes.add(note2);
			parcour.setNotes(notes);

			Parcour parcourCreated = parcourService.create(parcour);

			System.out.println("Parcours cr��s avec succ�s ID : " + parcourCreated.getId());


			/**
			 * Optionnel (bonus) : modifier un parcours existant
			 */

			//Maintenant on suppose que l'administrateur veut modifier un Parcours
			//On r�cup�re le Parcours
			Parcour parcourRech = parcourService.findById(parcourCreated.getId());

			//Il supprime la derni�re note du parcours et le remplace avec une nouvelle note
			ArrayList<Note> listeNotes =  new ArrayList<Note>();
			listeNotes.addAll(parcourRech.getNotes());
			listeNotes.remove(1);
			listeNotes.add(new Note("Nouvelle Note", new Date(),45.42333,4.5,"Mus�e","Saint Etienne", categorie));

			parcour.setNotes(new HashSet<Note>(listeNotes));
			//Modification
			parcourService.update(parcourRech);			

			System.out.println("Parcours Modifi� avec succ�s ");


			/**
			 * Optionnel (bonus) : afficher les statistiques d�emploi 
			 * des parcours par les utilisateurs finaux
			 */

			UtilisateurServiceRemote utilisateurService = (UtilisateurServiceRemote) ctx.lookup("UtilisateurServiceBean");

			//On va cr��r 2 Utilisateurs qui vont effectuer ce Parcours

			Utilisateur user1 = utilisateurService.create(new Utilisateur("ttgxt@gcmail.com", "user13", "password", "prenom"));
			Utilisateur user2 = utilisateurService.create(new Utilisateur("tetds25@gcmail.com", "user23", "password", "prenom"));

			System.out.println("Utilisateurs cr�es avec succ�s ");
			//Maintenant les utilisateurs vont effectuer le parcours
			user1.getParcours().add(parcourRech);
			user2.getParcours().add(parcourRech);
			System.out.println("Maj : les utilisateurs ont effectu� le parcours ");

			//Mettre � jour
			utilisateurService.update(user1);
			utilisateurService.update(user2);

			// on r�cup�re les statistiques d'emploi de ce parcours 

			int nb = parcourService.getStatistiques(parcourRech.getId());

			System.out.println("Ce parcours a �t� effectu�  " + nb + " fois");

			List<Utilisateur> listeUsers = parcourService.getStatistiquesUsers(parcourRech.getId());

			System.out.println("Les Utilisateurs qui ont effectu� ce parcours sont :");
			for (Utilisateur utilisateur : listeUsers) {
				System.out.println("  --->> L'utilisateur id : " + utilisateur.getId() +" Email : " + utilisateur.getEmail());
			}

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());

		}catch (GeoNotesException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getCause().getMessage());
		}


	}

}
