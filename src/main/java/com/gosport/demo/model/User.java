package com.gosport.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data // Asegúrate de que tienes Lombok en tu pom.xml para usar @Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos combinados: Laravel tenía nombre y apellidos separados
    // Usaremos un campo 'name' para el nombre completo por simplicidad, 
    // O puedes usar 'name' y 'lastName' si cambias la base de datos.
    // Asumiré que el formulario mapeará a 'name' si combinamos 'nombre' y 'apellidos'.
    @Column(nullable = false)
    private String name; // Lo mantendremos como 'name'

    // Nuevos campos del formulario de Laravel:
    @Column(name = "phone", length = 20)
    private String telefono; // Mapeado a 'telefono'

    @Column(name = "document_type", length = 5)
    private String tipoDocumento; // Mapeado a 'tipo_documento'

    @Column(name = "identification_number", length = 50, unique = true)
    private String numeroIdentificacion; // Mapeado a 'numero_identificacion'

    @Column(name = "gender", length = 20)
    private String genero; // Mapeado a 'genero'
    
    // Campos existentes
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(nullable = false)
    private String password;

    @Column(name = "remember_token")
    private String rememberToken;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Si usaste la opción 2 antes, ejecuta la app para que Hibernate cree/actualice la tabla
}