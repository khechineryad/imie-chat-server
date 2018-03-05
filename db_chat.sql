#------------------------------------------------------------
#        Script MySQL.
#------------------------------------------------------------


#------------------------------------------------------------
# Table: Utilisateur
#------------------------------------------------------------

CREATE TABLE Utilisateur(
        id_utilisateur int (11) Auto_increment  NOT NULL ,
        pseudo         Varchar (255) ,
        email          Varchar (255) ,
        mot_de_passe   Varchar (255) ,
        PRIMARY KEY (id_utilisateur )
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Groupe
#------------------------------------------------------------

CREATE TABLE Groupe(
        id_groupe      int (11) Auto_increment  NOT NULL ,
        nom_groupe     Varchar (255) ,
        id_utilisateur Int ,
        PRIMARY KEY (id_groupe )
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Message
#------------------------------------------------------------

CREATE TABLE Message(
        id_message     int (11) Auto_increment  NOT NULL ,
        contenu        Text ,
        message_date   Datetime ,
        id_utilisateur Int ,
        id_groupe      Int ,
        PRIMARY KEY (id_message )
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: Rejoint
#------------------------------------------------------------

CREATE TABLE Rejoint(
        id_utilisateur Int NOT NULL ,
        id_groupe      Int NOT NULL ,
        PRIMARY KEY (id_utilisateur ,id_groupe )
)ENGINE=InnoDB;

ALTER TABLE Groupe ADD CONSTRAINT FK_Groupe_id_utilisateur FOREIGN KEY (id_utilisateur) REFERENCES Utilisateur(id_utilisateur);
ALTER TABLE Message ADD CONSTRAINT FK_Message_id_utilisateur FOREIGN KEY (id_utilisateur) REFERENCES Utilisateur(id_utilisateur);
ALTER TABLE Message ADD CONSTRAINT FK_Message_id_groupe FOREIGN KEY (id_groupe) REFERENCES Groupe(id_groupe);
ALTER TABLE Rejoint ADD CONSTRAINT FK_Rejoint_id_utilisateur FOREIGN KEY (id_utilisateur) REFERENCES Utilisateur(id_utilisateur);
ALTER TABLE Rejoint ADD CONSTRAINT FK_Rejoint_id_groupe FOREIGN KEY (id_groupe) REFERENCES Groupe(id_groupe);
