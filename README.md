# TP JPA / Hibernate avec H2

## Description
Projet Maven illustrant JPA avec Hibernate et une base H2 en mémoire :
configuration, entités `Produit` et `Categorie`, relation OneToMany / ManyToOne,
insertion, lecture (JPQL, `find`) et vérification des données en SQL via la console H2.

## Technologies
- Java 8
- Maven
- Hibernate 5.6.5.Final (JPA 2.2)
- H2 2.1.214 (en mémoire)
- SLF4J Simple
- IntelliJ IDEA


## Configuration
Unité de persistance `hibernate-demo` :
- base `jdbc:h2:mem:testdb`, utilisateur `sa`, mot de passe vide
- `hibernate.hbm2ddl.auto=update` : tables créées automatiquement
- `hibernate.show_sql=true` : requêtes SQL affichées dans la console


## Modèle de données

| Entité | Annotation | Rôle |
|--------|-----------|------|
| `Categorie` | `@OneToMany(mappedBy = "categorie", cascade = ALL, orphanRemoval = true)` | côté inverse, liste des produits |
| `Produit` | `@ManyToOne` + `@JoinColumn(name = "categorie_id")` | côté propriétaire, clé étrangère |

- La clé étrangère `categorie_id` se trouve dans la table `PRODUIT`.
- `cascade = ALL` : persister une catégorie persiste ses produits.
- `orphanRemoval = true` : un produit retiré de la liste est supprimé.
- `ajouterProduit()` / `retirerProduit()` synchronisent les deux côtés.
- `JOIN FETCH` dans la requête JPQL évite le problème des requêtes N+1.

## Résultats

### 1. Console IntelliJ : démarrage et création des tables
Hibernate crée les tables `Categorie` et `Produit` (clé étrangère `categorie_id`).

<img width="1773" height="731" alt="Capture d&#39;écran 2026-10-13 004025" src="https://github.com/user-attachments/assets/c20f3791-fa73-4ea7-b72b-065905453b35" />

### 2. Console IntelliJ : insertion
Insertion des catégories et des produits (requêtes `insert` générées par Hibernate).

<img width="1229" height="798" alt="image" src="https://github.com/user-attachments/assets/9af5db45-a72f-46a0-8f22-fd44fdf7b076" />
<img width="1058" height="227" alt="image" src="https://github.com/user-attachments/assets/a4a976bb-07fa-4cb6-832f-ec73323d68d8" />


### 3. Console IntelliJ : lecture
Affichage des catégories avec leurs produits, de la liste des produits, des
produits de la catégorie « Mobile » et de la recherche par ID.

<img width="1086" height="692" alt="image" src="https://github.com/user-attachments/assets/a4e8bdbc-415c-425c-8ace-27ef639baa51" />
<img width="1050" height="822" alt="image" src="https://github.com/user-attachments/assets/32fae623-d01f-45e2-b91f-4802bb9eaba8" />
<img width="1277" height="151" alt="image" src="https://github.com/user-attachments/assets/dcfd665c-892f-4901-8bb1-10d91183baba" />


Sortie attendue :
```
Catégories et produits insérés avec succès !

Catégories et leurs produits :
Categorie{id=1, nom='Informatique'}
   - Produit{id=1, nom='Laptop', prix=999.99}
Categorie{id=2, nom='Mobile'}
   - Produit{id=2, nom='Smartphone', prix=499.99}
   - Produit{id=3, nom='Tablette', prix=299.99}

Produits de la catégorie 'Mobile' :
Produit{id=2, nom='Smartphone', prix=499.99}
Produit{id=3, nom='Tablette', prix=299.99}

Recherche du produit avec ID=2 :
Produit{id=2, nom='Smartphone', prix=499.99}
```

### 4. Console H2 : connexion
Connexion sur http://localhost:8082 avec `jdbc:h2:mem:testdb`.

<img width="1396" height="424" alt="Capture d&#39;écran 2026-10-13 003844" src="https://github.com/user-attachments/assets/81d145b3-49af-4f1e-af59-9e305fb75804" />

### 5. Console H2 : tables CATEGORIE et PRODUIT
```sql
SELECT * FROM CATEGORIE;
SELECT * FROM PRODUIT;
```
<img width="1908" height="1079" alt="Capture d&#39;écran 2026-10-13 003144" src="https://github.com/user-attachments/assets/fc592687-ceae-416f-9dae-38df361f140e" />

<img width="1919" height="1079" alt="Capture d&#39;écran 2026-10-08 142431" src="https://github.com/user-attachments/assets/7f951ea4-59b8-43ae-89a4-0ad439387157" />

### 6. Console H2 : jointure produits / catégories
```sql
SELECT p.id, p.nom AS produit, p.prix, c.nom AS categorie
FROM PRODUIT p
JOIN CATEGORIE c ON p.categorie_id = c.id;
```
<img width="1918" height="1079" alt="Capture d&#39;écran 2026-10-13 003211" src="https://github.com/user-attachments/assets/ac809935-0210-4dad-bc20-76c3e321ed8a" />

### 7. Console H2 : produits de la catégorie Mobile
```sql
SELECT p.*
FROM PRODUIT p
JOIN CATEGORIE c ON p.categorie_id = c.id
WHERE c.nom = 'Mobile';
```
<img width="1919" height="1079" alt="Capture d&#39;écran 2026-10-13 003230" src="https://github.com/user-attachments/assets/838302a4-389d-4661-86dc-4ce98eee08c0" />

### 8. Console H2 : nombre de produits par catégorie
```sql
SELECT c.nom, COUNT(p.id) AS nb_produits
FROM CATEGORIE c
LEFT JOIN PRODUIT p ON p.categorie_id = c.id
GROUP BY c.nom;
```
<img width="1919" height="1079" alt="Capture d&#39;écran 2026-10-13 003249" src="https://github.com/user-attachments/assets/065f85e0-deb8-4617-ae87-acbd14909700" />

### 9. Console H2 : structure de la table PRODUIT
```sql
SHOW COLUMNS FROM PRODUIT;
```
<img width="1919" height="1079" alt="Capture d&#39;écran 2026-10-13 003306" src="https://github.com/user-attachments/assets/e32f4d34-95f0-45b9-8454-50a7fa411f5b" />

## Conclusion
Ce TP a permis de configurer Hibernate avec H2, de créer des entités JPA,
de modéliser une relation OneToMany / ManyToOne bidirectionnelle, de réaliser
des insertions et lectures avec JPQL, puis de vérifier les données en SQL.
