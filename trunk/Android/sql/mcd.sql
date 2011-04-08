SET NAMES 'utf8';

drop database if exists gestionnaire_taches;
CREATE DATABASE gestionnaire_taches;
USE gestionnaire_taches;

drop table if exists apourfils;
drop table if exists apourtag;
drop table if exists tache;
drop table if exists tag;
drop table if exists utilisateur;
drop table if exists profil;
drop table if exists etat;
drop table if exists priorite;


create table priorite (
	idPriorite int not null auto_increment,
	libellePriorite varchar(64) not null,
	primary key(idPriorite));

create table etat (
	idEtat int not null auto_increment,
	libelleEtat varchar(64) not null,
	primary key(idEtat));

create table profil (
	idProfil int not null auto_increment,
	libelleProfil varchar(64) not null,
	primary key(idProfil));

create table utilisateur (
	identifiant varchar(64) not null,
	mdPasse varchar (64) not null,
	nom varchar (64),
	prenom varchar (64),
	mail varchar (64),
	idProfil int not null,
	primary key(identifiant),
	foreign key(idProfil) references profil(idProfil));

create table tag (
	idTag int not null,
	libelleTag varchar(64) not null,
	versionTag int not null,
	identifiant varchar(64) not null,
	primary key(idTag, identifiant),
	foreign key(identifiant) references utilisateur(identifiant));
	
create table tache (
	idTache int not null,
	nomTache varchar(255) not null,
	descriptionTache text,
	dateLimite datetime,
	idEtat int not null,
	idPriorite int not null,
	versionTache int not null,
	identifiant varchar(64) not null,
	primary key(idTache, identifiant),
	foreign key(idEtat) references etat(idEtat),
	foreign key(idPriorite) references priorite(idPriorite),
	foreign key(identifiant) references utilisateur(identifiant));

create table apourtag (
	idTache int not null,
	idTag int not null,
	identifiant varchar(64) not null,
	foreign key(idTache) references tache(idTache),
	foreign key(idTag) references tag(idTag),
	foreign key(identifiant) references utilisateur(identifiant),
	primary key(idTache, idTag, identifiant));
	
create table apourfils (
	idPere int not null,
	idFils int not null,
	identifiant varchar(64) not null,
	foreign key(idPere) references tache(idPere),
	foreign key(idFils) references tache(idFils),
	foreign key(identifiant) references utilisateur(identifiant),
	primary key(idPere, idFils, identifiant));

insert into priorite(idPriorite, libellePriorite) values (null,'sans'), (null,'faible'), (null,'urgent'), (null,'ultra urgent');

insert into etat(idEtat, libelleEtat) values (null,'annulé'), (null,'à prévoir'), (null,'en cours'), (null,'terminé');

insert into profil(idProfil, libelleProfil) values (null,'administrateur'), (null,'connecte');

insert into utilisateur(identifiant, mdPasse, nom, prenom, mail, idProfil) values 
	('guillaume','c31b32364ce19ca8fcd150a417ecce58','guillaume','montavon','guillaume.montavon@gmail.com',1),
	('benoit','c31b32364ce19ca8fcd150a417ecce58','benoit','meilhac','guillaume.montavon@gmail.com',1),
	('anonyme','c31b32364ce19ca8fcd150a417ecce58','anonyme','anonyme','guillaume.montavon@gmail.com',2);

insert into tag(idTag, libelleTag, versionTag, identifiant) values 
	(1,'université',1,'guillaume'),
	(2,'personnel',1,'guillaume'),
	(3,'professionnel',1,'guillaume'),
	(4,'examen',1,'guillaume'),
	(1,'université',1,'benoit'),
	(2,'personnel',1,'benoit'),
	(3,'professionnel',1,'benoit'),
	(4,'examen',1,'benoit'),
	(1,'université',1,'anonyme'),
	(2,'personnel',1,'anonyme'),
	(3,'professionnel',1,'anonyme'),
	(4,'examen',1,'anonyme');	
	
insert into tache(idTache, nomTache, descriptionTache, dateLimite, idEtat, idPriorite, versionTache, identifiant) values 
	(1,'test tache 1','description test tache 1','0000-00-00 00:00:00',2,1,1,'guillaume'),
	(2,'test tache 2','description test tache 2','0000-00-00 00:00:00',1,4,1,'guillaume'),
	(1,'test tache 1','description test tache 1','0000-00-00 00:00:00',2,4,1,'benoit'),
	(2,'test tache 2','description test tache 2','0000-00-00 00:00:00',3,3,1,'benoit'),
	(1,'test tache 1','description test tache 1','0000-00-00 00:00:00',4,2,1,'anonyme'),
	(2,'test tache 2','description test tache 2','0000-00-00 00:00:00',3,2,1,'anonyme');

insert into apourtag(idTache, idTag, identifiant) values 
	(1,2,'guillaume'),
	(1,3,'guillaume'),
	(1,4,'guillaume'),
	(2,2,'guillaume'),
	(2,1,'guillaume'),
	(3,3,'guillaume'),
	(4,3,'guillaume'),
	(6,4,'guillaume'),
	(6,1,'guillaume'),
	(4,4,'guillaume'),
	(4,2,'guillaume'),
	(4,1,'guillaume');

insert into apourfils(idPere, idFils, identifiant) values 
	(1,2,'guillaume'),
	(5,6,'anonyme');