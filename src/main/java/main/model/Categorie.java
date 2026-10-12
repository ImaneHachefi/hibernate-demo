package main.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produit> produits = new ArrayList<>();

    public Categorie() {}

    public Categorie(String nom) { this.nom = nom; }


    public void ajouterProduit(Produit p) {
        produits.add(p);
        p.setCategorie(this);
    }

    public void retirerProduit(Produit p) {
        produits.remove(p);
        p.setCategorie(null);
    }


    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public List<Produit> getProduits() { return produits; }


    @Override
    public String toString() {
        return "Categorie{id=" + id + ", nom='" + nom + "'}";
    }
}