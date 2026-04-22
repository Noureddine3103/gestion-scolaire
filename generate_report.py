import docx
from docx.shared import Pt, RGBColor, Inches
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml.ns import qn

def add_heading(doc, text, level):
    heading = doc.add_heading(text, level=level)
    run = heading.runs[0]
    run.font.name = 'Segoe UI'
    if level == 1:
        run.font.color.rgb = RGBColor(0x3A, 0x0C, 0xA3) # Primary Dark Blue
        run.font.size = Pt(24)
    elif level == 2:
        run.font.color.rgb = RGBColor(0x43, 0x61, 0xEE) # Vibrant Blue
        run.font.size = Pt(18)
    elif level == 3:
        run.font.color.rgb = RGBColor(0x2B, 0x2B, 0x2B) # Dark gray
        run.font.size = Pt(14)
    return heading

def add_paragraph(doc, text, bold=False, italic=False):
    p = doc.add_paragraph()
    run = p.add_run(text)
    run.font.name = 'Calibri'
    run.font.size = Pt(11)
    run.font.color.rgb = RGBColor(0x40, 0x40, 0x40)
    run.bold = bold
    run.italic = italic
    p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    return p

def add_bullet(doc, text):
    p = doc.add_paragraph(style='List Bullet')
    run = p.add_run(text)
    run.font.name = 'Calibri'
    run.font.size = Pt(11)
    return p

