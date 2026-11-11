package br.edu.ifc.bikes.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UsuarioRoleConverter implements AttributeConverter<Usuario.Role, String> {

    @Override
    public String convertToDatabaseColumn(Usuario.Role role) {
        return role == null ? null : "ROLE_" + role.name();
    }

    @Override
    public Usuario.Role convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        String role = value.startsWith("ROLE_") ? value.substring("ROLE_".length()) : value;
        return Usuario.Role.valueOf(role);
    }
}
