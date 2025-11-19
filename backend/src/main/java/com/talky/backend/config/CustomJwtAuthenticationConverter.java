package com.talky.backend.config;

import com.talky.backend.model.User;
import com.talky.backend.repository.UserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * Converter personalizado que obtiene el rol del usuario desde la base de datos
 * en lugar de depender solo de los grupos de Cognito.
 */
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter defaultConverter;
    private final UserRepository userRepository;

    public CustomJwtAuthenticationConverter(UserRepository userRepository) {
        this.defaultConverter = new JwtGrantedAuthoritiesConverter();
        this.userRepository = userRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Obtener autoridades por defecto (si hay grupos en Cognito)
        Collection<GrantedAuthority> authorities = new HashSet<>(defaultConverter.convert(jwt));

        // Obtener el rol desde la base de datos usando el cognitoSub del JWT
        String cognitoSub = jwt.getClaimAsString("sub");
        if (cognitoSub != null) {
            Optional<User> userOpt = userRepository.findByCognitoSub(cognitoSub);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Agregar el rol de la BD como autoridad
                String role = "ROLE_" + user.getRole().name();
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}

