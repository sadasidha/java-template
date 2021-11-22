package simple.mind.template;

/**
 * @author Mohibur Rashid
 */
class BlockMissingException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  BlockMissingException(String msg) {
    super(msg);
  }

}
