package com.pulpapp.msorders.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuración explícita de Liquibase para ms-orders.
 *
 * Por qué existe esta clase:
 * Spring Boot 4.x auto-configura Liquibase solo si detecta la dependencia
 * en el classpath Y la propiedad spring.liquibase.enabled=true.
 * Esta configuración explícita garantiza que SpringLiquibase se inicialice
 * correctamente y ejecute el changelog antes de que Hibernate valide el esquema.
 *
 * Orden de inicialización:
 * 1. DataSource (conexión a PostgreSQL)
 * 2. SpringLiquibase (crea/actualiza tablas via changeSets)
 * 3. Hibernate EntityManagerFactory (valida tablas existentes)
 * 4. Spring MVC (levanta endpoints REST)
 */
@Configuration
public class LiquibaseConfig {

    /**
     * Ruta al changelog maestro dentro del classpath.
     * Valor por defecto: classpath:db/changelog/changelog-master.yml
     */
    @Value("${spring.liquibase.change-log:classpath:db/changelog/changelog-master.yml}")
    private String changeLog;

    /**
     * Bean SpringLiquibase que ejecuta las migraciones al arrancar.
     * Recibe el DataSource configurado en application.properties.
     *
     * @param dataSource conexión a PostgreSQL inyectada por Spring
     * @return instancia configurada de SpringLiquibase
     */
    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setShouldRun(true);
        return liquibase;
    }
}
