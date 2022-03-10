package com.pauete.PostgreDataGenerator.Tests;

import com.pauete.PostgreDataGenerator.*;
import com.pauete.PostgreDataGenerator.Exceptions.NoSuchTableException;

public class Test1 {

    public static void main(String[] args) {

        DMLReader reader = new DMLReader();
        reader.read("""
                drop table if exists TENER_TOKEN;
                drop table if exists ETIQUETAR;
                drop table if exists CONTENER_EXPANSION;
                drop table if exists CONTENER_INICIAL;
                drop table if exists INCIDENCIA;
                drop table if exists TIPO_DE_INCIDENCIA;
                drop table if exists REVISAR;
                drop table if exists CAPTCHA;
                drop table if exists VOLUNTARIO;
                drop table if exists REVISOR;
                drop table if exists ETIQUETA;
                drop table if exists OCURRENCIA;
                drop table if exists IDIOMA;
                drop table if exists HUMANO;
                drop table if exists IA;
                drop table if exists ETIQUETADOR;
                drop table if exists NIVEL_DE_CONFIANZA;
                drop table if exists IMAGEN;
                drop table if exists EXPANSION;
                drop table if exists TOKEN;
                drop table if exists PROGRAMA;
                                       
                create table PROGRAMA(
                	id SERIAL not null,
                	descripcion VARCHAR(240),
                	fechaInicio DATE,
                	fechaFin DATE not null,
                	limiteImagenes NUMERIC(12),
                	limiteImagenesPorMinuto NUMERIC(6),
                	desarrollador VARCHAR(60) not null,
                	email VARCHAR(30) not null,
                                       
                	constraint PK_PROGRAMA primary key (id),
                	constraint CHECK_PROGRAMA_FECHAINICIO check (fechaInicio > NOW()),
                	constraint CHECK_PROGRAMA_FECHAFIN check (fechaFin > NOW() AND fechaFin > fechaInicio)
                );
                                       
                create table TOKEN(
                	id SERIAL not null,
                	hashKey VARCHAR(64) not null,
                	clavePrivada VARCHAR(64) not null,
                                       
                	constraint PK_TOKEN primary key (id)
                );
                                       
                create table EXPANSION(
                	id SERIAL not null,
                	programa INTEGER not null,
                                       
                	constraint PK_EXPANSION primary key (id),
                	constraint FK_EXPANSION_PROGRAMA foreign key (programa) references PROGRAMA (id),
                	constraint UK_EXPANSION unique (programa)
                );
                                       
                create table IMAGEN(
                	id SERIAL not null,
                	ruta VARCHAR(128) not null,
                	fecha DATE,
                                       
                	constraint PK_IMAGEN primary key (id)
                );
                                       
                create table NIVEL_DE_CONFIANZA(
                	nivel VARCHAR(16) not null,
                                       
                	constraint PK_CONFIANZA primary key (nivel)
                );
                                       
                create table ETIQUETADOR(
                	id SERIAL not null,
                	fechaPrimerEtiquetado DATE,
                	etiquetasCreadas NUMERIC(12),
                	confianza VARCHAR(16) not null,
                                       
                	constraint PK_ETIQUETADOR primary key (id),
                	constraint FK_ETIQUETADOR_NIVEL foreign key (confianza) references NIVEL_DE_CONFIANZA (nivel)
                );
                                       
                create table IA(
                	id INTEGER not null,
                	nombre VARCHAR(30) not null,
                	desarrollador VARCHAR(30),
                	email VARCHAR(30) not null,
                                       
                	constraint PK_IA primary key (id),
                	constraint FK_IA_ETIQUETADOR foreign key (id) references ETIQUETADOR (id)
                );
                                       
                create table HUMANO(
                	id INTEGER not null,
                	usuario VARCHAR(15) not null,
                	email VARCHAR(30) not null,
                                       
                	constraint PK_HUMANO primary key (id),
                	constraint FK_HUMANO_ETIQUETADOR foreign key (id) references ETIQUETADOR (id)
                );
                                       
                create table IDIOMA(
                	codigo VARCHAR(5) not null,
                	idioma VARCHAR(20),
                                       
                	constraint PK_IDIOMA primary key (codigo)
                );
                                       
                create table OCURRENCIA(
                	ocurrencia VARCHAR(20) not null,
                	/* CantidadImagenes se ha eliminado*/
                	superocurrencia VARCHAR(20),
                                       
                	constraint PK_OCURRENCIA primary key (ocurrencia),
                	constraint FK_OCURRENCIA foreign key (superocurrencia) references OCURRENCIA (ocurrencia)
                );
                                       
                create table ETIQUETA(
                	id SERIAL not null,
                	fecha DATE,
                	precision NUMERIC(2),
                	ocurrencia VARCHAR(20) not null,
                                       
                	constraint PK_ETIQUETA primary key (id),
                	constraint FK_ETIQUETA_OCURRENCIA foreign key (ocurrencia) references OCURRENCIA (ocurrencia)
                );
                                       
                create table REVISOR(
                	id SERIAL not null,
                	modificador NUMERIC(2),
                                       
                	constraint PK_REVISOR primary key (id)
                );
                                       
                create table VOLUNTARIO(
                	id INTEGER not null,
                	usuario VARCHAR(15) not null,
                	nombre VARCHAR(30),
                                       
                	constraint PK_VOLUNTARIO primary key (id),
                	constraint FK_VOLUNTARIO_REVISOR foreign key (id) references REVISOR (id)
                );
                                       
                create table CAPTCHA(
                	id INTEGER not null,
                	dominio VARCHAR(40),
                	nombre VARCHAR(30),
                	email VARCHAR(30),
                                       
                	constraint PK_CAPTCHA primary key (id),
                	constraint FK_CAPTCHA_REVISOR foreign key (id) references REVISOR (id)
                );
                                       
                create table REVISAR(
                	id_revisor INTEGER not null,
                	id_etiqueta INTEGER not null,
                	precision NUMERIC(2) not null,
                                       
                	constraint PK_REVISAR primary key (id_revisor, id_etiqueta)
                );
                                       
                create table TIPO_DE_INCIDENCIA(
                	codigo VARCHAR(30) not null,
                                       
                	constraint PK_TIPOINCIDENCIA primary key (codigo)
                );
                                       
                create table INCIDENCIA(
                	id SERIAL not null,
                	detalles VARCHAR(50),
                	tipo VARCHAR(30) not null,
                	id_revisor INTEGER not null,
                	id_etiqueta INTEGER not null,
                                       
                	constraint PK_INCIDENCIA primary key (id),
                	constraint FK_INCIDENCIA_TIPO foreign key (tipo) references TIPO_DE_INCIDENCIA (codigo),
                	constraint FK_INCIDENCIA_REVISAR foreign key (id_revisor, id_etiqueta) references REVISAR (id_revisor, id_etiqueta)
                );
                                       
                create table CONTENER_INICIAL(
                	ocurrencia VARCHAR(20) not null,
                	id_programa INTEGER not null,
                                       
                	constraint PK_CONTENERINICIAL primary key (ocurrencia, id_programa),
                	constraint FK_CONTENERINICIAL_OCURRENCIA foreign key (ocurrencia) references OCURRENCIA (ocurrencia),
                	constraint FK_CONTENERINICIAL_PROGRAMA foreign key (id_programa) references PROGRAMA (id)
                );
                                       
                create table CONTENER_EXPANSION(
                	ocurrencia VARCHAR(20) not null,
                	id_expansion INTEGER not null,
                                       
                	constraint PK_CONTENEREXPANSION primary key (ocurrencia, id_expansion),
                	constraint FK_CONTENEREXPANSION_OCURRENCIA foreign key (ocurrencia) references OCURRENCIA (ocurrencia),
                	constraint FK_CONTENEREXPANSION_PROGRAMA foreign key (id_expansion) references EXPANSION (id)
                );
                                       
                create table ETIQUETAR(
                	etiqueta INTEGER not null,
                	etiquetador INTEGER not null,
                	imagen INTEGER not null,
                                       
                	constraint PK_ETIQUETAR primary key (etiqueta, etiquetador),
                	constraint UK_ETIQUETAR unique (etiqueta, imagen),
                	constraint FK_ETIQUETAR_ETIQUETA foreign key (etiqueta) references ETIQUETA (id),
                	constraint FK_ETIQUETAR_ETIQUETADOR foreign key (etiquetador) references ETIQUETADOR (id),
                	constraint FK_ETIQUETAR_IMAGEN foreign key (imagen) references IMAGEN (id)
                );
                                       
                create table TENER_TOKEN(
                	token INTEGER not null,
                	programa INTEGER not null,
                                       
                	constraint PK_TENER_TOKEN primary key (token),
                	constraint UK_TENER_TOKEN unique (programa),
                	constraint FK_TENER_TOKEN_TOKEN foreign key (token) references TOKEN (id),
                	constraint FK_TENER_TOKEN_PROGRAMA foreign key (programa) references PROGRAMA (id)
                );
                """);

        reader.generateDatabase();


        DataGenerator generator = new DataGenerator(reader.getDatabase());
        String presets = generator.generateAllPresets(3);
        System.out.println(presets);
//        try {
//            String preset = generator.generatePreset("programa");
//            System.out.println(preset);
//        } catch (NoSuchTableException e) {
//            System.out.println("Specified table does not exist into the current database");
//        }
    }

}
