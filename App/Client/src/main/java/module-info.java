module com.campusdocs.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.web;
    requires spring.beans;
    requires spring.data.jpa;
    requires spring.data.commons;
    requires spring.context;
    requires org.apache.tomcat.embed.core;
    requires spring.security.core;
    requires jjwt.api;
    requires spring.security.config;
    requires spring.security.web;
    requires spring.security.crypto;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires io;
    requires kernel;
    requires layout;
    requires flying.saucer.pdf;
    requires itext;
    requires jakarta.persistence;

    opens com.campusdocs.client to javafx.fxml;
    exports com.campusdocs.client;
}
