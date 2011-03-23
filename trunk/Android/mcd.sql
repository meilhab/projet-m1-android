SET NAMES 'latin1';

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
	idTag int not null auto_increment,
	libelleTag varchar(64) not null,
	identifiant varchar(64) not null,
	primary key(idTag),
	foreign key(identifiant) references utilisateur(identifiant));
	
create table tache (
	idTache int not null auto_increment,
	nomTache varchar(255) not null,
	descriptionTache text,
	dateLimite datetime,
	idEtat int not null,
	idPriorite int not null,
	identifiant varchar(64) not null,
	primary key(idTache),
	foreign key(idEtat) references etat(idEtat),
	foreign key(idPriorite) references priorite(idPriorite),
	foreign key(identifiant) references utilisateur(identifiant));

create table apourtag (
	idTache int not null,
	idTag int not null,
	foreign key(idTache) references tache(idTache),
	foreign key(idTag) references tag(idTag),
	primary key(idTache, idTag));
	
create table apourfils (
	idPere int not null,
	idFils int not null,
	foreign key(idPere) references tache(idPere),
	foreign key(idFils) references tache(idFils),
	primary key(idPere, idFils));

insert into tag(idTag, libelleTag) values (null,'université'), (null,'personnel'), (null,'professionnel'), (null,'examen');

insert into priorite(idPriorite, libellePriorite) values (null,'sans'), (null,'faible'), (null,'urgent'), (null,'ultra urgent');

insert into etat(idEtat, libelleEtat) values (null,'annulé'), (null,'à prévoir'), (null,'en cours'), (null,'terminé');

insert into profil(idProfil, libelleProfil) values (null,'administrateur'), (null,'connecte');

insert into utilisateur(identifiant, mdPasse, nom, prenom, mail, idProfil) values 
	('guillaume','android','guillaume','montavon','guillaume.montavon@gmail.com',1),
	('benoit','andoid','benoit','meilhac','guillaume.montavon@gmail.com',1),
	('anonyme','andoid','anonyme','anonyme','guillaume.montavon@gmail.com',2);

insert into tache(idTache, nomTache, descriptionTache, dateLimite, idEtat, idPriorite, identifiant) values 
	(null,'test tache 1','description test tache 1',null,2,1,'guillaume'),
	(null,'test tache 2','description test tache 2',null,1,4,'guillaume'),
	(null,'test tache 3','description test tache 3',null,2,4,'benoit'),
	(null,'test tache 4','description test tache 4',null,3,3,'benoit'),
	(null,'test tache 5','description test tache 5',null,4,2,'anonyme'),
	(null,'test tache 6','description test tache 6',null,3,2,'anonyme');

insert into apourtag(idTache, idTag) values 
	(1,2),
	(1,3),
	(1,4),
	(2,2),
	(2,1),
	(3,3),
	(4,3),
	(6,4),
	(6,1),
	(4,4),
	(4,2),
	(4,1);

insert into apourfils(idPere, idFils) values 
	(1,2),
	(5,6);