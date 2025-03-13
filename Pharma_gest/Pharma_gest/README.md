@startuml
title Diagramme UML - Système de gestion de pharmacie PHARMAGEST

abstract class Utilisateur {

- String identifiant
- String motDePasse
- Role role

+ seConnecter()
+ deconnecter()
  }

class Pharmacien {

+ validerVente()
+ validerCommande()
+ verifierApprovisionnement()
+ modifierQuantiteCommande()
+ validerReceptionCommande()  ' 🔥 Ajout de la validation réception
  }

class Vendeur {

+ effectuerVente()
+ gererPaiement()
  }

class Medicament {

- String nom
- String forme
- double prixAchat
- double prixVente
- int stock
- int seuilCommande
- int qteMax
- Famille famille
- Unite unite
  }

class Famille {

- String nom
  }

class Fournisseur {

- String nom
- String adresse
- String contact
- String email

+ getPrix(Medicament)
  }

class Commande {

- double montant

+ genererCommande()
+ validerCommande()
+ envoyerCommandePDF()
  }

class LigneDeCommande {

- int quantiteVendu
- double prixUnitaire
- int quantiteRecue   ' 🔥 Ajout pour suivre ce qui est réellement reçu
- double prixAchatReel ' 🔥 Ajout pour stocker le prix réel de la réception
- double prixVenteReel ' 🔥 Ajout pour stocker le prix réel de la réception
  }

class Vente {

- Date dateVente
- double montantTotal
- TypeVente typeVente

+ effectuerVente()
  }

class LigneVente {

- int quantiteVendu
- double prixUnitaire
  }

class Facture {

- Date dateEmission
- double montantTotal
- String numeroFacture

+ genererFacture()
  }

class Paiement {

- double montant
- String modePaiement
- StatutPaiement statut

+ effectuerPaiement()
  }

class Prescription {

- String nomMedecin
- Date datePrescription

+ getNumeroPrescription()
  }

class Patient {

- String nom
- String prenom
- Date dateNaissance
- String adresse
- String contact
  }

class Livraison {

- Date dateLivraison
- String status

+ suivreLivraison()
+ recevoirCommande()
+ mettreAJourStock()
  }

class LogReception {

- Date dateReception
- int quantiteRecue
- double prixAchat
- double prixVente
- String remarque
  }

enum TypeVente {
Libre
Prescrite
}

enum StatutPaiement {
En_attente
Valide
Rejete
}

enum Role {
Pharmacien
Vendeur
}

class Unite {

- String nomUnite
  }

' --- Relations avec cardinalités et symboles ---

Medicament o-- "1" Famille : "appartient à"
Famille o-- "0..*" Medicament : "contient"

Medicament o-- "1" Fournisseur : "fournisseur habituel"
Fournisseur o-- "0..*" Medicament : "fournit"

Commande *-- "0..*" LigneDeCommande : "contient"
LigneDeCommande *-- "1" Medicament : "réfère à"
Commande --> "1" Fournisseur : "passée à"

Vente *-- "0..*" LigneVente : "contient"
LigneVente *-- "1" Medicament : "réfère à"

Commande --> "1" Pharmacien : "validée par"
Commande --> "1..*" Livraison : "est livrée par"
Livraison --> "1..*" Fournisseur : "effectuée par"

Vente --> "1..*" Medicament : "vendu"
Vente --> "0..1" Prescription : "peut être liée à"
Vente --> "1" Vendeur : "effectuée par"

Prescription --> "1" Patient : "donnée à"
Vente --> "1" Facture : "génère"
Vente --> "1" Paiement : "associée à"
Paiement --> "1" StatutPaiement : "a pour statut"

Utilisateur <|-- Pharmacien : Héritage - Un pharmacien est un utilisateur
Utilisateur <|-- Vendeur : Héritage - Un vendeur est un utilisateur

Vente --> "1..*" TypeVente : "type de vente"
Medicament --> "1" Unite : "a pour unité"

' 🔥 NOUVELLES RELATIONS
Livraison --> "1" Pharmacien : "reçue par"
Livraison *-- "0..*" LigneDeCommande : "contient"
Livraison --> "0..*" LogReception : "trace"
Pharmacien --> "0..*" LogReception : "enregistre"
@enduml