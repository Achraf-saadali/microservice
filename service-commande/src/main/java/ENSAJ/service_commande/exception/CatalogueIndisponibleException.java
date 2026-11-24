package ENSAJ.service_commande.exception;

/** Le service catalogue est injoignable, sans instance disponible, ou trop lent. */
public class CatalogueIndisponibleException extends RuntimeException {

    public CatalogueIndisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
