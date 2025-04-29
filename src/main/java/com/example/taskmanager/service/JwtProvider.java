package com.example.taskmanager.service;

import com.example.taskmanager.model.User;
import com.example.taskmanager.model.Role;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JwtProvider {

    private final String jwtSecret = Optional.ofNullable(System.getenv("JWT_SECRET"))
            .orElseGet(() -> Dotenv.load().get("JWT_SECRET"));
    private static final long jwtExpirationMs = 900000; //15 minutes

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public JwtProvider() {
        try{
            this.privateKey = loadPrivateKey("private_key.pem");
            this.publicKey = loadPublicKey("public_key.pem");

        } catch (Exception e){
            throw new RuntimeException("Could not load RSA keys",e);

        }
    }

    //Load private key
    private PrivateKey loadPrivateKey(String resourcePath) throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) throw new FileNotFoundException("Missing " + resourcePath);

        String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    //Load public key
    private PublicKey loadPublicKey(String resourcePath) throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) throw new FileNotFoundException("Missing " + resourcePath);

        String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    // Generate token with roles as claims
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList())) // Add roles as a claim
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList())) // Add roles as a claim
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    //Validate token after signing
    public boolean validateToken(String token) {
        try {
            JwtParser jwtParser = Jwts.parser()
                    .setSigningKey(publicKey);
            jwtParser.parseClaimsJws(token);  // Parses and validates the token
            return true; // If parsing succeeds, the token is valid
        } catch (Exception e) {
            // Token is either expired, tampered, or invalid
            return false;
        }
    }

    //Extract username from token
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}

