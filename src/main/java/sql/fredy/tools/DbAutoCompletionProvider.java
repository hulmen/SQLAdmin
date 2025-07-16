package sql.fredy.tools;

import org.fife.ui.autocomplete.DefaultCompletionProvider;

/**
 *
 * @author sql@hulmen.ch
 */
public class DbAutoCompletionProvider extends DefaultCompletionProvider {

    protected boolean isValidChar(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_' || ch == '.';
    }

}
