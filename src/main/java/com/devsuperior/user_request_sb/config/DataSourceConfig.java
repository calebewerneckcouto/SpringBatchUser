package com.devsuperior.user_request_sb.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Classe de configuração para definir múltiplas fontes de dados
 * e gerenciar transações para cada uma delas.
 */
@Configuration // Indica que esta classe contém definições de beans para o Spring.
public class DataSourceConfig {

    /**
     * Configura a fonte de dados primária usada pelo Spring Batch.
     * As propriedades da conexão são lidas de "spring.datasource" no arquivo de configuração.
     * 
     * @return Um objeto DataSource configurado para o banco de dados do Spring Batch.
     */
    @Primary // Define este DataSource como a conexão principal (default).
    @Bean // Registra este método como um bean gerenciado pelo Spring.
    @ConfigurationProperties(prefix = "spring.datasource") // Liga as propriedades "spring.datasource" ao DataSource.
    public DataSource springBatchDB() {
        return DataSourceBuilder.create().build(); // Cria e retorna o DataSource com as configurações especificadas.
    }
    
    /**
     * Configura uma fonte de dados secundária usada pela aplicação.
     * As propriedades da conexão são lidas de "app.datasource" no arquivo de configuração.
     * 
     * @return Um objeto DataSource configurado para o banco de dados da aplicação.
     */
    @Bean // Registra este método como um bean gerenciado pelo Spring.
    @ConfigurationProperties(prefix = "app.datasource") // Liga as propriedades "app.datasource" ao DataSource.
    public DataSource appDB() {
        return DataSourceBuilder.create().build(); // Cria e retorna o DataSource com as configurações especificadas.
    }
    
    /**
     * Define o gerenciador de transações para o banco de dados da aplicação.
     * Utiliza o DataSource configurado no método appDB().
     * 
     * @param dataSource O DataSource que será gerenciado (appDB).
     * @return Um objeto PlatformTransactionManager para gerenciar transações.
     */
    @Bean // Registra este método como um bean gerenciado pelo Spring.
    public PlatformTransactionManager transactionManagerApp(@Qualifier("appDB") DataSource dataSource) {
        // Cria e retorna o gerenciador de transações vinculado ao appDB.
        return new DataSourceTransactionManager(dataSource);
    }
}
