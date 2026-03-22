module com.campusdocs.server {
    requires java.sql;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.web;
    requires jakarta.persistence;
    requires spring.data.jpa;
    requires spring.beans;
    exports com.campusdocs.server;
}