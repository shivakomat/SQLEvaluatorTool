package jsonsqlmodel;

import utils.JacksonUtil;

/**
 * Base class for nodes, currently just for a JSON-based toString().
 */
public abstract class Node {
    public final String toString() {
        return JacksonUtil.toString(this);
    }
}
