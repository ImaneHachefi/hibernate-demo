package main;

import main.model.Categorie;
import main.model.Produit;
import org.h2.tools.Server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        try {
            Server.createWebServer("-web", "-webPort", "8082").start();
            System.out.println("Console H2 disponible sur : http://localhost:8082");
        } catch (Exception e) {
            System.out.println("Erreur lors du démarrage de la console H2");
            e.printStackTrace();
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-demo");

        insererProduits(emf);
        lireProduits(emf);

        // Pause pour garder la base en mémoire ouverte (captures H2)
        System.out.println("\nAppuie sur Entrée pour quitter...");
        new Scanner(System.in).nextLine();

        emf.close();
    }

    private static void insererProduits(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Categorie informatique = new Categorie("Informatique");
            Categorie mobile = new Categorie("Mobile");

            informatique.ajouterProduit(new Produit("Laptop", new BigDecimal("999.99")));
            mobile.ajouterProduit(new Produit("Smartphone", new BigDecimal("499.99")));
            mobile.ajouterProduit(new Produit("Tablette", new BigDecimal("299.99")));

            // cascade = ALL : les produits sont persistés avec leur catégorie
            em.persist(informatique);
            em.persist(mobile);

            em.getTransaction().commit();
            System.out.println("Catégories et produits insérés avec succès !");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void lireProduits(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            // 1. Catégories avec leurs produits
            System.out.println("\nCatégories et leurs produits :");
            List<Categorie> categories = em.createQuery(
                    "SELECT DISTINCT c FROM Categorie c LEFT JOIN FETCH c.produits",
                    Categorie.class).getResultList();
            for (Categorie c : categories) {
                System.out.println(c);
                for (Produit p : c.getProduits()) {
                    System.out.println("   - " + p);
                }
            }

            // 2. Tous les produits
            System.out.println("\nListe des produits :");
            List<Produit> produits = em.createQuery("SELECT p FROM Produit p", Produit.class)
                    .getResultList();
            for (Produit produit : produits) {
                System.out.println(produit);
            }

            // 3. Produits d'une catégorie
            System.out.println("\nProduits de la catégorie 'Mobile' :");
            em.createQuery("SELECT p FROM Produit p WHERE p.categorie.nom = :nom", Produit.class)
                    .setParameter("nom", "Mobile")
                    .getResultList()
                    .forEach(System.out::println);

            // 4. Recherche par ID
            System.out.println("\nRecherche du produit avec ID=2 :");
            Produit produit = em.find(Produit.class, 2L);
            System.out.println(produit != null ? produit : "Produit non trouvé");
        } finally {
            em.close();
        }
    }
}