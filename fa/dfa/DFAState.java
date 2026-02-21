package fa.dfa;

import fa.State;

/**
 * Represents a single state in a DFA.
 * Extends the abstract State class.
 * @author Davis and Ado
 */
public class DFAState extends State {

    /**
     * Constructs a DFAState with the given name.
     * @param name the label of this state
     */
    public DFAState(String name) {
        super(name);
    }
}