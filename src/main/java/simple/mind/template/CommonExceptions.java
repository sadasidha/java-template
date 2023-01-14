package simple.mind.template;

/**
 * @author Mohibur Rashid
 */

class CommonExceptions extends RuntimeException {
  private static final long serialVersionUID = 1L;

  CommonExceptions(String msg) {
    super(msg);
  }
}

class BadIOException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  BadIOException(String ex) {
    super(ex);
  }
}

class BlockMissingException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  BlockMissingException(String msg) {
    super(msg);
  }

}

class DuplicateNameException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  DuplicateNameException(String message) {
    super(message);
  }
}

class IncorrectActionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public IncorrectActionException(String ex) {
    super(ex);
  }
}

class NotImpementedYetException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  NotImpementedYetException(String ex) {
    super(ex);
  }
}

class RepeatBlockWithoutEndException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  RepeatBlockWithoutEndException(String ex) {
    super(ex);
  }
}