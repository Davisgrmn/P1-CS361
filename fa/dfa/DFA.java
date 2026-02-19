package fa.dfa;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a Deterministic Finite Automaton (DFA).
 * Models the 5-tuple (Initial, Final, Q, Sigma, )
 * @author Davis and Ado
 */
public class DFA implements DFAInterface {

    // Q
    private LinkedHashSet<DFAState> states;

    // Sigma
    private LinkedHashSet<Character> sigma;

    // Transitions
    private LinkedHashMap<String, DFAState> transitions;

    // Initial
    private DFAState startState;

    // Final
    private LinkedHashSet<DFAState> finalStates;

    /**
     * Default constructor. Initializes all DFA components.
     */
    public DFA() {
        states = new LinkedHashSet<>();
        sigma = new LinkedHashSet<>();
        transitions = new LinkedHashMap<>();
        startState = null;
        finalStates = new LinkedHashSet<>();
    }

    @Override
    public boolean addState(String name) {
        // Return false if a state with this name already exists
        if (getState(name) != null) {
            return false;
        }
        DFAState newState = new DFAState(name);
        states.add(newState);
        return true;
    }

    @Override
    public boolean setFinal(String name) {
        DFAState state = getStateObj(name);
        if (state == null) {
            return false;
        }
        finalStates.add(state);
        return true;
    }

    @Override
    public boolean setStart(String name) {
        DFAState state = getStateObj(name);
        if (state == null) {
            return false;
        }
        startState = state;
        return true;
    }

    @Override
    public void addSigma(char symbol) {
        sigma.add(symbol);
    }

    @Override
    public boolean accepts(String s) {
        // "e" represents the empty string
        if (startState == null) {
            return false;
        }

        DFAState current = startState;

        // If the input is "e"
        // Check if start state is final
        if (s.equals("e")) {
            return finalStates.contains(current);
        }

        // Process each character in the input string
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            String key = makeKey(current.getName(), c);
            DFAState next = transitions.get(key);
            if (next == null) {
                return false; // no transition (false // reject)
            }
            current = next;
        }

        return finalStates.contains(current);
    }

    @Override
    public Set<Character> getSigma() {
        return sigma;
    }

    @Override
    public State getState(String name) {
        return getStateObj(name);
    }

    @Override
    public boolean isFinal(String name) {
        DFAState state = getStateObj(name);
        if (state == null) {
            return false;
        }
        return finalStates.contains(state);
    }

    @Override
    public boolean isStart(String name) {
        if (startState == null) {
            return false;
        }
        return startState.getName().equals(name);
    }

    @Override
    public boolean addTransition(String fromState, String toState, char onSymb) {
        // Validate that both states exist and the symbol is in the alphabet
        DFAState from = getStateObj(fromState);
        DFAState to = getStateObj(toState);

        if (from == null || to == null || !sigma.contains(onSymb)) {
            return false;
        }

        String key = makeKey(fromState, onSymb);
        transitions.put(key, to);
        return true;
    }

    @Override
    public DFA swap(char symb1, char symb2) {
        // Create a deep copy with transition labels swapped
        DFA copy = new DFA();

        // Copy sigma
        for (char c : sigma) {
            copy.addSigma(c);
        }

        // Copy states (new DFAState objects)
        for (DFAState state : states) {
            copy.addState(state.getName());
        }

        // Set start state
        if (startState != null) {
            copy.setStart(startState.getName());
        }

        // Set final states
        for (DFAState fState : finalStates) {
            copy.setFinal(fState.getName());
        }

        // Copy transitions with swapped symbols
        for (Map.Entry<String, DFAState> entry : transitions.entrySet()) {
            String key = entry.getKey();
            DFAState toState = entry.getValue();

            // Parse the key to get fromState and symbol
            String fromName = key.substring(0, key.length() - 2);
            char sym = key.charAt(key.length() - 1);

            // Swap the symbol
            char newSym;
            if (sym == symb1) {
                newSym = symb2;
            } else if (sym == symb2) {
                newSym = symb1;
            } else {
                newSym = sym;
            }

            copy.addTransition(fromName, toState.getName(), newSym);
        }

        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Q = { ... }
        sb.append(" Q = {");
        for (DFAState state : states) {
            sb.append(" ").append(state.getName());
        }
        sb.append(" }\n");

        // Sigma = { ... }
        sb.append("Sigma = {");
        for (char c : sigma) {
            sb.append(" ").append(c);
        }
        sb.append(" }\n");

        // delta =
        sb.append("delta =\n");

        // Header row with symbols
        sb.append("\t\t");
        for (char c : sigma) {
            sb.append(c).append("\t");
        }
        sb.append("\n");

        // One row per state
        for (DFAState state : states) {
            sb.append("\t").append(state.getName());
            for (char c : sigma) {
                String key = makeKey(state.getName(), c);
                DFAState dest = transitions.get(key);
                sb.append("\t");
                if (dest != null) {
                    sb.append(dest.getName());
                }
            }
            sb.append("\n");
        }

        // q0 = ...
        sb.append("q0 = ").append(startState != null ? startState.getName() : "").append("\n");

        // F = { ... }
        sb.append("F = {");
        for (DFAState fState : finalStates) {
            sb.append(" ").append(fState.getName());
        }
        sb.append(" }\n");

        return sb.toString();
    }

    /**
     * Helper to find a DFAState object by name.
     * @param name the state name to search for
     * @return the DFAState with the given name, or null if not found
     */
    private DFAState getStateObj(String name) {
        for (DFAState state : states) {
            if (state.getName().equals(name)) {
                return state;
            }
        }
        return null;
    }

    /**
     * Helper to create a transition key from state name and symbol.
     * @param stateName the name of the from-state
     * @param symbol the transition symbol
     * @return a string key for the transitions map
     */
    private String makeKey(String stateName, char symbol) {
        return stateName + "," + symbol;
    }
}