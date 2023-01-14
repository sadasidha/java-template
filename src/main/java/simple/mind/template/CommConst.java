package simple.mind.template;

/**
 * @author Mohibur Rashid
 */
class CommConst {
  /**
   * Added as extension of file name
   */
  static final String TEMPLATE = ".template";

  /**
   * Added spaces
   */
  static final int SETVAR_LENGTH = 2;
}

class TagsConst {
  /*
   * All command in template file
   */
  // file can be imported multiple time;
  static final String TAG_IMPORT = "#import ";
  static final String TAG_START = "#start ";
  static final String TAG_END = "#end ";
  static final String TAG_INSERT = "#insert ";
  static final String TAG_COMMENT = "#comment ";
  static final String TAG_SETVAR = "##";

  // file can be imported onetime only;
  static final String TAG_IMPORT_ONCE = "#import_once ";

}

class Token {
  boolean processed = false;
  String token = null;

  Token(boolean b, String t) {
    processed = b;
    token = t;
  }

  @Override
  public String toString() {
    return "[ " + processed + ", \"" + token + "\"]";
  }
}

enum BlockType {
  BLOCK_IMPORT, BLOCK_SIMPLE_LINE, BLOCK_REPEATE, BLOCK_INSERT, BLOCK_IMPORT_ONCE, BLOCK_COMMENT
}