def main():
    doc = docx.Document()
    
    # Set default style
    style = doc.styles['Normal']
    font = style.font
    font.name = 'Calibri'
    font.size = Pt(11)

    # Title Page
    doc.add_picture('src/main/resources/com/school/gestion/images/logo.png', width=Inches(2.0)) if False else None # Placeholder if logo exists
    title = doc.add_paragraph()
    title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    title_run = title.add_run("\n\n\n\nRapport de Projet Universitaire\nSystème de Gestion Scolaire\n")
    title_run.font.name = 'Segoe UI'
    title_run.font.size = Pt(32)
    title_run.font.bold = True
    title_run.font.color.rgb = RGBColor(0x3A, 0x0C, 0xA3)
    
    subtitle = doc.add_paragraph()
    subtitle.alignment = WD_ALIGN_PARAGRAPH.CENTER
    sub_run = subtitle.add_run("Une application moderne de gestion pour les établissements d'enseignement\n\n\n\n")
    sub_run.font.size = Pt(16)
    sub_run.font.color.rgb = RGBColor(0x75, 0x75, 0x75)

    doc.add_page_break()

    # Introduction
    add_heading(doc, '1. Introduction', 1)
    add_paragraph(doc, "Le présent rapport décrit en détail les fonctionnalités, l'architecture et les choix techniques du projet \"Gestion Scolaire\". Il s'agit d'une application de bureau développée en JavaFX destinée à simplifier et automatiser les processus de gestion au sein d'un établissement scolaire. L'application propose une interface moderne, intuitive et sécurisée, séparant les accès selon deux profils principaux : l'Administrateur et l'Enseignant.")

    # Architecture et Technologies
    add_heading(doc, '2. Architecture et Choix Technologiques', 1)
    add_paragraph(doc, "L'application a été conçue selon une architecture multicouche (Modèle-Vue-Contrôleur) afin de garantir une séparation claire entre la logique métier, l'accès aux données et l'interface utilisateur.")
    
    add_heading(doc, '2.1 Technologies Utilisées', 2)
    add_bullet(doc, "Langage de programmation : Java 17")
    add_bullet(doc, "Interface Graphique : JavaFX 21 avec FXML et CSS3 pour un design moderne (Thème Ultra-Modern Professional)")
    add_bullet(doc, "Base de données : MySQL / MariaDB (Compatible XAMPP) ou SQL Server")
    add_bullet(doc, "Accès aux données : JDBC")
    add_bullet(doc, "Sécurité : BCrypt pour le hachage sécurisé des mots de passe")
    add_bullet(doc, "Gestion de projet : Maven")

    # Fonctionnalités par Rôle
    add_heading(doc, '3. Fonctionnalités de l\'Application', 1)

    # Administrateur
    add_heading(doc, '3.1 Espace Administrateur', 2)
    add_paragraph(doc, "L'administrateur dispose des pleins droits sur le système et gère la structure globale de l'établissement.")
    
    add_bullet(doc, "Tableau de Bord : Vue d'ensemble avec des indicateurs statistiques clés (nombre total d'élèves, de classes et d'enseignants).")
    add_bullet(doc, "Gestion des Élèves : Ajout de nouveaux élèves avec leurs informations personnelles, recherche, et visualisation de la liste complète.")
    add_bullet(doc, "Gestion des Enseignants : Enregistrement des enseignants, de leurs grades et de leurs coordonnées.")
    add_bullet(doc, "Gestion des Niveaux et des Classes : Création des niveaux scolaires (ex: 1ère Année) et des classes. Le système intègre un suivi visuel du remplissage des classes (barre de progression).")
    add_bullet(doc, "Gestion des Matières : Définition des matières enseignées et de leurs coefficients respectifs.")
    add_bullet(doc, "Inscriptions et Affectations : Affectation des élèves non inscrits vers les classes disponibles, avec un contrôle strict de la capacité maximale (20 élèves par classe).")
    add_bullet(doc, "Saisie des Notes Globale : Possibilité pour l'administration de consulter et de saisir des notes si nécessaire.")
    add_bullet(doc, "Génération des Bulletins : Module permettant la génération des bulletins de notes par trimestre et par classe.")

    # Enseignant
    add_heading(doc, '3.2 Espace Enseignant', 2)
    add_paragraph(doc, "L'interface enseignant est centrée sur le suivi pédagogique des classes qui lui sont affectées.")
    
    add_bullet(doc, "Tableau de Bord Enseignant : Résumé rapide de ses classes, matières et du nombre total de ses élèves.")
    add_bullet(doc, "Saisie des Notes : Interface ergonomique sous forme de grille permettant la saisie rapide des notes (Devoir, Examen, Composition) pour une classe et une matière spécifiques. Le système gère l'enregistrement en masse.")
    add_bullet(doc, "Mes Élèves : Consultation de la liste des élèves inscrits dans les classes de l'enseignant.")

    # Règles Métier
    add_heading(doc, '4. Règles Métier et Sécurité', 1)
    add_paragraph(doc, "Le système intègre plusieurs contraintes pour assurer la cohérence et la sécurité des données :")
    
    add_bullet(doc, "Capacité des classes : Limitée strictement à 20 élèves. Des triggers au niveau de la base de données (et des vérifications côté code) empêchent toute affectation au-delà de cette limite.")
    add_bullet(doc, "Authentification sécurisée : Tous les mots de passe sont hachés avec l'algorithme BCrypt. Lors de la connexion, le système vérifie le profil (Admin/Enseignant) pour rediriger vers le tableau de bord approprié.")
    add_bullet(doc, "Contrôle de saisie : Les notes saisies doivent obligatoirement être comprises entre 0 et 20.")
    add_bullet(doc, "Année scolaire active : Toutes les inscriptions et saisies de notes sont automatiquement rattachées à l'année scolaire définie comme 'active' dans le système.")

    # Structure Base de données
    add_heading(doc, '5. Modèle de Données', 1)
    add_paragraph(doc, "La base de données relationnelle est structurée autour de plusieurs entités clés :")
    add_bullet(doc, "UTILISATEUR : Stocke les accès et les rôles.")
    add_bullet(doc, "ELEVE & ENSEIGNANT : Contiennent les données signalétiques.")
    add_bullet(doc, "CLASSE, NIVEAU & MATIERE : Définissent la structure pédagogique.")
    add_bullet(doc, "INSCRIPTION : Gère l'historique des affectations des élèves aux classes par année scolaire.")
    add_bullet(doc, "NOTE : Centralise les évaluations trimestrielles.")

    add_heading(doc, '6. Conclusion', 1)
    add_paragraph(doc, "Ce projet de gestion scolaire fournit une solution complète, robuste et évolutive. L'utilisation de JavaFX avec un design soigné offre une excellente expérience utilisateur, tandis que l'architecture backend et la base de données garantissent l'intégrité et la pérennité des informations de l'établissement.")

    doc.save('Rapport_Gestion_Scolaire.docx')
    print("Document Word généré avec succès : Rapport_Gestion_Scolaire.docx")

if __name__ == '__main__':
    main()
