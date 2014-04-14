package com.v2soft.AndLib.dao;

/**
 * Created by V.Shcryabets on 4/14/14.
 *
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public class EqualTools {
    public static boolean checkObjects(Object o1, Object o2) {
        if ( o1 == null && o2 == null) {
            return true;
        }
        if ( o1 != null && o2 == null ) {
            return false;
        }
        if ( o1 == null && o2 != null ) {
            return false;
        }
        return o1.equals(o2);
    }

    public static void checkNotNull(String label, Object object) throws NullPointerException{
        if ( object == null ) {
            throw new NullPointerException(String.format("%s is null", label));
        }
    }
}
