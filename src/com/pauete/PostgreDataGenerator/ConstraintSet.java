package com.pauete.Lienzo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ConstraintSet implements Iterable<Constraint>, Iterator<Constraint> {

    private final ArrayList<Constraint> constraints;
    private int count = 0;

    public ConstraintSet() {
        constraints = new ArrayList<>();
    }

    public boolean isEmpty() {
        return this.constraints.size() == 0;
    }

    public ConstraintSet addConstraint(@NotNull Constraint ctr) {
        this.constraints.add(ctr);
        return this;
    }

    public boolean hasPublicKey() {
        for (Constraint constraint : constraints) {
            if (constraint.getType().equals(Constraint.ConstraintType.PK)) {
                return true;
            }
        }
        return false;
    }


    @NotNull
    @Override
    public Iterator<Constraint> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return this.count < this.constraints.size();
    }

    @Override
    public Constraint next() {
        if (this.count == this.constraints.size())
            throw new NoSuchElementException();
        this.count++;
        return this.constraints.get(this.count - 1);
    }
}
