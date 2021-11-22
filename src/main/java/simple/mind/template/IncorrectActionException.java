package simple.mind.template;

/**
 * @author Mohibur Rashid
 */
class IncorrectActionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public IncorrectActionException(String ex) {
    super(ex);
  }
}
