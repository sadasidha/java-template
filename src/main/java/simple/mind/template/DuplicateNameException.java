package simple.mind.template;

/**
 * @author Mohibur Rashid
 */
class DuplicateNameException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  DuplicateNameException(String message) {
    super(message);
  }
}
