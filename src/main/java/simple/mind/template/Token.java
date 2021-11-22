package simple.mind.template;

/**
 * @author Mohibur Rashid
 *
 */
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
