package com.casalprim.marc.tickettoridecalculator.utils;

import android.util.Pair;

import java.io.Serializable;

/**
 * Class that extends Pair so their instances can be equal if their elements are swapped
 * Created by marc on 24/01/18.
 */

public class PairS<C> extends Pair<C, C> implements Serializable {

    public PairS(C first, C second) {
        super(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        PairS<C> pair = (PairS<C>) o;
        boolean originalCondition = pair.first.equals(this.first) && pair.second.equals(this.second);
        boolean symmetricalCondition = pair.first.equals(this.second) && pair.second.equals(this.first);
        return originalCondition || symmetricalCondition;
    }

    @Override
    public int hashCode() {
        return this.first.hashCode() ^ this.second.hashCode();
    }
}
