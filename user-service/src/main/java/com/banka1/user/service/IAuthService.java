package com.banka1.user.service;

import com.banka1.user.DTO.request.LoginRequest;
import com.banka1.user.model.helper.Permission;
import com.banka1.user.model.helper.Position;
import io.jsonwebtoken.Claims;

import java.util.List;

/**
 * Servis za autentifikaciju i autorizaciju.
 * Odgovoran je za generisanje i validiranje JWT-ova i namenjen je za korišćenje od strane middleware-a koji se vezuje za kontroler metode drugih servisa.
 */
public interface IAuthService {
    /**
     * Izvlači JWT iz autorizacionog header-a HTTP zahteva.
     *
     * @param authHeader Autorizacioni header zahteva koji sadrži token.
     * @return JWT token koji je deo datog header-a, ili <code>null</code> ako header nije u pravilnom formatu.
     */
    String getToken(String authHeader);

    /**
     * Parsira token za pristup.
     *
     * @param token String reprezentacija JWT-a koji se parsira.
     * @return {@link Claims} objekat koji sadrži informacije o korisniku koji je poslao request, ili <code>null</code> ako nije bilo moguće parsirati token.
     */
    Claims parseToken(String token);

    /**
     * Kreira novi token za pristup na osnovu postojećeg.
     *
     * @param token String reprezentacija već aktivnog JWT-a.
     * @return Novi token, ili <code>null</code> ako nije bilo moguće parsirati postojeći token.
     */
    String recreateToken(String token);

    // Kreira jwt sa korisnickim ID-em, rolom i permisijama.
    String generateToken(Long userId, Position position, List<Permission> permissions);

    // Login metoda omogucava korisnicima da se prijave koristeci email i lozinku.
    // Prvo trazi korisnika kao customer, ako nije pronadjen trazi korisnika kao employee, u suprotnom baca izuzetak.
    String login(LoginRequest loginRequest) throws IllegalArgumentException;
}
