package simple.mind.template;

/**
 * @author Mohibur Rashid
 */
class BadFormatException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  BadFormatException(String msg) {
    super(msg);
  }
}
