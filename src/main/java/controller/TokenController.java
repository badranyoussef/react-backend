package controller;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dtos.UserDTO;
import exceptions.APIException;
import exceptions.NotAuthorizedException;
import io.javalin.http.HttpStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TokenController {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String timestamp = dateFormat.format(new Date());

    private static Set<String> blacklistedTokens = new HashSet<>();
    public static void invalidateToken(String token) {
        blacklistedTokens.add(token);
    }
    public static String createToken(UserDTO user){
        String ISSUER = System.getenv("ISSUER");
        String TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
        //String SECRET_KEY = System.getenv("SECRET_KEY");
        //String SECRET_KEY = System.getProperty("SECRET_KEY");
        String SECRET_KEY = "5465726d6432637344764c58774e70377958636356376b7846554d63695463585979697972485465";
        // https://codecurated.com/blog/introduction-to-jwt-jws-jwe-jwa-jwk/
        try {
            //What needs to be stored in the token is set here, with a Claimset
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .issuer(ISSUER)
                    .claim("id", user.getId())
                    .claim("name", user.getName())
                    .claim("email", user.getEmail())
                    .claim("phone", user.getPhone())
                    .claim("roles", user.getRoles().stream().reduce((s1, s2) -> s1 + "," + s2).get())
                    .expirationTime(new Date(new Date().getTime() + Integer.parseInt(TOKEN_EXPIRE_TIME)))
                    .build();
            Payload payload = new Payload(claimsSet.toJSONObject());

            JWSSigner signer = new MACSigner(SECRET_KEY);
            JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            jwsObject.sign(signer);
            return jwsObject.serialize();

        } catch (JOSEException e) {
            e.printStackTrace();
            throw new APIException(500, "Could not create token", timestamp);
        }
    }
    public static UserDTO verifyToken(String token){
        String SECRET = System.getenv("SECRET_KEY");
        try {
            if (blacklistedTokens.contains(token)) {
                throw new NotAuthorizedException(403, "Token is blacklisted");
            }
            else if (validateToken(token, SECRET) && tokenNotExpired(token)) {
                return getUserWithRoleFromToken(token);
            } else {
                throw new NotAuthorizedException(403, "Token is not valid");
            }
        } catch (ParseException | JOSEException | NotAuthorizedException e) {
            e.printStackTrace();
            throw new APIException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token", timestamp);
        }
    }

    private static UserDTO getUserWithRoleFromToken(String token) throws ParseException {
        // Return a user with Set of roles as strings
        SignedJWT jwt = SignedJWT.parse(token);
        String roles = jwt.getJWTClaimsSet().getClaim("roles").toString();
        String email = jwt.getJWTClaimsSet().getClaim("email").toString();
        String name = jwt.getJWTClaimsSet().getClaim("name").toString();
        int phone = Integer.parseInt(jwt.getJWTClaimsSet().getClaim("phone").toString());
        int id = Integer.parseInt(jwt.getJWTClaimsSet().getClaim("id").toString());

        Set<String> rolesSet = Arrays
                .stream(roles.split(","))
                .collect(Collectors.toSet());
        return new UserDTO(id, name, email, phone, rolesSet);
    }

    private static boolean validateToken(String token, String secret) throws NotAuthorizedException, JOSEException, ParseException {
        SignedJWT jwt = SignedJWT.parse(token);
            if (jwt.verify(new MACVerifier(secret)))
                return true;
            else
                throw new NotAuthorizedException(403, "Token is not valid");
    }
    private static boolean tokenNotExpired(String token) throws NotAuthorizedException, ParseException {
        if (timeToExpire(token) > 0)
            return true;
        else
            throw new NotAuthorizedException(403, "Token has expired");
    }
    private static int timeToExpire(String token) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(token);
        return (int) (jwt.getJWTClaimsSet().getExpirationTime().getTime() - new Date().getTime());
    }
}
