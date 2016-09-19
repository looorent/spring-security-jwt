package be.looorent.security.jwt;

/**
 * Reason when a JWS token parsing occurs.
 * @author Lorent Lempereur - lorent.lempereur.dev@gmail.com
 */
public enum JwtExceptionType {

    /**
     * see {@link io.jsonwebtoken.UnsupportedJwtException}
     */
    UNSUPPORTED("jws_unsupported_by_application"),

    /**
     * {@link io.jsonwebtoken.MalformedJwtException}
     */
    MALFORMED("jws_malformed"),

    /**
     * {@link io.jsonwebtoken.ExpiredJwtException}
     */
    EXPIRED("jwt_expired"),

    /**
     * {@link io.jsonwebtoken.SignatureException}
     */
    WRONG_SIGNATURE("jwt_wrong_signature");

    private final String code;

    JwtExceptionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
