package org.flymine.objectstore.query.presentation;

/*
 * Copyright (C) 2002-2003 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import org.flymine.objectstore.query.*;
import org.flymine.objectstore.query.fql.FqlQuery;

import java.util.ArrayList;

/**
 * Printable representation of a query constraint.
 *
 * @author Matthew Wakeling
 * @author Richard Smith
 */
public class PrintableConstraint
{
    protected Query query;
    protected Constraint constraint;

    /**
     * No-argument constructor, to allow overriding.
     */
    protected PrintableConstraint() {
    }

    /**
     * Constructor, takes a Query and a Constraint.
     *
     * @param query a Query with which to look up aliases
     * @param constraint a Constraint to encapsulate
     */
    public PrintableConstraint(Query query, Constraint constraint) {
        this.query = query;
        this.constraint = constraint;
    }

    /**
     * Return the wrapped Constraint.
     *
     * @return the original Constraint
     */
    public Constraint getConstraint() {
        return this.constraint;
    }

    /**
     * Returns the text of the left-hand side of this constraint, omitting any FromElement alias.
     *
     * @return a String
     */
    public String getLeft() {
        if (constraint instanceof ConstraintSet) {
            return null;
        }

        Object left = ConstraintHelper.getLeftArgument(constraint);

        if (left instanceof QueryField) {
            QueryField qf = (QueryField) left;
            return qf.getFieldName() + (qf.getSecondFieldName() == null ? "" : "."
                                        + qf.getSecondFieldName());
        } else if (left instanceof QueryNode) {
            return FqlQuery.nodeToString(query, (QueryNode) left);
        } else if (left instanceof QueryReference) {
            return ((QueryReference) left).getFieldName();
        }
        throw new IllegalArgumentException("Unknown left argument type");
    }


    /**
     * Returns the text of the operation of the constraint.
     *
     * @return a String
     */
    public String getOp() {
        if (constraint instanceof ConstraintSet) {
            return null;
        } else if (constraint instanceof ClassConstraint) {
            return (((ClassConstraint) constraint).isNotEqual() ? "!=" : "=");
        } else if (constraint instanceof ContainsConstraint) {
            return (((ContainsConstraint) constraint).isNotContains() ? "DOES NOT CONTAIN"
                    : "CONTAINS");
        } else if (constraint instanceof SimpleConstraint) {
            return ((SimpleConstraint) constraint).getType().toString();
        } else if (constraint instanceof SubqueryConstraint) {
            return (((SubqueryConstraint) constraint).isNotIn() ? "IS NOT IN" : "IN");
        }
        throw new IllegalArgumentException("Unknown Constraint type");
    }


        /**
     * Returns the text of the right-hand side of this constraint.
     *
     * @return a String
     */
    public String getRight() {
        if (constraint instanceof ConstraintSet) {
            return null;
        }

        Object right = ConstraintHelper.getRightArgument(constraint);

        /*if (right instanceof QueryField) {
            QueryField qf = (QueryField) right;
            return qf.getFieldName() + (qf.getSecondFieldName() == null ? "" : "."
                                        + qf.getSecondFieldName());
        } else*/ if (right instanceof QueryNode) {
            return FqlQuery.nodeToString(query, (QueryNode) right);
        } else if (right instanceof Query) {
            return ((Query) right).toString();
        } else if (right instanceof Object) { // should check this is a business object
            return ("\"" + right + "\"");
        }
        throw new IllegalArgumentException("Unknown right argument type");
    }


    /**
     * Returns a String describing the constraint.
     *
     * @return a String
     */
    public String toString() {
        return FqlQuery.constraintToString(query, constraint, new ArrayList());
    }

    /**
     * @see Object#equals
     */
    public boolean equals(Object obj) {
        if (obj.getClass() == this.getClass()) {
            PrintableConstraint pc = (PrintableConstraint) obj;
            return (query == pc.query) && (constraint == pc.constraint);
        }
        return false;
    }

    /**
     * @see Object#hashCode
     */
    public int hashCode() {
        return 5 * constraint.hashCode();
    }
}
