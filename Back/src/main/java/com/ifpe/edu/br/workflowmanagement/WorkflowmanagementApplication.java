package com.ifpe.edu.br.workflowmanagement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class WorkflowmanagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowmanagementApplication.class, args);
    }

    // Este método roda assim que o sistema inicia e garante que os papéis existam no banco
    @Bean
    public CommandLineRunner initData(JdbcTemplate jdbcTemplate) {
        return args -> {
            // INSERT IGNORE: Se já existir (ID 1, 2 ou 3), ele não faz nada. Se não, ele cria.
            jdbcTemplate.execute("INSERT IGNORE INTO papeis (id, nome) VALUES (1, 'ADMIN')");
            jdbcTemplate.execute("INSERT IGNORE INTO papeis (id, nome) VALUES (2, 'GERENTE')");
            jdbcTemplate.execute("INSERT IGNORE INTO papeis (id, nome) VALUES (3, 'DESENVOLVEDOR')");
            
            System.out.println("=== Banco de Dados Inicializado com Papéis Padrão ===");
        };
    }
}